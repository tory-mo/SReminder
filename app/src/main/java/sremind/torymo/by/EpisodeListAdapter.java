package sremind.torymo.by;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import sremind.torymo.by.data.SReminderContract;

public class EpisodeListAdapter extends CursorAdapter {

    public static final SimpleDateFormat dateListFormat = new SimpleDateFormat("dd.MM.yyyy");

    public class ViewHolder{
        TextView name;
        TextView info;

        public ViewHolder(View view){
            name = (TextView)view.findViewById(R.id.tvName);
            info = (TextView)view.findViewById(R.id.tvDate);
        }
    }

    public EpisodeListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.episodes, viewGroup, false);
        if(Utility.getBooleanFromDB(cursor.getInt(SReminderContract.COL_EPISODE_SEEN))){
            view.setBackground(context.getDrawable(R.drawable.episode));
        }
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        viewHolder.name.setText(cursor.getString(SReminderContract.COL_EPISODE_NAME));
        Date date = Utility.getCalendarFromFormattedLong(cursor.getLong(SReminderContract.COL_EPISODE_DATE));
        String epNum = cursor.getString(SReminderContract.COL_EPISODE_NUMBER);

        if(Utility.getBooleanFromDB(cursor.getInt(SReminderContract.COL_EPISODE_SEEN))){
            view.setBackground(context.getDrawable(R.drawable.seen_episode));
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.text_primary));
        }else{
            view.setBackground(context.getDrawable(R.drawable.episode));
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.text_secondary));
        }

        if(date==null){
            viewHolder.info.setText(epNum);
        }else{
            String dateStr = dateListFormat.format(date);
            viewHolder.info.setText(context.getString(R.string.format_episode_info1,
                    epNum, dateStr));
        }
    }


}
