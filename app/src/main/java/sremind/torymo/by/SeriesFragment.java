package sremind.torymo.by;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	ListView mListView;

	public interface Callback{
		void onItemSelected(String imdbId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.series_fragment, container, false);


		mSeriesAdapter = new SeriesAdapter(getActivity(), new ArrayList<Series>());
		mListView = rootView.findViewById(R.id.lvSeries);
		mListView.setAdapter(mSeriesAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Series s = (Series) adapterView.getItemAtPosition(position);
				if (s != null) {
					((Callback)getActivity())
							.onItemSelected(s.getImdbId());
				}
				mPosition = position;
			}
		});

		LiveData<List<Series>> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getWatchlist();
		series.observe(this, new Observer<List<Series>>() {
			@Override
			public void onChanged(@Nullable List<Series> series) {
				if(!mSeriesAdapter.isEmpty())
					mSeriesAdapter.clear();
				mSeriesAdapter.addAll(series);
				mSeriesAdapter.notifyDataSetChanged();
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
		LiveData<List<Series>> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getWatchlist();
		series.observe(this, new Observer<List<Series>>() {
			@Override
			public void onChanged(@Nullable List<Series> series) {
				if(!mSeriesAdapter.isEmpty())
					mSeriesAdapter.clear();
				mSeriesAdapter.addAll(series);
				mSeriesAdapter.notifyDataSetChanged();
			}
		});
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
	public void onSaveInstanceState(Bundle outState) {
		if(mPosition != ListView.INVALID_POSITION){
			outState.putInt(SELECTED_KEY, mPosition);
		}
		super.onSaveInstanceState(outState);
	}
}
