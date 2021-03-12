package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.repository.DirectoryRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.DeleteService;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.mapper.DirectoryMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectoryServiceImpl implements DirectoryService {

  private final Logger log = LoggerFactory.getLogger(DirectoryServiceImpl.class);

  public static final String ENTITY_NAME = "directory";

  private final DirectoryRepository directoryRepository;
  private final DirectoryMapper mapper;
  private final ResourceFileRepository fileRepository;
  private final DeleteService deleteService;

  public DirectoryServiceImpl(DirectoryRepository directoryRepository, DirectoryMapper mapper,
                              ResourceFileRepository fileRepository, DeleteService deleteService) {
    this.directoryRepository = directoryRepository;
    this.mapper = mapper;
    this.fileRepository = fileRepository;
    this.deleteService = deleteService;
  }

  @Override
  public DirectoryDTO update(DirectoryDTO directoryDTO) {
    Optional<Directory> tmp = directoryRepository.findById(directoryDTO.getId());
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getId());
    }
    Directory parent = null;
    if(directoryDTO.getParentId() != null) {
      Optional<Directory> parentTmp = directoryRepository.findById(directoryDTO.getParentId());
      if(parentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
      parent = parentTmp.get();
    }
    Directory directory = tmp.get();
    directory.setParent(parent);
    directory.setModified(LocalDateTime.now());
    directory.setName(directoryDTO.getName());
    return mapper.toDto(directoryRepository.save(directory));
  }

  @Override
  public DirectoryDTO create(DirectoryDTO directoryDTO) {
    Directory parent = null;
    if(directoryDTO.getParentId() != null) {
      Optional<Directory> parentTmp = directoryRepository.findById(directoryDTO.getParentId());
      if(parentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
      parent = parentTmp.get();
    }
    Directory directory = new Directory();
    directory.setParent(parent);
    directory.setCreated(LocalDateTime.now());
    directory.setModified(LocalDateTime.now());
    directory.setName(directoryDTO.getName());
    return mapper.toDto(directoryRepository.save(directory));
  }

  @Override
  public List<DirectoryDTO> findAllChildren(Long parentId) {
    log.debug("Request to get all child Directories of Directory {}", parentId);
    return directoryRepository.findAll().stream()
        .map(mapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  @Transactional(readOnly = true)
  public List<DirectoryDTO> findAll() {
    log.debug("Request to get all Directories");
    return directoryRepository.findAll().stream()
        .map(mapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<DirectoryDTO> findOne(Long id) {
    log.debug("Request to get Directory : {}", id);
    return directoryRepository.findById(id)
        .map(mapper::toDto);
  }

  @Override
  public void delete(Long id) {
    log.debug("Request to delete Directory : {}", id);
    Optional<Directory> directory = directoryRepository.findById(id);
    if(directory.isEmpty()) {
      return;
    }
    Set<ResourceFile> files = this.getFiles(directory.get());
    deleteService.deleteFiles(files);
    files.forEach(fileRepository::delete);
    directoryRepository.delete(directory.get());
  }

  @Override
  public DirectoryDTO updateName(Long id, String name) {
    Optional<Directory> tmp = directoryRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, id);
    }
    Directory directory = tmp.get();
    directory.setModified(LocalDateTime.now());
    directory.setName(name);
    return mapper.toDto(directoryRepository.save(directory));
  }

  @Override
  public DirectoryDTO updateParentDirectory(Long id, Long parentDirectoryId) {
    Optional<Directory> tmp = directoryRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, id);
    }
    Directory parent = null;
    if(parentDirectoryId != null) {
      Optional<Directory> parentTmp = directoryRepository.findById(parentDirectoryId);
      if(parentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, parentDirectoryId);
      }
      parent = parentTmp.get();
    }
    Directory directory = tmp.get();
    directory.setParent(parent);
    directory.setModified(LocalDateTime.now());
    return mapper.toDto(directoryRepository.save(directory));
  }

  @Override
  public void delete(IDsDTO ids) {
    log.debug("Request to delete Directories : {}", ids);
    Collection<Directory> directories = directoryRepository.findAllById(ids.getIds());
    if(directories.isEmpty()) {
      return;
    }
    List<ResourceFile> files = directories.stream().map(this::getFiles).flatMap(Set::stream).collect(Collectors.toList());
    deleteService.deleteFiles(files);
    files.forEach(fileRepository::delete);
    directoryRepository.deleteAll(directories);
  }

  private Set<ResourceFile> getFiles(Directory dir) {
    Set<ResourceFile> files = dir.getFiles();
    dir.getChildren().forEach(child -> files.addAll(this.getFiles(child)));
    return files;
  }

  @Override
  public List<DirectoryDTO> updateParentDirectory(IDsDTO ids, Long parentDirectoryId) {
    Collection<Directory> tmp = directoryRepository.findAllById(ids.getIds());
    List<Long> next = ids.getIds();
    next.removeAll(tmp.stream().map(Directory::getId).collect(Collectors.toList()));
    if(!next.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, next);
    }
    Directory parent = null;
    if(parentDirectoryId != null) {
      Optional<Directory> parentTmp = directoryRepository.findById(parentDirectoryId);
      if(parentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, parentDirectoryId);
      }
      parent = parentTmp.get();
    }
    Directory finalParent = parent;
    tmp.forEach(dir -> {
      dir.setModified(LocalDateTime.now());
      dir.setParent(finalParent);
    });
    return directoryRepository.saveAll(tmp).stream().map(mapper::toDto).collect(Collectors.toList());
  }
}

