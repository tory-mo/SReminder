package sremind.torymo.by.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Series.class, Episode.class, SearchResult.class}, version = 6, exportSchema = false)
public abstract class SReminderDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "sreminder_database.db";
    private static SReminderDatabase INSTANCE;

    public abstract SeriesDao seriesDao();
    public abstract EpisodeDao episodeDao();
    public abstract SearchResultDao searchResultDao();

    public static SReminderDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SReminderDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
