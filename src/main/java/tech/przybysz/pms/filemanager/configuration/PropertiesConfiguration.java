package tech.przybysz.pms.filemanager.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("tech.przybysz.pms.filemanager.configuration.properties")
public class PropertiesConfiguration {
}
