package sremind.torymo.by;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class WatchlistFragment extends Fragment {
	// List view
    private ListView lv;
     
    // Listview Adapter
    WatchlistAdapter adapter;
    SReminderDatabase db;
     
    // Search EditText
    EditText inputSearch;
     
     
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;
    public List<SRSeries> seriesL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_watchlist, container, false);
		db = new SReminderDatabase(getActivity());
        
         
        lv = (ListView) rootView.findViewById(R.id.list_view);
        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        seriesL = db.getAllSeriesInfo(); 
        // Adding items to listview
        adapter = new WatchlistAdapter(getActivity(), R.layout.watchlist_item, seriesL);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View v, int position,
        			long id) {
        		CheckBox item = (CheckBox) v.findViewById(R.id.cb1);
        		item.performClick();
        		boolean checked = item.isChecked();
        		SRSeries series = seriesL.get(position);
        		series.WatchList(checked);
        		final String imdbId = series.ImdbId();
        		db.changeWatchlistStatus(imdbId, checked);
        		if(checked){      			
        			addEpisodes(getActivity(), db, imdbId);
        			
        		}else{
        			db.deleteEpisodesForSeries(imdbId);
        			Toast.makeText(getActivity(), "Episodes are deleted", Toast.LENGTH_SHORT).show();
        		}
				getActivity().setResult(getActivity().RESULT_OK);
        	}
		});
        
        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {
             
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                WatchlistFragment.this.adapter.filter(cs.toString());
            }
             
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                          
            }
        });
		return rootView;
	}
	
	public static void addEpisodes(final Context context, final SReminderDatabase db, final String imdbId){
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("imdbId",imdbId);
	    ParseCloud.callFunctionInBackground("getEpisodes", params,new FunctionCallback<String>() {
	    	@Override
	    	public void done(String object, ParseException e) {
	    		if(e==null){
	    			if(object.length()>0){
	    				String [] episodesList = object.split(":::");
	    				 int ii = 0;
	    				for(int i = 0; i< episodesList.length; i++){
	    					String[] episode = episodesList[i].split("\\+;\\+");
	    					try{
	    						Date date = null;
	    						try{
	    							if(episode[1].contains("."))
	    								date = new SimpleDateFormat("dd MMM. yyyy", Locale.ENGLISH).parse(episode[1]);
	    							else
	    								date = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).parse(episode[1]);
	    						}catch(Exception ex){
	    							date = null;
	    						}	    							    						
		    					db.addEpisode(imdbId, episode[2], date, episode[0]);	
		    					ii++;
	    					}catch(Exception exception){
	    						Log.e("com.parse.push", "failed to parse date", exception);
	    					}	    					
	    				}
	    				Toast.makeText(context, ii+" of "+episodesList.length+" episodes are downloaded", Toast.LENGTH_SHORT).show();
	    			}
	    		}else{
	    			Log.e("com.parse.push", "failed to get Episodes", e);
	    		}        		    		
	    	}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.watchlist_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_update_series:
				//get all series
			    ParseQuery<ParseObject> query = ParseQuery.getQuery("Series");
			    query.findInBackground(new FindCallback<ParseObject>() {
					
					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						if(e==null){
							int cnt = objects.size();
							for(int i = 0; i<cnt; i++){
								
								db.addSeries(objects.get(i).getString(getResources().getString(R.string.NAME)), objects.get(i).getString("imdbId"));
							}
							seriesL.clear();
							seriesL.addAll(db.getAllSeriesInfo());
							adapter.setListData(seriesL);
							adapter.notifyDataSetChanged();
							Toast.makeText(getActivity(), R.string.slist_updated, Toast.LENGTH_SHORT).show();
							Log.d("com.parse.push", cnt + "objects founded");
						}else{
							Log.e("com.parse.push", "failed to get series", e);
						}				
					}
				});
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}

}
