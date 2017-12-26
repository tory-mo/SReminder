package sremind.torymo.by.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SeriesDao {
    @Query("SELECT * FROM series")
    List<Series> getAll();

    @Query("SELECT * FROM series WHERE watchlist = 1")
    List<Series> getWatchlist();

    @Query("SELECT * FROM series WHERE imdbid like :imdbId limit 1")
    Series getSeriesByImdbId(String imdbId);

    @Query("SELECT * FROM series WHERE mdbid like :mdbId limit 1")
    Series getSeriesByMdbId(String mdbId);

    @Query("UPDATE series set watchlist = :watchlist where imdbid like :imdbId")
    void setWatchlist(String imdbId, boolean watchlist);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Series series);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Series> series);

    @Query("Delete from series where imdbid like :imdbId")
    void delete(String imdbId);

    @Delete
    void delete(Series series);
}
