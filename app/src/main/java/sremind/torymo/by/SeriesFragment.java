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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import sremind.torymo.by.data.SReminderContract;

public class SeriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int SERIES_LOADER = 1;
	static final String  SELECTED_KEY = "SELECTED_ITEM";
	private int mPosition = ListView.INVALID_POSITION;
	SeriesAdapter mSeriesAdapter;



	ListView mListView;

	public interface Callback{
		void onItemSelected(Uri dateUri);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.series_fragment, container, false);

		mSeriesAdapter = new SeriesAdapter(getActivity(), null, 0);
		mListView = (ListView)rootView.findViewById(R.id.lvSeries);
		mListView.setAdapter(mSeriesAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
				if (cursor != null) {
					((Callback)getActivity())
							.onItemSelected(SReminderContract.EpisodeEntry.buildEpisodesSeries(cursor.getString(SReminderContract.COL_SERIES_IMDB_ID)));
				}
				mPosition = position;
			}
		});

		if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
			mPosition = savedInstanceState.getInt(SELECTED_KEY);
		}

		return  rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder = SReminderContract.SeriesEntry.COLUMN_NAME + " ASC";
		Uri seriesUri  = SReminderContract.SeriesEntry.buildSeriesWatchlist();

		return new CursorLoader(
				getActivity(),
				seriesUri,
				SReminderContract.SERIES_COLUMNS,
				null,
				null,
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mSeriesAdapter.swapCursor(data);
		if(mPosition!=ListView.INVALID_POSITION) {
			mListView.smoothScrollToPosition(mPosition);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mSeriesAdapter.swapCursor(null);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		getLoaderManager().initLoader(SERIES_LOADER, null, this);
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
