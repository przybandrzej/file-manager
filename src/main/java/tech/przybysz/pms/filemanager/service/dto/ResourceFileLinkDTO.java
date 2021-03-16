package tech.przybysz.pms.filemanager.service.dto;

public class ResourceFileLinkDTO {

  private Long id;
  private String linkName;
  private Long parentFileId;
  private Long childFileId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLinkName() {
    return linkName;
  }

  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }

  public Long getParentFileId() {
    return parentFileId;
  }

  public void setParentFileId(Long parentFileId) {
    this.parentFileId = parentFileId;
  }

  public Long getChildFileId() {
    return childFileId;
  }

  public void setChildFileId(Long childFileId) {
    this.childFileId = childFileId;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof ResourceFileLinkDTO)) {
      return false;
    }
    return id != null && id.equals(((ResourceFileLinkDTO) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public String toString() {
    return "ResourceFileLinkDTO{" +
        "id=" + id +
        ", linkName='" + linkName + '\'' +
        ", parentFileId=" + parentFileId +
        ", childFileId=" + childFileId +
        '}';
  }
}
