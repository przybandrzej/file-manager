package tech.przybysz.pms.filemanager.service;

public interface DownloadService {

  FileResource get(Long fileId);

  FileResource getDirectory(Long directoryId);
}
