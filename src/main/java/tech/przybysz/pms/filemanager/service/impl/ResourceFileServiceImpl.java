package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.domain.ResourceFileLink;
import tech.przybysz.pms.filemanager.repository.DirectoryRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileLinkRepository;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.DeleteService;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.io.BackupService;
import tech.przybysz.pms.filemanager.service.io.StorageService;
import tech.przybysz.pms.filemanager.service.mapper.ResourceFileMapper;
import tech.przybysz.pms.filemanager.service.util.PathUtils;
import tech.przybysz.pms.filemanager.service.util.RandomUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceFileServiceImpl implements ResourceFileService {

  private final Logger log = LoggerFactory.getLogger(ResourceFileServiceImpl.class);

  public static final String ENTITY_NAME = "resourceFile";

  private final ResourceFileRepository fileRepository;
  private final ResourceFileMapper mapper;
  private final DeleteService deleteService;
  private final DirectoryRepository directoryRepository;
  private final ResourceFileLinkRepository linkRepository;
  private final BackupService backupService;
  private final StorageService storageService;

  public ResourceFileServiceImpl(ResourceFileRepository repository, ResourceFileMapper mapper,
                                 DeleteService deleteService, DirectoryRepository directoryRepository,
                                 ResourceFileLinkRepository linkRepository, BackupService backupService,
                                 StorageService storageService) {
    this.fileRepository = repository;
    this.mapper = mapper;
    this.deleteService = deleteService;
    this.directoryRepository = directoryRepository;
    this.linkRepository = linkRepository;
    this.backupService = backupService;
    this.storageService = storageService;
  }

  @Override
  public ResourceFileDTO update(ResourceFileDTO fileDTO) {
    Optional<ResourceFile> tmp = fileRepository.findById(fileDTO.getId());
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, fileDTO.getId());
    }
    Optional<Directory> directory = directoryRepository.findById(fileDTO.getParentDirectoryId());
    if(directory.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, fileDTO.getParentDirectoryId());
    }
    ResourceFile file = tmp.get();
    file.setModified(LocalDateTime.now());
    file.setParentDirectory(directory.get());
    file.setExtension(fileDTO.getExtension());
    file.setOriginalName(fileDTO.getOriginalName());
    file.setBackedUp(fileDTO.getBackedUp());
    return mapper.toDto(fileRepository.save(file));
  }

  @Override
  public ResourceFileDTO create(ResourceFileDTO fileDTO) {
    if(directoryRepository.findById(fileDTO.getParentDirectoryId()).isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, fileDTO.getParentDirectoryId());
    }
    ResourceFile file = mapper.toEntity(fileDTO);
    file.setBackedUp(false);
    file.setCreated(LocalDateTime.now());
    file.setModified(LocalDateTime.now());
    file.setGeneratedName(RandomUtil.generateFileNameSlug());
    return mapper.toDto(fileRepository.save(file));
  }

  @Override
  public List<ResourceFileDTO> findAll() {
    log.debug("Request to get all ResourceFile");
    return fileRepository.findAll().stream()
        .map(mapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  public Optional<ResourceFileDTO> findOne(Long id) {
    log.debug("Request to get ResourceFile : {}", id);
    return fileRepository.findById(id)
        .map(mapper::toDto);
  }

  @Override
  public void delete(Long id) {
    log.debug("Request to delete ResourceFile : {}", id);
    Optional<ResourceFile> resourceFile = fileRepository.findById(id);
    if(resourceFile.isEmpty()) {
      return;
    }
    deleteService.deleteFile(resourceFile.get());
    linkRepository.deleteAll(linkRepository.findAllByParentFileIdOrChildFileId(id, id));
    fileRepository.deleteById(id);
  }

  @Override
  public List<ResourceFileDTO> findAllOfDirectory(Long directoryId) {
    log.debug("Request to get all child ResourceFiles of ResourceFile {}", directoryId);
    return fileRepository.findAllByParentDirectoryId(directoryId).stream()
        .map(mapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  public ResourceFileDTO updateName(Long id, String name) {
    Optional<ResourceFile> tmp = fileRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, id);
    }
    ResourceFile file = tmp.get();
    file.setModified(LocalDateTime.now());
    file.setOriginalName(name);
    return mapper.toDto(fileRepository.save(file));
  }

  @Override
  public ResourceFileDTO updateParentDirectory(Long id, Long parentDirectoryId) {
    Optional<ResourceFile> tmp = fileRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, id);
    }
    Optional<Directory> directory = directoryRepository.findById(parentDirectoryId);
    if(directory.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, parentDirectoryId);
    }
    ResourceFile file = tmp.get();
    file.setModified(LocalDateTime.now());
    file.setParentDirectory(directory.get());
    return mapper.toDto(fileRepository.save(file));
  }

  @Override
  public void delete(IDsDTO ids) {
    log.debug("Request to delete ResourceFiles : {}", ids);
    Collection<ResourceFile> resourceFile = fileRepository.findAllById(ids.getIds());
    if(resourceFile.isEmpty()) {
      return;
    }
    deleteService.deleteFiles(resourceFile);
    List<ResourceFileLink> collect = ids.getIds().stream()
        .map(id -> linkRepository.findAllByParentFileIdOrChildFileId(id, id))
        .flatMap(Collection::stream).collect(Collectors.toList());
    linkRepository.deleteAll(collect);
    fileRepository.deleteAll(resourceFile);
  }

  @Override
  public List<ResourceFileDTO> updateParentDirectory(IDsDTO ids, Long parentDirectoryId) {
    Collection<ResourceFile> tmp = fileRepository.findAllById(ids.getIds());
    List<Long> next = ids.getIds();
    next.removeAll(tmp.stream().map(ResourceFile::getId).collect(Collectors.toList()));
    if(!next.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, next);
    }
    Optional<Directory> directory = directoryRepository.findById(parentDirectoryId);
    if(directory.isEmpty()) {
      throw new EntityNotFoundException(DirectoryServiceImpl.ENTITY_NAME, parentDirectoryId);
    }
    tmp.forEach(file -> {
      file.setModified(LocalDateTime.now());
      file.setParentDirectory(directory.get());
    });
    return fileRepository.saveAll(tmp).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public List<ResourceFileDTO> findAllOfTag(Long tagId) {
    log.debug("Request to get ResourceFiles of Tag : {}", tagId);
    return fileRepository.findAllByTagsId(tagId).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public ResourceFileDTO updateBackup(Long id, boolean backUp) {
    log.debug("Request to update backup property of ResourceFile : {}", id);
    Optional<ResourceFile> tmp = fileRepository.findById(id);
    if(tmp.isEmpty()) {
      throw new EntityNotFoundException(ENTITY_NAME, id);
    }
    ResourceFile resourceFile = tmp.get();
    ResourceFileDTO resourceFileDTO = mapper.toDto(resourceFile);
    if(backUp == resourceFile.getBackUp()) {
        return resourceFileDTO;
    }
    if(resourceFile.getBackUp() && resourceFile.getBackedUp()) {
      // remove backup
      boolean deleted = backupService.deleteBackup(resourceFileDTO);
      resourceFile.setBackedUp(!deleted);
      resourceFile.setBackUp(backUp);
    } else if(!resourceFile.getBackUp() && !resourceFile.getBackedUp()) {
      // add backup
      String fileName = PathUtils.getFileFullGeneratedName(resourceFileDTO);
      File file = storageService.read(fileName);
      InputStream inputStream;
      try {
        inputStream = new FileInputStream(file);
      } catch(FileNotFoundException e) {
        throw new  tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException("File not found", fileName);
      }
      boolean backedUp = backupService.backup(resourceFileDTO, inputStream);
      resourceFile.setBackedUp(backedUp);
      resourceFile.setBackUp(backUp);
    }
    return mapper.toDto(fileRepository.save(resourceFile));
  }
}
