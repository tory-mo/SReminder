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
import sremind.torymo.by.databinding.SeriesElemBinding;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> implements View.OnClickListener{

    private List<Series> dataSet = new ArrayList<>();
    private Context mContext;

    public SeriesAdapter(Context context, @NonNull List<Series> data) {
        this.dataSet = data;
        this.mContext = context;
    }

    class SeriesViewHolder extends RecyclerView.ViewHolder{
        private final SeriesElemBinding binding;

        SeriesViewHolder(SeriesElemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Series item) {
            binding.setSeries(item);
            binding.executePendingBindings();
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
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        SeriesElemBinding itemBinding =
                SeriesElemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandler(this);
        return new SeriesViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder viewHolder, int position) {
        Series series = dataSet.get(position);
        viewHolder.bind(series);
    }
}
