package tech.przybysz.pms.filemanager.service.io;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface IOService {

  boolean save(String filename, InputStream stream, Path storage);

  File read(String fileName, Path storage);

  List<String> readContent(String filename, Path storage);

  Resource loadAsResource(String filename, Path storage);

  boolean remove(String filename, Path storage);
}
