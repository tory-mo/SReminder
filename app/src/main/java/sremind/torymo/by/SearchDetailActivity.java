package sremind.torymo.by;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SearchDetailActivity extends AppCompatActivity {

    final String MOVIE_DB_URL = "http://api.themoviedb.org/3/tv";
    final String POSTER_PATH = "http://image.tmdb.org/t/p/w300/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail_activity);

        if(savedInstanceState == null){
            SearchDetailFragment fragment = new SearchDetailFragment();

            String mdbId = getIntent().getStringExtra(SearchDetailFragment.SEARCH_DETAIL_URI);

            getImdbId(mdbId);

            Bundle arguments = new Bundle();
            arguments.putString(SearchDetailFragment.SEARCH_DETAIL_URI, mdbId);
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_detail_fragment, fragment)
                    .commit();

            LiveData<SearchResult> sr = SReminderDatabase.getAppDatabase(this).searchResultDao().getSeriesResultById(mdbId);
            sr.observe(this, new Observer<SearchResult>() {
                @Override
                public void onChanged(@Nullable SearchResult searchResult) {
                    getSupportActionBar().setTitle(searchResult.getName());
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
    }

    private void getElements(JSONObject obj, String imdbId){
        try {
            JSONArray genres = obj.getJSONArray("genres");
            String genresStr = genres.getJSONObject(0).getString("name");
            for(int i = 1; i<genres.length(); i++){
                genresStr = genresStr.concat(", " + genres.getJSONObject(i).getString("name"));
            }

            String overview = obj.getString("overview");
            if(overview == null)
                overview = "";
            String episodeTime = obj.getString("episode_run_time");
            String[] times = episodeTime.split(",");
            episodeTime = times[0].replaceAll("[^\\d.]", "");
            for(int i = 1; i<times.length; i++){
                episodeTime = episodeTime.concat(","+times[i].replaceAll("[^\\d.]", ""));
            }

            String firstDate = obj.getString("first_air_date");
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
            sr.setHomepage(obj.getString("homepage"));
            sr.setOngoing(obj.getBoolean("in_production"));
            sr.setSeasons(obj.getInt("number_of_seasons"));
            sr.setSRId(obj.getString("id"));
            sr.setPoster(POSTER_PATH+obj.getString("poster_path"));
            sr.setName(obj.getString("name"));
            sr.setPopularity((float) obj.getDouble("popularity"));
            sr.setGenres(genresStr);
            sr.setOverview(overview);
            sr.setEpisodeTime(episodeTime);
            sr.setFirstDate(date.getTime());

            SReminderDatabase.getAppDatabase(this).searchResultDao().insert(sr);

            SearchDetailFragment sdf = (SearchDetailFragment)getSupportFragmentManager().findFragmentById(R.id.search_detail_fragment);
            sdf.refresh(sr.getSRId());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void getImdbId(final String id){

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
                    public void onResponse(JSONObject response){
                        try {
                            getData(id, response.getString("imdb_id"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        RequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    public void getData(String mdbId, final String imdbId){
        final String APPKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(mdbId)
                .appendQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        getElements(response, imdbId);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        RequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
