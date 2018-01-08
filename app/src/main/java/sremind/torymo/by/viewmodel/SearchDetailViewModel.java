package sremind.torymo.by.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.SearchResult;
import sremind.torymo.by.data.Series;

public class SearchDetailViewModel extends AndroidViewModel {

    private final LiveData<SearchResult> mObservableSearchResult;
    private final LiveData<Series> mObservableSeries;

    public SearchDetailViewModel(@NonNull Application application, String mdbId) {
        super(application);
        mObservableSearchResult = SReminderDatabase.getAppDatabase(application).searchResultDao().getSeriesResultById(mdbId);
        mObservableSeries = SReminderDatabase.getAppDatabase(application).seriesDao().getSeriesByMdbId(mdbId);
    }

    public LiveData<Series> getSeries(){
        return mObservableSeries;
    }

    public LiveData<SearchResult> getSearchResult(){
        return mObservableSearchResult;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mdbId;


        public Factory(@NonNull Application application, String mdbId) {
            mApplication = application;
            this.mdbId = mdbId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SearchDetailViewModel(mApplication, mdbId);
        }
    }
}
