package sremind.torymo.by;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

static final int FIRST_DAY_OF_WEEK =1; // Sunday = 0, Monday = 1
	
	
	private Context mContext;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;
    private SReminderDatabase database;
    ArrayList<Date> datesOfEpisodes;
    
    public CalendarAdapter(Context c, Calendar monthCalendar) {
    	database = new SReminderDatabase(c);
    	datesOfEpisodes = new ArrayList<Date>();
    	month = monthCalendar;
    	selectedDate = (Calendar)monthCalendar.clone();
    	mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        refreshDays();
    }
    
    public void setItems(ArrayList<String> items) {
    	for(int i = 0;i != items.size();i++){
    		if(items.get(i).length()==1) {
    		items.set(i, "0" + items.get(i));
    		}
    	}
    	this.items = items;
    }
    

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
    	TextView dayView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.day, null);
        	
        }
        dayView = (TextView)v.findViewById(R.id.date);
        dayView.setTextColor(mContext.getResources().getColor(R.color.font));
        
        // disable empty days from the beginning
        if(days[position].equals("")) {
        	dayView.setClickable(false);
        	dayView.setFocusable(false);
        }
        else {
        	// mark current day as focused
        	if(days[position].event){
        		v.setBackgroundColor(mContext.getResources().getColor(R.color.film_day));
        		dayView.setTextColor(mContext.getResources().getColor(R.color.ordinary_day));
        		/*dayView.setOnClickListener(new OnClickListener() {  
        	        public void onClick(View v)
        	            {
        	                //perform action
        	            }
        	         });*/
        	}
        	else if(month.get(Calendar.YEAR)== selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)== selectedDate.get(Calendar.MONTH) && days[position].day.equals(""+selectedDate.get(Calendar.DAY_OF_MONTH))) {
        		v.setBackgroundColor(mContext.getResources().getColor(R.color.current_day));
        		//v.setBackgroundResource(R.drawable.item_background_focused);
        	}
        	else {
        		v.setBackgroundColor(mContext.getResources().getColor(R.color.ordinary_day));
        		//v.setBackgroundResource(R.drawable.list_item_background);
        	}
        	
        }
        dayView.setText(days[position].day);
        
        // create date string for comparison
        String date = days[position].day;
    	
        if(date.length()==1) {
    		date = "0"+date;
    	}
    	String monthStr = ""+(month.get(Calendar.MONTH)+1);
    	if(monthStr.length()==1) {
    		monthStr = "0"+monthStr;
    	}
        return v;
    }
    
    public void refreshDays()
    {
    	// clear items
    	items.clear();
    	int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = (int)month.get(Calendar.DAY_OF_WEEK);
        int year = month.get(Calendar.YEAR)-1900;
        int curr_month = month.get(Calendar.MONTH);
        datesOfEpisodes = database.getDatesInPeriod(new Date(year, curr_month,1), new Date(year, curr_month,lastDay), MainActivity.onlySeen);
        int cnt = datesOfEpisodes.size();
        // figure size of the array
        if(firstDay==1){
        	days = new CalDay[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
        	days = new CalDay[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }
        
        int j=FIRST_DAY_OF_WEEK;
        for(int i=j-1;i<days.length;i++) {
        	days[i] = new CalDay();
        }
        // populate empty days before first real day
        if(firstDay>1) {
	        for(j=0;j<firstDay-FIRST_DAY_OF_WEEK;j++) {
	        	days[j].day = "";
	        }
        }
	    else {
	    	for(j=0;j<FIRST_DAY_OF_WEEK*6;j++) {
	        	days[j].day = "";
	        }
	    	j=FIRST_DAY_OF_WEEK*6+1; // sunday => 1, monday => 7
	    }
        
        // populate days
        int dayNumber = 1;
        for(int i=j-1;i<days.length;i++) {
        	days[i].day = ""+dayNumber;
        	days[i].event = false;
        	if(datesOfEpisodes.size()>0){
        		cnt = datesOfEpisodes.size();
        		for(int k = 0; k<cnt; k++){
        			if(datesOfEpisodes.get(k).getDate()==dayNumber){
        				days[i].event = true;
        				datesOfEpisodes.remove(k);
        				break;
        			}
        		}
        	}
        	dayNumber++;
        }
    }

    // references to our items
    public CalDay[] days;
    
    class CalDay{
    	String day;
    	boolean event;
    }

}
