package tech.przybysz.pms.filemanager.service.io.impl;

public class FileReadException extends RuntimeException {

  private String fileName;

  public FileReadException(String msg, String fileName) {
    super(msg);
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
