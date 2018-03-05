package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import sremind.torymo.by.R;
import sremind.torymo.by.data.Episode;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.EpisodesViewHolder> implements View.OnClickListener{

    static final SimpleDateFormat dateListFormat = new SimpleDateFormat("dd MMMM yyyy");
    private List<Episode> dataSet = new ArrayList<>();
    private Context mContext;

    public EpisodeListAdapter(Context context, @NonNull List<Episode> data) {
        this.dataSet = data;
        this.mContext = context;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClicked(Episode episode, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(final View view)
    {
        if (this.onItemClickListener != null)
        {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION)
            {
                final Episode episode = dataSet.get(position);
                this.onItemClickListener.onItemClicked(episode, position);
            }
        }
    }

    public void setItems(final List<Episode> productList) {
        if (dataSet == null) {
            dataSet = productList;
            notifyItemRangeInserted(0, productList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return dataSet.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return dataSet.get(oldItemPosition).getId() ==
                            productList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Episode newProduct = productList.get(newItemPosition);
                    Episode oldProduct = dataSet.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && Objects.equals(newProduct.getSeries(), oldProduct.getSeries())
                            && Objects.equals(newProduct.getName(), oldProduct.getName())
                            && newProduct.getDate() == oldProduct.getDate();
                }
            });
            dataSet = productList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemCount() {
        return (dataSet == null) ? 0 : dataSet.size();
    }

    @Override
    public EpisodesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.episode_item, parent, false);
        itemView.setOnClickListener(this);
        return new EpisodesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EpisodesViewHolder viewHolder, int position) {
        Episode ep = dataSet.get(position);

        viewHolder.tvEpisodeName.setText(ep.getName());
        Date date = new Date(ep.getDate());

        if(ep.isSeen()){
            viewHolder.ivSeen.setImageResource(R.drawable.eye);
        }else{
            viewHolder.ivSeen.setImageResource(R.drawable.eye_off);
        }

        viewHolder.tvEpisodeInfo.setText(mContext.getString(R.string.format_episode_number, ep.getNumber(), ep.getSeasonNumber()));
        if(ep.getOverview().length() != 0) {
            viewHolder.tvOverview.setText(ep.getOverview());
            viewHolder.tvOverview.setVisibility(View.VISIBLE);
        }else
            viewHolder.tvOverview.setVisibility(View.GONE);

        if(date != null){
            String dateStr = dateListFormat.format(date);
            viewHolder.tvDate.setText(dateStr);
        }

        Picasso.with(viewHolder.ivPoster.getContext()).load(ep.getPoster()).error(R.drawable.no_photo).into(viewHolder.ivPoster);
    }

    class EpisodesViewHolder extends RecyclerView.ViewHolder{
        TextView tvEpisodeName;
        TextView tvDate;
        TextView tvEpisodeInfo;
        TextView tvOverview;
        ImageView ivSeen;
        ImageView ivPoster;

        EpisodesViewHolder(View view){
            super(view);
            tvEpisodeName = view.findViewById(R.id.tvName);
            tvDate = view.findViewById(R.id.tvDate);
            tvEpisodeInfo = view.findViewById(R.id.tvEpisodeInfo);
            tvOverview = view.findViewById(R.id.tvOverview);
            ivSeen = view.findViewById(R.id.ivSeenIcon);
            ivPoster = view.findViewById(R.id.ivPoster);
        }
    }
}
