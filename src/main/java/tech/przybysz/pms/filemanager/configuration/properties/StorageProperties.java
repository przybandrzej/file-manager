package tech.przybysz.pms.filemanager.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

  private String location;
  private BackupProperties backup;

  public StorageProperties(String location, BackupProperties backup) {
    this.location = location;
    this.backup = backup;
  }

  public StorageProperties() {
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public BackupProperties getBackup() {
    return backup;
  }

  public void setBackup(BackupProperties backup) {
    this.backup = backup;
  }
}
