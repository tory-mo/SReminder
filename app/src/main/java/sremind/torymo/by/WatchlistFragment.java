package sremind.torymo.by;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.adapters.WatchlistAdapter;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.service.EpisodesJsonRequest;
import sremind.torymo.by.viewmodel.WatchlistViewModel;

public class WatchlistFragment extends Fragment{

    WatchlistAdapter mWatchlistAdapter;
	public static final int CM_DELETE_SERIES = 2;


	RecyclerView watchlistListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
    
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.watchlist_fragment, container, false);

		watchlistListView = rootView.findViewById(R.id.watchlistListView);
		watchlistListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

		registerForContextMenu(watchlistListView);

		mWatchlistAdapter = new WatchlistAdapter(new ArrayList<Series>());
		watchlistListView.setAdapter(mWatchlistAdapter);

		mWatchlistAdapter.setOnItemClickListener(new WatchlistAdapter.OnItemClickListener() {
			@Override
			public void onItemClicked(View v, Series series) {
				CheckBox item = v.findViewById(R.id.watchlistCheckBox);
				item.performClick();
				boolean checked = item.isChecked();

				final String imdbId = series.getImdbId();
				changeWatchlist(imdbId, series.getMdbId(), checked);
			}

			@Override
			public void onMenuAction(MenuItem item, Series series) {
				switch(item.getItemId()){
					case CM_DELETE_SERIES://удаляем  запись
						final String imdbId = series.getImdbId();
						SReminderDatabase.getAppDatabase(getActivity()).seriesDao().deleteByImdbId(imdbId);
				}
			}
		});

		return rootView;
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

	private void changeWatchlist(String imdbId, String mdbId, boolean watchlist){
		SReminderDatabase.getAppDatabase(getActivity()).seriesDao().setWatchlist(imdbId, watchlist);
		if(watchlist){
			EpisodesJsonRequest.getEpisodes(this, getActivity(), mdbId, imdbId);
		}else{
			SReminderDatabase.getAppDatabase(getActivity()).episodeDao().delete(imdbId);
			Toast.makeText(getActivity(), R.string.episodes_deleted, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final WatchlistViewModel viewModel =
				ViewModelProviders.of(this).get(WatchlistViewModel.class);

		subscribeUi(viewModel);
	}

	private void subscribeUi(WatchlistViewModel viewModel) {
		viewModel.getSeries().observe(this, new Observer<List<Series>>() {
			@Override
			public void onChanged(@Nullable List<Series> series) {
				mWatchlistAdapter.setItems(series);
				mWatchlistAdapter.notifyDataSetChanged();
			}
		});
	}
}
