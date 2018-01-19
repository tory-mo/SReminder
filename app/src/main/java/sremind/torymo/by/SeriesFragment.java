package sremind.torymo.by;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.adapters.SeriesAdapter;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;

public class SeriesFragment extends Fragment{

	static final String  SELECTED_KEY = "SELECTED_ITEM";
	private int mPosition = ListView.INVALID_POSITION;
	SeriesAdapter mSeriesAdapter;
	RecyclerView mListView;

	public interface Callback{
		void onItemSelected(String imdbId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.series_fragment, container, false);
		mSeriesAdapter = new SeriesAdapter(getActivity(), new ArrayList<Series>());
		mListView = rootView.findViewById(R.id.lvSeries);
		mListView.setAdapter(mSeriesAdapter);
		mListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

		mSeriesAdapter.setOnItemClickListener(new SeriesAdapter.OnItemClickListener() {
			@Override
			public void onItemClicked(Series series, int position) {
				Activity activity = getActivity();
				if (series != null && activity != null) {
					((Callback)activity).onItemSelected(series.getImdbId());
				}
				mPosition = position;
			}
		});

		refreshSeries();

		if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
			mPosition = savedInstanceState.getInt(SELECTED_KEY);
		}

		return  rootView;
	}

	private void refreshSeries(){
		LiveData<List<Series>> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getWatchlist();
		series.observe(this, new Observer<List<Series>>() {
			@Override
			public void onChanged(@Nullable List<Series> series) {
				mSeriesAdapter.setItems(series);
				mSeriesAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshSeries();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		if(mPosition != ListView.INVALID_POSITION){
			outState.putInt(SELECTED_KEY, mPosition);
		}
		super.onSaveInstanceState(outState);
	}
}
