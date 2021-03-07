package tech.przybysz.pms.filemanager.service.impl;

import java.util.List;

public class EntityNotFoundException extends RuntimeException {

  private String entity;
  private Long entityId;
  private List<Long> ids;

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
}
