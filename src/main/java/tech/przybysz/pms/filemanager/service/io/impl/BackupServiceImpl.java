package tech.przybysz.pms.filemanager.service.io.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.configuration.properties.BackupProperties;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.io.IOService;
import tech.przybysz.pms.filemanager.service.util.PathUtils;

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

  private final IOService ioService;

  private List<Path> storages = new ArrayList<>();

  public BackupServiceImpl(IOService ioService, BackupProperties backupProperties) {
    this.ioService = ioService;
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
    String name = PathUtils.getFileFullGeneratedName(resourceFileDTO);
    boolean backedUp = storages.stream().allMatch(storage -> ioService.save(name, stream, storage));
    resourceFileDTO.setBackedUp(backedUp);
    return backedUp;
  }

  @Override
  public boolean deleteBackup(ResourceFileDTO resourceFileDTO) {
    if(mode == BackupProperties.BackupMode.STRICT || storages.isEmpty()) {
      return false;
    }
    String name = PathUtils.getFileFullGeneratedName(resourceFileDTO);
    storages.forEach(storage -> ioService.remove(name, storage));
    resourceFileDTO.setBackedUp(false);
    return true;
  }
}
