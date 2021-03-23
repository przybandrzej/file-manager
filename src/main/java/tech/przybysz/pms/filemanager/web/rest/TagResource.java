package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.TagService;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.dto.TagDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagResource {

  private final Logger log = LoggerFactory.getLogger(TagResource.class);
  private static final String ENTITY_NAME = "tag";
  @Value("${spring.application.name}")
  private String applicationName;

  private final TagService tagService;
  private final ResourceFileService fileService;

  public TagResource(TagService tagService, ResourceFileService fileService) {
    this.tagService = tagService;
    this.fileService = fileService;
  }

  @GetMapping
  public List<TagDTO> getAllTags() {
    log.debug("REST request to get all Tags");
    return tagService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagDTO> getTag(@PathVariable Long id) {
    log.debug("REST request to get Tag {}", id);
    return ResponseUtil.wrapOrNotFound(tagService.findOne(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
    log.debug("REST request to delete Tag {}", id);
    tagService.delete(id);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
        .build();
  }

  @PostMapping
  public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDTO) {
    log.debug("REST request to create Tag {}", tagDTO);
    TagDTO save = tagService.create(tagDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PutMapping
  public ResponseEntity<TagDTO> updateTag(@RequestBody TagDTO tagDTO) {
    log.debug("REST request to update Tag {}", tagDTO);
    TagDTO save = tagService.update(tagDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @GetMapping("/{id}/files")
  public List<ResourceFileDTO> getAllFilesOfTag(@PathVariable Long id) {
    log.debug("REST request to get all files of Tag {}", id);
    return fileService.findAllOfTag(id);
  }

  @DeleteMapping("/_bulk")
  public ResponseEntity<Void> deleteTags(@RequestBody IDsDTO ids) {
    log.debug("REST request to delete Tags {}", ids);
    tagService.delete(ids);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            ids.getIds().stream().map(Object::toString).collect(Collectors.toList())))
        .build();
  }

  @GetMapping("/q")
  public List<TagDTO> findTagsByName(@RequestParam String name, @RequestParam boolean exact) {
    log.debug("REST request to find Tags by name {} exact {}", name, exact);
    return tagService.findAllByName(name, exact);
  }
}
