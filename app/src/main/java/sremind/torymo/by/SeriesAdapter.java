package sremind.torymo.by;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import sremind.torymo.by.data.SReminderContract;

public class SeriesAdapter extends CursorAdapter {

    public SeriesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public class ViewHolder{
        TextView seriesName;

        public ViewHolder(View view){
            seriesName = (TextView)view.findViewById(R.id.tvName);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.series_elem, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.seriesName.setText(cursor.getString(SReminderContract.COL_SERIES_NAME));
    }
}
