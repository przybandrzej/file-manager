package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;

@RestController
@RequestMapping("/api/directories")
public class ResourceFileResource {

  private final Logger log = LoggerFactory.getLogger(ResourceFileResource.class);
  private static final String ENTITY_NAME = "resourceFile";
  @Value("${spring.application.name}")
  private String applicationName;

  private final ResourceFileService fileService;

  public ResourceFileResource(ResourceFileService fileService) {
    this.fileService = fileService;
  }

  @GetMapping
  public List<ResourceFileDTO> getAllFiles() {
    log.debug("REST request to get all ResourceFiles");
    return fileService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceFileDTO> getFile(@PathVariable Long id) {
    log.debug("REST request to get ResourceFile {}", id);
    return ResponseUtil.wrapOrNotFound(fileService.findOne(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
    log.debug("REST request to delete ResourceFile {}", id);
    fileService.delete(id);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
        .build();
  }

  @PostMapping
  public ResponseEntity<ResourceFileDTO> createFile(@RequestBody ResourceFileDTO fileDTO) {
    log.debug("REST request to create ResourceFile {}", fileDTO);
    ResourceFileDTO save = fileService.create(fileDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PutMapping
  public ResponseEntity<ResourceFileDTO> updateFile(@RequestBody ResourceFileDTO fileDTO) {
    log.debug("REST request to update ResourceFile {}", fileDTO);
    ResourceFileDTO save = fileService.update(fileDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }
}
