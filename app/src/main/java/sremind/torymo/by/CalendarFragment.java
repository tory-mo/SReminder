package sremind.torymo.by;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sremind.torymo.by.adapters.EpisodesForDateAdapter;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.response.EpisodesResponseResult;
import sremind.torymo.by.response.MdbEpisodesResponse;
import sremind.torymo.by.response.SeriesResponseResult;


public class CalendarFragment extends Fragment{

    private static final String EP_NAME = "epname";
    private static final String EP_DATE = "epdate";
    private static final String EP_SER = "epser";


    private static final String BACKUP_FILE = "sreminder.backup";
    private static final String BACKUP_EPISODE = "-|-episode_list_item-|-";
    private static final String BACKUP_SEPAR = "-|-";

    Calendar month;

    RecyclerView lvEpisodesForDay;
    TextView tvToday;
    EpisodesForDateAdapter episodesForDateAdapter;
    CalendarView cvCalendar;
    ProgressBar pbSeriesUpdateProgress;
    TextView tvProgress;
    LinearLayout llUpdateProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment, container, false);


        month = Calendar.getInstance();

        llUpdateProgress = rootView.findViewById(R.id.llUpdateProgress);
        tvProgress = rootView.findViewById(R.id.tvProgress);
        pbSeriesUpdateProgress = rootView.findViewById(R.id.pbSeriesUpdateProgress);
        lvEpisodesForDay = rootView.findViewById(R.id.lvEpisodesForDay);
        lvEpisodesForDay.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        tvToday = rootView.findViewById(R.id.tvToday);

        episodesForDateAdapter = new EpisodesForDateAdapter(getActivity(),  new ArrayList<String[]>());

        lvEpisodesForDay.setAdapter(episodesForDateAdapter);


        cvCalendar = (rootView.findViewById(R.id.calendar_view));
        // assign event handler
        cvCalendar.setEventHandler(new CalendarView.EventHandler()
        {
            @Override
            public void onDayPress(Date date) {
                showEpisodesForDay(date);
            }

            @Override
            public void onMonthChanged(Date startDate, Date endDate) {
                getEpisodesForMonth(startDate, endDate);
            }
        });
        cvCalendar.updateCalendar();

        Date[] startEnd = cvCalendar.getCurrentMonthStartEnd();
        getEpisodesForMonth(startEnd[0], startEnd[1]);

        return rootView;
    }

    private void getEpisodesForMonth(Date startDate, Date endDate){
        final LifecycleOwner lifecycleOwner = this;
        LiveData<List<Episode>> episodes;
        if(Utility.getSeenParam(getActivity())){
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getNotSeenEpisodesBetweenDates(startDate.getTime(), endDate.getTime());
        }else {
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBetweenDates(startDate.getTime(), endDate.getTime());
        }
        episodes.observe(lifecycleOwner, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {
                HashSet<Date> events = new HashSet<>();
                for (Episode ep: episodes){
                    events.add(new Date(ep.getDate()));
                }
                cvCalendar.updateCalendar(events);
            }
        });
    }

    private void changeSeenTitle(MenuItem miOnlySeen){
        int seenTitle = R.string.action_only_seen;
        if(Utility.getSeenParam(getActivity())) seenTitle = R.string.action_all;
        miOnlySeen.setTitle(getResources().getString(seenTitle));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        MenuItem miOnlySeen = menu.findItem(R.id.action_only_seen);
        changeSeenTitle(miOnlySeen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_update_episodes:
                episodesForDateAdapter.clearItems();
                LiveData<List<Series>> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getWatchlist();
                series.observe(this, new Observer<List<Series>>() {

                    @Override
                    public void onChanged(@Nullable final List<Series> series) {
                        if(series == null) return;
                        final int seriesSize = series.size();
                        llUpdateProgress.setVisibility(View.VISIBLE);
                        pbSeriesUpdateProgress.setProgress(0);
                        pbSeriesUpdateProgress.setMax(seriesSize);
                        final Context ctx = getActivity();
                        final Counter activeRequests = new Counter(0);
                        final Counter requests = new Counter(0);
                        new Thread() {
                            public void run() {

                                if(!series.isEmpty()){
                                    for (Series s: series) {

                                        final HashMap<String, String> params = new HashMap<>();
                                        String currLanguage = Locale.getDefault().getLanguage();
                                        String needLang = Utility.LANGUAGE_EN;
                                        if(!currLanguage.equals(needLang)){
                                            needLang = currLanguage + "-" + Utility.LANGUAGE_EN;
                                        }

                                        params.put(Utility.LANGUAGE_PARAM, needLang);
                                        params.put(Utility.APPEND_TO_RESPONSE, Utility.EXTERNAL_IDS_PARAM);
                                        try {
                                            //timeouts to avoid tmd API limit of 40 requests per second
                                            TimeUnit.MILLISECONDS.sleep(500);

                                                SRemindApp.getMdbService().getSeriesDetails(s.getMdbId(), params).enqueue(new Callback<SeriesResponseResult>() {
                                                    @Override
                                                    public void onResponse(Call<SeriesResponseResult> call, Response<SeriesResponseResult> response) {
                                                        final SeriesResponseResult responseResult = response.body();
                                                        if (responseResult == null) return;
                                                        try {
                                                                TimeUnit.MILLISECONDS.sleep(500);

                                                                SRemindApp.getMdbService().getEpisodes(responseResult.getMdbId(), responseResult.getSeasons(), params).enqueue(new Callback<MdbEpisodesResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<MdbEpisodesResponse> call, Response<MdbEpisodesResponse> response) {
                                                                        if (response.code() != 200)
                                                                            Toast.makeText(getActivity(), "code: " + response.code(), Toast.LENGTH_LONG).show();
                                                                        MdbEpisodesResponse responseBody = response.body();
                                                                        if (responseBody == null)
                                                                            return;
                                                                        final List<EpisodesResponseResult> episodesResponse = responseBody.episodes;
                                                                        if (episodesResponse == null)
                                                                            return;


                                                                        for (int i = 0; i < episodesResponse.size(); i++) {
                                                                            EpisodesResponseResult res = episodesResponse.get(i);

                                                                            if (res.getDate() == null)
                                                                                continue;
                                                                            List<Episode> episodesDb = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesBySeriesAndNumber(responseResult.getExternalIds().imdb_id, res.getNumber(), res.getSeasonNumber());
                                                                            try {
                                                                                if (episodesDb != null && !episodesDb.isEmpty()) {
                                                                                    SReminderDatabase.getAppDatabase(getActivity()).episodeDao().update(episodesDb.get(0).getId(), res.getName(), res.getNumber(), res.getSeasonNumber(), res.getDate().getTime(), res.getPoster(), res.getOverview());
                                                                                } else {
                                                                                    Episode episode = new Episode(res.getName(), res.getDate().getTime(), responseResult.getExternalIds().imdb_id, res.getNumber(), res.getSeasonNumber(), res.getPoster(), res.getOverview());
                                                                                    SReminderDatabase.getAppDatabase(getActivity()).episodeDao().insert(episode);
                                                                                }
                                                                            } catch (Exception ex) {
                                                                                ex.printStackTrace();
                                                                            }

                                                                        }
                                                                        activeRequests.increase();

                                                                        ((Activity) ctx).runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                tvProgress.setText(getString(R.string.are_updated, episodesResponse.size(), activeRequests.get(), seriesSize));
                                                                                pbSeriesUpdateProgress.setProgress(activeRequests.get());
                                                                            }

                                                                        });

                                                                        Log.d("progress111", getString(R.string.are_updated, episodesResponse.size(), activeRequests.get(), seriesSize) + " - " + new Date().toString());
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<MdbEpisodesResponse> call, Throwable t) {
                                                                        Log.e(CalendarFragment.class.getName(), t.getMessage());
                                                                    }
                                                                });
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<SeriesResponseResult> call, Throwable t) {
                                                        Log.e(CalendarFragment.class.getName(), t.getMessage());
                                                    }
                                                });
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                });

                return true;
            case R.id.action_only_seen:
                episodesForDateAdapter.clearItems();
                Utility.changeSeenParam(getActivity());
                changeSeenTitle(item);
                Date[] startEnd = cvCalendar.getCurrentMonthStartEnd();
                getEpisodesForMonth(startEnd[0], startEnd[1]);
                return true;
//          case R.id.action_make_backup:
//                String separator = System.getProperty("line.separator");
//                    getActivity().getContentResolver().query(all)
//                ArrayList<SReminderDatabase.Episode> episodesList = database.getAllEpisodes();
//                File backupFile = new File(Environment.getExternalStorageDirectory()+File.separator+BACKUP_FILE);
//                try{
//                    if(!backupFile.exists())backupFile.createNewFile();
//                    FileOutputStream sw = new FileOutputStream(backupFile);
//                    String line = "";
//                    for(int i = 0; i<seriesList.size(); i++){
//                        if(seriesList.get(i).WatchList()) {
//                            line = seriesList.get(i).ImdbId() + BACKUP_SEPAR + seriesList.get(i).Name() + BACKUP_SEPAR + seriesList.get(i).WatchList() + separator;
//                            sw.write(line.getBytes());
//                        }
//                    }
//
//                    sw.write(BACKUP_EPISODE.getBytes());
//                    line = "";
//                    String date = "";
//                    for(int i = 0; i<episodesList.size(); i++){
//                        date = episodesList.get(i).date==null?"":episodesList.get(i).date.toString();
//                        line = episodesList.get(i).episodeNumber+BACKUP_SEPAR+episodesList.get(i).episodeName+BACKUP_SEPAR+episodesList.get(i).seriesId
//                                +BACKUP_SEPAR+episodesList.get(i).seen+BACKUP_SEPAR+date+separator;
//                        sw.write(line.getBytes());
//
//                    }
//
//                    sw.close();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return true;
//            case R.id.action_restore:
//                File backupFile1 = new File(Environment.getExternalStorageDirectory()+File.separator+BACKUP_FILE);
//                try{
//                    if(!backupFile1.exists())backupFile1.createNewFile();
//                    FileReader fileReader = new FileReader(backupFile1);
//                    BufferedReader bufferedReader = new BufferedReader(fileReader);
//                    StringBuffer stringBuffer = new StringBuffer();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null || !line.equals(BACKUP_EPISODE)) {
//                        String[] ss = line.split(BACKUP_SEPAR);
//                        database.addSeries(ss[1],ss[0],Boolean.valueOf(ss[2]));
//                    }
//                    while ((line = bufferedReader.readLine()) != null) {
//                        String[] ss = line.split(BACKUP_SEPAR);
//
//                        database.addEpisode(ss[2],ss[1], ss[4]==""?null:SReminderDatabase.dateFormat.parse(ss[4]), ss[0],Boolean.valueOf(ss[3]));
//                    }
//                    fileReader.close();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showEpisodesForDay(Date touchedDate){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if(today.getTime().compareTo(touchedDate) == 0){
            tvToday.setText(R.string.today);
        }else{
            tvToday.setText(Utility.dateToStrFormat.format(touchedDate));
        }

        List<Episode> episodes;
        if(Utility.getSeenParam(getActivity())){
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getNotSeenEpisodesForDate(touchedDate.getTime());
        }else {
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesForDate(touchedDate.getTime());
        }
        episodesForDateAdapter.clearItems();
        if(episodes != null && !episodes.isEmpty()){
            for (final Episode ep: episodes) {
                Series series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getSeriesByImdbId(ep.getSeries());
                String[] hm;
                hm = new String[5];
                hm[0] = String.valueOf(ep.getNumber());
                hm[1] = ep.getName();
                hm[2] = String.valueOf(ep.isSeen());
                hm[3] = series.getName();
                hm[4] = String.valueOf(ep.getSeasonNumber());
                episodesForDateAdapter.addItem(hm);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        llUpdateProgress.setVisibility(View.GONE);
    }

    private class Counter{
        private int i = 0;

        Counter(int i){
            this.i = i;
        }

        void set(int i){
            this.i = i;
        }

        int get(){
            return i;
        }

        void increase(){
            i++;
        }

        void decrease(){
            i--;
        }
    }
}
