package com.shansown.androidtests.util;

import android.util.Log;

import com.shansown.android.lollipoptest.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
  private static final String LOG_PREFIX = "TestsApp_";
  private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
  private static final int MAX_LOG_TAG_LENGTH = 23;

  private static final String LOG_FILE_PATH = "sdcard/lollipop_tests.log";

  public static String makeLogTag(String str) {
    if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
      return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
    }
    return LOG_PREFIX + str;
  }

  /**
   * Don't use this when obfuscating class names!
   */
  public static String makeLogTag(Class cls) {
    return makeLogTag(cls.getSimpleName());
  }

  public static void LOGD(final String tag, String message) {
    //noinspection PointlessBooleanExpression,ConstantConditions
    if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
      Log.d(tag, message);
      writeLog(getLogTime(System.currentTimeMillis()) + " D/" + tag + "﹕ " + message);
    }
  }

  public static void LOGD(final String tag, String message, Throwable cause) {
    //noinspection PointlessBooleanExpression,ConstantConditions
    if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
      Log.d(tag, message, cause);
      writeLog(getLogTime(System.currentTimeMillis()) + " D/" + tag + "﹕ " + message);
    }
  }

  public static void LOGV(final String tag, String message) {
    //noinspection PointlessBooleanExpression,ConstantConditions
    if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.VERBOSE)) {
      Log.v(tag, message);
      writeLog(getLogTime(System.currentTimeMillis()) + " V/" + tag + "﹕ " + message);
    }
  }

  public static void LOGV(final String tag, String message, Throwable cause) {
    //noinspection PointlessBooleanExpression,ConstantConditions
    if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.VERBOSE)) {
      Log.v(tag, message, cause);
      writeLog(getLogTime(System.currentTimeMillis()) + " V/" + tag + "﹕ " + message);
    }
  }

  public static void LOGI(final String tag, String message) {
    Log.i(tag, message);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " I/" + tag + "﹕ " + message);
    }
  }

  public static void LOGI(final String tag, String message, Throwable cause) {
    Log.i(tag, message, cause);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " I/" + tag + "﹕ " + message);
    }
  }

  public static void LOGW(final String tag, String message) {
    Log.w(tag, message);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " W/" + tag + "﹕ " + message);
    }
  }

  public static void LOGW(final String tag, String message, Throwable cause) {
    Log.w(tag, message, cause);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " W/" + tag + "﹕ " + message);
    }
  }

  public static void LOGE(final String tag, String message) {
    Log.e(tag, message);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " E/" + tag + "﹕ " + message);
    }
  }

  public static void LOGE(final String tag, String message, Throwable cause) {
    Log.e(tag, message, cause);
    if (BuildConfig.DEBUG) {
      writeLog(getLogTime(System.currentTimeMillis()) + " E/" + tag + "﹕ " + message);
    }
  }

  public static boolean deleteLogFile() {
    File logFile = new File(LOG_FILE_PATH);
    return logFile.exists() && logFile.delete();
  }

  private static void writeLog(String logText) {
    File logFile = new File(LOG_FILE_PATH);

    if (!logFile.exists()) {
      try {
        logFile.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      //BufferedWriter for performance, true to set append to file flag
      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
      buf.append(logText);
      buf.newLine();
      buf.flush();
      buf.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static String getLogTime(long milliseconds) {
    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss.SSSZ");
    return format.format(new Date(milliseconds));
  }

  private LogUtils() {
  }
}