package sremind.torymo.by;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import sremind.torymo.by.data.SReminderContract;

public class SearchAdapter extends CursorAdapter {

    private class ViewHolder{
        TextView nameTextView;
        TextView overviewTextView;

        public ViewHolder(View view){
            nameTextView = (TextView) view.findViewById(R.id.tvName);
            overviewTextView = (TextView) view.findViewById(R.id.tvDate);
        }
    }

    public SearchAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nameTextView.setText(cursor.getString(SReminderContract.COL_SEARCH_RESULT_NAME));
        String overview = cursor.getString(SReminderContract.COL_SEARCH_RESULT_OVERVIEW);
        if(overview.length()>140)
            overview = overview.substring(0, 140)+"...";
        viewHolder.overviewTextView.setText(overview);

    }
}
