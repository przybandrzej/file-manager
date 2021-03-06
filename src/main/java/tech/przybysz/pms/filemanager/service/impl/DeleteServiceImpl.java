package tech.przybysz.pms.filemanager.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.service.DeleteService;
import tech.przybysz.pms.filemanager.service.io.IOService;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeleteServiceImpl implements DeleteService {

  @Value("${storage.backup.execute}")
  private Boolean isBackup;

  @Value("${storage.backup.locations}")
  private String[] backupLocations;

  @Value("${storage.location}")
  private String storageLocation;

  private final IOService ioService;

  private List<Path> storages = new ArrayList<>();

  public DeleteServiceImpl(IOService ioService) {
    this.ioService = ioService;
  }

  @PostConstruct
  public void init() {
    if(Boolean.TRUE.equals(isBackup)) {
      storages = Arrays.stream(backupLocations).map(Paths::get).collect(Collectors.toList());
    }
    storages.add(Paths.get(storageLocation));
  }

  @Override
  public void deleteFile(ResourceFile file) {
    String filename = file.getGeneratedName() + "." + file.getExtension();
    storages.forEach(storage -> ioService.remove(filename, storage));
  }

  @Override
  public void deleteFiles(Collection<ResourceFile> files) {
    for(ResourceFile file : files) {
      String filename = file.getGeneratedName() + "." + file.getExtension();
      storages.forEach(storage -> ioService.remove(filename, storage));
    }
  }
}
