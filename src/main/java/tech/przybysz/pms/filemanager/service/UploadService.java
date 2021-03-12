package tech.przybysz.pms.filemanager.service;

import org.springframework.web.multipart.MultipartFile;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

import java.util.List;

public interface UploadService {

  List<ResourceFileDTO> save(Long directoryId, List<MultipartFile> files);
}
