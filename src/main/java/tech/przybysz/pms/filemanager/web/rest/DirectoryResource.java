package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.DownloadService;
import tech.przybysz.pms.filemanager.service.FileResource;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.DirectoryPathDTO;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/directories")
public class DirectoryResource {

  private final Logger log = LoggerFactory.getLogger(DirectoryResource.class);
  private static final String ENTITY_NAME = "directory";
  @Value("${spring.application.name}")
  private String applicationName;

  private final DirectoryService directoryService;
  private final ResourceFileService fileService;
  private final DownloadService downloadService;

  public DirectoryResource(DirectoryService directoryService, ResourceFileService fileService,
                           DownloadService downloadService) {
    this.directoryService = directoryService;
    this.fileService = fileService;
    this.downloadService = downloadService;
  }

  @GetMapping
  public List<DirectoryDTO> getAllDirectories() {
    log.debug("REST request to get all Directories");
    return directoryService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<DirectoryDTO> getDirectory(@PathVariable Long id) {
    log.debug("REST request to get Directory {}", id);
    return ResponseUtil.wrapOrNotFound(directoryService.findOne(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDirectory(@PathVariable Long id) {
    log.debug("REST request to delete Directory {}", id);
    directoryService.delete(id);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
        .build();
  }

  @PostMapping
  public ResponseEntity<DirectoryDTO> createDirectory(@RequestBody DirectoryDTO directoryDTO) {
    log.debug("REST request to create Directory {}", directoryDTO);
    DirectoryDTO save = directoryService.create(directoryDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PutMapping
  public ResponseEntity<DirectoryDTO> updateDirectory(@RequestBody DirectoryDTO directoryDTO) {
    log.debug("REST request to update Directory {}", directoryDTO);
    DirectoryDTO save = directoryService.update(directoryDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @GetMapping("/{id}/children")
  public List<DirectoryDTO> getAllChildDirectories(@PathVariable Long id) {
    log.debug("REST request to get all child Directories of Directory {}", id);
    return directoryService.findAllChildren(id);
  }

  @GetMapping("/{id}/files")
  public List<ResourceFileDTO> getAllFilesOfDirectory(@PathVariable Long id) {
    log.debug("REST request to get all files of Directory {}", id);
    return fileService.findAllOfDirectory(id);
  }

  @PatchMapping("{id}/change-name/{name}")
  public ResponseEntity<DirectoryDTO> updateDirectoryName(@PathVariable Long id, @PathVariable String name) {
    log.debug("REST request to update Directory {}name to {}", id, name);
    DirectoryDTO save = directoryService.updateName(id, name);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PatchMapping("{id}/change-directory/{directoryId}")
  public ResponseEntity<DirectoryDTO> updateParentDirectory(@PathVariable Long id, @PathVariable Long directoryId) {
    log.debug("REST request to change DirectoryDTO {} directory to {}", id, directoryId);
    DirectoryDTO save = directoryService.updateParentDirectory(id, directoryId);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @DeleteMapping("/_bulk")
  public ResponseEntity<Void> deleteDirectories(@RequestBody IDsDTO ids) {
    log.debug("REST request to delete Directories {}", ids);
    directoryService.delete(ids);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            ids.getIds().stream().map(Object::toString).collect(Collectors.toList())))
        .build();
  }

  @PatchMapping("/change-directory/{directoryId}")
  public ResponseEntity<List<DirectoryDTO>> updateParentDirectoryBulk(@RequestBody IDsDTO ids, @PathVariable Long directoryId) {
    log.debug("REST request to change Directories {} directory to {}", ids, directoryId);
    List<DirectoryDTO> save = directoryService.updateParentDirectory(ids, directoryId);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityUpdateAlert(applicationName, true, ENTITY_NAME,
            save.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(save);
  }

  @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> download(@PathVariable Long id) {
    log.debug("REST request to download Directory {}", id);
    FileResource resource = downloadService.getDirectory(id);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createDownloadFileAlert(applicationName, true,
            resource.getFileName()))
        .contentLength(resource.getResource().contentLength())
        .body(resource.getResource());
  }

  @PostMapping("/_bulk")
  public ResponseEntity<List<DirectoryDTO>> createDirectories(@RequestBody List<DirectoryDTO> dirs) {
    log.debug("REST request to create Directories {}", dirs);
    List<DirectoryDTO> save = directoryService.create(dirs);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            save.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(save);
  }

  @PostMapping("/by-path")
  public ResponseEntity<DirectoryDTO> findDirectoryByPathOrCreate(@RequestBody DirectoryPathDTO path) {
    log.debug("REST request to find Directory by path {}", path);
    DirectoryDTO dir = directoryService.findOrCreatePath(path);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
            dir.getId().toString()))
        .body(dir);
  }
}
