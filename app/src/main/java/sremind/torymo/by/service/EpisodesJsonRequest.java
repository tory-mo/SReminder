package sremind.torymo.by.service;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
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
import java.util.List;
import java.util.Locale;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.R;
import sremind.torymo.by.RequestSingleton;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;

public class EpisodesJsonRequest{

    final static String MOVIE_DB_URL = "https://api.themoviedb.org/3/tv";

    public static void getEpisodes(final LifecycleOwner lifecycleOwner, final Context context, final String mdbId, final String imdbId){
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(mdbId)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String seasons = response.getString("number_of_seasons");
                            final String SEASON_PATH = "season";
                            final String APPKEY_PARAM = "api_key";

                            Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                                    .appendPath(mdbId)
                                    .appendPath(SEASON_PATH)
                                    .appendPath(seasons)
                                    .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                    .build();
                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                    (Request.Method.GET, builtUri.toString(), null,
                                            EpisodesJsonRequest.episodesResponse(lifecycleOwner, context, imdbId, seasons),
                                            EpisodesJsonRequest.errorListener(context));
                            RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, errorListener(context));
        RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private static Response.Listener<JSONObject> episodesResponse(final LifecycleOwner lifecycleOwner, final Context context, final String imdbId, final String seasons){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    final String AIR_DATE = "air_date";
                    final String EPISODE_NUMBER = "episode_number";
                    final String EPISODE_NAME = "name";
                    final String EPISODES = "episodes";

                    JSONArray tvResultsJson = response.getJSONArray(EPISODES);

                    for(int i = 0; i<tvResultsJson.length(); i++){
                        JSONObject episode = tvResultsJson.getJSONObject(i);
                        try{
                            String dateStr = episode.getString(AIR_DATE);
                            String numberStr = context.getString(R.string.format_episode_number, seasons, episode.getString(EPISODE_NUMBER));
                            String nameStr = episode.getString(EPISODE_NAME);
                            addUpdateEpisode(lifecycleOwner, context, dateStr,imdbId,numberStr,nameStr);
                        }catch(Exception exception){
                            exception.printStackTrace();
                        }
                    }
                    Toast.makeText(context,tvResultsJson.length() + context.getString(R.string.are_updated), Toast.LENGTH_SHORT).show();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
    }

    private static void addUpdateEpisode(final LifecycleOwner lifecycleOwner, final Context context, final String dateStr, final String imdbId, final String numberStr, final String nameStr){


        LiveData<List<Episode>> episodes = SReminderDatabase.getAppDatabase(context).episodeDao().getEpisodesBySeriesAndNumber(imdbId, numberStr);
        episodes.observe(lifecycleOwner, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {
                Date date;
                try{
                    if(dateStr.contains("."))
                        date = new SimpleDateFormat("dd MMM. yyyy", Locale.ENGLISH).parse(dateStr);
                    else if(dateStr.contains("-")){
                        date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateStr);
                    }
                    else
                        date = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).parse(dateStr);
                }catch(Exception ex){
                    date = null;
                }

                if(!episodes.isEmpty()){
                    SReminderDatabase.getAppDatabase(context).episodeDao().update(episodes.get(0).getId(),nameStr, numberStr, date.getTime());
                }else {
                    Episode episode = new Episode(nameStr, date.getTime(), imdbId, numberStr);
                    SReminderDatabase.getAppDatabase(context).episodeDao().insert(episode);
                }
            }
        });

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
