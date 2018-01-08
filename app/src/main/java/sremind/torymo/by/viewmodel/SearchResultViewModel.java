package sremind.torymo.by.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;

public class SearchResultViewModel extends AndroidViewModel {

    private final LiveData<List<SearchResult>> mObservableSearchResults;

    public SearchResultViewModel(@NonNull Application application) {
        super(application);
        mObservableSearchResults = SReminderDatabase.getAppDatabase(application).searchResultDao().getAll();
    }

    public LiveData<List<SearchResult>> getSearchResults(){
        return mObservableSearchResults;
    }
}
