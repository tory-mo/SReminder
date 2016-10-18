package sremind.torymo.by;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.service.SeriesService;

public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String SEARCH_DETAIL_URI = "OVERVIEW_URI";
    private static final int SEARCH_DETAIL_LOADER = 0;
    private Uri mUri;
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
            mUri = arguments.getParcelable(OverviewFragment.SEARCH_DETAIL_URI);
            String mdbid = SReminderContract.SearchResultEntry.getIdfromUri(mUri);
            Cursor cursor = getActivity().getContentResolver().query(SReminderContract.SeriesEntry.buildSeriesBymdbId(mdbid),null,null,null,null);
            if(cursor!=null && cursor.moveToFirst()){
                mInList = true;
            }
            Intent intent = new Intent(getActivity(), SeriesService.class);
            intent.putExtra(SeriesService.SERIES_QUERY_EXTRA, mUri.toString());
            getActivity().startService(intent);

            mOverviewTextView = (TextView) rootView.findViewById(R.id.overviewTextView);
            mFirstDateTextView = (TextView) rootView.findViewById(R.id.firstDateTextView);
            mOngoingTextView = (TextView) rootView.findViewById(R.id.ongoingTextView);
            mHomepageTextView = (TextView) rootView.findViewById(R.id.homepageTextView);
            mSeasonsTextView = (TextView) rootView.findViewById(R.id.seasonsTextView);
            mEpisodeTimeTextView = (TextView) rootView.findViewById(R.id.episodeTimeTextView);
            mGenresTextView = (TextView) rootView.findViewById(R.id.genresTextView);
        }
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    SReminderContract.SEARCH_RESULT_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            String overview = data.getString(SReminderContract.COL_SEARCH_RESULT_OVERVIEW);
            Date firstDate = Utility.getCalendarFromFormattedLong(data.getLong(SReminderContract.COL_SEARCH_RESULT_FIRST_DATE));
            boolean ongoing = Utility.getBooleanFromDB(data.getInt(SReminderContract.COL_SEARCH_RESULT_ONGOING));
            mPoster = data.getString(SReminderContract.COL_SEARCH_RESULT_POSTER);
            String homepage = data.getString(SReminderContract.COL_SEARCH_RESULT_HOMEPAGE);
            String seasons = data.getString(SReminderContract.COL_SEARCH_RESULT_SEASONS);
            String episodeTime = data.getString(SReminderContract.COL_SEARCH_RESULT_EPISODETIME);
            String genres = data.getString(SReminderContract.COL_SEARCH_RESULT_GENRES);
            mName = data.getString(SReminderContract.COL_SEARCH_RESULT_NAME);
            mImdbId = data.getString(SReminderContract.COL_SEARCH_RESULT_IMDB);

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SEARCH_DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

}
