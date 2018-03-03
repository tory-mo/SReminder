package sremind.torymo.by;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.databinding.SearchDetailFragmentBinding;
import sremind.torymo.by.response.EpisodesResponseResult;
import sremind.torymo.by.response.MdbEpisodesResponse;
import sremind.torymo.by.response.SeriesResponseResult;
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
    private int mSeasons = 0;

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
            if (getActivity().getClass().equals(SearchActivity.class)) {
               SRemindApp.getMdbService().getSeries(mdbId, params).enqueue(new Callback<SeriesResponseResult>() {
                    @Override
                    public void onResponse(Call<SeriesResponseResult> call, Response<SeriesResponseResult> response) {
                        SeriesResponseResult series = response.body();
                        String genresStr = "";
                        for(int i = 1; i<series.getGenres().size(); i++){
                            genresStr = genresStr.concat(", " + series.getGenres().get(i).name);
                        }

        //            String overview = series.getOverview();
        //            if(overview == null)
        //                overview = "";
                        String episodeTime = "";
                        for(int i = 1; i<series.getEpisodeTime().length; i++){
                            episodeTime = episodeTime.concat("," + series.getEpisodeTime()[i]);
                        }
                        SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().update(Integer.parseInt(series.getMdbId()),
                                series.getExternalIds().imdb_id,
                                series.getHomepage(),
                                genresStr,
                                series.isOngoing(),
                                series.getSeasons(),
                                series.getOverview(),
                                episodeTime);

                    }

                    @Override
                    public void onFailure(Call<SeriesResponseResult> call, Throwable t) {
                        Log.e(SearchDetailFragment.class.getName() + "from " + getActivity().getClass(), t.getMessage());
                    }
                });
            } else {
                SRemindApp.getMdbService().getSeriesDetails(mdbId, params).enqueue(new Callback<SeriesResponseResult>() {
                    @Override
                    public void onResponse(Call<SeriesResponseResult> call, Response<SeriesResponseResult> response) {
                        SeriesResponseResult series = response.body();
                        SearchResult sr = SeriesResponseResult.seriesToSearchResult(series);
                        if(sr != null)
                            SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().insert(sr);
                    }

                    @Override
                    public void onFailure(Call<SeriesResponseResult> call, Throwable t) {
                        Log.e(SearchDetailFragment.class.getName() + "from " + getActivity().getClass(), t.getMessage());
                    }
                });
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
                    mSeasons = searchResult.getSeasons();
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
                    final Series s = new Series(mName, mOriginalName, mImdbId, mdbId, mPoster, true);
                    SReminderDatabase.getAppDatabase(getActivity()).seriesDao().insert(s);

                    final HashMap<String, String> params = new HashMap<>();
                    String currLanguage = Locale.getDefault().getLanguage();
                    String needLang = Utility.LANGUAGE_EN;
                    if(!currLanguage.equals(needLang)){
                        needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
                    }

                    params.put(Utility.LANGUAGE_PARAM, needLang);
                    params.put(Utility.APPEND_TO_RESPONSE, Utility.EXTERNAL_IDS_PARAM);
                    params.put(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

                    SRemindApp.getMdbService().getEpisodes(s.getImdbId(), mSeasons, params).enqueue(new Callback<MdbEpisodesResponse>() {
                        @Override
                        public void onResponse(Call<MdbEpisodesResponse> call, Response<MdbEpisodesResponse> response) {
                            MdbEpisodesResponse responseBody = response.body();
                            if(responseBody == null || responseBody.episodes == null) {
                                Toast.makeText(getActivity(), "No response", Toast.LENGTH_LONG).show();
                                return;
                            }
                            List<EpisodesResponseResult> episodesResponse = responseBody.episodes;

                            for(int i = 0; i<episodesResponse.size(); i++){
                                EpisodesResponseResult res = episodesResponse.get(i);

                                if(res.getDate() == null) continue;
                                List<Episode> episodesDb = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeriesAndNumber(s.getImdbId(), res.getNumber(), res.getSeasonNumber());
                                try{
                                    if(episodesDb != null && !episodesDb.isEmpty()){
                                        SReminderDatabase.getAppDatabase(getActivity()).episodeDao().update(episodesDb.get(0).getId(),res.getName(), res.getNumber(), res.getSeasonNumber(), res.getDate().getTime());
                                    }else {
                                        Episode episode = new Episode(res.getName(), res.getDate().getTime(), s.getImdbId(), res.getNumber(), res.getSeasonNumber());
                                        SReminderDatabase.getAppDatabase(getActivity()).episodeDao().insert(episode);
                                    }
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                            Toast.makeText(getActivity(), getString(R.string.are_updated_short, episodesResponse.size()), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<MdbEpisodesResponse> call, Throwable t) {
                            Log.e(SearchDetailFragment.class.getName(), t.getMessage());
                        }
                    });
                }
                mInList = !mInList;
                changeMenuTitle(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
