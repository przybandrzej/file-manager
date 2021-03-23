package tech.przybysz.pms.filemanager.service.util;

public class PathUtils {

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
}
