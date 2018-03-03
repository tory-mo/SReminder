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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sremind.torymo.by.adapters.WatchlistAdapter;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.response.EpisodesResponseResult;
import sremind.torymo.by.response.MdbEpisodesResponse;
import sremind.torymo.by.response.SeriesResponseResult;
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
			final HashMap<String, String> params = new HashMap<>();
			String currLanguage = Locale.getDefault().getLanguage();
			String needLang = Utility.LANGUAGE_EN;
			if(!currLanguage.equals(needLang)){
				needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
			}

			params.put(Utility.LANGUAGE_PARAM, needLang);
			params.put(Utility.APPEND_TO_RESPONSE, Utility.EXTERNAL_IDS_PARAM);
			params.put(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

			SRemindApp.getMdbService().getSeriesDetails(mdbId, params).enqueue(new Callback<SeriesResponseResult>() {
				@Override
				public void onResponse(Call<SeriesResponseResult> call, Response<SeriesResponseResult> response) {
					final SeriesResponseResult responseResult = response.body();
					SRemindApp.getMdbService().getEpisodes(responseResult.getMdbId(), responseResult.getSeasons(), params).enqueue(new Callback<MdbEpisodesResponse>() {
						@Override
						public void onResponse(Call<MdbEpisodesResponse> call, Response<MdbEpisodesResponse> response) {
							MdbEpisodesResponse responseBody = response.body();
							if(responseBody == null || responseBody.episodes == null) {
								Toast.makeText(getActivity(), "No response", Toast.LENGTH_LONG).show();
								return;
							}
							List<EpisodesResponseResult> episodesResponse = responseBody.episodes;

							for(int i = 0; i<episodesResponse.size(); i++){
								EpisodesResponseResult res = episodesResponse.get(i);

								if(res.getDate() == null) continue;
								List<Episode> episodesDb = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeriesAndNumber(responseResult.getExternalIds().imdb_id, res.getNumber(), res.getSeasonNumber());
								try{
									if(episodesDb != null && !episodesDb.isEmpty()){
										SReminderDatabase.getAppDatabase(getActivity()).episodeDao().update(episodesDb.get(0).getId(),res.getName(), res.getNumber(), res.getSeasonNumber(), res.getDate().getTime());
									}else {
										Episode episode = new Episode(res.getName(), res.getDate().getTime(), responseResult.getExternalIds().imdb_id, res.getNumber(), res.getSeasonNumber());
										SReminderDatabase.getAppDatabase(getActivity()).episodeDao().insert(episode);
									}
								}catch(Exception ex){
									ex.printStackTrace();
								}
							}
							Toast.makeText(getActivity(), getString(R.string.are_updated_short, episodesResponse.size()), Toast.LENGTH_LONG).show();

						}
						@Override
						public void onFailure(Call<MdbEpisodesResponse> call, Throwable t) {
							Log.e(WatchlistFragment.class.getName(), t.getMessage());
						}
					});
				}

				@Override
				public void onFailure(Call<SeriesResponseResult> call, Throwable t) {
					Log.e(WatchlistFragment.class.getName(), t.getMessage());
				}
			});
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
