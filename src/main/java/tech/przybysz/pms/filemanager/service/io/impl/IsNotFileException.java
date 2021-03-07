package tech.przybysz.pms.filemanager.service.io.impl;

public class IsNotFileException extends StorageException {

  private String filename;

  public IsNotFileException(String filename) {
    super("Resource is not a file.");
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
