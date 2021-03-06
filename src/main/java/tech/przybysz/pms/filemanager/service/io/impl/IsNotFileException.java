package tech.przybysz.pms.filemanager.service.io.impl;

public class IsNotFileException extends StorageException {

  private String filename;

  public IsNotFileException(String filename) {
    super("Resource is not a file.");
    this.filename = filename;
  }
}
