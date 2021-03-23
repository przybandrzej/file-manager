package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.domain.Tag;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.repository.TagRepository;
import tech.przybysz.pms.filemanager.service.TagService;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.TagDTO;
import tech.przybysz.pms.filemanager.service.mapper.TagMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

  private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

  public static final String ENTITY_NAME = "tag";

  private final TagRepository tagRepository;
  private final TagMapper mapper;
  private final ResourceFileRepository fileRepository;

  public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper, ResourceFileRepository fileRepository) {
    this.tagRepository = tagRepository;
    this.mapper = tagMapper;
    this.fileRepository = fileRepository;
  }

  @Override
  public TagDTO create(TagDTO tagDTO) {
    log.debug("Request to create Tag {}", tagDTO);
    if(tagRepository.existsByName(tagDTO.getName())) {
      throw new EntityAlreadyExistsException(ENTITY_NAME, tagDTO.getName());
    }
    Tag tag = new Tag();
    tag.setName(tagDTO.getName());
    return mapper.toDto(tagRepository.save(tag));
  }

  @Override
  public TagDTO update(TagDTO tagDTO) {
    log.debug("Request to update Tag {}", tagDTO);
    if(tagRepository.existsByName(tagDTO.getName())) {
      throw new EntityAlreadyExistsException(ENTITY_NAME, tagDTO.getName());
    }
    Tag tag = mapper.toEntity(tagDTO);
    tag.setName(tagDTO.getName());
    return mapper.toDto(tagRepository.save(tag));
  }

  @Override
  public List<TagDTO> findAll() {
    log.debug("Request to get all Tags");
    return tagRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public Optional<TagDTO> findOne(Long id) {
    log.debug("Request to get Tag {}", id);
    return tagRepository.findById(id).map(mapper::toDto);
  }

  @Override
  public List<TagDTO> findAllOfFile(Long fileId) {
    log.debug("Request to get all Tags of ResourceFile {}", fileId);
    return fileRepository.findById(fileId)
        .map(ResourceFile::getTags).map(tags -> tags.stream().map(mapper::toDto).collect(Collectors.toList()))
        .orElseThrow(() -> new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, fileId));
  }

  @Override
  public void delete(Long id) {
    log.debug("Request to delete Tag {}", id);
    tagRepository.deleteById(id);
  }

  @Override
  public void delete(IDsDTO ids) {
    log.debug("Request to delete Tags {}", ids);
    tagRepository.deleteByIdIn(ids.getIds());
  }

  @Override
  public List<TagDTO> findAllByName(String name, boolean exact) {
    log.debug("Request to get Tags by name {} exact {}", name, exact);
    Collection<Tag> tags = new ArrayList<>();
    if(exact) {
      Optional<Tag> byName = tagRepository.findByName(name);
      if(byName.isPresent()) {
        tags.add(byName.get());
      }
    } else {
      tags = tagRepository.findAllByNameContainsIgnoreCase(name);
    }
    return tags.stream().map(mapper::toDto).collect(Collectors.toList());
  }
}
