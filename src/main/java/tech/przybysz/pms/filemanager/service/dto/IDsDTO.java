package tech.przybysz.pms.filemanager.service.dto;

import java.util.ArrayList;
import java.util.List;

public class IDsDTO {

  private List<Long> ids = new ArrayList<>();

  public IDsDTO(List<Long> ids) {
    this.ids = ids;
  }

  public IDsDTO() {
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  @Override
  public String toString() {
    return "IDsDTO{" +
        "ids=" + ids +
        '}';
  }
}
