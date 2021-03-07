package tech.przybysz.pms.filemanager.web.rest.errors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.przybysz.pms.filemanager.service.impl.EntityNotFoundException;
import tech.przybysz.pms.filemanager.service.io.impl.FileNotFoundException;
import tech.przybysz.pms.filemanager.service.io.impl.FileReadException;
import tech.przybysz.pms.filemanager.service.io.impl.IsNotFileException;
import tech.przybysz.pms.filemanager.service.io.impl.StorageException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler({EntityNotFoundException.class, EntityNotFoundException.class})
  protected ResponseEntity<Object> handleEntityNotFound(RuntimeException ex, WebRequest request) {
    Error apiError = new Error(NOT_FOUND);
    String message = "";
    if(ex instanceof tech.przybysz.pms.filemanager.web.rest.util.EntityNotFoundException) {
      message = "NOT FOUND";
    } else {
      EntityNotFoundException exception = (EntityNotFoundException) ex;
      String entity = exception.getIds().isEmpty() ? exception.getEntityId().toString() : exception.getIds().stream().map(Objects::toString)
          .collect(Collectors.joining(","));
      String name = exception.getEntity();
      message = name + "[" + entity + "]" + " not found.";
    }
    apiError.setMessage(message);
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileNotFoundException.class)
  protected ResponseEntity<Object> handleFileNotFound(FileNotFoundException ex, WebRequest request) {
    Error apiError = new Error(INTERNAL_SERVER_ERROR);
    apiError.setMessage("[" + ex.getFilename() + "]" + ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileReadException.class)
  protected ResponseEntity<Object> handleFileReadException(FileReadException ex, WebRequest request) {
    Error apiError = new Error(BAD_REQUEST);
    apiError.setMessage("[" + ex.getFileName() + "]" + ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(IsNotFileException.class)
  protected ResponseEntity<Object> handleIsNotFile(IsNotFileException ex, WebRequest request) {
    Error apiError = new Error(INTERNAL_SERVER_ERROR);
    apiError.setMessage("[" + ex.getFilename() + "]" + ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(StorageException.class)
  protected ResponseEntity<Object> handleStorageException(StorageException ex, WebRequest request) {
    Error apiError = new Error(INTERNAL_SERVER_ERROR);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(Error apiError) {
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
