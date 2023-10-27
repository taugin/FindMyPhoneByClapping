package com.clapping.find.phone.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    private static final String PREFS_NAME = "PREFS";

    public static boolean setPreference(Context context, String key, String value) {
        try {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            return editor.commit();
        } catch (Exception e) {
        }
        return false;
    }

    public static String getPreference(Context context, String key, String defaultValue) {
        try {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return settings.getString(key, defaultValue);
        } catch (Exception e) {
        }
        return null;
    }
}
