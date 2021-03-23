package tech.przybysz.pms.filemanager.service.dto;

public class DirectoryPathDTO {

  private String path;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return "DirectoryPathDTO{" +
        "path='" + path + '\'' +
        '}';
  }
}
