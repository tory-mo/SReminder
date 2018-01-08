package sremind.torymo.by.service;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.RequestSingleton;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SeriesDetailsRequest {
    final static String MOVIE_DB_URL = "http://api.themoviedb.org/3/tv";
    final static String POSTER_PATH = "http://image.tmdb.org/t/p/w300/";

    public static void getDetails(final Context context, final String id){
        final String EXTERNAL_PARAM = "external_ids";
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(id)
                .appendPath(EXTERNAL_PARAM)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imdbId = response.getString("imdb_id");
                            if(imdbId == null) return;

                            Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                                    .appendPath(id)
                                    .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                    .build();
                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                    (Request.Method.GET, builtUri.toString(), null,
                                            getDetailsResponse(context, imdbId),
                                            errorListener(context));
                            RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, errorListener(context));
        RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private static Response.Listener<JSONObject> getDetailsResponse(final Context context, final String imdbId){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray genres = response.getJSONArray("genres");
                    String genresStr = genres.getJSONObject(0).getString("name");
                    for(int i = 1; i<genres.length(); i++){
                        genresStr = genresStr.concat(", " + genres.getJSONObject(i).getString("name"));
                    }

                    String overview = response.getString("overview");
                    if(overview == null)
                        overview = "";
                    String episodeTime = response.getString("episode_run_time");
                    String[] times = episodeTime.split(",");
                    episodeTime = times[0].replaceAll("[^\\d.]", "");
                    for(int i = 1; i<times.length; i++){
                        episodeTime = episodeTime.concat(","+times[i].replaceAll("[^\\d.]", ""));
                    }

                    String firstDate = response.getString("first_air_date");
                    Date date;
                    try {
                        if (firstDate.contains("."))
                            date = new SimpleDateFormat("dd MMM. yyyy", Locale.ENGLISH).parse(firstDate);
                        else if (firstDate.contains("-")) {
                            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(firstDate);
                        } else
                            date = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).parse(firstDate);
                    }catch(Exception ex){
                        date = null;
                    }

                    SearchResult sr = new SearchResult();
                    sr.setImdbId(imdbId);
                    sr.setHomepage(response.getString("homepage"));
                    sr.setOngoing(response.getBoolean("in_production"));
                    sr.setSeasons(response.getInt("number_of_seasons"));
                    sr.setMdbId(response.getString("id"));
                    sr.setPoster(POSTER_PATH+response.getString("poster_path"));
                    sr.setName(response.getString("name"));
                    sr.setPopularity((float) response.getDouble("popularity"));
                    sr.setGenres(genresStr);
                    sr.setOverview(overview);
                    sr.setEpisodeTime(episodeTime);
                    sr.setFirstDate(date.getTime());

                    SReminderDatabase.getAppDatabase(context).searchResultDao().insert(sr);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
    }

    private static Response.ErrorListener errorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }
}
