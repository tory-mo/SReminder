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
import sremind.torymo.by.Utility;
import sremind.torymo.by.data.SReminderContract;

public class SeriesService extends IntentService{
    private static final String APP_NAME = "SReminder";
    final String MOVIE_DB_URL = "http://api.themoviedb.org/3/tv";
    public static final String SERIES_QUERY_EXTRA = "ssqe";
    public static final String SERIES_RESULT_EXTRA = "ssre";

    public SeriesService() {
        super(APP_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null || !intent.hasExtra(SERIES_QUERY_EXTRA)){
            return;
        }
        Uri uri = Uri.parse(intent.getStringExtra(SERIES_QUERY_EXTRA));
        String id = SReminderContract.SearchResultEntry.getIdfromUri(uri);
        if(id.length() == 0){
            return;
        }
        final String APPKEY_PARAM = "api_key";
        final String LANGUAGE_PARAM = "language";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(id)
                //.appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();
        try {
            JSONObject infoJson = getData(builtUri);
            if (infoJson == null) {
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put(SReminderContract.SearchResultEntry.COLUMN_HOMEPAGE, infoJson.getString("homepage"));
            JSONArray genres = infoJson.getJSONArray("genres");
            String genresStr = genres.getJSONObject(0).getString("name");
            for(int i = 1; i<genres.length(); i++){
                genresStr += ", "+genres.getJSONObject(i).getString("name");
            }
            cv.put(SReminderContract.SearchResultEntry.COLUMN_GENRES, genresStr);
            cv.put(SReminderContract.SearchResultEntry.COLUMN_ONGOING, Utility.getBooleanForDB(infoJson.getBoolean("in_production")));
            cv.put(SReminderContract.SearchResultEntry.COLUMN_SEASONS, infoJson.getString("number_of_seasons"));
            String overview = infoJson.getString("overview");
            if(overview!=null)
                cv.put(SReminderContract.SearchResultEntry.COLUMN_OVERVIEW, infoJson.getString("overview"));
            String episodeTime = infoJson.getString("episode_run_time");
            String[] times = episodeTime.split(",");
            episodeTime = times[0].replaceAll("[^\\d.]", "");
            for(int i = 1; i<times.length; i++){
                episodeTime += ","+times[i].replaceAll("[^\\d.]", "");
            }

            cv.put(SReminderContract.SearchResultEntry.COLUMN_EPISODE_TIME, episodeTime);
            getContentResolver().update(uri, cv, SReminderContract.SearchResultEntry.COLUMN_ID + "= ?", new String[]{id});
            getImdbId(uri, id);
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

    private void getImdbId(Uri uri, String id) throws JSONException {

        final String EXTERNAL_PARAM = "external_ids";
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(id)
                .appendPath(EXTERNAL_PARAM)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JSONObject infoJson = getData(builtUri);

        if(infoJson!=null) {
            ContentValues cv = new ContentValues();
            cv.put(SReminderContract.SearchResultEntry.COLUMN_IMDB, infoJson.getString("imdb_id"));
            getContentResolver().update(uri, cv, SReminderContract.SearchResultEntry.COLUMN_ID + "= ?", new String[]{id});
        }
    }
}
