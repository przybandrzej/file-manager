package tech.przybysz.pms.filemanager.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "tech.przybysz.pms.filemanager")
@EnableTransactionManagement
public class ApplicationConfiguration {
}
