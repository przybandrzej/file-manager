package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.przybysz.pms.filemanager.service.ResourceFileService;
import tech.przybysz.pms.filemanager.service.UploadService;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;
import tech.przybysz.pms.filemanager.web.rest.util.ResponseUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resource-files")
public class ResourceFileResource {

  private final Logger log = LoggerFactory.getLogger(ResourceFileResource.class);
  private static final String ENTITY_NAME = "resourceFile";
  @Value("${spring.application.name}")
  private String applicationName;

  private final ResourceFileService fileService;
  private final UploadService uploadService;

  public ResourceFileResource(ResourceFileService fileService, UploadService uploadService) {
    this.fileService = fileService;
    this.uploadService = uploadService;
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

  @PostMapping(value = "/upload/{directoryId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<List<ResourceFileDTO>> upload(@PathVariable Long directoryId, @RequestPart("file") List<MultipartFile> files) {
    log.debug("REST request to upload {} ResourceFiles to Directory {}", files.size(), directoryId);
    List<ResourceFileDTO> save = uploadService.save(directoryId, files);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            save.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(save);
  }

}
