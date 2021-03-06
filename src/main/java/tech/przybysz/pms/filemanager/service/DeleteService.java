package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.domain.ResourceFile;

import java.util.Collection;

public interface DeleteService {

  void deleteFile(ResourceFile file);

  void deleteFiles(Collection<ResourceFile> files);
}
