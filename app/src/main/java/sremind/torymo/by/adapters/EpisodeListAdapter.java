package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sremind.torymo.by.R;
import sremind.torymo.by.data.Episode;

public class EpisodeListAdapter extends ArrayAdapter<Episode> {

    public static final SimpleDateFormat dateListFormat = new SimpleDateFormat("dd.MM.yyyy");
    private List<Episode> dataSet = new ArrayList<>();
    Context mContext;

    public EpisodeListAdapter(Context context, @NonNull List<Episode> data) {
        super(context, R.layout.episode_list_item, data);
        this.dataSet.addAll(data);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return (dataSet == null)?0:dataSet.size();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Episode ep = getItem(i);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.episode_list_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tvName);
            viewHolder.info = convertView.findViewById(R.id.tvDate);
            viewHolder.icon = convertView.findViewById(R.id.ivSeenIcon);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.name.setText(ep.getName());
        Date date = new Date(ep.getDate());
        String epNum = ep.getNumber();

        if(ep.isSeen()){
            viewHolder.icon.setImageResource(R.drawable.eye);
        }else{
            viewHolder.icon.setImageResource(R.drawable.eye_off);
        }

        if(date==null){
            viewHolder.info.setText(epNum);
        }else{
            String dateStr = dateListFormat.format(date);
            viewHolder.info.setText(mContext.getString(R.string.format_episode_info1,
                    epNum, dateStr));
        }

        return result;
    }

    public class ViewHolder{
        TextView name;
        TextView info;
        ImageView icon;
    }
}
