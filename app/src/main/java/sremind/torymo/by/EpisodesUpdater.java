package sremind.torymo.by;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import sremind.torymo.by.data.SReminderContract;

public class EpisodesUpdater extends AsyncTask<String, Void, Integer>{
    final String MOVIE_DB_URL = "https://api.themoviedb.org/3/";

    Context context;

    public EpisodesUpdater(Context context){
        this.context = context;
    }
    @Override
    protected Integer doInBackground(String... params) {
        if(params.length ==0){
            return null;
        }
        try {
            String id = getIDFromJson(params[0]);
            String lastSeason = getLastSeasonNum(id);

            return getEpisodes(params[0], id, lastSeason);
        } catch (JSONException e) {
            Log.e("", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String getIDFromJson(String imdbId) throws JSONException{
        String res = null;

        final String FIND_PATH = "find";
        final String EXTERNAL_PARAM = "external_source";
        final String IMDB_VALUE = "imdb_id";
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(FIND_PATH)
                .appendPath(imdbId)
                .appendQueryParameter(EXTERNAL_PARAM, IMDB_VALUE)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JSONObject infoJson = getData(builtUri);

        if(infoJson!=null) {
            JSONArray tvResultsJson = infoJson.getJSONArray("tv_results");
            JSONObject obj = tvResultsJson.getJSONObject(0);
            res = obj.getString("id");
        }
        return res;
    }

    private String getLastSeasonNum(String id) throws JSONException{
        String res = null;

        final String TV_PATH = "tv";
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(TV_PATH)
                .appendPath(id)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JSONObject infoJson = getData(builtUri);
        if(infoJson!=null) {
            res = infoJson.getString("number_of_seasons");
        }

        return res;
    }

    private Integer getEpisodes(String imdbId, String id, String season) throws JSONException{
        int res = 0;

        final String TV_PATH = "tv";
        final String SEASON_PATH = "season";
        final String APPKEY_PARAM = "api_key";

        final String AIR_DATE = "air_date";
        final String EPISODE_NUMBER = "episode_number";
        final String EPISODE_NAME = "name";
        final String EPISODES = "episodes";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(TV_PATH)
                .appendPath(id)
                .appendPath(SEASON_PATH)
                .appendPath(season)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JSONObject infoJson = getData(builtUri);
        if(infoJson == null){
            return null;
        }

        JSONArray tvResultsJson = infoJson.getJSONArray(EPISODES);

        ContentResolver contentResolver = context.getContentResolver();
        for(int i = 0; i<tvResultsJson.length(); i++){
            JSONObject episode = tvResultsJson.getJSONObject(i);
            try{
                String dateStr = episode.getString(AIR_DATE);
                String numberStr = context.getString(R.string.format_episode_number, season, episode.getString(EPISODE_NUMBER));
                String nameStr = episode.getString(EPISODE_NAME);
                addUpdateEpisode(contentResolver, dateStr,imdbId,numberStr,nameStr);
                res++;
            }catch(Exception exception){
                Log.e("com.parse.push", "failed to parse date", exception);
            }
        }
        return res;
    }

    @Override
    protected void onPostExecute(Integer episodesCnt) {
        Toast.makeText(context,episodesCnt + context.getString(R.string.are_updated), Toast.LENGTH_SHORT).show();
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

    private void addUpdateEpisode(ContentResolver contentResolver, String dateStr, String imdbId, String numberStr, String nameStr){
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(SReminderContract.EpisodeEntry.COLUMN_NUMBER, numberStr);
        contentValues.put(SReminderContract.EpisodeEntry.COLUMN_SERIES_ID, imdbId);
        contentValues.put(SReminderContract.EpisodeEntry.COLUMN_NAME, nameStr);
        if(date!=null) {
            contentValues.put(SReminderContract.EpisodeEntry.COLUMN_DATE, Utility.getDateTime(date));
        }

        Cursor cursor = contentResolver.query(SReminderContract.EpisodeEntry.CONTENT_URI,
                SReminderContract.EPISODES_COLUMNS,
                SReminderContract.EpisodeEntry.COLUMN_SERIES_ID + " = ? AND "+
                        SReminderContract.EpisodeEntry.COLUMN_NUMBER + " like ?",
                new String[]{imdbId, numberStr},
                null);

        if(cursor != null &&cursor.moveToFirst()){
            contentResolver.update(SReminderContract.EpisodeEntry.CONTENT_URI,
                    contentValues,
                    SReminderContract.EpisodeEntry._ID + " = ? ",
                    new String[]{cursor.getString(SReminderContract.COL_EPISODE_ID)});
        }else {
            contentValues.put(SReminderContract.EpisodeEntry.COLUMN_SEEN, Utility.getBooleanForDB(false));
            context.getContentResolver().insert(
                    SReminderContract.EpisodeEntry.CONTENT_URI,
                    contentValues);
        }
        if(cursor != null) cursor.close();
    }
}
