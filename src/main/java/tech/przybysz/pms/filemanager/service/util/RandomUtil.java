package tech.przybysz.pms.filemanager.service.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

  private static final int MIN_COUNT = 20;
  private static final int MAX_COUNT = 30;

  private RandomUtil() {
  }


  public static String generateFileNameSlug() {
    return RandomStringUtils.randomAlphanumeric(MIN_COUNT, MAX_COUNT);
  }

}
