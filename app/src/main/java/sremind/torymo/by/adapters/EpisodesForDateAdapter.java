package sremind.torymo.by.adapters;

import android.content.Context;
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
        dataSet.add(newItem);
        notifyDataSetChanged();
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

        viewHolder.tvDate.setText(ep[0]);
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
