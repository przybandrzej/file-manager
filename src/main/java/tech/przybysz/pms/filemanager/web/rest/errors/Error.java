package tech.przybysz.pms.filemanager.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Error {

  private HttpStatus status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private String message;
  private List<SubError> subErrors = new ArrayList<>();

  private Error() {
    timestamp = LocalDateTime.now();
  }

  public Error(HttpStatus status) {
    this();
    this.status = status;
  }

  public Error(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
  }

  public Error(HttpStatus status, String message, List<SubError> errors) {
    this();
    this.status = status;
    this.message = message;
    this.subErrors = errors;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<SubError> getSubErrors() {
    return subErrors;
  }

  public void setSubErrors(List<SubError> subErrors) {
    this.subErrors = subErrors;
  }
}
