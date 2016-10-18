package sremind.torymo.by;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.data.SReminderContract.EpisodeEntry;
import sremind.torymo.by.data.SReminderContract.SeriesEntry;

public class CalendarAdapter extends BaseAdapter {

	public static final String[] EPISODES_COLUMNS = {
			EpisodeEntry.TABLE_NAME + "." + EpisodeEntry._ID,
			EpisodeEntry.COLUMN_DATE,
			EpisodeEntry.TABLE_NAME + "." +EpisodeEntry.COLUMN_NAME,
			EpisodeEntry.COLUMN_NUMBER,
			SeriesEntry.TABLE_NAME + "." + SeriesEntry.COLUMN_NAME,
			EpisodeEntry.TABLE_NAME + "." + EpisodeEntry.COLUMN_SEEN
	};
	public static final int COL_EPISODE_ID = 0;
	public static final int COL_EPISODE_DATE = 1;
	public static final int COL_EPISODE_NAME = 2;
	public static final int COL_EPISODE_NUMBER = 3;
	public static final int COL_EPISODE_SERIES_ID = 4;
	public static final int COL_EPISODE_SEEN = 5;

	static final int FIRST_DAY_OF_WEEK = 1; // Sunday = 0, Monday = 1
	private static final String EP_NAME = "epname";
	private static final String EP_SER = "epser";

	private Context mContext;
    private Calendar mChosenMonth;

	// references to our items
	public CalDay[] mDaysOfMonth;

	class CalDay{
		String day;
		boolean event;
	}
    
    public CalendarAdapter(Context context, Calendar monthCalendar) {
		mContext = context;

		mChosenMonth = monthCalendar;
		mChosenMonth.set(Calendar.HOUR_OF_DAY, 0);
		mChosenMonth.set(Calendar.MINUTE, 0);
		mChosenMonth.set(Calendar.SECOND, 0);
		mChosenMonth.set(Calendar.MILLISECOND, 0);
		mDaysOfMonth = new CalDay[0];

		//refreshDays should be invoked manually after constructor
    }

    public int getCount() {
        return mDaysOfMonth.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	TextView dayView;
		CalDay currDay = mDaysOfMonth[position];

		convertView = LayoutInflater.from(mContext).inflate(R.layout.day, viewGroup, false);
        dayView = (TextView)convertView.findViewById(R.id.date);
        
        // disable empty days from the beginning
        if(currDay.day.equals("")) {
        	dayView.setClickable(false);
        	dayView.setFocusable(false);
        }
        else {
        	// mark current day as focused
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			mChosenMonth.set(Calendar.DATE, Integer.valueOf(currDay.day));
			GradientDrawable gd = new GradientDrawable();
        	if(mChosenMonth.compareTo(today) == 0 && currDay.event) {
				//gd.setStroke(3, mContext.getResources().getColor(R.color.accent));
				//gd.setColor(mContext.getResources().getColor(R.color.accent));
				dayView.setTextColor(mContext.getResources().getColor(R.color.light_bg));
				dayView.setBackgroundResource(R.drawable.today_episode_day);
        	}else if(mChosenMonth.compareTo(today) == 0) {
				//gd.setStroke(3, mContext.getResources().getColor(R.color.text_secondary));
				dayView.setBackgroundResource(R.drawable.today_day);
			}else if(currDay.event){
				//gd.setStroke(3, mContext.getResources().getColor(R.color.accent));
				dayView.setBackgroundResource(R.drawable.episode_day);
			}
			if(currDay.event){
				convertView.setOnClickListener(new View.OnClickListener() {
        	        public void onClick(View v)
					{
						TextView date = (TextView)v.findViewById(R.id.date);
						if(date != null) {
							String day = date.getText().toString();
							mChosenMonth.set(Calendar.DATE,Integer.valueOf(day));
							showEpisodesForDay(mChosenMonth.getTime(), ((Activity)mContext));
						}
					}
				});
        	}

			convertView.setBackgroundDrawable(gd);
        	
        }
        dayView.setText(currDay.day);

        return convertView;
    }
    
    public void refreshDays()
    {
		mChosenMonth.set(Calendar.DATE,1);
		Date startDate = mChosenMonth.getTime();
		int lastDay = mChosenMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = mChosenMonth.get(Calendar.DAY_OF_WEEK)-1-FIRST_DAY_OF_WEEK;//convert to start with zero
		if(firstDay<0)firstDay = 6;
		int lastWeekNum = mChosenMonth.getActualMaximum(Calendar.WEEK_OF_MONTH);

		mChosenMonth.set(Calendar.DATE,lastDay);
		Date endDate = mChosenMonth.getTime();

		mDaysOfMonth = new CalDay[lastWeekNum*7];
		for(int i = 0; i < mDaysOfMonth.length; i++) {
			mDaysOfMonth[i] = new CalDay();
			mDaysOfMonth[i].day = "";
		}

		Uri uri;
		if(Utility.getSeenParam(mContext)){
			uri = EpisodeEntry.buildEpisodesBetweenDatesUnseenUri(startDate, endDate);
		}else {
			uri = SReminderContract.EpisodeEntry.buildEpisodesBetweenDatesUri(startDate, endDate);
		}
		Cursor cursor = mContext.getContentResolver().query(
				uri,
				EPISODES_COLUMNS,
				null,
				null,
				null
		);

		ArrayList<Date> datesOfEpisodes = new ArrayList<>();
		if(cursor!=null) {
			while (cursor.moveToNext()) {
				long date = cursor.getLong(COL_EPISODE_DATE);
				datesOfEpisodes.add(Utility.getCalendarFromFormattedLong(date));
			}
			cursor.close();
		}


        int dayNumber = 1;
        for(int i = firstDay; i < (lastDay+firstDay); i++) {
			mDaysOfMonth[i].day = "" + dayNumber;
			mDaysOfMonth[i].event = false;
			for(int k = 0; k < datesOfEpisodes.size(); k++){
				if(datesOfEpisodes.get(k).getDate() == dayNumber){
					mDaysOfMonth[i].event = true;
					datesOfEpisodes.remove(k);
					break;
				}
			}
        	dayNumber++;
        }
    }

	public void showEpisodesForDay(Date touchedDate, Activity activity){
		ArrayList<String[]> episodesForDateList = new ArrayList<>();
		EpisodesForDateAdapter episodesForDateAdapter = new EpisodesForDateAdapter(mContext, episodesForDateList);
//		SimpleAdapter episodesForDayAdapter = new SimpleAdapter(mContext,
//				episodesForDateList,
//				R.layout.episode_list_item,
//				new String[]{EP_NAME,EP_SER	},
//				new int[]{R.id.tvName, R.id.tvDate}
//		);

		ListView lv = (ListView)activity.findViewById(R.id.lvEpisodesForDay);
		if (lv == null) return;

		TextView tvChosenDate = (TextView)activity.findViewById(R.id.tvToday);
		if(tvChosenDate != null) {
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			if(today.getTime().compareTo(touchedDate) == 0){
				tvChosenDate.setText(R.string.today);
			}else{
				tvChosenDate.setText(Utility.dateToStrFormat.format(touchedDate));
			}
		}
		lv.setAdapter(episodesForDateAdapter);
		//HashMap<String, Object> hm;
		String[] hm;
		Uri uri;

		if(Utility.getSeenParam(mContext)){
			uri = EpisodeEntry.buildEpisodesForDateUnseenUri(touchedDate);
		}else {
			uri = EpisodeEntry.buildEpisodesForDateUri(touchedDate);
		}

		Cursor cursor = mContext.getContentResolver().query(
				uri,
				EPISODES_COLUMNS,
				null,
				null,
				null);
		episodesForDateList.clear();
		if(cursor!=null) {
			while (cursor.moveToNext()) {
				hm = new String[3];
				hm[0] = cursor.getString(COL_EPISODE_NUMBER) + "; " + cursor.getString(COL_EPISODE_SERIES_ID);
				hm[1] = cursor.getString(COL_EPISODE_NAME);
				hm[2] = cursor.getString(COL_EPISODE_SEEN);
				//hm.put(EP_NAME, cursor.getString(COL_EPISODE_NAME));
				//hm.put(EP_SER, cursor.getString(COL_EPISODE_NUMBER) + "; " + cursor.getString(COL_EPISODE_SERIES_ID));
				episodesForDateList.add(hm);
			}
			cursor.close();
		}
	}


}
