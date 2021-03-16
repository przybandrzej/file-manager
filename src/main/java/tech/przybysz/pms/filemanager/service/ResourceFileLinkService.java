package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileLinkDTO;

import java.util.List;
import java.util.Optional;

public interface ResourceFileLinkService {

  ResourceFileLinkDTO create(ResourceFileLinkDTO fileDTO);

  ResourceFileLinkDTO update(ResourceFileLinkDTO fileDTO);

  List<ResourceFileLinkDTO> findAll();

  Optional<ResourceFileLinkDTO> findOne(Long id);

  void delete(Long id);

  List<ResourceFileLinkDTO> findAllOfParentFile(Long parentFileId);

  List<ResourceFileLinkDTO> findAllOfChildFile(Long childId);

  ResourceFileLinkDTO updateName(Long id, String name);

  void delete(IDsDTO ids);

  List<ResourceFileLinkDTO> create(List<ResourceFileLinkDTO> links);
}
