package tech.przybysz.pms.filemanager.service.dto;

import java.time.LocalDateTime;

public class ResourceFileDTO {

  private Long id;
  private String originalName;
  private String generatedName;
  private String extension;
  private Long parentDirectoryId;
  private LocalDateTime created;
  private LocalDateTime modified;
  private Boolean backedUp;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public String getGeneratedName() {
    return generatedName;
  }

  public void setGeneratedName(String generatedName) {
    this.generatedName = generatedName;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public Long getParentDirectoryId() {
    return parentDirectoryId;
  }

  public void setParentDirectoryId(Long parentDirectoryId) {
    this.parentDirectoryId = parentDirectoryId;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getModified() {
    return modified;
  }

  public void setModified(LocalDateTime modified) {
    this.modified = modified;
  }

  public Boolean getBackedUp() {
    return backedUp;
  }

  public void setBackedUp(Boolean backedUp) {
    this.backedUp = backedUp;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof ResourceFileDTO)) {
      return false;
    }
    return id != null && id.equals(((ResourceFileDTO) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public String toString() {
    return "ResourceFileDTO{" +
        "id=" + id +
        ", originalName='" + originalName + '\'' +
        ", generatedName='" + generatedName + '\'' +
        ", extension='" + extension + '\'' +
        ", parentDirectoryId=" + parentDirectoryId +
        ", created=" + created +
        ", modified=" + modified +
        ", backedUp=" + backedUp +
        '}';
  }
}
