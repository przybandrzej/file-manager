package tech.przybysz.pms.filemanager.service;

import org.springframework.core.io.ByteArrayResource;

public class FileResource {

  private final ByteArrayResource resource;
  private final String fileName;

  public FileResource(ByteArrayResource resource, String fileName) {
    this.resource = resource;
    this.fileName = fileName;
  }

  public ByteArrayResource getResource() {
    return resource;
  }

  public String getFileName() {
    return fileName;
  }
}
