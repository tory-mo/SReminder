package sremind.torymo.by;

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

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    SearchAdapter mSearchAdapter;
    private static final int SEARCH_LOADER = 0;
    static final String  SELECTED_KEY = "SELECTED_ITEM";
    private int mPosition = ListView.INVALID_POSITION;
    ListView mSearchListView;

    public interface Callback{
        void onItemSelected(Uri dateUri);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        mSearchAdapter = new SearchAdapter(getActivity(), null, 0);

        mSearchListView = (ListView) rootView.findViewById(R.id.searchListView);
        mSearchListView.setAdapter(mSearchAdapter);

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback)getActivity())
                            .onItemSelected(SReminderContract.SearchResultEntry.buildSearchResultId(cursor.getString(SReminderContract.COL_SEARCH_RESULT_MDBID)));
                }
                mPosition = position;

            }
        });

        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = SReminderContract.SearchResultEntry.COLUMN_POPULARITY + " DESC";

        return new CursorLoader(
                getActivity(),
                SReminderContract.SearchResultEntry.CONTENT_URI,
                SReminderContract.SEARCH_RESULT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSearchAdapter.swapCursor(data);
        if(mPosition!=ListView.INVALID_POSITION) {
            mSearchListView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSearchAdapter.swapCursor(null);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SEARCH_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
