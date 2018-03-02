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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.RequestSingleton;
import sremind.torymo.by.Utility;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;

public class EpisodesJsonRequest{

    private OnEpisodesLoadedListener listener;
    private Context context;
    private int requestsCount = 0;

    public interface OnEpisodesLoadedListener {
        void onEpisodesLoaded(int episodesCount, int activeRequests);
    }

    public void setOnEpisodesLoadedListener(OnEpisodesLoadedListener listener) {
        this.listener = listener;
    }

    public EpisodesJsonRequest(Context context){
        this.context = context;
    }

    public void getEpisodes(final String mdbId, final String imdbId){
        Uri builtUri = Uri.parse(Utility.MOVIE_DB_URL).buildUpon()
                .appendPath(mdbId)
                .appendQueryParameter(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int seasons = response.getInt("number_of_seasons");

                            String currLanguage = Locale.getDefault().getLanguage();
                            String needLang = Utility.LANGUAGE_EN;
                            if(!currLanguage.equals(needLang)){
                                needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
                            }

                            Uri builtUri = Uri.parse(Utility.MOVIE_DB_URL).buildUpon()
                                    .appendPath(mdbId)
                                    .appendPath(Utility.SEASON_PATH)
                                    .appendPath(String.valueOf(seasons))
                                    .appendQueryParameter(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                    .appendQueryParameter(Utility.LANGUAGE_PARAM, needLang)
                                    .build();
                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                    (Request.Method.GET, builtUri.toString(), null,
                                            episodesResponse(imdbId, seasons),
                                            errorListener(context));
                            RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, errorListener(context));
        requestsCount++;
        RequestSingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public void getEpisodes(final HashMap<String, String> seriesList){
        for(Map.Entry<String, String> entry : seriesList.entrySet()) {
            final String mdbId = entry.getKey();
            final String imdbId = entry.getValue();
            getEpisodes(mdbId, imdbId);
        }
    }

    private Response.Listener<JSONObject> episodesResponse(final String imdbId, final int seasons){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String AIR_DATE = "air_date";
                    final String EPISODE_NUMBER = "episode_number";
                    final String EPISODE_NAME = "name";
                    final String EPISODES = "episodes";

                    JSONArray tvResultsJson = response.getJSONArray(EPISODES);
                    requestsCount--;

                    for(int i = 0; i<tvResultsJson.length(); i++){
                        JSONObject episode = tvResultsJson.getJSONObject(i);
                        String dateStr = episode.getString(AIR_DATE);
                        String nameStr = episode.getString(EPISODE_NAME);
                        if(!dateStr.equals("null"))
                            addUpdateEpisode(context, dateStr,imdbId, episode.getInt(EPISODE_NUMBER), seasons,nameStr);
                    }
                    if(listener != null) listener.onEpisodesLoaded(tvResultsJson.length(), requestsCount);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void addUpdateEpisode(final Context context, final String dateStr, final String imdbId, final int episodeNumber, final int seasonNumber, final String nameStr){
        List<Episode> episodes = SReminderDatabase.getAppDatabase(context).episodeDao().getEpisodesBySeriesAndNumber(imdbId, episodeNumber, seasonNumber);
        Date date;
        try{
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateStr);

            if(episodes != null && !episodes.isEmpty()){
                SReminderDatabase.getAppDatabase(context).episodeDao().update(episodes.get(0).getId(),nameStr, episodeNumber, seasonNumber, date.getTime());
            }else {
                Episode episode = new Episode(nameStr, date.getTime(), imdbId, episodeNumber, seasonNumber);
                SReminderDatabase.getAppDatabase(context).episodeDao().insert(episode);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private Response.ErrorListener errorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }
}
