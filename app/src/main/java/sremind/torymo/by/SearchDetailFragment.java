package sremind.torymo.by;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.service.EpisodesJsonRequest;
import sremind.torymo.by.service.SeriesDetailsRequest;
import sremind.torymo.by.service.SeriesRequest;

public class SearchDetailFragment extends Fragment{

    static final String SEARCH_DETAIL_URI = "SD_URI";
    boolean mInList = false;
    String mdbId;
    private String mName = "";
    private String mImdbId = "";
    private String mPoster = "";

    //ImageView mPosterImageView;
    TextView mOverviewTextView;
    TextView mFirstDateTextView;
    TextView mOngoingTextView;
    TextView mHomepageTextView;
    TextView mSeasonsTextView;
    TextView mEpisodeTimeTextView;
    TextView mGenresTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_fragment,container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mOverviewTextView =  rootView.findViewById(R.id.overviewTextView);
            mFirstDateTextView =  rootView.findViewById(R.id.firstDateTextView);
            mOngoingTextView =  rootView.findViewById(R.id.ongoingTextView);
            mHomepageTextView =  rootView.findViewById(R.id.homepageTextView);
            mSeasonsTextView =  rootView.findViewById(R.id.seasonsTextView);
            mEpisodeTimeTextView =  rootView.findViewById(R.id.episodeTimeTextView);
            mGenresTextView =  rootView.findViewById(R.id.genresTextView);

            mdbId = arguments.getString(SearchDetailFragment.SEARCH_DETAIL_URI);
            refresh(mdbId);

        }
        return rootView;
    }

    public void refresh(String mdbId){
        LiveData<Series> seriesInList = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getSeriesByMdbId(mdbId);
        seriesInList.observe(this, new Observer<Series>() {
            @Override
            public void onChanged(@Nullable Series series) {
                if(series != null)
                    mInList = true;
            }
        });

        if(getActivity().getClass().equals(SearchActivity.class)) {
            SeriesRequest.getSeries(getActivity(), mdbId);
        }else{
            SeriesDetailsRequest.getDetails(getActivity(), mdbId);
        }

        LiveData<SearchResult> searchResultLD = SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().getSeriesResultById(mdbId);
        searchResultLD.observe(this, new Observer<SearchResult>() {
            @Override
            public void onChanged(@Nullable SearchResult searchResult) {
                if(searchResult == null ) return;

                String overview = searchResult.getOverview();
                Date firstDate = new Date(searchResult.getFirstDate());
                boolean ongoing = searchResult.isOngoing();
                mPoster = searchResult.getPoster();
                String homepage = searchResult.getHomepage();
                String seasons = String.valueOf(searchResult.getSeasons());
                String episodeTime = searchResult.getEpisodeTime();
                String genres = searchResult.getGenres();
                mName = searchResult.getName();
                mImdbId = searchResult.getImdbId();


                String firstDateStr = Utility.dateToStrFormat.format(firstDate);
                mFirstDateTextView.setText(getActivity().getString(R.string.format_first_date, firstDateStr));

                String ongoing_status;
                if(ongoing)
                    ongoing_status = getActivity().getString(R.string.format_ongoing_true);
                else
                    ongoing_status = getActivity().getString(R.string.format_ongoing_false);
                mOngoingTextView.setText(Html.fromHtml(getActivity().getString(R.string.format_ongoing, ongoing_status)));


                mHomepageTextView.setMovementMethod(LinkMovementMethod.getInstance());
                homepage = "<a href=\""+homepage+"\">"+homepage+"</a>";
                mHomepageTextView.setText(Html.fromHtml(getActivity().getString(R.string.format_homepage, homepage)));

                mSeasonsTextView.setText(Html.fromHtml(getActivity().getString(R.string.format_seasons, seasons)));
                mEpisodeTimeTextView.setText(Html.fromHtml(getActivity().getString(R.string.format_episode_time, episodeTime)));
                mGenresTextView.setText(Html.fromHtml(getActivity().getString(R.string.format_genres, genres)));
                mOverviewTextView.setText(getActivity().getString(R.string.format_overview, overview));
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void changeMenuTitle(MenuItem miOnlySeen){
        int seenTitle = R.string.add_series_title;
        if(mInList)seenTitle = R.string.delete_series_title;
        miOnlySeen.setTitle(getResources().getString(seenTitle));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_detail_menu, menu);
        MenuItem miOnlySeen = menu.findItem(R.id.action_add_series);
        changeMenuTitle(miOnlySeen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_add_series:
                if(mInList){
                    SReminderDatabase.getAppDatabase(getActivity()).seriesDao().delete(mImdbId);
                }else {
                    Series s = new Series(mName, mImdbId, mdbId, mPoster, true);
                    SReminderDatabase.getAppDatabase(getActivity()).seriesDao().insert(s);

                    EpisodesJsonRequest.getEpisodes(this, getActivity(), mdbId, mImdbId);
                }
                mInList = !mInList;
                changeMenuTitle(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
