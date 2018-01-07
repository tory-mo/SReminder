package sremind.torymo.by.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;

public class WatchlistViewModel  extends AndroidViewModel {

    private final LiveData<List<Series>> mObservableSeries;

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        mObservableSeries = SReminderDatabase.getAppDatabase(application).seriesDao().getAll();
    }

    public LiveData<List<Series>> getSeries(){
        return mObservableSeries;
    }
}
