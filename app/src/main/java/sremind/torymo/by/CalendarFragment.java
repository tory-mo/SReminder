package sremind.torymo.by;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.service.EpisodesService;

public class CalendarFragment extends Fragment{

    private static final String EP_NAME = "epname";
    private static final String EP_DATE = "epdate";
    private static final String EP_SER = "epser";


    private static final String BACKUP_FILE = "sreminder.backup";
    private static final String BACKUP_EPISODE = "-|-episode_list_item-|-";
    private static final String BACKUP_SEPAR = "-|-";

    private static final SimpleDateFormat mMonthTitleFormat = new SimpleDateFormat("MMMM yyyy");

    Calendar month;

    CalendarAdapter mCalendarAdapter;

    GridView daysTitlesGridView;
    TextView mMonthTitle;
    ArrayAdapter<String> daysAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment, container, false);
        mMonthTitle  = (TextView) rootView.findViewById(R.id.monthTitle);
        daysTitlesGridView = (GridView) rootView.findViewById(R.id.gvDays);
        GridView calendarGridView = (GridView) rootView.findViewById(R.id.gvCalendar);
        TextView previousMonth  = (TextView) rootView.findViewById(R.id.previousMonth);
        TextView nextMonth  = (TextView) rootView.findViewById(R.id.next);

        month = Calendar.getInstance();

        mCalendarAdapter = new CalendarAdapter(getActivity(), month);
        daysAdapter = new ArrayAdapter<>(getActivity(), R.layout.day_name, R.id.name_day, getResources().getStringArray(R.array.weekDays));

        calendarGridView.setAdapter(mCalendarAdapter);
        daysTitlesGridView.setAdapter(daysAdapter);

        refreshCalendar();

        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR) - 1), month.getActualMaximum(Calendar.MONTH), 1);
                } else {
                    month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
                }
                refreshCalendar();
            }
        });

        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                }
                refreshCalendar();
            }
        });

        return rootView;
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
                Cursor cursor = getActivity().getContentResolver().query(SReminderContract.SeriesEntry.buildSeriesWatchlist(),
                        SReminderContract.SERIES_COLUMNS,
                        null,
                        null,
                        null);
                if(cursor!=null) {
                    while (cursor.moveToNext()) {
                        Intent intent = new Intent(getActivity(), EpisodesService.class);
                        intent.putExtra(EpisodesService.EPISODES_QUERY_EXTRA, cursor.getString(SReminderContract.COL_SERIES_IMDB_ID));
                        getActivity().startService(intent);
                    }
                    cursor.close();
                }
                Toast.makeText(getActivity(), R.string.slist_updated, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_only_seen:
                Utility.changeSeenParam(getActivity());
                refreshCalendar();
                changeSeenTitle(item);
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

    public void refreshCalendar()
    {
        mCalendarAdapter.refreshDays();
        mCalendarAdapter.notifyDataSetChanged();
        mMonthTitle.setText(mMonthTitleFormat.format(month.getTime()));
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        mCalendarAdapter.showEpisodesForDay(today.getTime(), getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCalendar();
    }
}
