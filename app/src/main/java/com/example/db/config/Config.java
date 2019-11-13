package com.example.db.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    public static boolean getThemeStatePref(Context context){
        SharedPreferences preferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("isDark", false);
        return isDark;
    }

}
