package tech.przybysz.pms.filemanager.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directory")
public class Directory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "parent_id", referencedColumnName = "id", nullable = false)
  private Directory parent;

  @Column(name = "created")
  private LocalDateTime created;

  @Column(name = "modified")
  private LocalDateTime modified;

  @OneToMany(mappedBy = "parent")
  private Set<Directory> children = new HashSet<>();

  @OneToMany(mappedBy = "parentDirectory")
  private Set<ResourceFile> files = new HashSet<>();

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

  public Directory getParent() {
    return parent;
  }

  public void setParent(Directory parent) {
    this.parent = parent;
  }

  public Set<Directory> getChildren() {
    return children;
  }

  public void setChildren(Set<Directory> children) {
    this.children = children;
  }

  public Set<ResourceFile> getFiles() {
    return files;
  }

  public void setFiles(Set<ResourceFile> files) {
    this.files = files;
  }

  public Directory addFile(ResourceFile file) {
    file.setParentDirectory(this);
    this.files.add(file);
    return this;
  }

  public Directory addChild(Directory directory) {
    directory.setParent(this);
    this.children.add(directory);
    return this;
  }

  public Directory removeFile(ResourceFile file) {
    this.files.remove(file);
    file.setParentDirectory(null);
    return this;
  }

  public Directory removeChild(Directory directory) {
    this.children.remove(directory);
    directory.setParent(null);
    return this;
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
    if(!(o instanceof Directory)) {
      return false;
    }
    return id != null && id.equals(((Directory) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }
}
