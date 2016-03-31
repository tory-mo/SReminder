package sremind.torymo.by.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sremind.torymo.by.data.SReminderContract.EpisodeEntry;
import sremind.torymo.by.data.SReminderContract.SeriesEntry;

public class SReminderDbHelper extends SQLiteOpenHelper {

    // константы для конструктора
    private static final String DATABASE_NAME = "sreminder_database.db";
    private static final int DATABASE_VERSION = 10;

    public SReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlSeries = "Create table "+ SeriesEntry.TABLE_NAME +"("
                + SeriesEntry._ID + " integer primary key autoincrement,"
                + SeriesEntry.COLUMN_IMDB_ID +" text,"
                + SeriesEntry.COLUMN_WATCHLIST +" integer default 0,"
                + SeriesEntry.COLUMN_NAME +" text,"
                + " UNIQUE (" + SeriesEntry.COLUMN_IMDB_ID + ") ON CONFLICT REPLACE);";
        String sqlEpisodes = "Create table " + EpisodeEntry.TABLE_NAME + "("
                + EpisodeEntry._ID + " integer primary key autoincrement,"
                + EpisodeEntry.COLUMN_NAME + " text,"
                + EpisodeEntry.COLUMN_SERIES_ID + " text,"
                + EpisodeEntry.COLUMN_NUMBER + " text,"
                + EpisodeEntry.COLUMN_DATE + " integer,"
                + EpisodeEntry.COLUMN_SEEN + " integer default 0,"
                + " FOREIGN KEY (" + EpisodeEntry.COLUMN_SERIES_ID + ") REFERENCES "
                + SeriesEntry.TABLE_NAME + " (" + SeriesEntry.COLUMN_IMDB_ID + "));";
        db.execSQL(sqlSeries);


        db.execSQL(sqlEpisodes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EpisodeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SeriesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
