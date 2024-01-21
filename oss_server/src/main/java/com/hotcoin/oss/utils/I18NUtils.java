package com.hotcoin.oss.utils;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class I18NUtils {
  public static final String LANGUAGE_FILE_NAME = "i18n/messages";
  public static final String LANG_PARAM = "lang";
  public static final Locale DEFAULT_LOCALE;
  public static Map<String, ResourceBundle> ResourceBundleConfigMap;
  private static Log logger;

  public I18NUtils() {
  }

  public static HttpServletRequest getRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
  }

  public static String getString(String code, Object... args) {
    return getString(getCurrentLocale(), code, args);
  }

  public static String getCurrentLang() {
    String lang = null;

    try {
      HttpServletRequest request = getRequest();
      lang = request.getHeader("lang");
      if (StringUtils.isBlank(lang)) {
        lang = request.getParameter("lang");
      }

      if (StringUtils.isBlank(lang)) {
        lang = DEFAULT_LOCALE.toString();
      }
    } catch (Exception var2) {
      lang = DEFAULT_LOCALE.toString();
    }

    return lang;
  }

  public static Locale getCurrentLocale() {
    return lang2Locale(getCurrentLang());
  }

  public static String getString(Locale locale, String code, Object... params) {
    String language = locale.getLanguage() + "_" + locale.getCountry();
    ResourceBundle config = (ResourceBundle)ResourceBundleConfigMap.get(language);
    String msg = "";

    try {
      if (config == null) {
        ResourceBundleConfigMap.put(language, ResourceBundle.getBundle("i18n/messages", locale));
      }

      msg = (new MessageFormat(new String(((ResourceBundle)ResourceBundleConfigMap.get(language)).getString(code).getBytes(
          StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8))).format(params).trim();
    } catch (Exception var7) {
      msg = "";
    }

    return msg;
  }

  public static Locale lang2Locale(String lang) {
    try {
      String[] locale = lang.split("_");
      return new Locale(locale[0], locale[1]);
    } catch (Exception var2) {
      logger.debug("语言转换成Locale失败，已设置成默认语言");
      return DEFAULT_LOCALE;
    }
  }

  static {
    DEFAULT_LOCALE = Locale.TAIWAN;
    ResourceBundleConfigMap = new HashMap();
    logger = LogFactory.getLog(I18NUtils.class);
  }
}
