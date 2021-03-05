package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.DirectoryService;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.DirectoryDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;

@RestController
@RequestMapping("/api/directories")
public class DirectoryResource {

  private final Logger log = LoggerFactory.getLogger(DirectoryResource.class);
  private static final String ENTITY_NAME = "directory";
  @Value("${spring.application.name}")
  private String applicationName;

  private final DirectoryService directoryService;
  private final ResourceFileService fileService;

  public DirectoryResource(DirectoryService directoryService, ResourceFileService fileService) {
    this.directoryService = directoryService;
    this.fileService = fileService;
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
}
