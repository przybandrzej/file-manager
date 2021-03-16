package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.ResourceFileLinkService;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileLinkDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resource-file-links")
public class ResourceFileLinkResource {

  private final Logger log = LoggerFactory.getLogger(ResourceFileLinkResource.class);
  private static final String ENTITY_NAME = "ResourceFileLink";
  @Value("${spring.application.name}")
  private String applicationName;

  private final ResourceFileLinkService linkService;

  public ResourceFileLinkResource(ResourceFileLinkService linkService) {
    this.linkService = linkService;
  }

  @GetMapping
  public List<ResourceFileLinkDTO> getAllLinks() {
    log.debug("REST request to get all ResourceFileLinks");
    return linkService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceFileLinkDTO> getLink(@PathVariable Long id) {
    log.debug("REST request to get ResourceFileLink {}", id);
    return ResponseUtil.wrapOrNotFound(linkService.findOne(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFileLink(@PathVariable Long id) {
    log.debug("REST request to delete ResourceFileLink {}", id);
    linkService.delete(id);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
        .build();
  }

  @PostMapping
  public ResponseEntity<ResourceFileLinkDTO> createLink(@RequestBody ResourceFileLinkDTO fileDTO) {
    log.debug("REST request to create ResourceFileLink {}", fileDTO);
    ResourceFileLinkDTO save = linkService.create(fileDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PutMapping
  public ResponseEntity<ResourceFileLinkDTO> updateLink(@RequestBody ResourceFileLinkDTO fileDTO) {
    log.debug("REST request to update ResourceFileLink {}", fileDTO);
    ResourceFileLinkDTO save = linkService.update(fileDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PatchMapping("{id}/change-name/{name}")
  public ResponseEntity<ResourceFileLinkDTO> updateLinkName(@PathVariable Long id, @PathVariable String name) {
    log.debug("REST request to change ResourceFileLink {} name to {}", id, name);
    ResourceFileLinkDTO save = linkService.updateName(id, name);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @DeleteMapping("/_bulk")
  public ResponseEntity<Void> deleteLinks(@RequestBody IDsDTO ids) {
    log.debug("REST request to delete ResourceFileLinks {}", ids);
    linkService.delete(ids);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createBulkEntityDeletionAlert(applicationName, true, ENTITY_NAME,
            ids.getIds().stream().map(Object::toString).collect(Collectors.toList())))
        .build();
  }

  @PostMapping("/_bulk")
  public ResponseEntity<List<ResourceFileLinkDTO>> createLinks(@RequestBody List<ResourceFileLinkDTO> links) {
    log.debug("REST request to create ResourceFileLinks {}", links);
    List<ResourceFileLinkDTO> saved = linkService.create(links);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            saved.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(saved);
  }
}
