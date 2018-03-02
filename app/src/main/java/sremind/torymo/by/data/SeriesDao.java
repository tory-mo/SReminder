package sremind.torymo.by.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SeriesDao {
    @Query("SELECT * FROM series ORDER BY name asc")
    LiveData<List<Series>> getAll();

    @Query("SELECT * FROM series WHERE watchlist = 1 ORDER BY name asc")
    LiveData<List<Series>> getWatchlist();

    @Query("SELECT * FROM series WHERE imdbid like :imdbId limit 1")
    Series getSeriesByImdbId(String imdbId);

    @Query("SELECT * FROM series WHERE mdbid like :mdbId limit 1")
    LiveData<Series> getSeriesByMdbId(String mdbId);

    @Query("UPDATE series set watchlist = :watchlist where imdbid like :imdbId")
    void setWatchlist(String imdbId, boolean watchlist);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Series series);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Series> series);

    @Query("Delete from series where imdbid like :imdbId")
    void deleteByImdbId(String imdbId);

    @Query("Delete from series where mdbid like :mdbid")
    void deleteByMdbId(String mdbid);

    @Delete
    void delete(Series series);
}
