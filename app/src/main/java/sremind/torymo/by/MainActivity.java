package sremind.torymo.by;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import sremind.torymo.by.SReminderDatabase.Episode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	final int SHOW_EPISODES = 1;
	final int ADD_EPISODE_DLG = 2;
	private static final String EP_NAME = "epname";
    private static final String EP_DATE = "epdate";
    private static final String EP_SER = "epser";
    private static final String PREF_SEEN = "pref_seen";
	
    private ArrayList <HashMap<String, Object>> myEpisodes;
    public static boolean onlySeen = false;
    Calendar month;
	Handler handler;
	CalendarAdapter adapter;
	public ArrayList<String> items;
	SReminderDatabase database;
	Date selectedDate;
	  
	GridView gvMain;
	ArrayAdapter<String> daysAdapter;
	ArrayList<Episode> episodesList;
	SimpleAdapter sAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		database = new SReminderDatabase(this);
		
		
		daysAdapter = new ArrayAdapter<String>(this, R.layout.day_name, R.id.name_day, getResources().getStringArray(R.array.weekDays));
        gvMain = (GridView) findViewById(R.id.gvDays);
        gvMain.setAdapter(daysAdapter);
        episodesList = new ArrayList<Episode>();
        myEpisodes = new ArrayList<HashMap<String,Object>>();
        
		month = Calendar.getInstance();
		TextView title  = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMM yyyy", month));
		   
		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month);
		    
		GridView gridview = (GridView) findViewById(R.id.gvCalendar);
		gridview.setAdapter(adapter);
		    
		handler = new Handler();
		handler.post(calendarUpdater);
		    
		    
		    
		TextView previous  = (TextView) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});
		    
		TextView next  = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {				
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
		/*gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long id) {
				TextView date = (TextView)v.findViewById(R.id.date);
			    if(date instanceof TextView && !date.getText().equals("")) {				    	
			        String day = date.getText().toString();
			        selectedDate = new Date(month.get(Calendar.YEAR),month.get(Calendar.MONTH),Integer.valueOf(day));
			        showDialog(ADD_EPISODE_DLG);
			    }
				return true;
			}
			
		});*/
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			    TextView date = (TextView)v.findViewById(R.id.date);
			    if(date instanceof TextView && !date.getText().equals("")) {				    	
			        String day = date.getText().toString();
			        selectedDate = new Date(month.get(Calendar.YEAR)-1900,month.get(Calendar.MONTH),Integer.valueOf(day));
			        showDialog(SHOW_EPISODES);
			    }			        
		    }
		});
		
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		MainActivity.onlySeen = pref.getBoolean(PREF_SEEN, false);
		
	}
	
	private void changeSeenTitle(MenuItem miOnlySeen){
		
		int seenTitle = R.string.action_only_seen;
		if(MainActivity.onlySeen) seenTitle = R.string.action_all;
		miOnlySeen.setTitle(getResources().getString(seenTitle));
	}

	@Override
	protected void onStart() {
		refreshCalendar();
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem miOnlySeen = menu.findItem(R.id.action_only_seen);
		changeSeenTitle(miOnlySeen);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_series:
				Intent seriesActivty = new Intent(getBaseContext(),SeriesActivity.class);
	        	startActivity(seriesActivty);
				return true;
			case R.id.action_update_episodes:
				ArrayList<SRSeries> series = database.getWatchListSeries();
				int cnt = series.size();
				SRSeries tmp;
				for(int i = 0; i<cnt; i++){
					tmp = series.get(i);
					//database.deleteEpisodesForSeries(tmp.ImdbId());
					SeriesForChoosing.addEpisodes(getBaseContext(), database, tmp.ImdbId());
				}
				Toast.makeText(getBaseContext(), "Series list is updated", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.action_only_seen:
				MainActivity.onlySeen = !MainActivity.onlySeen;
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putBoolean(PREF_SEEN, MainActivity.onlySeen);
				editor.commit();
				refreshCalendar();
				changeSeenTitle(item);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
	
	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		
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
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == SHOW_EPISODES){
			AlertDialog aDialog = (AlertDialog) dialog;
		    ListAdapter lAdapter = aDialog.getListView().getAdapter();
		 
			HashMap<String, Object> hm;
			episodesList = database.getEpisodesForDate(selectedDate, MainActivity.onlySeen);
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
		if(id == ADD_EPISODE_DLG){			
			ArrayList<String> seriesList = database.getAllSeriesList();
			ArrayAdapter<String> seriesAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, seriesList);
			seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        Spinner spinner = (Spinner) dialog.getWindow().findViewById(R.id.mySpinner);
	        spinner.setAdapter(seriesAdapter);
	        spinner.setPrompt("Series");		
		}
		
	};
	
	@Override
	  protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if(id == SHOW_EPISODES){
			
			sAdapter = new SimpleAdapter(this, 
                    myEpisodes, 
                    R.layout.episodes, new String[]{ // ������ ��������
                    EP_NAME, 
                    EP_SER        //������ �����
                    }, new int[]{    //������ ����
                    R.id.tvName,      //��� id TextBox'a � list.xml
                    R.id.tvDate});    //������ �� ������ ������������ �����
           // ����������� ������� ������
           adb.setAdapter(sAdapter, null);
           
		    
		}
		if(id == ADD_EPISODE_DLG){
			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.add_episode_dlg1, null);
			adb.setView(view);
			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Window window = ((AlertDialog) dialog).getWindow();
					
					String episodeName = ((EditText)window.findViewById(R.id.myEpisode)).getText().toString();
					String seriesName = ((Spinner)window.findViewById(R.id.mySpinner)).getSelectedItem().toString();
					//TODO: add episode
					//database.addEpisode(seriesName, episodeName, selectedDate);
					refreshCalendar();
				}
			};
			adb.setPositiveButton("Ok", onClickListener);
		}
		return adb.create();
	  }
	
	protected void onStop(){
		database.close();
        super.onStop();
        //Log.e("Activity", "Stop");
    }

}
