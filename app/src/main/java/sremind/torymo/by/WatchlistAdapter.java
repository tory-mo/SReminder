package sremind.torymo.by;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import sremind.torymo.by.data.Series;

public class WatchlistAdapter extends ArrayAdapter<Series> {

	private List<Series> dataSet;
	Context mContext;

	public WatchlistAdapter(Context context, List<Series> data) {
		super(context, R.layout.series_elem, data);
		this.dataSet = data;
		this.mContext = context;
	}
	
	private class ViewHolder {
		TextView name;
		CheckBox watchlist;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup parent) {
		Series series = getItem(i);
		ViewHolder viewHolder; // view lookup cache stored in tag

		final View result;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.watchlist_item, parent, false);
			viewHolder.name = convertView.findViewById(R.id.seriesNameWatchlist);
			viewHolder.watchlist = convertView.findViewById(R.id.watchlistCheckBox);

			result=convertView;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			result=convertView;
		}

		if(series != null) {
			viewHolder.name.setText(series.getName());
			viewHolder.watchlist.setChecked(series.isWatchlist());
		}

		return result;
	}

}
