package tech.przybysz.pms.filemanager.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.przybysz.pms.filemanager.service.DownloadService;
import tech.przybysz.pms.filemanager.service.FileResource;
import tech.przybysz.pms.filemanager.service.dto.DownloadDTO;
import tech.przybysz.pms.filemanager.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api/download")
public class DownloadResource {

  private final Logger log = LoggerFactory.getLogger(DownloadResource.class);
  @Value("${spring.application.name}")
  private String applicationName;

  private final DownloadService downloadService;

  public DownloadResource(DownloadService downloadService) {
    this.downloadService = downloadService;
  }

  @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> downloadBulk(@RequestBody DownloadDTO downloadDTO) {
    log.debug("REST request to download {}", downloadDTO);
    FileResource resource = downloadService.get(downloadDTO);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createDownloadFileAlert(applicationName, true,
            resource.getFileName()))
        .contentLength(resource.getResource().contentLength())
        .body(resource.getResource());
  }
}
