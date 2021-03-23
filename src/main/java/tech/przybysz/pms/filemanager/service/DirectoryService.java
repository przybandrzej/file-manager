package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.DirectoryPathDTO;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;

import java.util.List;
import java.util.Optional;

public interface DirectoryService {

  DirectoryDTO create(DirectoryDTO directoryDTO);

  List<DirectoryDTO> create(List<DirectoryDTO> dtos);

  DirectoryDTO update(DirectoryDTO directoryDTO);

  List<DirectoryDTO> findAll();

  Optional<DirectoryDTO> findOne(Long id);

  void delete(Long id);

  List<DirectoryDTO> findAllChildren(Long parentId);

  DirectoryDTO updateName(Long id, String name);

  DirectoryDTO updateParentDirectory(Long id, Long parentDirectoryId);

  void delete(IDsDTO ids);

  List<DirectoryDTO> updateParentDirectory(IDsDTO ids, Long parentDirectoryId);

  DirectoryDTO findOrCreatePath(DirectoryPathDTO path);
}
