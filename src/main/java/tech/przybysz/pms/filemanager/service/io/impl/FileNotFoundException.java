package tech.przybysz.pms.filemanager.service.io.impl;

public class FileNotFoundException extends RuntimeException {

  private String filename;

  public FileNotFoundException(String msg, String filename) {
    super(msg);
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
