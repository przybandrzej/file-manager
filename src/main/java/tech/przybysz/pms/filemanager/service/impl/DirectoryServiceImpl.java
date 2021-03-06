package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.repository.DirectoryRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileLinkRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.DeleteService;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.DirectoryPathDTO;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.mapper.DirectoryMapper;
import tech.przybysz.pms.filemanager.service.util.PathUtils;

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
  private final ResourceFileLinkRepository linkRepository;

  public DirectoryServiceImpl(DirectoryRepository directoryRepository, DirectoryMapper mapper,
                              ResourceFileRepository fileRepository, DeleteService deleteService,
                              ResourceFileLinkRepository linkRepository) {
    this.directoryRepository = directoryRepository;
    this.mapper = mapper;
    this.fileRepository = fileRepository;
    this.deleteService = deleteService;
    this.linkRepository = linkRepository;
  }

  @Override
  public DirectoryDTO update(DirectoryDTO directoryDTO) {
    Optional<Directory> tmp = directoryRepository.findById(directoryDTO.getId());
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getId());
    }
    Directory directory = tmp.get();
    Directory newParent = null;
    if(directoryDTO.getParentId() != null) {
      Optional<Directory> newParentTmp = directoryRepository.findById(directoryDTO.getParentId());
      if(newParentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
      newParent = newParentTmp.get();
    }
    checkParentChildren(directoryDTO.getId(), directoryDTO.getName(), newParent);
    directory.setParent(newParent);
    directory.setModified(LocalDateTime.now());
    directory.setName(directoryDTO.getName());
    return mapper.toDto(directoryRepository.save(directory));
  }

  private void checkParentChildren(Long dirId, String dirName, Directory newParent) {
    Long newParentId = null;
    if(newParent != null) {
      newParentId = newParent.getId();
    }
    Collection<Directory> parentChildren = directoryRepository.findByNameAndParentId(dirName, newParentId);
    if(!parentChildren.isEmpty()) {
      if(parentChildren.stream().anyMatch(it -> it.getId().equals(dirId))) {
        return;
      }
      throw new DirectoryNameTakenException(newParentId, dirName);
    }
  }

  @Override
  public DirectoryDTO create(DirectoryDTO directoryDTO) {
    Directory newParent = null;
    if(directoryDTO.getParentId() != null) {
      Optional<Directory> newParentTmp = directoryRepository.findById(directoryDTO.getParentId());
      if(newParentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
      newParent = newParentTmp.get();
    }
    checkParentChildren(null, directoryDTO.getName(), newParent);
    Directory directory = new Directory();
    directory.setParent(newParent);
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
    Optional<Directory> tmp = directoryRepository.findById(id);
    if(tmp.isEmpty()) {
      return;
    }
    Directory directory = tmp.get();
    Set<ResourceFile> files = this.getFiles(directory);
    deleteService.deleteFiles(files);
    linkRepository.deleteAll(
        files.stream().map(it -> linkRepository.findAllByParentFileIdOrChildFileId(it.getId(), it.getId()))
            .flatMap(Collection::stream).collect(Collectors.toList()));
    files.forEach(fileRepository::delete);
    Set<Directory> children = getChildren(directory);
    directoryRepository.deleteAll(children);
    directoryRepository.delete(directory);
  }

  @Override
  public DirectoryDTO updateName(Long id, String name) {
    Optional<Directory> tmp = directoryRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, id);
    }
    Directory directory = tmp.get();
    checkParentChildren(directory.getId(), name, directory.getParent());
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
    checkParentChildren(directory.getId(), directory.getName(), parent);
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
    linkRepository.deleteAll(
        files.stream().map(it -> linkRepository.findAllByParentFileIdOrChildFileId(it.getId(), it.getId()))
            .flatMap(Collection::stream).collect(Collectors.toList()));
    files.forEach(fileRepository::delete);
    List<Directory> children = directories.stream().map(this::getChildren).flatMap(Set::stream).collect(Collectors.toList());
    directoryRepository.deleteAll(children);
    directoryRepository.deleteAll(directories);
  }

  private Set<ResourceFile> getFiles(Directory dir) {
    Set<ResourceFile> files = dir.getFiles();
    dir.getChildren().forEach(child -> files.addAll(this.getFiles(child)));
    return files;
  }

  private Set<Directory> getChildren(Directory dir) {
    Set<Directory> directories = dir.getChildren();
    dir.getChildren().forEach(child -> directories.addAll(this.getChildren(child)));
    return directories;
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
      checkParentChildren(dir.getId(), dir.getName(), finalParent);
      dir.setModified(LocalDateTime.now());
      dir.setParent(finalParent);
    });
    return directoryRepository.saveAll(tmp).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public List<DirectoryDTO> create(List<DirectoryDTO> dtos) {
    dtos.forEach(directoryDTO -> {
      if(directoryDTO.getParentId() != null && !directoryRepository.existsById(directoryDTO.getParentId())) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
    });
    List<Directory> collect = dtos.stream().map(directoryDTO -> {
      Optional<Directory> parentTmp = directoryRepository.findById(directoryDTO.getParentId());
      if(parentTmp.isEmpty()) {
        throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, directoryDTO.getParentId());
      }
      Directory parent = parentTmp.get();
      checkParentChildren(null, directoryDTO.getName(), parent);
      Directory directory = new Directory();
      directory.setParent(parent);
      directory.setCreated(LocalDateTime.now());
      directory.setModified(LocalDateTime.now());
      directory.setName(directoryDTO.getName());
      return directory;
    }).collect(Collectors.toList());
    return collect.stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public DirectoryDTO findOrCreatePath(DirectoryPathDTO path) {
    path.setPath(PathUtils.normalizePath(path.getPath()));
    List<String> dirs = Arrays.asList(path.getPath().split("/"));
    Directory lastDir;
    Optional<Directory> root = directoryRepository.findByNameAndParentId(dirs.get(0), null).stream().findFirst();
    if(root.isEmpty()) {
      lastDir = createPath(dirs, null);
    } else {
      Directory parent = root.get();
      lastDir = parent;
      for(int i = 1; i < dirs.size(); i++) {
        Optional<Directory> current = directoryRepository.findByNameAndParentId(dirs.get(i), parent.getId()).stream().findFirst();
        if(current.isEmpty()) {
          lastDir = createPath(dirs.subList(i, dirs.size()), parent.getId());
          break;
        } else {
          lastDir = current.get();
          parent = lastDir;
        }
      }
    }
    return mapper.toDto(lastDir);
  }

  private Directory createPath(List<String> path, Long parentId) {
    Directory last = mapper.fromId(parentId);
    for(String s : path) {
      Directory dir = new Directory();
      dir.setParent(last);
      dir.setName(s);
      dir.setCreated(LocalDateTime.now());
      dir.setModified(LocalDateTime.now());
      last = directoryRepository.save(dir);
    }
    return last;
  }
}

