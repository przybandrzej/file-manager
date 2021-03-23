package tech.przybysz.pms.filemanager.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "directory")
public class Tag implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", unique = true)
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
    if(!(o instanceof Tag)) {
      return false;
    }
    return id != null && id.equals(((Tag) o).id);
  }

  @Override
  public int hashCode() {
    return 31;
  }
}
