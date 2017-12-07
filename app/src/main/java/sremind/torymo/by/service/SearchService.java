package sremind.torymo.by.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sremind.torymo.by.BuildConfig;
import sremind.torymo.by.Utility;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SearchService extends IntentService{
    private static final String APP_NAME = "SReminder";
    public static final String SEARCH_QUERY_EXTRA = "sqe";
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

            Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                    .appendQueryParameter(EXTERNAL_PARAM, IMDB_VALUE)
                    .appendQueryParameter(QUERY, strQuery)
                    .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();
            JSONObject obj = getData(builtUri);

            if(getElements(obj) > 0)
                ;
    }

    private int getElements(JSONObject obj){
        int added = 0;
        String id;
        String name;
        String poster;
        String overview;
        String firstDate;
        float popularity;
        Date date;
        List<SearchResult> searchResults = new ArrayList<>();
        try {
            JSONArray array = obj.getJSONArray("results");
            for(int i = 0; i<array.length(); i++){
                JSONObject item = array.getJSONObject(i);
                id = item.getString("id");
                poster = POSTER_PATH + item.getString("poster_path");
                overview = item.getString("overview");
                name = item.getString("name");
                popularity = (float) item.getDouble("popularity");
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

                SearchResult sr = new SearchResult();
                sr.setSRId(id);
                sr.setPoster(poster);
                sr.setName(name);
                sr.setOverview(overview);
                sr.setPopularity(popularity);
                if(date != null)
                    sr.setFirstDate(Utility.getDateTime(date));

                searchResults.add(sr);
                added++;
            }
            SReminderDatabase.getAppDatabase(this).searchResultDao().insert(searchResults);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return added;
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
