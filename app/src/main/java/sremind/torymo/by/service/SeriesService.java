package sremind.torymo.by.service;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.data.SReminderDatabase;

public class SeriesService extends IntentService{
    private static final String APP_NAME = "SReminder";
    final String MOVIE_DB_URL = "http://api.themoviedb.org/3/tv";
    public static final String SERIES_QUERY_EXTRA = "ssqe";

    public SeriesService() {
        super(APP_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null || !intent.hasExtra(SERIES_QUERY_EXTRA)){
            return;
        }
        String id = intent.getStringExtra(SERIES_QUERY_EXTRA);
        if(id.length() == 0){
            return;
        }
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();
        try {
            JSONObject infoJson = getData(builtUri);
            if (infoJson == null) {
                return;
            }

            JSONArray genres = infoJson.getJSONArray("genres");
            String genresStr = genres.getJSONObject(0).getString("name");
            for(int i = 1; i<genres.length(); i++){
                genresStr = genresStr.concat(", "+genres.getJSONObject(i).getString("name"));
            }

            String overview = infoJson.getString("overview");
            if(overview == null) overview = "";

            String episodeTime = infoJson.getString("episode_run_time");
            String[] times = episodeTime.split(",");
            episodeTime = times[0].replaceAll("[^\\d.]", "");
            for(int i = 1; i<times.length; i++){
                episodeTime = episodeTime.concat(","+times[i].replaceAll("[^\\d.]", ""));
            }

            String imdbId = getImdbId(id);
            if(imdbId == null) return;

            SReminderDatabase.getAppDatabase(this).searchResultDao().update(Integer.parseInt(id),
                    imdbId,
                    infoJson.getString("homepage"),
                    genresStr,
                    infoJson.getBoolean("in_production"),
                    infoJson.getInt("number_of_seasons"),
                    overview,
                    episodeTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private JSONObject getData(Uri builtUri){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return new JSONObject(buffer.toString());

        } catch (IOException e) {
            Log.e("", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }
        catch (JSONException e) {
            Log.e("", e.getMessage(), e);
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private String getImdbId(String id) throws JSONException {

        final String EXTERNAL_PARAM = "external_ids";
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(id)
                .appendPath(EXTERNAL_PARAM)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JSONObject infoJson = getData(builtUri);

        if(infoJson!=null) {
            return infoJson.getString("imdb_id");
        }
        return null;
    }
}
