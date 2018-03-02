package sremind.torymo.by;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SearchActivity extends AppCompatActivity implements SearchFragment.Callback {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        if (savedInstanceState == null) {

            SearchFragment fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment, fragment)
                    .commit();
        }

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        handleIntent(getIntent());

        SReminderDatabase.getAppDatabase(this).searchResultDao().delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.onActionViewExpanded();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SReminderDatabase.getAppDatabase(this).searchResultDao().delete();
            if(query.length()>1) {
                String currLanguage = Locale.getDefault().getLanguage();
                String needLang = Utility.LANGUAGE_EN;
                if(!currLanguage.equals(needLang)){
                    needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
                }

                Uri builtUri = Uri.parse(Utility.SEARCH_MOVIE_DB_URL).buildUpon()
                        .appendQueryParameter(Utility.QUERY, query)
                        .appendQueryParameter(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .appendQueryParameter(Utility.LANGUAGE_PARAM, needLang)
                        .build();

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                getElements(response);
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                RequestSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            }
        }
    }

    @Override
    public void onItemSelected(String srId) {
            Intent intent = new Intent(this, SearchDetailActivity.class)
                    .putExtra(SearchDetailFragment.SEARCH_DETAIL_URI, srId);
            startActivity(intent);
    }

    private int getElements(JSONObject obj){
        int added = 0;
        String mdbId;
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
                mdbId = item.getString("id");
                poster = Utility.POSTER_PATH + item.getString("poster_path");
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

                if(mdbId == null) continue;

                SearchResult sr = new SearchResult();
                sr.setMdbId(mdbId);
                sr.setPoster(poster);
                sr.setName(name);
                sr.setOverview(overview);
                sr.setPopularity(popularity);
                if(date != null)
                    sr.setFirstDate(date.getTime());

                searchResults.add(sr);
                added++;
            }
            if(added != 0)
                SReminderDatabase.getAppDatabase(this).searchResultDao().insert(searchResults);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return added;
    }
}
