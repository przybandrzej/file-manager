package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.ResourceFile;
import tech.przybysz.pms.filemanager.repository.ResourceFileRepository;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceFileServiceImpl implements ResourceFileService {

  private final Logger log = LoggerFactory.getLogger(ResourceFileServiceImpl.class);

  private final ResourceFileRepository fileRepository;

  public ResourceFileServiceImpl(ResourceFileRepository repository) {
    this.fileRepository = repository;
  }

  @Override
  public ResourceFileDTO save(ResourceFileDTO fileDTO) {


    return null;
  }

  @Override
  public List<ResourceFileDTO> findAll() {
    log.debug("Request to get all ResourceFile");
    return fileRepository.findAll().stream()
        .map(applicationMapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  public Optional<ResourceFileDTO> findOne(Long id) {
    log.debug("Request to get ResourceFile : {}", id);
    return fileRepository.findById(id)
        .map(applicationMapper::toDto);
  }

  @Override
  public void delete(Long id) {
    log.debug("Request to delete ResourceFile : {}", id);
    Optional<ResourceFile> directory = fileRepository.findById(id);
    if(directory.isEmpty()) {
      return;
    }
    deleteInternals(directory.get());
    fileRepository.deleteById(id);
  }

  private void deleteInternals(ResourceFile file) {

  }

  @Override
  public List<ResourceFileDTO> findAllOfDirectory(Long directoryId) {
    log.debug("Request to get all child ResourceFiles of ResourceFile {}", directoryId);
    return fileRepository.findAllByParentDirectoryId(directoryId).stream()
        .map(applicationMapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
  }
}
