package tech.przybysz.pms.filemanager.service.dto;

import java.util.ArrayList;
import java.util.List;

public class DownloadDTO {

  private List<Long> directories = new ArrayList<>();
  private List<Long> files = new ArrayList<>();

  public List<Long> getDirectories() {
    return directories;
  }

  public void setDirectories(List<Long> directories) {
    this.directories = directories;
  }

  public List<Long> getFiles() {
    return files;
  }

  public void setFiles(List<Long> files) {
    this.files = files;
  }

  @Override
  public String toString() {
    return "DownloadDTO{" +
        "directories=" + directories +
        ", files=" + files +
        '}';
  }
}
