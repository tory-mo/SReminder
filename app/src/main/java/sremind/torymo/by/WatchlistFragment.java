package sremind.torymo.by;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.List;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.service.EpisodesService;

public class WatchlistFragment extends Fragment{

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

		ListView watchlistListView = rootView.findViewById(R.id.watchlistListView);

		List<Series> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getAll();

		mWatchlistAdapter = new WatchlistAdapter(getActivity(), series);
		watchlistListView.setAdapter(mWatchlistAdapter);
		registerForContextMenu(watchlistListView);

		watchlistListView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        		CheckBox item = v.findViewById(R.id.watchlistCheckBox);
        		item.performClick();
        		boolean checked = item.isChecked();

				Series s = (Series) adapterView.getItemAtPosition(position);
        		final String imdbId = s.getImdbId();
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
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void changeWatchlist(String imdbId, boolean watchlist){
		SReminderDatabase.getAppDatabase(getActivity()).seriesDao().setWatchlist(imdbId, watchlist);

		if(watchlist){
			addEpisodes(getActivity(), imdbId);
		}else{
			SReminderDatabase.getAppDatabase(getActivity()).episodeDao().delete(imdbId);
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

				Series series = mWatchlistAdapter.getItem(info.position);
				final String imdbId = series.getImdbId();
				SReminderDatabase.getAppDatabase(getActivity()).seriesDao().delete(imdbId);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
}
