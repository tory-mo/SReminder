package sremind.torymo.by;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static final String PACKAGE_NAME = "by.torymo.sremind";
    public static final String BROADCAST_ACTION = PACKAGE_NAME+".BROADCAST_ACTION";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat dateToStrFormat = new SimpleDateFormat("dd.MM.yyyy");

    private static final String PREF_SEEN = "pref_seen";

    public static Date getCalendarFromFormattedLong(long l){
        try {
            String str = String.valueOf(l);
            Date date = dateFormat.parse(str);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Long getDateTime(Date date) {
        return Long.parseLong(dateFormat.format(date));
    }

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
