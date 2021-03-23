package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.przybysz.pms.filemanager.service.*;
import tech.przybysz.pms.filemanager.service.dto.IDsDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileDTO;
import tech.przybysz.pms.filemanager.service.dto.ResourceFileLinkDTO;
import tech.przybysz.pms.filemanager.service.dto.TagDTO;
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
  private final DownloadService downloadService;
  private final ResourceFileLinkService linkService;
  private final TagService tagService;

  public ResourceFileResource(ResourceFileService fileService, UploadService uploadService,
                              DownloadService downloadService, ResourceFileLinkService linkService,
                              TagService tagService) {
    this.fileService = fileService;
    this.uploadService = uploadService;
    this.downloadService = downloadService;
    this.linkService = linkService;
    this.tagService = tagService;
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

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ResourceFileDTO>> upload(@RequestParam Long directoryId, @RequestPart("file") List<MultipartFile> files) {
    log.debug("REST request to upload {} ResourceFiles to Directory {}", files.size(), directoryId);
    List<ResourceFileDTO> save = uploadService.save(directoryId, files);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityCreationAlert(applicationName, true, ENTITY_NAME,
            save.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(save);
  }

  @PatchMapping("{id}/change-name/{name}")
  public ResponseEntity<ResourceFileDTO> updateFileName(@PathVariable Long id, @PathVariable String name) {
    log.debug("REST request to change ResourceFile {} name to {}", id, name);
    ResourceFileDTO save = fileService.updateName(id, name);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @PatchMapping("{id}/change-directory/{directoryId}")
  public ResponseEntity<ResourceFileDTO> updateParentDirectory(@PathVariable Long id, @PathVariable Long directoryId) {
    log.debug("REST request to change ResourceFile {} directory to {}", id, directoryId);
    ResourceFileDTO save = fileService.updateParentDirectory(id, directoryId);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, save.getId().toString()))
        .body(save);
  }

  @DeleteMapping("/_bulk")
  public ResponseEntity<Void> deleteFiles(@RequestBody IDsDTO ids) {
    log.debug("REST request to delete ResourceFiles {}", ids);
    fileService.delete(ids);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createBulkEntityDeletionAlert(applicationName, true, ENTITY_NAME,
            ids.getIds().stream().map(Object::toString).collect(Collectors.toList())))
        .build();
  }

  @PatchMapping("/change-directory/{directoryId}")
  public ResponseEntity<List<ResourceFileDTO>> updateParentDirectoryBulk(@RequestBody IDsDTO ids, @PathVariable Long directoryId) {
    log.debug("REST request to change ResourceFiles {} directory to {}", ids, directoryId);
    List<ResourceFileDTO> save = fileService.updateParentDirectory(ids, directoryId);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createBulkEntityUpdateAlert(applicationName, true, ENTITY_NAME,
            save.stream().map(it -> it.getId().toString()).collect(Collectors.toList())))
        .body(save);
  }

  @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> download(@PathVariable Long id) {
    log.debug("REST request to download ResourceFile {}", id);
    FileResource resource = downloadService.get(id);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createDownloadFileAlert(applicationName, true,
            resource.getFileName()))
        .contentLength(resource.getResource().contentLength())
        .body(resource.getResource());
  }

  @GetMapping("/{id}/links")
  public List<ResourceFileLinkDTO> getFileLinks(@PathVariable Long id) {
    log.debug("REST request to get all ResourceFileLinks of ResourceFile {}", id);
    return linkService.findAllOfFile(id);
  }

  @GetMapping("/{id}/tags")
  public List<TagDTO> getFileTags(@PathVariable Long id) {
    log.debug("REST request to get all Tags of ResourceFile {}", id);
    return tagService.findAllOfFile(id);
  }
}
