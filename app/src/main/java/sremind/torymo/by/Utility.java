package sremind.torymo.by;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class Utility {

    //the moviedb paths
    public final static String BASE_URL = "http://api.themoviedb.org/";
    public final static String POSTER_PATH = "http://image.tmdb.org/t/p/w300/";
    public final static String EXTERNAL_IDS_PARAM = "external_ids";
    public final static String APPKEY_PARAM = "api_key";
    public final static String LANGUAGE_PARAM = "language";
    public final static String LANGUAGE_EN = "en";
    public final static String QUERY = "query";
    public final static String APPEND_TO_RESPONSE = "append_to_response";

    public static final SimpleDateFormat dateToStrFormat = new SimpleDateFormat("dd MMMM yyyy");

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
