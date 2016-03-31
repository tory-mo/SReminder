package sremind.torymo.by.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class SReminderProvider extends ContentProvider {

    static final int EPISODES = 100;
    static final int EPISODE_WITH_SERIES = 101;
    static final int EPISODE_WITH_SERIES_AND_DATE = 102;
    static final int EPISODES_BETWEEN_DATES = 103;
    static final int EPISODES_BETWEEN_DATES_UNSEEN = 105;
    static final int EPISODES_WITH_DATE = 104;
    static final int EPISODES_WITH_DATE_UNSEEN = 106;
    static final int SERIES = 300;
    static final int SERIES_WITH_IMDB = 301;
    static final int SERIES_WATCHLIST = 302;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SReminderDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sEpisodesBySeriesQueryBuilder;
    private static final SQLiteQueryBuilder sSeries;

    static{
        sEpisodesBySeriesQueryBuilder = new SQLiteQueryBuilder();
        sSeries = new SQLiteQueryBuilder();
        sSeries.setTables(SReminderContract.SeriesEntry.TABLE_NAME);

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sEpisodesBySeriesQueryBuilder.setTables(
                SReminderContract.EpisodeEntry.TABLE_NAME + " INNER JOIN " +
                        SReminderContract.SeriesEntry.TABLE_NAME +
                        " ON " + SReminderContract.EpisodeEntry.TABLE_NAME +
                        "." + SReminderContract.EpisodeEntry.COLUMN_SERIES_ID +
                        " = " + SReminderContract.SeriesEntry.TABLE_NAME +
                        "." + SReminderContract.SeriesEntry.COLUMN_IMDB_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SReminderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        try {
            switch (match) {
//            // "weather/*/*"
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            {
//                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
//                break;
//            }
                // "episodes/series/*"
                case EPISODE_WITH_SERIES: {
                    retCursor = getEpisodesForSeries(uri, projection, sortOrder);
                    break;
                }
                // "episodes/date/*/*"
                case EPISODES_BETWEEN_DATES: {
                    retCursor = getEpisodesBetweenDates(uri, projection, sortOrder);
                    break;
                }
                // "episodes/date/*"
                case EPISODES_WITH_DATE: {
                    retCursor = getEpisodesForDate(uri, projection, sortOrder);
                    break;
                }
                // "episodes"
                case EPISODES: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            SReminderContract.EpisodeEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }
                // "series"
                case SERIES: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            SReminderContract.SeriesEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }
                // "series/watchlist"
                case SERIES_WATCHLIST: {
                    retCursor = getSeriesWatchlist(uri, projection, sortOrder);
                    break;
                }
                // "series/imdbid/*"
                case SERIES_WITH_IMDB: {
                    retCursor = getSeriesImdbId(uri, projection, sortOrder);
                    break;
                }

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Cursor getEpisodesForSeries(Uri uri, String[] projection, String sortOrder) {
        String imdbId = SReminderContract.EpisodeEntry.getImdbIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        //location.location_setting = ?
        String sLocationSettingSelection =
                SReminderContract.EpisodeEntry.TABLE_NAME+
                        "." + SReminderContract.EpisodeEntry.COLUMN_SERIES_ID + " = ? ";

        selection = sLocationSettingSelection;
        selectionArgs = new String[]{imdbId};

        return sEpisodesBySeriesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEpisodesBetweenDates(Uri uri, String[] projection, String sortOrder) {
        String dateFrom = SReminderContract.EpisodeEntry.getDateFromFromUri(uri);
        String dateTo = SReminderContract.EpisodeEntry.getDateToFromUri(uri);
        boolean unseen = SReminderContract.EpisodeEntry.getSeenFromUri(uri);

        String[] selectionArgs;
        String selection;

        //location.location_setting = ?
        String sLocationSettingSelection =
                SReminderContract.EpisodeEntry.TABLE_NAME+
                        "." + SReminderContract.EpisodeEntry.COLUMN_DATE + " BETWEEN ?  AND ?";
        selectionArgs = new String[]{dateFrom, dateTo};
        if(unseen){
            sLocationSettingSelection += " AND "+ SReminderContract.EpisodeEntry.COLUMN_SEEN+" = ?";
            selectionArgs = new String[]{dateFrom, dateTo, "0"};
        }

        selection = sLocationSettingSelection;


        return sEpisodesBySeriesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEpisodesForDate(Uri uri, String[] projection, String sortOrder) {
        String date = SReminderContract.EpisodeEntry.getDateFromUri(uri);
        boolean unseen = SReminderContract.EpisodeEntry.getSeenFromUri(uri);

        String[] selectionArgs;
        String selection;

        //location.location_setting = ?
        String sLocationSettingSelection =
                SReminderContract.EpisodeEntry.TABLE_NAME+
                        "." + SReminderContract.EpisodeEntry.COLUMN_DATE + " = ?";
        if(unseen){
            sLocationSettingSelection += " AND "+SReminderContract.EpisodeEntry.COLUMN_SEEN+" = ?";
            selectionArgs = new String[]{date, "0"};
        }else {
            selectionArgs = new String[]{date};
        }

        selection = sLocationSettingSelection;

        return sEpisodesBySeriesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSeriesWatchlist(Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs;
        String selection;

        String sLocationSettingSelection =
                SReminderContract.SeriesEntry.TABLE_NAME+
                        "." + SReminderContract.SeriesEntry.COLUMN_WATCHLIST + " = ? ";

        selection = sLocationSettingSelection;
        selectionArgs = new String[]{"1"};

        return sSeries.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSeriesImdbId(Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs;
        String selection;

        String sLocationSettingSelection =
                SReminderContract.SeriesEntry.TABLE_NAME+
                        "." + SReminderContract.SeriesEntry.COLUMN_IMDB_ID + " = ? ";
        String imdbId = SReminderContract.SeriesEntry.getImdbIdFromUri(uri);

        selection = sLocationSettingSelection;
        selectionArgs = new String[]{imdbId};

        return sSeries.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EPISODE_WITH_SERIES_AND_DATE:
                return SReminderContract.EpisodeEntry.CONTENT_ITEM_TYPE;
            case EPISODE_WITH_SERIES:
            case EPISODES_BETWEEN_DATES:
            case EPISODES_WITH_DATE:
            case EPISODES:
                return SReminderContract.EpisodeEntry.CONTENT_TYPE;
            case SERIES:
            case SERIES_WITH_IMDB:
                return SReminderContract.SeriesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EPISODES: {
                //normalizeDate(values);
                long _id = db.insert(SReminderContract.EpisodeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SReminderContract.EpisodeEntry.buildEpisodeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SERIES: {
                long _id = db.insert(SReminderContract.SeriesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SReminderContract.SeriesEntry.buildSeriesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case EPISODES: {
                rowsDeleted = db.delete(SReminderContract.EpisodeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SERIES: {
                rowsDeleted = db.delete(SReminderContract.SeriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EPISODES:
            case EPISODE_WITH_SERIES:{
                //normalizeDate(values);
                rowsUpdated = db.update(SReminderContract.EpisodeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SERIES: {
                rowsUpdated = db.update(SReminderContract.SeriesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case EPISODES:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(SReminderContract.EpisodeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case SERIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SReminderContract.SeriesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }



    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SReminderContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,SReminderContract.PATH_EPISODES, EPISODES);
        matcher.addURI(authority, SReminderContract.PATH_EPISODES+"/"+ SReminderContract.EpisodeEntry.COLUMN_SERIES_ID+"/*", EPISODE_WITH_SERIES);
        //matcher.addURI(authority, SReminderContract.PATH_EPISODES+"/*/#", EPISODE_WITH_SERIES_AND_DATE);
        matcher.addURI(authority, SReminderContract.PATH_EPISODES+"/"+ SReminderContract.EpisodeEntry.COLUMN_DATE+"/*", EPISODES_WITH_DATE);
        matcher.addURI(authority, SReminderContract.PATH_EPISODES+"/"+ SReminderContract.EpisodeEntry.COLUMN_DATE+"/*/*", EPISODES_BETWEEN_DATES);

        matcher.addURI(authority,SReminderContract.PATH_SERIES,SERIES);
        matcher.addURI(authority,SReminderContract.PATH_SERIES+"/"+ SReminderContract.SeriesEntry.COLUMN_IMDB_ID+"/*",SERIES_WITH_IMDB);
        matcher.addURI(authority,SReminderContract.PATH_SERIES+"/"+ SReminderContract.SeriesEntry.COLUMN_WATCHLIST+"",SERIES_WATCHLIST);
        return matcher;
    }
}
