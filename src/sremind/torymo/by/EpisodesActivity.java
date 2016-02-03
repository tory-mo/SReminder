package sremind.torymo.by;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import sremind.torymo.by.SReminderDatabase.Episode;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EpisodesActivity extends Activity {
	
	private static final int CM_DELETE_EPISODE = 1;
	
	String seriesName;
	SReminderDatabase database;
	ListView lvEpisodes;
	ArrayList<Episode> episodesList;
	
	private ArrayList <HashMap<String, Object>> myEpisodes; //наш массив объектов, без него никак
    private static final String EP_NAME = "epname";    // Главное название, большими буквами
    private static final String EP_DATE = "epdate";  // Наименование, то что ниже главного
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_episodes);
		seriesName = getIntent().getStringExtra("name");
		this.setTitle(seriesName);
		database = new SReminderDatabase(this);
		lvEpisodes = (ListView)findViewById(R.id.lvEpisodes);
		myEpisodes = new ArrayList<HashMap<String,Object>>();
		registerForContextMenu(lvEpisodes);
		createListView();
	}
	
	public void createListView(){
		
		HashMap<String, Object> hm;
		episodesList = database.getEpisodesForSeries(seriesName);
		/*int cnt = episodesList.size();
		myEpisodes.clear();
		for(int i = 0; i<cnt; i++){
			hm = new HashMap<String, Object>();
	        hm.put(EP_NAME, episodesList.get(i).episodeName);  
	        Date date = episodesList.get(i).date;
	        if(date==null){
	        	hm.put(EP_DATE, episodesList.get(i).episodeNumber);
	        }else{
	        	hm.put(EP_DATE, episodesList.get(i).episodeNumber+"; "+episodesList.get(i).date.getDate()+"."+(episodesList.get(i).date.getMonth()+1)+"."+(episodesList.get(i).date.getYear()+1900)); 
	        }
	        
	        myEpisodes.add(hm);
		}*/
		
		try{        					    	        	
			/*SimpleAdapter sAdapter = new SimpleAdapter(this, 
                    myEpisodes, 
                    R.layout.episode_elem, new String[]{ // массив названий
                    EP_NAME,         //верхний текст
                    EP_DATE,        //нижний теккт
                    }, new int[]{    //массив форм
                    R.id.tvName,      //наш id TextBox'a в list.xml
                    R.id.tvDate});    //ссылка на объект отображающий текст*/
			final EpisodesAdapter sAdapter = new EpisodesAdapter(this, episodesList);
           // присваиваем адаптер списку
           lvEpisodes.setAdapter(sAdapter);
           lvEpisodes.setOnItemClickListener(new OnItemClickListener() {
        	   @Override
	        	public void onItemClick(AdapterView<?> lv, View v, int pos,
	        			long id) {
        		   ListView llv = (ListView)lv;
        		   //LinearLayout layout = (LinearLayout)llv.getChildAt(pos);
        		   	LinearLayout layout = (LinearLayout)v;
	   				TextView tvName = (TextView)layout.getChildAt(0);
	   				TextView tvInfo = (TextView)layout.getChildAt(1);
	   				Log.i("by.torymo.sremind.EpisodesAdapter","name="+seriesName);
	   				Episode ep = episodesList.get(pos);
	   				if(ep.seen){				
	   					/*layout.setBackgroundColor(getResources().getColor(R.color.current_day));
	   					tvName.setTextColor(getResources().getColor(R.color.font));
	   					tvInfo.setTextColor(getResources().getColor(R.color.font));*/
	   					//v.setBackgroundResource(R.layout.episodes);
	   					database.changeSeenStatus(ep.seriesId, ep.episodeName, false);
	   					
	   					
	   				}else{					
	   					/*layout.setBackgroundColor(getResources().getColor(R.color.film_day));
	   					tvName.setTextColor(getResources().getColor(R.color.ordinary_day));
	   					tvInfo.setTextColor(getResources().getColor(R.color.current_day));*/
	   					
	   					//v.setBackgroundResource(R.layout.episodes_seen);
	   					database.changeSeenStatus(ep.seriesId, ep.episodeName, true);
	   				}
	   				
	   				ep.seen = !ep.seen;
	   				sAdapter.itemSeen(pos, ep.seen);
	   				sAdapter.notifyDataSetChanged();
	   				//layout.invalidate();
	        		
	        	}
        	   
           });
           /*lvEpisodes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LinearLayout layout = (LinearLayout)v;
				TextView tvName = (TextView)layout.getChildAt(0);
				TextView tvInfo = (TextView)layout.getChildAt(1);
				Log.i("by.torymo.sremind.EpisodesAdapter","name="+seriesName);
				if(ep.seen){				
					layout.setBackgroundColor(getResources().getColor(R.color.current_day));
					tvName.setTextColor(getResources().getColor(R.color.font));
					tvInfo.setTextColor(getResources().getColor(R.color.font));
					database.changeSeenStatus(seriesName, tvName.toString(), false);
					
					
				}else{					
					layout.setBackgroundColor(getResources().getColor(R.color.film_day));
					tvName.setTextColor(getResources().getColor(R.color.ordinary_day));
					tvInfo.setTextColor(getResources().getColor(R.color.current_day));
					database.changeSeenStatus(seriesName, tvName.toString(), true);
				}
				ep.seen = !ep.seen;
				layout.invalidate();
				
			}
		});*/
		}catch(Exception e){
    		Log.e("", e.getMessage());
    	}
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, CM_DELETE_EPISODE, 0, "Delete");
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	// получаем инфу о пункте списка вызвавшем это меню
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		LinearLayout vv = (LinearLayout)acmi.targetView;
		TextView child1 = (TextView)vv.getChildAt(0);
		TextView child2 = (TextView)vv.getChildAt(1);
    	String str = child1.getText().toString();
    	String str1 = child2.getText().toString();
    	int ind = str1.indexOf(";")+2;
    	str1 = str1.substring(ind, str1.length());
    	String[] stringDate = str1.split("\\.");
    	Date date = new Date(Integer.parseInt(stringDate[2])-1900, Integer.parseInt(stringDate[1])-1, Integer.parseInt(stringDate[0]));
    	switch(item.getItemId()){
	    	case CM_DELETE_EPISODE://удаляем  запись	    		
	    		database.deleteEpisode(str, date);
	    		createListView();
	    		return true;
	    	default:
	    		return super.onContextItemSelected(item);
    	}      
    }

}
