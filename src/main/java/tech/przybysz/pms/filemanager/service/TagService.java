package tech.przybysz.pms.filemanager.service;

import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.TagDTO;

import java.util.List;
import java.util.Optional;

public interface TagService {

  TagDTO create(TagDTO tagDTO);

  TagDTO update(TagDTO tagDTO);

  List<TagDTO> findAll();

  Optional<TagDTO> findOne(Long id);

  void delete(Long id);

  List<TagDTO> findAllOfFile(Long fileId);

  void delete(IDsDTO ids);

  List<TagDTO> findAllByName(String name, boolean exact);
}
