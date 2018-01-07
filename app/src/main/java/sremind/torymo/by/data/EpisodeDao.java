package sremind.torymo.by.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EpisodeDao {

    @Query("SELECT * FROM episodes order by date asc")
    LiveData<List<Episode>> getAll();

    @Query("SELECT * FROM episodes WHERE series like :series order by date asc")
    LiveData<List<Episode>> getEpisodesBySeries(String series);

    @Query("SELECT * FROM episodes WHERE series like :series and ep_number like :number order by date asc")
    LiveData<List<Episode>> getEpisodesBySeriesAndNumber(String series, String number);

    @Query("SELECT * FROM episodes WHERE date between :date1 and :date2 order by date asc")
    LiveData<List<Episode>> getEpisodesBetweenDates(long date1, long date2);

    @Query("SELECT * FROM episodes WHERE (seen = 0) and (date between :date1 and :date2) order by date asc")
    LiveData<List<Episode>> getNotSeenEpisodesBetweenDates(long date1, long date2);

    @Query("SELECT * FROM episodes WHERE date = :date")
    LiveData<List<Episode>> getEpisodesForDate(long date);

    @Query("SELECT * FROM episodes WHERE date = :date and seen = 0")
    LiveData<List<Episode>> getNotSeenEpisodesForDate(long date);

    @Query("SELECT * FROM episodes WHERE date = :date and series like :series")
    LiveData<List<Episode>> getEpisodesForSeriesAndDate(String series, long date);

    @Query("UPDATE episodes set seen = :seen where id = :id")
    void setSeen(int id, boolean seen);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Episode episode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Episode> episodes);

    @Query("Update episodes set name = :name, ep_number = :number, date = :date where id = :id")
    void update(int id, String name, String number, long date);

    @Query("Delete from episodes where series like :series")
    void delete(String series);

    @Delete
    void delete(Episode episode);
}
