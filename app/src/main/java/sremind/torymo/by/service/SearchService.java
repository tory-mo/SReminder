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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.Utility;
import sremind.torymo.by.data.SReminderContract.SearchResultEntry;

public class SearchService extends IntentService{
    private static final String APP_NAME = "SReminder";
    public static final String SEARCH_QUERY_EXTRA = "sqe";
    public static final String SEARCH_RESULT_EXTRA = "sre";
    final String MOVIE_DB_URL = "http://api.themoviedb.org/3/search/tv";
    final String POSTER_PATH = "http://image.tmdb.org/t/p/w300/";
    public SearchService() {
        super(APP_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent==null || !intent.hasExtra(SEARCH_QUERY_EXTRA)) {
            return;
        }
        String strQuery = intent.getStringExtra(SEARCH_QUERY_EXTRA);
        if(strQuery.length() == 0){
            return;
        }
        //try {
            final String EXTERNAL_PARAM = "external_source";
            final String IMDB_VALUE = "imdb_id";
            final String QUERY = "query";
            final String APPKEY_PARAM = "api_key";
            final String LANGUAGE_PARAM = "language";

            Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                    .appendQueryParameter(EXTERNAL_PARAM, IMDB_VALUE)
                    .appendQueryParameter(QUERY, strQuery)
                    //.appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                    .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();
            JSONObject obj = getData(builtUri);

            getElements(obj);
            //int cnt = getEpisodes(imdbId, id, lastSeason);
            //Intent newIntent = new Intent(Utility.BROADCAST_ACTION);
            //newIntent.putExtra(EPISODES_RESULT_EXTRA, cnt);

            //LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
//        } catch (JSONException e) {
//            Log.e("", e.getMessage(), e);
//            e.printStackTrace();
//        }

    }

    private void getElements(JSONObject obj){
        String id;
        String name;
        String poster;
        String overview;
        String firstDate;
        double popularity;
        Date date;
        try {
            JSONArray array = obj.getJSONArray("results");
            for(int i = 0; i<array.length(); i++){
                JSONObject item = array.getJSONObject(i);
                id = item.getString("id");
                poster = POSTER_PATH+item.getString("poster_path");
                overview = item.getString("overview");
                name = item.getString("name");
                popularity = item.getDouble("popularity");
                firstDate = item.getString("first_air_date");
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
                ContentValues cv = new ContentValues();
                cv.put(SearchResultEntry.COLUMN_ID, id);
                cv.put(SearchResultEntry.COLUMN_POSTER, poster);
                cv.put(SearchResultEntry.COLUMN_NAME, name);
                cv.put(SearchResultEntry.COLUMN_OVERVIEW, overview);
                cv.put(SearchResultEntry.COLUMN_POPULARITY, popularity);
                if(date!=null)
                    cv.put(SearchResultEntry.COLUMN_FIRST_DATE,  Utility.getDateTime(date));
                getContentResolver().insert(SearchResultEntry.CONTENT_URI, cv);
            }
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
}
