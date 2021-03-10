package tech.przybysz.pms.filemanager.service.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.DownloadService;
import tech.przybysz.pms.filemanager.service.FileResource;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.StorageService;
import tech.przybysz.pms.filemanager.service.io.impl.StorageException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class DownloadServiceImpl implements DownloadService {

  private final ResourceFileService fileService;
  private final StorageService storageService;
  private final DirectoryService directoryService;

  public DownloadServiceImpl(ResourceFileService fileService, StorageService storageService,
                             DirectoryService directoryService) {
    this.fileService = fileService;
    this.storageService = storageService;
    this.directoryService = directoryService;
  }

  @Override
  public FileResource get(Long fileId) {
    ResourceFileDTO fileDTO = fileService.findOne(fileId).orElseThrow(() -> new EntityNotFoundException("resourceFile", fileId));
    String fileName = fileDTO.getGeneratedName() + "." + fileDTO.getExtension();
    String originalName = fileDTO.getOriginalName() + "." + fileDTO.getExtension();
    File file = storageService.read(fileName);
    try {
      return new FileResource(new ByteArrayResource(Files.readAllBytes(Paths.get(file.getAbsolutePath()))), originalName);
    } catch(IOException e) {
      throw new StorageException("Could not read file from storage.");
    }
  }

  @Override
  public FileResource getDirectory(Long directoryId) {
    try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos)) {
      String parentDirectories = "";
      DirectoryDTO directoryDTO = directoryService.findOne(directoryId).orElseThrow(() -> new EntityNotFoundException("directory", directoryId));
      processDirectory(directoryDTO, parentDirectories, out);
      return new FileResource(new ByteArrayResource(bos.toByteArray()), System.currentTimeMillis() + ".zip");
    } catch(IOException e) {
      throw new StorageException("Could not read file from storage.");
    }
  }

  private void processDirectory(DirectoryDTO directoryDTO, String parentDirectories, ZipOutputStream zipOutputStream) throws IOException {
    parentDirectories += directoryDTO.getName() + "/";
    addDirectoryToZip(directoryDTO.getName(), zipOutputStream);
    List<ResourceFileDTO> files = fileService.findAllOfDirectory(directoryDTO.getId());
    for(ResourceFileDTO file : files) {
      String generatedName = file.getGeneratedName() + "." + file.getExtension();
      String originalName = parentDirectories + file.getOriginalName() + "." + file.getExtension();
      addFileToZip(generatedName, originalName, zipOutputStream);
    }
    List<DirectoryDTO> allChildren = directoryService.findAllChildren(directoryDTO.getId());
    for(DirectoryDTO child : allChildren) {
      processDirectory(child, parentDirectories, zipOutputStream);
    }
  }

  private void addFileToZip(String filePath, String zipFilePath, ZipOutputStream zipStream) throws IOException {
    File fileToZip = storageService.read(filePath);
    try(FileInputStream fis = new FileInputStream(fileToZip)) {
      ZipEntry zipEntry = new ZipEntry(zipFilePath);
      zipStream.putNextEntry(zipEntry);
      IOUtils.copy(fis, zipStream);
    }
    zipStream.closeEntry();
  }

  private void addDirectoryToZip(String dirPath, ZipOutputStream zipOut) throws IOException {
    if(dirPath.endsWith("/")) {
      zipOut.putNextEntry(new ZipEntry(dirPath));
    } else {
      zipOut.putNextEntry(new ZipEntry(dirPath + "/"));
    }
    zipOut.closeEntry();
  }
}
