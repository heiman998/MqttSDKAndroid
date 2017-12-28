package com.heiman.mqttdemo.util;

import android.content.SharedPreferences.Editor;

import com.heiman.mqttdemo.HmApplication;

public class SharedPreferencesUtil {

    public static void keepShared(String key, String value) {
        Editor editor = HmApplication.sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void keepShared(String key, Integer value) {
        Editor editor = HmApplication.sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void keepShared(String key, long value) {
        Editor editor = HmApplication.sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void keepShared(String key, int value) {
        Editor editor = HmApplication.sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static void keepShared(String key, boolean value) {
        Editor editor = HmApplication.sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String queryValue(String key, String defvalue) {

        return HmApplication.sharedPreferences.getString(key, defvalue);
    }


    public static String queryValue(String key) {
        String value = HmApplication.sharedPreferences.getString(key, "");
        if ("".equals(value)) {
            return "";
        }

        return value;
    }

    public static Integer queryIntValue(String key) {

        return HmApplication.sharedPreferences.getInt(key, 0);
    }


    public static boolean queryBooleanValue(String key) {
        return HmApplication.sharedPreferences.getBoolean(key, false);
    }


    public static long queryLongValue(String key) {
        return HmApplication.sharedPreferences.getLong(key, 0);
    }


    public static boolean deleteAllValue() {

        return HmApplication.sharedPreferences.edit().clear().commit();
    }


    public static void deleteValue(String key) {
        HmApplication.sharedPreferences.edit().remove(key).apply();
    }
}
