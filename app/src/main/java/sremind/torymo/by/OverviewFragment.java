package sremind.torymo.by;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.service.SeriesService;

public class OverviewFragment extends Fragment{

    static final String SEARCH_DETAIL_URI = "OVERVIEW_URI";

    String mdbId;
    boolean mInList = false;
    private String mName = "";
    private String mImdbId = "";
    private String mPoster = "";

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_fragment,container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mdbId = arguments.getString(OverviewFragment.SEARCH_DETAIL_URI);
            Series seriesInList = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getSeriesByMdbId(mdbId);
            if(seriesInList != null){
                mInList = true;
            }
            Intent intent = new Intent(getActivity(), SeriesService.class);
            intent.putExtra(SeriesService.SERIES_QUERY_EXTRA, mdbId);
            getActivity().startService(intent);

            mOverviewTextView =  rootView.findViewById(R.id.overviewTextView);
            mFirstDateTextView =  rootView.findViewById(R.id.firstDateTextView);
            mOngoingTextView =  rootView.findViewById(R.id.ongoingTextView);
            mHomepageTextView =  rootView.findViewById(R.id.homepageTextView);
            mSeasonsTextView =  rootView.findViewById(R.id.seasonsTextView);
            mEpisodeTimeTextView =  rootView.findViewById(R.id.episodeTimeTextView);
            mGenresTextView =  rootView.findViewById(R.id.genresTextView);

            SearchResult searchResult = SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().getSeriesResultById(mdbId);

            if(searchResult == null ) return rootView;


            String overview = searchResult.getOverview();
            Date firstDate = Utility.getCalendarFromFormattedLong(searchResult.getFirstDate());
            boolean ongoing = searchResult.isOngoing();
            mPoster = searchResult.getPoster();
            String homepage = searchResult.getHomepage();
            String seasons = String.valueOf(searchResult.getSeasons());
            String episodeTime = searchResult.getEpisodeTime();
            String genres = searchResult.getGenres();
            mName = searchResult.getName();
            mImdbId = searchResult.getImdbId();
            String firstDateStr = "";
            if(firstDate!=null)
                firstDateStr = Utility.dateToStrFormat.format(firstDate);
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
