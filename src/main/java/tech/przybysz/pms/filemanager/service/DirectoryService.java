package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;

import java.util.List;
import java.util.Optional;

public interface DirectoryService {

  DirectoryDTO create(DirectoryDTO directoryDTO);

  DirectoryDTO update(DirectoryDTO directoryDTO);

  List<DirectoryDTO> findAll();

  Optional<DirectoryDTO> findOne(Long id);

  void delete(Long id);

  List<DirectoryDTO> findAllChildren(Long parentId);

}
