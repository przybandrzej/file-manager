package tech.przybysz.pms.filemanager.service.util;

import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

public class PathUtils {

  private PathUtils() {}

  public static String normalizePath(String path) {
    path = path.replace('\\', '/');
    path = path.replaceAll("(/)\\1+", "/");
    if(path.startsWith("/")) {
      path = path.substring(1);
    }
    if(path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  public static String getFileFullGeneratedName(ResourceFileDTO resourceFileDTO) {
    String path = "";
    path = resourceFileDTO.getGeneratedName();
    if(resourceFileDTO.getExtension() != null && !resourceFileDTO.getExtension().isBlank()) {
      path += "." + resourceFileDTO.getExtension();
    }
    return path;
  }
}
