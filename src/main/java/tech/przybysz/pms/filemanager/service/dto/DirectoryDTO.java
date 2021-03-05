package tech.przybysz.pms.filemanager.service.dto;

import java.time.LocalDateTime;

public class DirectoryDTO {

  private Long id;
  private String name;
  private Long parentId;
  private LocalDateTime created;
  private LocalDateTime modified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
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

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof DirectoryDTO)) {
      return false;
    }
    return id != null && id.equals(((DirectoryDTO) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public String toString() {
    return "DirectoryDTO{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", parentId=" + parentId +
        ", created=" + created +
        ", modified=" + modified +
        '}';
  }
}
