package sremind.torymo.by;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sremind.torymo.by.data.Episode;
import sremind.torymo.by.data.SReminderDatabase;

public class CalendarAdapter extends BaseAdapter {

	static final int FIRST_DAY_OF_WEEK = 1; // Sunday = 0, Monday = 1

	private Context mContext;
    private Calendar mChosenMonth;

	// references to our items
	CalDay[] mDaysOfMonth;

	class CalDay{
		String day;
		boolean event;
	}
    
    CalendarAdapter(Context context, Calendar monthCalendar) {
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
        dayView = convertView.findViewById(R.id.date);
        
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
						TextView date = v.findViewById(R.id.date);
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
    
    void refreshDays()
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


		List<Episode> episodes;
		if(Utility.getSeenParam(mContext)){

			episodes = SReminderDatabase.getAppDatabase(mContext).episodeDao().getNotSeenEpisodesBetweenDates(startDate.getTime(), endDate.getTime());
		}else {

			episodes = SReminderDatabase.getAppDatabase(mContext).episodeDao().getEpisodesBetweenDates(startDate.getTime(), endDate.getTime());
		}

		ArrayList<Date> datesOfEpisodes = new ArrayList<>();
		if(episodes != null && !episodes.isEmpty()){
			for (Episode ep: episodes) {
				datesOfEpisodes.add(Utility.getCalendarFromFormattedLong(ep.getDate()));
			}
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

	void showEpisodesForDay(Date touchedDate, Activity activity){
		ArrayList<String[]> episodesForDateList = new ArrayList<>();
		EpisodesForDateAdapter episodesForDateAdapter = new EpisodesForDateAdapter(mContext, episodesForDateList);
//		SimpleAdapter episodesForDayAdapter = new SimpleAdapter(mContext,
//				episodesForDateList,
//				R.layout.episode_list_item,
//				new String[]{EP_NAME,EP_SER	},
//				new int[]{R.id.tvName, R.id.tvDate}
//		);

		ListView lv = activity.findViewById(R.id.lvEpisodesForDay);
		if (lv == null) return;

		TextView tvChosenDate = activity.findViewById(R.id.tvToday);
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

		List<Episode> episodes;
		if(Utility.getSeenParam(mContext)){

			episodes = SReminderDatabase.getAppDatabase(mContext).episodeDao().getNotSeenEpisodesForDate(touchedDate.getTime());
		}else {

			episodes = SReminderDatabase.getAppDatabase(mContext).episodeDao().getEpisodesForDate(touchedDate.getTime());
		}

		episodesForDateList.clear();
		if(episodes != null && !episodes.isEmpty()){
			for (Episode ep: episodes) {
				hm = new String[3];
				hm[0] = ep.getNumber() + "; " + ep.getSeries();
				hm[1] = ep.getName();
				hm[2] = String.valueOf(ep.isSeen());
				episodesForDateList.add(hm);
			}
		}
	}


}
