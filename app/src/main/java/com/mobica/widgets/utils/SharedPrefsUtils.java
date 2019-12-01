package com.mobica.widgets.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobica.widgets.R;

public class SharedPrefsUtils {

    public static String loadLocationForWidget(Context context, int id) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.weather_name, id), "");
    }

    public static void saveLocationForWidget(Context context, int id, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.weather_name, id), name.replace(" ", ""));
        editor.apply();
    }
}
