package tech.przybysz.pms.filemanager.service.io;

import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

import java.io.InputStream;

public interface BackupService {

  boolean backup(ResourceFileDTO resourceFileDTO, InputStream stream);

  boolean deleteBackup(ResourceFileDTO resourceFileDTO);
}
