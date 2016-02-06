package sremind.torymo.by;

import java.util.ArrayList;
import java.util.Date;

import sremind.torymo.by.SReminderDatabase.Episode;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EpisodesAdapter extends ArrayAdapter<Episode> {

	private ArrayList<Episode> list;
	private final Activity context;
	private SReminderDatabase database;
	
	public EpisodesAdapter(Activity context, ArrayList<Episode> inList) {
		super(context, R.layout.episodes, inList);
		this.database = new SReminderDatabase(context);
		this.context = context;
		this.list = new ArrayList<SReminderDatabase.Episode>();
		this.list.addAll(inList);
	}

	static class ViewHolder {
		TextView name;
        TextView info;
    }
	
	@Override
    public int getCount() {
        return this.list.size();
    }
	
	public void itemSeen(int position, boolean seen){
		this.list.get(position).seen = seen;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final Episode ep = this.list.get(position);
		LayoutInflater inflator = context.getLayoutInflater();	        	
			
			if(ep.seen)
				convertView = inflator.inflate(R.layout.episodes_seen, null);
			else
				convertView = inflator.inflate(R.layout.episodes, null);
	            
	        holder = new ViewHolder();
	        holder.name = (TextView) convertView.findViewById(R.id.tvName);
	        holder.info = (TextView) convertView.findViewById(R.id.tvDate);
	            
	        convertView.setTag(holder);

		holder.name.setText(ep.episodeName);
		Date date = ep.date;
        if(date==null){
        	holder.info.setText(ep.episodeNumber);
        }else{
        	holder.info.setText(ep.episodeNumber+"; "+ep.date.getDate()+"."+(ep.date.getMonth()+1)+"."+(ep.date.getYear()+1900)); 
        }

		return convertView;
	}
	
	public void setListData(ArrayList<Episode> data){
	    this.list = data;
	}
}
