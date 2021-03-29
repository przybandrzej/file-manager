package tech.przybysz.pms.filemanager.service.io.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.configuration.properties.BackupProperties;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.io.IOService;
import tech.przybysz.pms.filemanager.service.util.PathUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.*;
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

  private final Logger log = LoggerFactory.getLogger(BackupServiceImpl.class);

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

  @Override
  public File read(String filename) {
    File file;
    for(Path storage : storages) {
      try {
        file = ioService.read(filename, storage);
      } catch(tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException e) {
        continue;
      }
      return file;
    }
    throw new tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException("File not found", filename);
  }

  @Override
  public ResourceFileDTO checkFileBackUp(ResourceFileDTO resourceFileDTO, File file) {
    log.debug("Checking backups of file {}", resourceFileDTO);
    log.debug("Application backup parameters: mode {}, execute {}, locations {}", mode, execute, storages.size());
    String filename = PathUtils.getFileFullGeneratedName(resourceFileDTO);
    if((resourceFileDTO.getBackUp() || mode == BackupProperties.BackupMode.STRICT) && Boolean.TRUE.equals(execute)) {
      boolean saved = storages.stream().allMatch(storage -> {
        File tmp = null;
        try {
          tmp = ioService.read(filename, storage);
        } catch(tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException ignored) {
        }
        if(tmp == null || !tmp.exists()) {
          if(file == null) {
            return false;
          }
          try(InputStream stream = new FileInputStream(file)) {
            return ioService.save(filename, stream, storage);
          } catch(FileNotFoundException e) {
            throw new tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException("File not found", filename);
          } catch(IOException e) {
            throw new StorageException("Failed to store file " + filename);
          }
        }
        return true;
      });
      resourceFileDTO.setBackedUp(saved);
    } else if((!execute && resourceFileDTO.getBackUp()) || (!resourceFileDTO.getBackUp() && mode != BackupProperties.BackupMode.STRICT)) {
      boolean removed = storages.stream().allMatch(storage -> ioService.remove(filename, storage));
      resourceFileDTO.setBackedUp(!removed);
    }
    log.debug("Indexing backups finished for file {}", resourceFileDTO);
    return resourceFileDTO;
  }
}
