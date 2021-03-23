package tech.przybysz.pms.filemanager.domain;

import tech.przybysz.pms.filemanager.domain.enumeration.FileSizeUnit;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resource_file")
public class ResourceFile implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_name")
  private String originalName;

  @Column(name = "generated_name")
  private String generatedName;

  @Column(name = "extension")
  private String extension;

  @Column(name = "size")
  private Long size;

  @Enumerated(EnumType.STRING)
  @Column(name = "size_unit")
  private FileSizeUnit sizeUnit;

  @Column(name = "created")
  private LocalDateTime created;

  @Column(name = "modified")
  private LocalDateTime modified;

  @Column(name = "backed_up")
  private Boolean backedUp;

  @ManyToOne
  @JoinColumn(name = "parent_directory_id", referencedColumnName = "id", nullable = false)
  private Directory parentDirectory;

  @ManyToMany
  @JoinTable(name = "resource_file_tag",
      joinColumns = @JoinColumn(name = "resource_file_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

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

  public Directory getParentDirectory() {
    return parentDirectory;
  }

  public void setParentDirectory(Directory parentDirectory) {
    this.parentDirectory = parentDirectory;
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

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public FileSizeUnit getSizeUnit() {
    return sizeUnit;
  }

  public void setSizeUnit(FileSizeUnit sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof ResourceFile)) {
      return false;
    }
    return id != null && id.equals(((ResourceFile) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }
}
