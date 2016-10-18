package sremind.torymo.by;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.data.SReminderContract.EpisodeEntry;

public class EpisodeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri mUri;
    EpisodeListAdapter mEpisodeAdapter;
    ListView mListView;
    static final String  SELECTED_KEY = "SELECTED_ITEM";
    private int mPosition = ListView.INVALID_POSITION;
    private static final int EPISODE_LOADER = 0;

    static final String EPISODE_LIST_URI = "EPISODES_URI";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(EpisodeListFragment.EPISODE_LIST_URI);
        }
        mEpisodeAdapter = new EpisodeListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.episodes_fragment, container, false);

        mListView = (ListView)rootView.findViewById(R.id.lvEpisodes);

        mListView.setAdapter(mEpisodeAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	   @Override
	        	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                   Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
                   String seriesId = cursor.getString(SReminderContract.COL_EPISODE_ID);
                   boolean seen = !Utility.getBooleanFromDB(cursor.getInt(SReminderContract.COL_EPISODE_SEEN)); //get inverted to current value
                   ContentValues cv = new ContentValues();
                   cv.put(EpisodeEntry.COLUMN_SEEN, Utility.getBooleanForDB(seen));

                   getActivity().getContentResolver().update(EpisodeEntry.CONTENT_URI,
                           cv,
                           EpisodeEntry._ID + " = ? ",
                           new String[]{seriesId});
	        	}

        });


        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri!=null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    SReminderContract.EPISODES_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEpisodeAdapter.swapCursor(data);
        if(mPosition!=ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEpisodeAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(EPISODE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


}
