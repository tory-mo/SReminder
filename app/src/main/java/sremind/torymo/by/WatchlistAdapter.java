package sremind.torymo.by;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import sremind.torymo.by.data.SReminderContract;

public class WatchlistAdapter extends CursorAdapter {

	public WatchlistAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}
	
	private class ViewHolder {
		TextView name;
		CheckBox watchlist;

		public ViewHolder(View view){
			name = (TextView)view.findViewById(R.id.seriesNameWatchlist);
			watchlist = (CheckBox)view.findViewById(R.id.watchlistCheckBox);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = LayoutInflater.from(context).inflate(R.layout.watchlist_item, viewGroup, false);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.name.setText(cursor.getString(SReminderContract.COL_SERIES_NAME));
		viewHolder.watchlist.setChecked(Utility.getBooleanFromDB(cursor.getInt(SReminderContract.COL_SERIES_WATCHLIST)));
	}

}
