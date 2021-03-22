package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.ResourceFileLink;
import tech.przybysz.pms.filemanager.repository.ResourceFileLinkRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.ResourceFileLinkService;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileLinkDTO;
import tech.przybysz.pms.filemanager.service.mapper.ResourceFileLinkMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceFileLinkServiceImpl implements ResourceFileLinkService {

  private final Logger log = LoggerFactory.getLogger(ResourceFileLinkServiceImpl.class);

  public static final String ENTITY_NAME = "resourceFileLink";

  private final ResourceFileLinkMapper mapper;
  private final ResourceFileLinkRepository repository;
  private final ResourceFileRepository resourceFileRepository;

  public ResourceFileLinkServiceImpl(ResourceFileLinkMapper mapper, ResourceFileLinkRepository repository,
                                     ResourceFileRepository resourceFileRepository) {
    this.mapper = mapper;
    this.repository = repository;
    this.resourceFileRepository = resourceFileRepository;
  }

  @Override
  public ResourceFileLinkDTO create(ResourceFileLinkDTO fileDTO) {
    log.debug("Request to create ResourceFileLink {}", fileDTO);
    if(fileDTO.getParentFileId() == null || !resourceFileRepository.existsById(fileDTO.getParentFileId())) {
      throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, fileDTO.getParentFileId());
    }
    if(fileDTO.getChildFileId() == null || !resourceFileRepository.existsById(fileDTO.getChildFileId())) {
      throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, fileDTO.getChildFileId());
    }
    ResourceFileLink link = mapper.toEntity(fileDTO);
    link.setId(null);
    return mapper.toDto(repository.save(link));
  }

  @Override
  public ResourceFileLinkDTO update(ResourceFileLinkDTO fileDTO) {
    log.debug("Request to update ResourceFileLink {}", fileDTO);
    if(fileDTO.getId() == null || !repository.existsById(fileDTO.getId())) {
      throw new EntityNotFoundException(ENTITY_NAME, fileDTO.getId());
    }
    if(fileDTO.getParentFileId() == null || !resourceFileRepository.existsById(fileDTO.getParentFileId())) {
      throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, fileDTO.getParentFileId());
    }
    if(fileDTO.getChildFileId() == null || !resourceFileRepository.existsById(fileDTO.getChildFileId())) {
      throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, fileDTO.getChildFileId());
    }
    ResourceFileLink link = mapper.toEntity(fileDTO);
    return mapper.toDto(repository.save(link));
  }

  @Override
  public List<ResourceFileLinkDTO> findAll() {
    log.debug("Request to get all ResourceFileLinks");
    return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public Optional<ResourceFileLinkDTO> findOne(Long id) {
    log.debug("Request to get ResourceFileLink {}", id);
    return repository.findById(id).map(mapper::toDto);
  }

  @Override
  public void delete(Long id) {
    log.debug("Request to delete ResourceFileLink {}", id);
    Optional<ResourceFileLink> link = repository.findById(id);
    if(link.isEmpty()) {
      return;
    }
    repository.delete(link.get());
  }

  @Override
  public List<ResourceFileLinkDTO> findAllOfParentFile(Long parentFileId) {
    log.debug("Request to get all ResourceFileLinks of parent file {}", parentFileId);
    return repository.findAllByParentFileId(parentFileId).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public List<ResourceFileLinkDTO> findAllOfChildFile(Long childId) {
    log.debug("Request to get all ResourceFileLinks of child file {}", childId);
    return repository.findAllByChildFileId(childId).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public ResourceFileLinkDTO updateName(Long id, String name) {
    log.debug("Request to get update name of ResourceFileLink {} to {}", id, name);
    Optional<ResourceFileLink> tmp = repository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, id);
    }
    ResourceFileLink link = tmp.get();
    link.setLinkName(name);
    return mapper.toDto(repository.save(link));
  }

  @Override
  public void delete(IDsDTO ids) {
    log.debug("Request to delete ResourceFileLinks {}", ids);
    Collection<ResourceFileLink> links = repository.findAllById(ids.getIds());
    if(links.isEmpty()) {
      return;
    }
    repository.deleteAll(links);
  }

  @Override
  public List<ResourceFileLinkDTO> create(List<ResourceFileLinkDTO> links) {
    log.debug("Request to create ResourceFileLinks {}", links);
    links.forEach(it -> {
      if(it.getParentFileId() == null || !resourceFileRepository.existsById(it.getParentFileId())) {
        throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, it.getParentFileId());
      }
      if(it.getChildFileId() == null || !resourceFileRepository.existsById(it.getChildFileId())) {
        throw new EntityNotFoundException(ResourceFileServiceImpl.ENTITY_NAME, it.getChildFileId());
      }
    });
    List<ResourceFileLink> entities = links.stream().map(it -> {
      ResourceFileLink link = mapper.toEntity(it);
      link.setId(null);
      return link;
    }).collect(Collectors.toList());
    return repository.saveAll(entities).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public List<ResourceFileLinkDTO> findAllOfFile(Long fileId) {
    return repository.findAllByParentFileIdOrChildFileId(fileId, fileId).stream().map(mapper::toDto).collect(Collectors.toList());
  }
}
