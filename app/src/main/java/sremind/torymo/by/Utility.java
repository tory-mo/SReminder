package sremind.torymo.by;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class Utility {
    public static final SimpleDateFormat dateToStrFormat = new SimpleDateFormat("dd.MM.yyyy");

    private static final String PREF_SEEN = "pref_seen";

    public static boolean getSeenParam(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_SEEN,false);
    }

    public static void changeSeenParam(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean old = prefs.getBoolean(PREF_SEEN,false);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_SEEN, !old);
        editor.commit();
    }
}
