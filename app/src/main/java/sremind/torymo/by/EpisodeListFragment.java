package sremind.torymo.by;


import android.support.v4.app.Fragment;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.adapters.EpisodeListAdapter;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;

public class EpisodeListFragment extends Fragment {

    EpisodeListAdapter mEpisodeAdapter;
    ListView mListView;

    static final String EPISODE_LIST_URI = "EPISODES_URI";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String imdbId = arguments.getString(EpisodeListFragment.EPISODE_LIST_URI);

        final LiveData<List<Episode>> episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeries(imdbId);
        episodes.observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> productEntity) {
                if(!mEpisodeAdapter.isEmpty())
                    mEpisodeAdapter.clear();
                mEpisodeAdapter.addAll(productEntity);
                mEpisodeAdapter.notifyDataSetChanged();
            }
        });
        mEpisodeAdapter = new EpisodeListAdapter(getActivity(), new ArrayList<Episode>());

        View rootView = inflater.inflate(R.layout.episodes_fragment, container, false);

        mListView = rootView.findViewById(R.id.lvEpisodes);

        mListView.setAdapter(mEpisodeAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	   @Override
	        	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                   Episode ep = (Episode) adapterView.getItemAtPosition(position);
                   int episodeId = ep.getId();
                   boolean seen = !ep.isSeen(); //get inverted to current value
                   SReminderDatabase.getAppDatabase(getActivity()).episodeDao().setSeen(episodeId, seen);
                   final LiveData<List<Episode>> episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeries(imdbId);
                   episodes.observe(getTargetFragment(), new Observer<List<Episode>>() {
                       @Override
                       public void onChanged(@Nullable List<Episode> episodes) {
                           if(!mEpisodeAdapter.isEmpty())
                               mEpisodeAdapter.clear();
                           mEpisodeAdapter.addAll(episodes);
                           mEpisodeAdapter.notifyDataSetChanged();
                       }
                   });

	        	}

        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
