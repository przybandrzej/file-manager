package tech.przybysz.pms.filemanager.service.impl;

import java.util.ArrayList;
import java.util.List;

public class EntityNotFoundException extends RuntimeException {

  private String entity;
  private Long entityId;
  private List<Long> ids = new ArrayList<>();

  public EntityNotFoundException(String entity, Long entityId) {
    super();
    this.entity = entity;
    this.entityId = entityId;
  }

  public EntityNotFoundException(String entity, List<Long> ids) {
    super();
    this.entity = entity;
    this.ids = ids;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }
}
