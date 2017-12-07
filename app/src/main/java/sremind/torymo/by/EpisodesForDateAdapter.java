package sremind.torymo.by;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EpisodesForDateAdapter extends BaseAdapter {

	private Context mContext;

	ArrayList<String[]> episodesForDateList;

    public EpisodesForDateAdapter(Context context, ArrayList<String[]> data) {
		mContext = context;
		episodesForDateList = data;
    }

    public int getCount() {
        return episodesForDateList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup viewGroup) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.episode_list_item, viewGroup, false);
		TextView dateView = convertView.findViewById(R.id.tvDate);
		TextView nameView = convertView.findViewById(R.id.tvName);
		ImageView seenView = convertView.findViewById(R.id.ivSeenIcon);

		dateView.setText(episodesForDateList.get(position)[0]);
		nameView.setText(episodesForDateList.get(position)[1]);

		if(Boolean.parseBoolean(episodesForDateList.get(position)[2])){
			seenView.setImageResource(R.drawable.eye);
		}else{
			seenView.setImageResource(R.drawable.eye_off);
		}

        return convertView;
    }
}
