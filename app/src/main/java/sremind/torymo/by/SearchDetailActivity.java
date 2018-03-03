package sremind.torymo.by;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.response.SeriesResponseResult;

public class SearchDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail_activity);

        if(savedInstanceState == null){
            SearchDetailFragment fragment = new SearchDetailFragment();

            String mdbId = getIntent().getStringExtra(SearchDetailFragment.SEARCH_DETAIL_URI);

            getData(mdbId);

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

    public void getData(String mdbId){
        String currLanguage = Locale.getDefault().getLanguage();
        String needLang = Utility.LANGUAGE_EN;
        if(!currLanguage.equals(needLang)){
            needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
        }

        HashMap<String, String> params = new HashMap<>();

        params.put(Utility.LANGUAGE_PARAM, needLang);
        params.put(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
        params.put(Utility.APPEND_TO_RESPONSE, Utility.EXTERNAL_IDS_PARAM);

        SRemindApp.getMdbService().getSeriesDetails(mdbId, params).enqueue(new Callback<SeriesResponseResult>() {
            @Override
            public void onResponse(Call<SeriesResponseResult> call, retrofit2.Response<SeriesResponseResult> response) {
                SearchResult sr = SeriesResponseResult.seriesToSearchResult(response.body());
                if(sr != null){
                    SReminderDatabase.getAppDatabase(SearchDetailActivity.this).searchResultDao().insert(sr);

                    SearchDetailFragment sdf = (SearchDetailFragment)getSupportFragmentManager().findFragmentById(R.id.search_detail_fragment);
                    sdf.refresh(sr.getMdbId());
                }
            }

            @Override
            public void onFailure(Call<SeriesResponseResult> call, Throwable t) {
                Log.e(SearchDetailActivity.class.toString(), t.getMessage());
            }
        });
    }
}
