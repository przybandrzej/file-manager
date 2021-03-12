package tech.przybysz.pms.filemanager.service.io.impl;

public class FileAlreadyExistsException extends RuntimeException {

  public FileAlreadyExistsException(String msg) {
    super(msg);
  }
}
