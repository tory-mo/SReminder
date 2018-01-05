package sremind.torymo.by;


import android.support.v4.app.Fragment;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
    RecyclerView mListView;

    static final String EPISODE_LIST_URI = "EPISODES_URI";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String imdbId = arguments.getString(EpisodeListFragment.EPISODE_LIST_URI);

        mEpisodeAdapter = new EpisodeListAdapter(getActivity(), new ArrayList<Episode>());

        View rootView = inflater.inflate(R.layout.episodes_fragment, container, false);

        mListView = rootView.findViewById(R.id.lvEpisodes);

        mListView.setAdapter(mEpisodeAdapter);

        mEpisodeAdapter.setOnItemClickListener(new EpisodeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(final Episode ep, int position) {
                int episodeId = ep.getId();
                boolean seen = !ep.isSeen(); //get inverted to current value
                SReminderDatabase.getAppDatabase(getActivity()).episodeDao().setSeen(episodeId, seen);
                refreshEpisodes(imdbId);
            }
        });

        refreshEpisodes(imdbId);

        return rootView;
    }

    public void refreshEpisodes(String imdbId){
        final LiveData<List<Episode>> episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeries(imdbId);
        episodes.observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {
                mEpisodeAdapter.setItems(episodes);
                mEpisodeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
