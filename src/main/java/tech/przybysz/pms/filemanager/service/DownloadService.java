package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.DownloadDTO;

public interface DownloadService {

  FileResource getFile(Long fileId);

  FileResource getDirectory(Long directoryId);

  FileResource get(DownloadDTO downloadDTO);
}
