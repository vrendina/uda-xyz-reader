package io.levelsoftware.xyzreader.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

import io.levelsoftware.xyzreader.R;

public final class PreferenceUtils {

    private PreferenceUtils() {
    }

    public static long getLastUpdate(Context context) {
        String key = context.getString(R.string.pref_last_update_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getLong(key, -1);
    }

    public static void updateLastUpdate(Context context) {
        String key = context.getString(R.string.pref_last_update_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, (new Date()).getTime());
        editor.apply();
    }

}
