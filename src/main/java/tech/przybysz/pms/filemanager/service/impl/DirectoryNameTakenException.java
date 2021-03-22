package tech.przybysz.pms.filemanager.service.impl;

public class DirectoryNameTakenException extends RuntimeException {

  private Long parentDirectoryId;
  private String directoryName;

  public DirectoryNameTakenException(Long parentDirectoryId, String directoryName) {
    this.parentDirectoryId = parentDirectoryId;
    this.directoryName = directoryName;
  }

  public Long getParentDirectoryId() {
    return parentDirectoryId;
  }

  public void setParentDirectoryId(Long parentDirectoryId) {
    this.parentDirectoryId = parentDirectoryId;
  }

  public String getDirectoryName() {
    return directoryName;
  }

  public void setDirectoryName(String directoryName) {
    this.directoryName = directoryName;
  }
}
