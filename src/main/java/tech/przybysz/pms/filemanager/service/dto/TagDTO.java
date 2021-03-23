package tech.przybysz.pms.filemanager.service.dto;

public class TagDTO {

  private Long id;
  private String name;

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

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof TagDTO)) {
      return false;
    }
    return id != null && id.equals(((TagDTO) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public String toString() {
    return "TagDTO{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
