package sremind.torymo.by;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.adapters.SearchResultsAdapter;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.viewmodel.SearchResultViewModel;

public class SearchFragment extends Fragment{

    SearchResultsAdapter mSearchAdapter;
    static final String  SELECTED_KEY = "SELECTED_ITEM";
    private int mPosition = ListView.INVALID_POSITION;
    RecyclerView mSearchListView;

    public interface Callback{
        void onItemSelected(String srId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        mSearchAdapter = new SearchResultsAdapter(getActivity(), new ArrayList<SearchResult>());

        mSearchListView = rootView.findViewById(R.id.searchListView);
        mSearchListView.setAdapter(mSearchAdapter);

        mSearchAdapter.setOnItemClickListener(new SearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(SearchResult sr, int position) {
                Activity ac = getActivity();
                if (sr != null && ac != null) {
                    ((Callback)ac).onItemSelected(sr.getMdbId());
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SearchResultViewModel viewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);

        viewModel.getSearchResults().observe(this, new Observer<List<SearchResult>>() {
            @Override
            public void onChanged(@Nullable List<SearchResult> searchResults) {
                mSearchAdapter.setItems(searchResults);
                mSearchAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
