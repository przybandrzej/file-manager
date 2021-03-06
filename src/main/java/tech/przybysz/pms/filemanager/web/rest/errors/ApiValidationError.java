package tech.przybysz.pms.filemanager.web.rest.errors;

class ApiValidationError extends SubError {

  private String parameter;
  private String rejectedValue;
  private String message;

  public ApiValidationError(String parameter, String rejectedValue, String message) {
    this.parameter = parameter;
    this.rejectedValue = rejectedValue;
    this.message = message;
  }

  public ApiValidationError() {
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getRejectedValue() {
    return rejectedValue;
  }

  public void setRejectedValue(String rejectedValue) {
    this.rejectedValue = rejectedValue;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
