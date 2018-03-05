package sremind.torymo.by.service;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import sremind.torymo.by.response.MdbEpisodesResponse;
import sremind.torymo.by.response.SeriesResponseResult;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.response.MdbSearchResultResponse;
import sremind.torymo.by.data.Series;

public interface MDBService {
    /*
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/34307?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/1403?api_key=6ad01c833dba757c5132002b79e99751 - to get season number
     */
    @GET("/3/tv/{mdbId}")
    Call<SeriesResponseResult> getSeries(@Path("mdbId") String mdbId, @QueryMap Map<String, String> map);

    /*
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/34307?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/1403?api_key=6ad01c833dba757c5132002b79e99751 - to get season number
     */
    @GET("/3/tv/{mdbId}")
    Call<SeriesResponseResult> getSeriesDetails(@Path("mdbId") String mdbId, @QueryMap Map<String, String> map);

    /*
        http://api.themoviedb.org/3/tv/1403/season/5?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en - episodes for a season
    */
    @GET("/3/tv/{mdbId}/season/{season_number}")
    Call<MdbEpisodesResponse> getEpisodes(@Path("mdbId") String mdbId, @Path("season_number") long season_number, @QueryMap Map<String, String> map);

    /*
        http://api.themoviedb.org/3/search/tv?query=%D0%B4%D0%BE%D0%BA%D1%82%D0%BE%D1%80&api_key=6ad01c833dba757c5132002b79e99751&language=ru-en
     */
    @GET("/3/search/tv")
    Call<MdbSearchResultResponse> search(@QueryMap Map<String, String> map);

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/latest?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en
     */
    @GET("/3/tv/latest")
    Call<List<Series>> getLatestShows(@QueryMap Map<String, String> map);

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/airing_today?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&page=1
     */
    @GET("/3/tv/airing_today")
    Call<List<Series>> getAiringToday(@QueryMap Map<String, String> map);

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/airing_today?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&page=1
     */
    @GET("/3/tv/top_rated")
    Call<List<Series>> getTopRated(@QueryMap Map<String, String> map);
}
