package sremind.torymo.by.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {Series.class, Episode.class, SearchResult.class}, version = 9, exportSchema = false)
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
                            .addMigrations(MIGRATION_8_9)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Episodes ADD COLUMN overview TEXT");
            database.execSQL("ALTER TABLE Episodes ADD COLUMN poster TEXT");
            database.execSQL("ALTER TABLE search_result ADD COLUMN status TEXT");
            database.execSQL("ALTER TABLE Series ADD COLUMN genres TEXT");
            database.execSQL("ALTER TABLE Series ADD COLUMN ongoing INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Series ADD COLUMN seasons INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Series ADD COLUMN overview TEXT");
            database.execSQL("ALTER TABLE Series ADD COLUMN popularity REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Series ADD COLUMN status TEXT");


        }
    };
}
