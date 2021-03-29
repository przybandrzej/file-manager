package tech.przybysz.pms.filemanager.service.internal.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.internal.FileSystemIndexingService;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.io.StorageService;
import tech.przybysz.pms.filemanager.service.mapper.ResourceFileMapper;
import tech.przybysz.pms.filemanager.service.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileSystemIndexingServiceImpl implements FileSystemIndexingService {

  private final Logger log = LoggerFactory.getLogger(FileSystemIndexingServiceImpl.class);

  @Value("${application.indexing.batch-size}")
  private String indexingBatchSize;

  private final ResourceFileRepository fileRepository;
  private final ResourceFileMapper mapper;
  private final BackupService backupService;
  private final StorageService storageService;

  public FileSystemIndexingServiceImpl(ResourceFileRepository fileRepository, ResourceFileMapper mapper,
                                       BackupService backupService, StorageService storageService) {
    this.fileRepository = fileRepository;
    this.mapper = mapper;
    this.backupService = backupService;
    this.storageService = storageService;
  }

  @Scheduled(cron = "${application.indexing.cron}")
  public void indexFiles() {
    log.info("Executing indexing of all files");
    int pageSize = Integer.parseInt(indexingBatchSize);
    Sort sort = Sort.by("id").ascending();
    Pageable pageable = PageRequest.of(0, pageSize, sort);
    boolean last = false;
    while(!last) {
      Page<ResourceFile> page = fileRepository.findAll(pageable);
      List<ResourceFile> content = new ArrayList<>((int) page.getTotalElements());
      for(ResourceFile resourceFile : page.getContent()) {
        ResourceFileDTO resourceFileDTO = mapper.toDto(resourceFile);
        String fileName = PathUtils.getFileFullGeneratedName(resourceFileDTO);
        log.debug("Processing file {}", resourceFileDTO);
        File file = null;
        File backedUp = null;
        try {
          file = storageService.read(fileName);
        } catch(tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException ignored) {
        }
        try {
          backedUp = backupService.read(fileName);
        } catch(tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException ignored) {
        }
        if(file == null || !file.exists()) {
          if(backedUp != null && backedUp.exists()) {
            log.debug("File does not exist in main storage");
            boolean saved = storageService.store(fileName, backedUp);
            log.debug("File restored to the main storage = {}", saved);
          } else {
            log.error("Could not restore file {} to the main storage", resourceFileDTO);
          }
        }
        resourceFileDTO = backupService.checkFileBackUp(resourceFileDTO, file);
        content.add(mapper.toEntity(resourceFileDTO));
      }
      fileRepository.saveAll(content);
      last = page.isLast();
      pageable = page.nextPageable();
    }
    log.info("Ending indexing of all files");
  }
}
