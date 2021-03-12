package tech.przybysz.pms.filemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tech.przybysz.pms.filemanager.configuration.properties.BackupProperties;
import tech.przybysz.pms.filemanager.configuration.properties.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({BackupProperties.class, StorageProperties.class})
public class FileManagerApplication {

  private static final Logger log = LoggerFactory.getLogger(FileManagerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(FileManagerApplication.class, args);
  }

}
