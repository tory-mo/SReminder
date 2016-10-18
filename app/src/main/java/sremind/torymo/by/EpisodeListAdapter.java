package sremind.torymo.by;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import sremind.torymo.by.data.SReminderContract;

public class EpisodeListAdapter extends CursorAdapter {

    public static final SimpleDateFormat dateListFormat = new SimpleDateFormat("dd.MM.yyyy");

    public class ViewHolder{
        TextView name;
        TextView info;
        ImageView icon;

        public ViewHolder(View view){
            name = (TextView)view.findViewById(R.id.tvName);
            info = (TextView)view.findViewById(R.id.tvDate);
            icon = (ImageView)view.findViewById(R.id.ivSeenIcon);
        }
    }

    public EpisodeListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.episode_list_item, viewGroup, false);
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
            viewHolder.icon.setImageResource(R.drawable.eye);
        }else{
            viewHolder.icon.setImageResource(R.drawable.eye_off);
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
