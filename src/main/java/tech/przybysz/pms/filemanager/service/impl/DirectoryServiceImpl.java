package tech.przybysz.pms.filemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.przybysz.pms.filemanager.domain.Directory;
import tech.przybysz.pms.filemanager.repository.DirectoryRepository;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.mapper.DirectoryMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectoryServiceImpl implements DirectoryService {

  private final Logger log = LoggerFactory.getLogger(DirectoryServiceImpl.class);

  private final DirectoryRepository directoryRepository;
  private final DirectoryMapper mapper;

  public DirectoryServiceImpl(DirectoryRepository directoryRepository, DirectoryMapper mapper) {
    this.directoryRepository = directoryRepository;
    this.mapper = mapper;
  }

  @Override
  public DirectoryDTO update(DirectoryDTO directoryDTO) {


    return null;
  }

  @Override
  public DirectoryDTO create(DirectoryDTO directoryDTO) {


    return null;
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
    deleteInternals(directory.get());
    directoryRepository.deleteById(id);
  }

  private void deleteInternals(Directory directory) {
    /*application.setMainDataType(null);
    application.setConfigurationDataType(null);
    application.setStartPage(null);
    application.setLayoutPage(null);
    applicationRepository.save(application);
    applicationRepository.findAllByMasterSnapshotId(application.getId()).forEach(app -> delete(app.getId()));
    applicationPageService.findAllOfApplication(application.getId()).forEach(page -> applicationPageService.deleteWithLock(page.getId()));
    complexDataTypeService.findAllOfApplication(application.getId()).forEach(type -> complexDataTypeService.delete(type.getId()));
    generationEntryService.findAllOfApplication(application.getId()).forEach(entry -> generationEntryService.delete(entry.getId()));
    generationEntryService.findAllOfSnapshot(application.getId()).forEach(entry -> generationEntryService.delete(entry.getId()));
    activeSnapshotService.findAllBySnapshotId(application.getId()).forEach(entry-> activeSnapshotService.delete(entry.getId()));
    uiWidgetService.findAllByApplicationId(application.getId()).forEach(entry -> uiWidgetService.delete(entry.getId()));

    if (application.getJsonAppConfiguration() != null) {
      configurationService.deleteWithLock(application.getJsonAppConfiguration().getId());
    }
    if (application.getCss() != null) {
      cssStorageService.deleteWithLock(application.getCss().getId());
    }
    if (application.getConfigDataDirectory() != null) {
      directoryService.delete(application.getConfigDataDirectory().getId());
    }
    if (application.getUserDataDirectory() != null) {
      directoryService.delete(application.getUserDataDirectory().getId());
    }
    applicationRepository.deleteById(application.getId());*/
  }
}

