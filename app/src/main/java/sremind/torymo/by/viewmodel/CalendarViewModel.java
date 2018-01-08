package sremind.torymo.by.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;

public class CalendarViewModel extends AndroidViewModel {

    private LiveData<List<Episode>> mObservableEpisodes;
    private Application mApplication;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
    }

    public LiveData<List<Episode>> getEpisodes(long date1, long date2){
        mObservableEpisodes = SReminderDatabase.getAppDatabase(mApplication).episodeDao().getEpisodesBetweenDates(date1, date2);
        return mObservableEpisodes;
    }

    public LiveData<List<Episode>> getNotSeenEpisodes(long date1, long date2){
        mObservableEpisodes = SReminderDatabase.getAppDatabase(mApplication).episodeDao().getNotSeenEpisodesBetweenDates(date1, date2);
        return mObservableEpisodes;
    }
}
