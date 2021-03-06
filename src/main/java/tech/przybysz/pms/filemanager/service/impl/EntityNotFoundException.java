package tech.przybysz.pms.filemanager.service.impl;

public class EntityNotFoundException extends RuntimeException {

  private String entity;
  private Long entityId;

  public EntityNotFoundException(String entity, Long entityId) {
    super();
    this.entity = entity;
    this.entityId = entityId;
  }
}
