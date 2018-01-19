package sremind.torymo.by;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import sremind.torymo.by.adapters.EpisodesForDateAdapter;
import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.service.EpisodesJsonRequest;


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment, container, false);


        month = Calendar.getInstance();

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
                LiveData<List<Series>> series = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getWatchlist();
                series.observe(this, new Observer<List<Series>>() {

                    @Override
                    public void onChanged(@Nullable List<Series> series) {
                        if(series != null && !series.isEmpty()){
                            for (Series s: series) {
                                EpisodesJsonRequest.getEpisodes(getTargetFragment(), getActivity(), s.getMdbId(), s.getImdbId());
                            }
                        }
                        Toast.makeText(getActivity(), R.string.slist_updated, Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            case R.id.action_only_seen:
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

        LiveData<List<Episode>> episodes;
        if(Utility.getSeenParam(getActivity())){
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getNotSeenEpisodesForDate(touchedDate.getTime());
        }else {
            episodes = SReminderDatabase.getAppDatabase(getActivity()).episodeDao().getEpisodesForDate(touchedDate.getTime());
        }
        final LifecycleOwner lifecycleOwner = this;

        episodes.observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(@Nullable List<Episode> episodes) {

                episodesForDateAdapter.clearItems();
                if(episodes != null && !episodes.isEmpty()){
                    for (final Episode ep: episodes) {
                        LiveData<Series> s = SReminderDatabase.getAppDatabase(getActivity()).seriesDao().getSeriesByImdbId(ep.getSeries());
                        s.observe(lifecycleOwner, new Observer<Series>() {
                            @Override
                            public void onChanged(@Nullable Series series) {
                                String[] hm;
                                hm = new String[4];
                                hm[0] = ep.getNumber();
                                hm[1] = ep.getName();
                                hm[2] = String.valueOf(ep.isSeen());
                                hm[3] = series.getName();
                                episodesForDateAdapter.addItem(hm);
                                episodesForDateAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
