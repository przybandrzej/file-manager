package tech.przybysz.pms.filemanager.service.impl;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.service.DownloadService;
import tech.przybysz.pms.filemanager.service.FileResource;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.StorageService;
import tech.przybysz.pms.filemanager.service.io.impl.StorageException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Transactional
public class DownloadServiceImpl implements DownloadService {

  private final ResourceFileService fileService;
  private final StorageService storageService;

  public DownloadServiceImpl(ResourceFileService fileService, StorageService storageService) {
    this.fileService = fileService;
    this.storageService = storageService;
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
      // todo try to read from backup
    }
  }
}
