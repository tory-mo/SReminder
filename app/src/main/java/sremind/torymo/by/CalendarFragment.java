package sremind.torymo.by;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by torymo on 14.02.2016.
 */
public class CalendarFragment extends Fragment{

    private static final String EP_NAME = "epname";
    private static final String EP_DATE = "epdate";
    private static final String EP_SER = "epser";
    private static final String PREF_SEEN = "pref_seen";

    private static final String BACKUP_FILE = "sreminder.backup";
    private static final String BACKUP_EPISODE = "-|-episodes-|-";
    private static final String BACKUP_SEPAR = "-|-";

    private ArrayList<HashMap<String, Object>> myEpisodes;
    public static boolean onlySeen = false;
    Calendar month;
    Handler handler;
    CalendarAdapter adapter;
    public ArrayList<String> items;
    SReminderDatabase database;
    Date selectedDate;

    GridView gvMain;
    ArrayAdapter<String> daysAdapter;
    ArrayList<SReminderDatabase.Episode> episodesList;
    SimpleAdapter sAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar, container, false);

        database = new SReminderDatabase(getActivity());
        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        CalendarFragment.onlySeen = pref.getBoolean(PREF_SEEN, false);

        daysAdapter = new ArrayAdapter<String>(getActivity(), R.layout.day_name, R.id.name_day, getResources().getStringArray(R.array.weekDays));
        gvMain = (GridView) rootView.findViewById(R.id.gvDays);
        gvMain.setAdapter(daysAdapter);
        episodesList = new ArrayList<SReminderDatabase.Episode>();
        myEpisodes = new ArrayList<HashMap<String,Object>>();

        month = Calendar.getInstance();
        TextView title  = (TextView) rootView.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMM yyyy", month));

        items = new ArrayList<String>();
        adapter = new CalendarAdapter(getActivity(), month);

        GridView gridview = (GridView) rootView.findViewById(R.id.gvCalendar);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);


        TextView previous  = (TextView) rootView.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
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

        TextView next  = (TextView) rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                }
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);
                if(date instanceof TextView && !date.getText().equals("")) {
                    String day = date.getText().toString();
                    selectedDate = new Date(month.get(Calendar.YEAR)-1900,month.get(Calendar.MONTH),Integer.valueOf(day));
                    showEpisodesForDay();
                }
            }
        });




        return rootView;
    }

    private void changeSeenTitle(MenuItem miOnlySeen){
        int seenTitle = R.string.action_only_seen;
        if(CalendarFragment.onlySeen) seenTitle = R.string.action_all;
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
                ArrayList<SRSeries> series = database.getWatchListSeries();
                int cnt = series.size();
                SRSeries tmp;
                for(int i = 0; i<cnt; i++){
                    tmp = series.get(i);
                    //database.deleteEpisodesForSeries(tmp.ImdbId());
                    WatchlistFragment.addEpisodes(getActivity(), database, tmp.ImdbId());
                }
                Toast.makeText(getActivity(), R.string.slist_updated, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_only_seen:
                CalendarFragment.onlySeen = !CalendarFragment.onlySeen;
                SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(PREF_SEEN, CalendarFragment.onlySeen);
                editor.commit();
                refreshCalendar();
                changeSeenTitle(item);
                return true;
            case R.id.action_make_backup:
                String separator = System.getProperty("line.separator");
                ArrayList<SRSeries> seriesList = database.getAllSeriesInfo();
                ArrayList<SReminderDatabase.Episode> episodesList = database.getAllEpisodes();
                File backupFile = new File(Environment.getExternalStorageDirectory()+File.separator+BACKUP_FILE);
                try{
                    if(!backupFile.exists())backupFile.createNewFile();
                    FileOutputStream sw = new FileOutputStream(backupFile);
                    String line = "";
                    for(int i = 0; i<seriesList.size(); i++){
                        if(seriesList.get(i).WatchList()) {
                            line = seriesList.get(i).ImdbId() + BACKUP_SEPAR + seriesList.get(i).Name() + BACKUP_SEPAR + seriesList.get(i).WatchList() + separator;
                            sw.write(line.getBytes());
                        }
                    }

                    sw.write(BACKUP_EPISODE.getBytes());
                    line = "";
                    String date = "";
                    for(int i = 0; i<episodesList.size(); i++){
                        date = episodesList.get(i).date==null?"":episodesList.get(i).date.toString();
                        line = episodesList.get(i).episodeNumber+BACKUP_SEPAR+episodesList.get(i).episodeName+BACKUP_SEPAR+episodesList.get(i).seriesId
                                +BACKUP_SEPAR+episodesList.get(i).seen+BACKUP_SEPAR+date+separator;
                        sw.write(line.getBytes());

                    }

                    sw.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                return true;
            case R.id.action_restore:
                File backupFile1 = new File(Environment.getExternalStorageDirectory()+File.separator+BACKUP_FILE);
                try{
                    if(!backupFile1.exists())backupFile1.createNewFile();
                    FileReader fileReader = new FileReader(backupFile1);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null || !line.equals(BACKUP_EPISODE)) {
                        String[] ss = line.split(BACKUP_SEPAR);
                        database.addSeries(ss[1],ss[0],Boolean.valueOf(ss[2]));
                    }
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] ss = line.split(BACKUP_SEPAR);

                        database.addEpisode(ss[2],ss[1], ss[4]==""?null:SReminderDatabase.dateFormat.parse(ss[4]), ss[0],Boolean.valueOf(ss[3]));
                    }
                    fileReader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshCalendar()
    {
        TextView title  = (TextView) getActivity().findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some random calendar items

        title.setText(android.text.format.DateFormat.format("MMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            items.clear();
            // format random values. You can implement a dedicated class to provide real values
            for(int i=0;i<31;i++) {
                Random r = new Random();

                if(r.nextInt(10)>6)
                {
                    items.add(Integer.toString(i));
                }
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };


    private void showEpisodesForDay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        sAdapter = new SimpleAdapter(getActivity(),
                myEpisodes,
                R.layout.episodes, new String[]{ // ������ ��������
                EP_NAME,
                EP_SER        //������ �����
        }, new int[]{    //������ ����
                R.id.tvName,      //��� id TextBox'a � list.xml
                R.id.tvDate});    //������ �� ������ ������������ �����
        // ����������� ������� ������
        builder.setAdapter(sAdapter, null);
        AlertDialog dialog = builder.create();
        dialog.show();

        ListAdapter lAdapter = dialog.getListView().getAdapter();

        HashMap<String, Object> hm;
        episodesList = database.getEpisodesForDate(selectedDate, CalendarFragment.onlySeen);
        int cnt = episodesList.size();
        myEpisodes.clear();
        for(int i = 0; i<cnt; i++){
            hm = new HashMap<String, Object>();
            hm.put(EP_NAME, episodesList.get(i).episodeName);
            hm.put(EP_SER, episodesList.get(i).episodeNumber+"; "+episodesList.get(i).seriesName);
            myEpisodes.add(hm);
        }
        if (lAdapter instanceof SimpleAdapter) {
            // �������������� � ����� ������-����������� � ����� ������
            SimpleAdapter bAdapter = (SimpleAdapter) lAdapter;
            bAdapter.notifyDataSetChanged();
        }
    }
}
