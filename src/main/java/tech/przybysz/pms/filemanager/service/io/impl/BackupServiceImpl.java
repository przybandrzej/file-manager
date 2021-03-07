package tech.przybysz.pms.filemanager.service.io.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.configuration.properties.BackupProperties;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.IOService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BackupServiceImpl implements BackupService {

  private final Boolean execute;
  private final String[] locations;
  private final BackupProperties.BackupMode mode;
  private final Integer count;

  private final IOService ioService;
  private final ResourceFileService resourceFileService;

  private List<Path> storages = new ArrayList<>();

  public BackupServiceImpl(IOService ioService, ResourceFileService resourceFileService, BackupProperties backupProperties) {
    this.ioService = ioService;
    this.resourceFileService = resourceFileService;
    this.count = backupProperties.getCopiesCount();
    this.mode = backupProperties.getMode();
    this.locations = backupProperties.getLocations();
    this.execute = backupProperties.getExecute();
  }

  @PostConstruct
  public void init() {
    if(Boolean.FALSE.equals(execute)) {
      return;
    }
    storages = Arrays.stream(locations).map(Paths::get).collect(Collectors.toList());
    for(Path storage : storages) {
      try {
        Files.createDirectories(storage);
      } catch(IOException e) {
        storages.remove(storage);
        throw new BackupInitException("Could not initialize storage location [" + storage.toString() + "]");
      }
    }
  }

  @Override
  public boolean backup(ResourceFileDTO resourceFileDTO, InputStream stream) {
    if(Boolean.FALSE.equals(execute) || storages.isEmpty()) {
      return false;
    }
    String name = resourceFileDTO.getGeneratedName() + "." + resourceFileDTO.getExtension();
    boolean backedUp = storages.stream().allMatch(storage -> ioService.save(name, stream, storage));
    resourceFileDTO.setBackedUp(backedUp);
    resourceFileService.update(resourceFileDTO);
    return backedUp;
  }

  @Override
  public boolean deleteBackup(ResourceFileDTO resourceFileDTO) {
    if(Boolean.FALSE.equals(execute) || storages.isEmpty()) {
      return false;
    }
    String name = resourceFileDTO.getGeneratedName() + "." + resourceFileDTO.getExtension();
    storages.forEach(storage -> ioService.remove(name, storage));
    resourceFileDTO.setBackedUp(false);
    resourceFileService.update(resourceFileDTO);
    return true;
  }
}
