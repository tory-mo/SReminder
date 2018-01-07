package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import sremind.torymo.by.R;
import sremind.torymo.by.data.SearchResult;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> implements View.OnClickListener{

    private List<SearchResult> dataSet;
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClicked(SearchResult searchResult, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public SearchResultsAdapter(Context context, List<SearchResult> searchResults) {
        this.dataSet = searchResults;
        this.mContext = context;
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
                final SearchResult searchResult = dataSet.get(position);
                this.onItemClickListener.onItemClicked(searchResult, position);
            }
        }
    }

    public void setItems(final List<SearchResult> searchResults) {
        if (dataSet == null) {
            dataSet = searchResults;
            notifyItemRangeInserted(0, searchResults.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return dataSet.size();
                }

                @Override
                public int getNewListSize() {
                    return searchResults.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return dataSet.get(oldItemPosition).getId() ==
                            searchResults.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    SearchResult newProduct = searchResults.get(newItemPosition);
                    SearchResult oldProduct = dataSet.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && Objects.equals(newProduct.getImdbId(), oldProduct.getImdbId())
                            && Objects.equals(newProduct.getName(), oldProduct.getName())
                            && newProduct.getSeasons() == oldProduct.getSeasons();
                }
            });
            dataSet = searchResults;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.search_result_list_item, parent, false);
        itemView.setOnClickListener(this);
        return new SearchResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder viewHolder, int position) {
        SearchResult searchResult = dataSet.get(position);

        viewHolder.nameTextView.setText(searchResult.getName());
        String overview = searchResult.getOverview();
        if(overview.length()>140)
            overview = overview.substring(0, 140)+"...";
        viewHolder.overviewTextView.setText(overview);

        Picasso.with(mContext)
                .load(searchResult.getPoster())
                .error(R.drawable.no_photo)
                .into(viewHolder.posterImageView);
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView nameTextView;
        TextView overviewTextView;

        SearchResultViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.tvName);
            overviewTextView = view.findViewById(R.id.tvDate);
            posterImageView = view.findViewById(R.id.ivPoster);

        }
    }
}
