package sremind.torymo.by.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

import sremind.torymo.by.Utility;

public class SReminderContract {

    public static final String CONTENT_AUTHORITY = "by.torymo.sremind.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SERIES = "Series";
    public static final String PATH_EPISODES = "Episodes";

    public static final String[] SERIES_COLUMNS = {
            SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID,
            SeriesEntry.TABLE_NAME + "." + SeriesEntry.COLUMN_NAME,
            SeriesEntry.COLUMN_IMDB_ID,
            SeriesEntry.COLUMN_WATCHLIST
    };
    public static final int COL_SERIES_ID = 0;
    public static final int COL_SERIES_NAME = 1;
    public static final int COL_SERIES_IMDB_ID = 2;
    public static final int COL_SERIES_WATCHLIST = 3;

    public static final String[] EPISODES_COLUMNS = {
            EpisodeEntry.TABLE_NAME + "." + EpisodeEntry._ID,
            EpisodeEntry.COLUMN_DATE,
            EpisodeEntry.TABLE_NAME + "." +EpisodeEntry.COLUMN_NAME,
            EpisodeEntry.COLUMN_NUMBER,
            EpisodeEntry.COLUMN_SEEN,
            EpisodeEntry.COLUMN_SERIES_ID
    };
    public static final int COL_EPISODE_ID = 0;
    public static final int COL_EPISODE_DATE = 1;
    public static final int COL_EPISODE_NAME = 2;
    public static final int COL_EPISODE_NUMBER = 3;
    public static final int COL_EPISODE_SEEN = 4;
    public static final int COL_EPISODE_SERIES_ID = 5;

    public static final class SeriesEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_SERIES;

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMDB_ID = "imdbid";
        public static final String COLUMN_WATCHLIST = "watchlist";

//        static {
//            TABLE_NAME = PATH_SERIES;
//
//            COLUMN_NAME = "name";
//            COLUMN_IMDB_ID = "imdbid";
//            COLUMN_WATCHLIST = "watchlist";
//        }


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SERIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SERIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SERIES;

        public static Uri buildSeriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildSeriesWatchlist() {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_WATCHLIST).build();
        }

        public static Uri buildSeriesByImdbId(String imdbId){
            return CONTENT_URI.buildUpon().appendPath(COLUMN_IMDB_ID).appendPath(imdbId).build();
        }

        public static String getImdbIdFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

    }

    /* Inner class that defines the table contents of the weather table */
    public static final class EpisodeEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_EPISODES;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SERIES_ID = "series";
        public static final String COLUMN_NUMBER = "ep_number";
        public static final String COLUMN_SEEN = "seen";
//        static{
//            TABLE_NAME = PATH_EPISODES;
//            COLUMN_NAME = "name";
//            COLUMN_DATE = "date";
//            COLUMN_SERIES_ID = "series";
//            COLUMN_NUMBER = "ep_number";
//            COLUMN_SEEN = "seen";
//        }

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPISODES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPISODES;


        public static Uri buildEpisodeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEpisodesSeries(String series) {
            return CONTENT_URI.buildUpon()
                    .appendPath(EpisodeEntry.COLUMN_SERIES_ID)
                    .appendPath(series).build();
        }

        public static String getImdbIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildEpisodesBetweenDatesUri(Date dateFrom, Date dateTo){
            return CONTENT_URI.buildUpon().appendPath(EpisodeEntry.COLUMN_DATE)
                    .appendPath(Utility.getDateTime(dateFrom).toString())
                    .appendPath(Utility.getDateTime(dateTo).toString())
                    .build();
        }

        public static Uri buildEpisodesBetweenDatesUnseenUri(Date dateFrom, Date dateTo){
            return CONTENT_URI.buildUpon().appendPath(EpisodeEntry.COLUMN_DATE)
                    .appendPath(Utility.getDateTime(dateFrom).toString())
                    .appendPath(Utility.getDateTime(dateTo).toString())
                    .appendQueryParameter(EpisodeEntry.COLUMN_SEEN,"0")
                    .build();
        }

        public static String getDateFromFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getDateToFromUri(Uri uri){
            return uri.getPathSegments().get(3);
        }

        public static boolean getSeenFromUri(Uri uri){
            if(uri.getQueryParameter(COLUMN_SEEN) != null){
                return true;
            }
            return false;
        }

        public static Uri buildEpisodesForDateUri(Date date){
            return CONTENT_URI.buildUpon().appendPath(EpisodeEntry.COLUMN_DATE)
                    .appendPath(Utility.getDateTime(date).toString())
                    .build();
        }

        public static Uri buildEpisodesForDateUnseenUri(Date date){
            return CONTENT_URI.buildUpon().appendPath(EpisodeEntry.COLUMN_DATE)
                    .appendPath(Utility.getDateTime(date).toString())
                    .appendQueryParameter(EpisodeEntry.COLUMN_SEEN,"0")
                    .build();
        }

        public static String getDateFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        /*public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }*/
    }
}
