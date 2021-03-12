package tech.przybysz.pms.filemanager.service.io;

import java.io.File;
import java.io.InputStream;

public interface StorageService {

  boolean store(String filename, InputStream stream);

  File read(String fileName);

}
