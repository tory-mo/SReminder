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

    private LiveData<SearchResult> mObservableSearchResult;
    private LiveData<Series> mObservableSeries;

    public SearchDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Series> getSeries(String mdbId){
        mObservableSeries = SReminderDatabase.getAppDatabase(this.getApplication()).seriesDao().getSeriesByMdbId(mdbId);
        return mObservableSeries;
    }

    public LiveData<SearchResult> getSearchResult(String mdbId){
        mObservableSearchResult = SReminderDatabase.getAppDatabase(this.getApplication()).searchResultDao().getSeriesResultById(mdbId);
        return mObservableSearchResult;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;


        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SearchDetailViewModel(mApplication);
        }
    }
}
