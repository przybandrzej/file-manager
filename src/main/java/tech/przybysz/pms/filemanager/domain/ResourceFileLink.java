package tech.przybysz.pms.filemanager.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "resource_file_link")
public class ResourceFileLink implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "link_name")
  private String linkName;

  @ManyToOne
  @JoinColumn(name = "parent_file_id", referencedColumnName = "id", nullable = false)
  private ResourceFile parentFile;

  @ManyToOne
  @JoinColumn(name = "child_file_id", referencedColumnName = "id", nullable = false)
  private ResourceFile childFile;

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

  public ResourceFile getParentFile() {
    return parentFile;
  }

  public void setParentFile(ResourceFile parentFile) {
    this.parentFile = parentFile;
  }

  public ResourceFile getChildFile() {
    return childFile;
  }

  public void setChildFile(ResourceFile childFile) {
    this.childFile = childFile;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof ResourceFileLink)) {
      return false;
    }
    return id != null && id.equals(((ResourceFileLink) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }
}
