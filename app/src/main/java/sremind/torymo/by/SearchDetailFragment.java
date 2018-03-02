package sremind.torymo.by;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sremind.torymo.by.data.MdbSearchResultResponse;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.databinding.SearchDetailFragmentBinding;
import sremind.torymo.by.service.EpisodesJsonRequest;
import sremind.torymo.by.service.MDBService;
import sremind.torymo.by.service.SeriesDetailsRequest;
import sremind.torymo.by.service.SeriesRequest;
import sremind.torymo.by.viewmodel.SearchDetailViewModel;

public class SearchDetailFragment extends Fragment{

    static final String SEARCH_DETAIL_URI = "SD_URI";
    boolean mInList = false;
    boolean mShowMenu = false;
    String mdbId;
    private String mName = "";
    private String mImdbId = "";
    private String mPoster = "";
    private String mOriginalName = "";

    SearchDetailViewModel.Factory factory;
    SearchDetailViewModel model;

    MenuItem miOnlySeen;

    private SearchDetailFragmentBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mdbId = arguments.getString(SearchDetailFragment.SEARCH_DETAIL_URI);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.search_detail_fragment, container, false);

        refresh(mdbId);

        return mBinding.getRoot();
    }

    public void refresh(String mdbId){
        if(mdbId == null) return;
        HashMap<String, String> params = new HashMap<>();
        String currLanguage = Locale.getDefault().getLanguage();
        String needLang = Utility.LANGUAGE_EN;
        if(!currLanguage.equals(needLang)){
            needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
        }

        params.put(Utility.LANGUAGE_PARAM, needLang);
        params.put(Utility.APPEND_TO_RESPONSE, Utility.EXTERNAL_IDS_PARAM);
        params.put(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
        try {
            if (getActivity().getClass().equals(SearchActivity.class)) {
                //SeriesRequest.getSeries(getActivity(), mdbId);
                List<Series> seriesList = SRemindApp.getMdbService().getSeries(mdbId, params).execute().body();
            } else {
                //SeriesDetailsRequest.getDetails(getActivity(), mdbId);
                MdbSearchResultResponse seriesList = SRemindApp.getMdbService().getSeriesDetails(mdbId, params).execute().body();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        reQueryData(mdbId);
    }

    private void  reQueryData(String mdbId){
        if(model == null) return;
        model.getSeries(mdbId).observe(this, new Observer<Series>() {
            @Override
            public void onChanged(@Nullable Series series) {
                mInList = (series != null);
            }
        });

        model.getSearchResult(mdbId).observe(this, new Observer<SearchResult>() {
            @Override
            public void onChanged(@Nullable SearchResult searchResult) {
                if(searchResult != null) {
                    mPoster = searchResult.getPoster();
                    mName = searchResult.getName();
                    mOriginalName = searchResult.getOriginalName();
                    mImdbId = searchResult.getImdbId();
                    mShowMenu = (mImdbId != null);
                    changeMenuTitle(miOnlySeen);
                }
                mBinding.setSearchResult(searchResult);
            }
        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         factory = new SearchDetailViewModel.Factory(getActivity().getApplication());

        model = ViewModelProviders.of(this, factory)
                .get(SearchDetailViewModel.class);


        reQueryData(getArguments().getString(SearchDetailFragment.SEARCH_DETAIL_URI));
    }

    private void changeMenuTitle(MenuItem miOnlySeen){
        if(miOnlySeen == null) return;
        int seenTitle = R.string.add_series_title;
        if (mInList) seenTitle = R.string.delete_series_title;
        miOnlySeen.setTitle(getResources().getString(seenTitle));
        miOnlySeen.setVisible(mShowMenu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_detail_menu, menu);
        miOnlySeen = menu.findItem(R.id.action_add_series);
        changeMenuTitle(miOnlySeen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_add_series:
                if(mInList){
                    SReminderDatabase.getAppDatabase(getActivity()).seriesDao().deleteByMdbId(mdbId);
                }else {
                    if(mImdbId == null || mdbId == null){
                        Toast.makeText(getActivity(), "Can't be added", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    Series s = new Series(mName, mOriginalName, mImdbId, mdbId, mPoster, true);
                    SReminderDatabase.getAppDatabase(getActivity()).seriesDao().insert(s);

                    EpisodesJsonRequest request = new EpisodesJsonRequest(getActivity());
                    request.setOnEpisodesLoadedListener(new EpisodesJsonRequest.OnEpisodesLoadedListener() {
                        @Override
                        public void onEpisodesLoaded(int count, int activeRequests) {
                            Toast.makeText(getActivity(), getString(R.string.are_updated_short, count), Toast.LENGTH_SHORT).show();
                        }
                    });
                    request.getEpisodes(mdbId, mImdbId);
                }
                mInList = !mInList;
                changeMenuTitle(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
