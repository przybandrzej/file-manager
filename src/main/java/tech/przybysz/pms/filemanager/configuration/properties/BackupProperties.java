package tech.przybysz.pms.filemanager.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.backup")
public class BackupProperties {

  public enum BackupMode {
    STRICT, FIRST_OFF, FIST_ON
  }

  private Boolean execute = false;
  private String[] locations;
  private BackupMode mode;

  public BackupProperties(Boolean execute, String[] locations, BackupMode mode) {
    this.execute = execute;
    this.locations = locations;
    this.mode = mode;
  }

  public BackupProperties() {
  }

  public Boolean getExecute() {
    return execute;
  }

  public void setExecute(Boolean execute) {
    this.execute = execute;
  }

  public String[] getLocations() {
    return locations;
  }

  public void setLocations(String[] locations) {
    this.locations = locations;
  }

  public BackupMode getMode() {
    return mode;
  }

  public void setMode(BackupMode mode) {
    this.mode = mode;
  }
}
