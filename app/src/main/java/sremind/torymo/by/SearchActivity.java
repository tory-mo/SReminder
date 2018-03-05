package sremind.torymo.by;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sremind.torymo.by.response.MdbSearchResultResponse;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.response.SearchResponseResult;

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

                HashMap<String, String> params = new HashMap<>();

                params.put(Utility.LANGUAGE_PARAM, needLang);
                params.put(Utility.QUERY, query);

                SRemindApp.getMdbService().search(params).enqueue(new Callback<MdbSearchResultResponse>() {
                    @Override
                    public void onResponse(Call<MdbSearchResultResponse> call, Response<MdbSearchResultResponse> response) {
                        MdbSearchResultResponse results = response.body();
                        getElements(results.results);
                    }

                    @Override
                    public void onFailure(Call<MdbSearchResultResponse> call, Throwable t) {
                        Log.e("SearchActivity", t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(String srId) {
            Intent intent = new Intent(this, SearchDetailActivity.class)
                    .putExtra(SearchDetailFragment.SEARCH_DETAIL_URI, srId);
            startActivity(intent);
    }

    private int getElements(List<SearchResponseResult> results){
        int added = 0;
        List<SearchResult> searchResults = new ArrayList<>();
            for(SearchResponseResult result : results){
                if(result.getMdbId() == null) continue;

                SearchResult sr = new SearchResult();
                sr.setMdbId(result.getMdbId());
                sr.setPoster(result.getPoster());
                sr.setName(result.getName());
                sr.setOverview(result.getOverview());
                sr.setPopularity(result.getPopularity());
                sr.setOriginalName(result.getOriginalName());
                if(result.getFirstDate() != null)
                    sr.setFirstDate(result.getFirstDate().getTime());

                searchResults.add(sr);
                added++;
            }
            if(added != 0)
                SReminderDatabase.getAppDatabase(this).searchResultDao().insert(searchResults);

        return added;
    }
}
