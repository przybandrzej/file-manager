package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.przybysz.pms.filemanager.configuration.properties.BackupProperties;
import tech.przybysz.pms.filemanager.domain.enumeration.FileSizeUnit;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.UploadService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.StorageService;
import tech.przybysz.pms.filemanager.service.io.impl.FileReadException;
import tech.przybysz.pms.filemanager.service.util.PathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UploadServiceImpl implements UploadService {

  private final Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);

  private final ResourceFileService fileService;
  private final StorageService storageService;
  private final BackupService backupService;
  private final BackupProperties.BackupMode backupMode;

  public UploadServiceImpl(ResourceFileService fileService, StorageService storageService, BackupService backupService,
                           BackupProperties backupProperties) {
    this.fileService = fileService;
    this.storageService = storageService;
    this.backupService = backupService;
    this.backupMode = backupProperties.getMode();
  }

  @Override
  public List<ResourceFileDTO> save(Long directoryId, List<MultipartFile> files) {
    log.debug("Request to save {} files to Directory {}", files.size(), directoryId);
    List<ResourceFileDTO> resources = files.stream()
        .map(file -> this.createResourceDto(directoryId, file.getOriginalFilename(), file.getSize()))
        .map(fileService::create)
        .collect(Collectors.toList());
    for(int i = 0; i < files.size(); i++) {
      MultipartFile file = files.get(i);
      ResourceFileDTO resource = resources.get(i);
      try(InputStream stream = file.getInputStream()) {
        storageService.store(PathUtils.getFileFullGeneratedName(resource), stream);
        if(backupMode != BackupProperties.BackupMode.FIRST_OFF) {
          resource.setBackUp(true);
          resource.setBackedUp(backupService.backup(resource, file.getInputStream()));
        } else {
          resource.setBackUp(false);
        }
        fileService.updateInternal(resource);
      } catch(IOException e) {
        throw new FileReadException("Could not read the file.", resource.getOriginalName() + "." + resource.getExtension());
      }
    }
    return resources;
  }

  private ResourceFileDTO createResourceDto(Long directoryId, String originalName, long byteSize) {
    if(originalName == null) {
      originalName = "file";
    }
    int index = originalName.lastIndexOf('.');
    String name = index != -1 ? originalName.substring(0, index) : originalName;
    String extension = index != -1 ? originalName.substring(index + 1) : null;
    ResourceFileDTO dto = new ResourceFileDTO();
    dto.setParentDirectoryId(directoryId);
    dto.setOriginalName(name);
    dto.setExtension(extension);
    dto.setSize(byteSize);
    dto.setSizeUnit(FileSizeUnit.BYTE);
    dto.setBackUp(false);
    dto.setBackedUp(false);
    return dto;
  }

}
