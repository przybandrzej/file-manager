package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.repository.DirectoryRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.DeleteService;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.mapper.DirectoryMapper;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    Optional<Directory> parentTmp = directoryRepository.findById(directoryDTO.getParentId());
    if(parentTmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
    }
    Directory directory = tmp.get();
    directory.setParent(parentTmp.get());
    directory.setModified(LocalDateTime.now());
    directory.setName(directoryDTO.getName());
    return mapper.toDto(directoryRepository.save(directory));
  }

  @Override
  public DirectoryDTO create(DirectoryDTO directoryDTO) {
    Optional<Directory> parentTmp = directoryRepository.findById(directoryDTO.getParentId());
    if(parentTmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
    }
    Directory directory = new Directory();
    directory.setParent(parentTmp.get());
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
    delete(directory.get());
  }

  private void delete(Directory directory) {
    deleteInternals(directory);
    directoryRepository.delete(directory);
  }

  private void deleteInternals(Directory directory) {
    directory.getChildren().forEach(this::delete);
    directory.getFiles().forEach(fileRepository::delete);
    deleteService.deleteFiles(directory.getFiles());
  }
}

