package sremind.torymo.by;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.data.SReminderContract.SeriesEntry;
import sremind.torymo.by.service.EpisodesService;

public class WatchlistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int WATCHLIST_LOADER = 1;
    WatchlistAdapter mWatchlistAdapter;
	private static final int CM_DELETE_SERIES = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.watchlist_fragment, container, false);

		ListView watchlistListView = (ListView) rootView.findViewById(R.id.watchlistListView);

		mWatchlistAdapter = new WatchlistAdapter(getActivity(), null, 0);
		watchlistListView.setAdapter(mWatchlistAdapter);
		registerForContextMenu(watchlistListView);

		watchlistListView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        		CheckBox item = (CheckBox) v.findViewById(R.id.watchlistCheckBox);
        		item.performClick();
        		boolean checked = item.isChecked();

				Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        		final String imdbId = cursor.getString(SReminderContract.COL_SERIES_IMDB_ID);
				changeWatchlist(imdbId, checked);
        	}
		});

		return rootView;
	}
	
	public static void addEpisodes(final Context context, final String imdbId){
		Intent intent = new Intent(context, EpisodesService.class);
		intent.putExtra(EpisodesService.EPISODES_QUERY_EXTRA, imdbId);
		context.startService(intent);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.watchlist_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_search:
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder = SReminderContract.SeriesEntry.COLUMN_NAME + " ASC";
		Uri seriesList = SReminderContract.SeriesEntry.CONTENT_URI;

		return new CursorLoader(getActivity(),
				seriesList,
				SReminderContract.SERIES_COLUMNS,
				null,
				null,
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mWatchlistAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mWatchlistAdapter.swapCursor(null);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		getLoaderManager().initLoader(WATCHLIST_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	private void changeWatchlist(String imdbId, boolean watchlist){
		ContentValues cv = new ContentValues();
		cv.put(SeriesEntry.COLUMN_WATCHLIST, Utility.getBooleanForDB(watchlist) );
		getActivity().getContentResolver().update(
				SeriesEntry.CONTENT_URI,
				cv,
				SeriesEntry.COLUMN_IMDB_ID + " = ?",
				new String[]{imdbId});

		if(watchlist){
			addEpisodes(getActivity(), imdbId);
		}else{
			getActivity().getContentResolver().delete(
					SReminderContract.EpisodeEntry.CONTENT_URI,
					SReminderContract.EpisodeEntry.COLUMN_SERIES_ID + " = ?",
					new String[]{imdbId});
			Toast.makeText(getActivity(), R.string.episodes_deleted, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_SERIES, 0, R.string.DELETE);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case CM_DELETE_SERIES://удаляем  запись
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

				Cursor cursor = (Cursor) mWatchlistAdapter.getItem(info.position);
				final String imdbId = cursor.getString(SReminderContract.COL_SERIES_IMDB_ID);
				getActivity().getContentResolver().delete(SeriesEntry.CONTENT_URI,
						SeriesEntry.COLUMN_IMDB_ID + " = ?",
						new String[]{imdbId});
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
}
