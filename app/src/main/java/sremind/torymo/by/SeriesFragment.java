package sremind.torymo.by;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesFragment extends Fragment {

	private static final int CM_ADD_EPISODE = 1;
	private static final int CM_DELETE_SERIES = 2;
	final int THIS_ACTIVITY_CODE = 456;
	
	String serialName;
	SReminderDatabase database;
	ListView lvSeries;
	List<String> seriesList;
	ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_series, container, false);

		database = new SReminderDatabase(getActivity());
		lvSeries = (ListView)rootView.findViewById(R.id.lvSeries);
		registerForContextMenu(lvSeries);
		createListView();
		return  rootView;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, CM_DELETE_SERIES, 0, R.string.DELETE);
    }

	public void createListView(){
		try{        		
			seriesList = database.getWatchList();    	
        	adapter = new ArrayAdapter<String>(getActivity(), /*android.R.layout.simple_list_item_1*/ R.layout.series_elem, seriesList);

           // присваиваем адаптер списку
           lvSeries.setAdapter(adapter);	
           
           lvSeries.setOnItemClickListener(new OnItemClickListener() {
 		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 		    	  String seriesName = ((TextView)view).getText().toString();
 		    	 Intent intent = new Intent(getActivity(), EpisodesActivity.class);
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

}
