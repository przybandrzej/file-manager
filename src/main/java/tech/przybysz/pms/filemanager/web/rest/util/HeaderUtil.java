package tech.przybysz.pms.filemanager.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

  private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

  private HeaderUtil() {
  }

  /**
   * <p>createAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param message a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createAlert(String applicationName, String message, String param) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-" + applicationName + "-alert", message);
    try {
      headers.add("X-" + applicationName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
    } catch (UnsupportedEncodingException e) {
      // StandardCharsets are supported by every Java implementation so this exception will never happen
    }
    return headers;
  }

  /**
   * <p>createEntityCreationAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createEntityCreationAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? applicationName + "." + entityName + ".created"
        : "A new " + entityName + " is created with identifier " + param;
    return createAlert(applicationName, message, param);
  }

  /**
   * <p>createEntityUpdateAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createEntityUpdateAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? applicationName + "." + entityName + ".updated"
        : "A " + entityName + " is updated with identifier " + param;
    return createAlert(applicationName, message, param);
  }

  /**
   * <p>createEntityDeletionAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createEntityDeletionAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
    String message = enableTranslation ? applicationName + "." + entityName + ".deleted"
        : "A " + entityName + " is deleted with identifier " + param;
    return createAlert(applicationName, message, param);
  }

  /**
   * <p>createFailureAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param errorKey a {@link java.lang.String} object.
   * @param defaultMessage a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createFailureAlert(String applicationName, boolean enableTranslation, String entityName, String errorKey, String defaultMessage) {
    log.error("Entity processing failed, {}", defaultMessage);

    String message = enableTranslation ? "error." + errorKey : defaultMessage;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-" + applicationName + "-error", message);
    headers.add("X-" + applicationName + "-params", entityName);
    return headers;
  }

  /**
   * <p>createBulkEntityCreationAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createBulkEntityCreationAlert(String applicationName, boolean enableTranslation, String entityName, List<String> param) {
    String params = String.join(",", param);
    String message = enableTranslation ? applicationName + "." + entityName + ".created"
        : "New " + entityName + " are created with identifiers " + params;
    return createAlert(applicationName, message, params);
  }

  /**
   * <p>createBulkEntityDeletionAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createBulkEntityDeletionAlert(String applicationName, boolean enableTranslation, String entityName, List<String> param) {
    String params = String.join(",", param);
    String message = enableTranslation ? applicationName + "." + entityName + ".deleted"
        : "A " + entityName + " is deleted with identifier " + param;
    return createAlert(applicationName, message, params);
  }

  /**
   * <p>createBulkEntityUpdateAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param entityName a {@link java.lang.String} object.
   * @param param a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createBulkEntityUpdateAlert(String applicationName, boolean enableTranslation, String entityName, List<String> param) {
    String params = String.join(",", param);
    String message = enableTranslation ? applicationName + "." + entityName + ".updated"
        : "A " + entityName + " is updated with identifier " + param;
    return createAlert(applicationName, message, params);
  }

  /**
   * <p>downloadFileAlert.</p>
   *
   * @param applicationName a {@link java.lang.String} object.
   * @param enableTranslation a boolean.
   * @param fileName a {@link java.lang.String} object.
   * @return a {@link org.springframework.http.HttpHeaders} object.
   */
  public static HttpHeaders createDownloadFileAlert(String applicationName, boolean enableTranslation, String fileName) {
    String message = enableTranslation ? applicationName + ".file.downloaded"
        : "A file is downloaded with name " + fileName;
    HttpHeaders headers = createAlert(applicationName, message, fileName);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
    return headers;
  }
}
