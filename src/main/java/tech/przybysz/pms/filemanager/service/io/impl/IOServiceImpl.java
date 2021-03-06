package tech.przybysz.pms.filemanager.service.io.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.przybysz.pms.filemanager.service.io.IOService;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IOServiceImpl implements IOService {

  @Value("${storage.location}")
  private String storageLocation;

  private Path storage;

  @PostConstruct
  public void init() {
    storage = Paths.get(storageLocation);
    try {
      Files.createDirectories(storage);
    } catch(IOException e) {
      throw new StorageException("Could not initialize storage location [" + storageLocation + "]");
    }
  }

  @Override
  public boolean save(String filename, InputStream stream) {
    if(filename == null || filename.isBlank() || stream == null) {
      throw new StorageException("File name or content is empty.");
    }
    File file = new File(StringUtils.cleanPath(filename));
    try {
      if(file.exists()) {
        throw new FileAlreadyExistsException(filename);
      }
      if(filename.contains("..")) {
        // This is a security check
        throw new StorageException(
            "Cannot store file with relative path outside current directory "
                + filename);
      }
      try(stream) {
        Files.copy(stream, this.storage.resolve(filename));
      }
    } catch(IOException e) {
      throw new StorageException("Failed to store file " + filename);
    }
    return true;
  }

  @Override
  public File read(String filename) {
    if(filename == null || filename.isBlank()) {
      throw new FileNotFoundException("Could not read file. The name is empty.", filename);
    }
    try {
      File file = storage.resolve(filename).toFile();
      if(file.exists()) {
        if(file.isFile()) {
          return file;
        } else {
          throw new IsNotFileException(filename);
        }
      } else {
        throw new FileNotFoundException("Could not read file.", filename);
      }
    } catch(InvalidPathException e) {
      throw new FileNotFoundException("Could not read file. The path is malformed.", filename);
    }
  }

  @Override
  public List<String> readContent(String filename) {
    Path file = storage.resolve(filename);

    try(BufferedReader reader = Files.newBufferedReader(file)) {
      return reader.lines().collect(Collectors.toList());
    } catch(IOException e) {
      throw new StorageException("Could not read file.");
    }
  }

  @Override
  public Resource loadAsResource(String filename) {
    if(filename == null || filename.isEmpty()) {
      throw new FileNotFoundException("Could not read file. The name is empty.", filename);
    }
    try {
      Path file = storage.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if(resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new FileNotFoundException("Could not read file.", filename);
      }
    } catch(MalformedURLException e) {
      throw new FileNotFoundException("Could not read file. The path is malformed.", filename);
    }
  }

}
