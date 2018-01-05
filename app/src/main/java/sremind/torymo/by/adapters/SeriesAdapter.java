package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sremind.torymo.by.R;
import sremind.torymo.by.data.Series;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> implements View.OnClickListener{

    private List<Series> dataSet = new ArrayList<>();
    private Context mContext;

    public SeriesAdapter(Context context, @NonNull List<Series> data) {
        this.dataSet = data;
        this.mContext = context;
    }

    class SeriesViewHolder extends RecyclerView.ViewHolder{
        TextView seriesName;

        SeriesViewHolder(View view){
            super(view);
            seriesName = view.findViewById(R.id.tvName);
        }

    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClicked(Series searchResult, int position);
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
                final Series searchResult = dataSet.get(position);
                this.onItemClickListener.onItemClicked(searchResult, position);
            }
        }
    }

    public void setItems(final List<Series> series) {
        if (dataSet == null) {
            dataSet = series;
            notifyItemRangeInserted(0, series.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return dataSet.size();
                }

                @Override
                public int getNewListSize() {
                    return series.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return dataSet.get(oldItemPosition).getId() ==
                            series.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Series newProduct = series.get(newItemPosition);
                    Series oldProduct = dataSet.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && Objects.equals(newProduct.getImdbId(), oldProduct.getImdbId())
                            && Objects.equals(newProduct.getName(), oldProduct.getName())
                            && Objects.equals(newProduct.getMdbId(), oldProduct.getMdbId());
                }
            });
            dataSet = series;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemCount() {
        return (dataSet == null)?0:dataSet.size() ;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.series_elem, parent, false);
        itemView.setOnClickListener(this);
        return new SeriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder viewHolder, int position) {
        Series series = dataSet.get(position);
        viewHolder.seriesName.setText(series.getName());
    }
}
