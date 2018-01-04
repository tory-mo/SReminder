package sremind.torymo.by;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.adapters.SearchAdapter;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SearchFragment extends Fragment{

    SearchAdapter mSearchAdapter;
    static final String  SELECTED_KEY = "SELECTED_ITEM";
    private int mPosition = ListView.INVALID_POSITION;
    ListView mSearchListView;

    public interface Callback{
        void onItemSelected(String srId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        LiveData<List<SearchResult>> searchResults = SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().getAll();
        searchResults.observe(this, new Observer<List<SearchResult>>() {
            @Override
            public void onChanged(@Nullable List<SearchResult> searchResults) {
                if(!mSearchAdapter.isEmpty())
                    mSearchAdapter.clear();
                mSearchAdapter.addAll(searchResults);
                mSearchAdapter.notifyDataSetChanged();
            }
        });
        mSearchAdapter = new SearchAdapter(getActivity(), new ArrayList<SearchResult>());

        mSearchListView = rootView.findViewById(R.id.searchListView);
        mSearchListView.setAdapter(mSearchAdapter);

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SearchResult sr = (SearchResult) adapterView.getItemAtPosition(position);
                if (sr != null) {
                    ((Callback)getActivity())
                            .onItemSelected(sr.getSRId());
                }
                mPosition = position;

            }
        });

        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    public void refresh(){
        if(mSearchAdapter == null) return;

        LiveData<List<SearchResult>> searchResults = SReminderDatabase.getAppDatabase(getActivity()).searchResultDao().getAll();
        searchResults.observe(this, new Observer<List<SearchResult>>() {
            @Override
            public void onChanged(@Nullable List<SearchResult> searchResults) {
                if(!mSearchAdapter.isEmpty())
                    mSearchAdapter.clear();
                mSearchAdapter.addAll(searchResults);
                mSearchAdapter.notifyDataSetChanged();
            }
        });
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
