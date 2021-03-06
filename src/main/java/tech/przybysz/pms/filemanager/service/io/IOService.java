package tech.przybysz.pms.filemanager.service.io;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IOService {

  boolean save(String filename, InputStream stream);

  File read(String fileName);

  List<String> readContent(String filename);

  Resource loadAsResource(String filename);
}
