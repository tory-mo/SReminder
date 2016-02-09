package sremind.torymo.by;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesActivity extends Activity {
	
	final int ADD_SERIES_DLG = 1;
	final int ADD_EPISODE_DLG = 2;
	private static final int CM_ADD_EPISODE = 1;
	private static final int CM_DELETE_SERIES = 2;
	final int THIS_ACTIVITY_CODE = 456;
	
	String serialName;
	SReminderDatabase database;
	ListView lvSeries;
	List<String> seriesList;
	ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_series);
		database = new SReminderDatabase(this);
		lvSeries = (ListView)findViewById(R.id.lvSeries);
		registerForContextMenu(lvSeries);
		createListView();
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, CM_DELETE_SERIES, 0, R.string.DELETE);
    }
	
	@Override
	  protected Dialog onCreateDialog(int id) {
		if(id == ADD_SERIES_DLG){
			AlertDialog.Builder adb = new AlertDialog.Builder(this);

		    //Создадим текстовое поле для ввода
			final EditText editText = new EditText(getBaseContext());
			
		    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {					
					String name = editText.getText().toString();
					database.changeWatchlistStatus(name, true);
					if(seriesList!=null && adapter!=null){
						seriesList.clear();
			        	adapter.notifyDataSetChanged();	        	
			            createListView();  
			        };
				}
			};
			
		    adb.setTitle(R.string.add_series_title)
		    .setMessage(R.string.NAME)
			.setPositiveButton(R.string.ADD, onClickListener)
			.setView(editText);
		    
		    return adb.create();
		}
		if(id == ADD_EPISODE_DLG){
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			LinearLayout view = new LinearLayout(getBaseContext());
			
		    //Создадим текстовое поле для ввода
			final EditText editText = new EditText(getBaseContext());
			//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			//editText.setLayoutParams(params);
			editText.setSingleLine();
			view.setOrientation(LinearLayout.VERTICAL);
			final DatePicker dp = new DatePicker(getBaseContext());
			view.addView(editText);
			view.addView(dp);
		    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {					
					String name = editText.getText().toString();
					int year = dp.getYear();
					int month = dp.getMonth();
					int day = dp.getDayOfMonth();					
					Date date = new Date(year,month,day);
					/*if(database.addEpisode(serialName, name, date)){
						if(seriesList!=null && adapter!=null){
							seriesList.clear();
			        		adapter.notifyDataSetChanged();	        	
			            	createListView();  

			        	};
					}*/
				}
			};
			
		    adb.setTitle(R.string.add_episode_title)
		    .setMessage(R.string.NAME)
			.setPositiveButton(R.string.ADD, onClickListener)
			.setView(view);
			return adb.create();
		}
	    return null;
	  }
	public void createListView(){
		try{        		
			seriesList = database.getWatchList();    	
        	adapter = new ArrayAdapter<String>(this, /*android.R.layout.simple_list_item_1*/ R.layout.series_elem, seriesList);

           // присваиваем адаптер списку
           lvSeries.setAdapter(adapter);	
           
           lvSeries.setOnItemClickListener(new OnItemClickListener() {
 		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 		    	  String seriesName = ((TextView)view).getText().toString();
 		    	 Intent intent = new Intent(getBaseContext(), EpisodesActivity.class);
 		    	 intent.putExtra("name", seriesName);
 		    	 startActivity(intent);
 		      }
 		    });
 		
           //lvSeries.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}catch(Exception e){
    		Log.e("", e.getMessage());
    	}
		
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	// получаем инфу о пункте списка вызвавшем это меню
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
    	TextView vv = (TextView)acmi.targetView;
    	String str = vv.getText().toString();
    	switch(item.getItemId()){
	    	case CM_DELETE_SERIES://удаляем  запись	 
	    		String imdbId = database.seriesNameByImdbid(str);
	    		database.changeWatchlistStatus(imdbId, false);
	    		database.deleteEpisodesForSeries(imdbId);
	    		createListView();
	    		return true;
	    	case CM_ADD_EPISODE:
	    		serialName = str;
	    		showDialog(ADD_EPISODE_DLG);    		
	    		return true;
	    	default:
	    		return super.onContextItemSelected(item);
    	}      
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==this.THIS_ACTIVITY_CODE && resultCode== RESULT_OK){
			createListView();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
