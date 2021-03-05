package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

import java.util.List;
import java.util.Optional;

public interface ResourceFileService {

  ResourceFileDTO create(ResourceFileDTO fileDTO);

  ResourceFileDTO update(ResourceFileDTO fileDTO);

  List<ResourceFileDTO> findAll();

  Optional<ResourceFileDTO> findOne(Long id);

  void delete(Long id);

  List<ResourceFileDTO> findAllOfDirectory(Long directoryId);

}
