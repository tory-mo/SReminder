package sremind.torymo.by.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SearchResultDao {

    @Query("SELECT * FROM search_result")
    LiveData<List<SearchResult> > getAll();

    @Query("SELECT * FROM search_result WHERE imdbid like :imdbId limit 1")
    LiveData<SearchResult> getSeriesResultByImdbId(String imdbId);

    @Query("SELECT * FROM search_result WHERE sr_id like :searchResultId limit 1")
    LiveData<SearchResult> getSeriesResultById(String searchResultId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchResult searchResult);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<SearchResult> searchResults);

    @Query("Update search_result set imdbid = :imdbId, homepage = :homepage, genres = :genres, ongoing = :ongoing, seasons = :seasons, overview = :overview, episode_time = :episodeTime where sr_id = :id")
    void update(int id, String imdbId, String homepage, String genres, boolean ongoing, int seasons, String overview, String episodeTime);

    @Delete
    void delete(SearchResult searchResult);

    @Query("Delete from search_result")
    void delete();
}
