package tech.przybysz.pms.filemanager.service.io.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.przybysz.pms.filemanager.service.io.IOService;
import tech.przybysz.pms.filemanager.service.io.StorageService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl implements StorageService {

  @Value("${storage.location}")
  private String storageLocation;

  private Path storage;

  @PostConstruct
  public void init() {
    storage = Paths.get(storageLocation);
    try {
      Files.createDirectories(storage);
    } catch(IOException e) {
      throw new StorageInitException("Could not initialize storage location [" + storageLocation + "]");
    }
  }

  private final IOService ioService;

  public StorageServiceImpl(IOService ioService) {
    this.ioService = ioService;
  }

  @Override
  public boolean store(String filename, InputStream stream) {
    return ioService.save(filename, stream, storage);
  }

  @Override
  public File read(String fileName) {
    return ioService.read(fileName, storage);
  }
}
