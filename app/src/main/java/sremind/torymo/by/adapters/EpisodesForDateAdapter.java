package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sremind.torymo.by.R;

public class EpisodesForDateAdapter extends RecyclerView.Adapter<EpisodesForDateAdapter.EpisodesForDateViewHolder> {

	private Context mContext;

	ArrayList<String[]> dataSet = new ArrayList<>();

    public EpisodesForDateAdapter(Context context, ArrayList<String[]> data) {
		mContext = context;
		dataSet.addAll(data);
    }

    @Override
    public int getItemCount() {
        return (dataSet == null) ? 0 : dataSet.size();
    }

    public void clearItems(){
        if(dataSet != null) {
            dataSet.clear();
            notifyDataSetChanged();
        }
    }

    public void addItem(String[] newItem){
        if(dataSet.size() == 0) {
            dataSet.add(newItem);
            notifyDataSetChanged();
        }else {
            int i = 0;
            for(String[] item : dataSet){
                if(item[0].equals(newItem[0]) && item[1].equals(newItem[1]) && item[2].equals(newItem[2]) && item[3].equals(newItem[3])){
                    break;
                }
                i++;
            }
            if(i == dataSet.size()) {
                dataSet.add(newItem);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public EpisodesForDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.episode_list_item, parent, false);
        parent.setFocusable(false);
        parent.setClickable(false);
        return new EpisodesForDateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EpisodesForDateViewHolder viewHolder, int position) {
        String[] ep = dataSet.get(position);

        viewHolder.tvDate.setText(mContext.getString(R.string.format_episode_number, Integer.parseInt(ep[0]), Integer.parseInt(ep[4])));
        viewHolder.tvEpisodeInfo.setText(ep[3]);
        viewHolder.tvName.setText(ep[1]);

        if(Boolean.parseBoolean(ep[2])){
            viewHolder.ivSeen.setImageResource(R.drawable.eye);
        }else{
            viewHolder.ivSeen.setImageResource(R.drawable.eye_off);
        }
    }

    class EpisodesForDateViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvDate;
        TextView tvEpisodeInfo;
        ImageView ivSeen;

        EpisodesForDateViewHolder(View view){
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvDate = view.findViewById(R.id.tvDate);
            tvEpisodeInfo = view.findViewById(R.id.tvEpisodeInfo);
            ivSeen = view.findViewById(R.id.ivSeenIcon);
        }
    }
}
