package tech.przybysz.pms.filemanager.service.impl;

public class EntityAlreadyExistsException extends RuntimeException {

  private String entity;
  private String identity;

  public EntityAlreadyExistsException(String entity, String identity) {
    this.entity = entity;
    this.identity = identity;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
