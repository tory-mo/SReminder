package sremind.torymo.by.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sremind.torymo.by.R;
import sremind.torymo.by.data.Series;
import sremind.torymo.by.databinding.WatchlistItemBinding;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> implements View.OnClickListener,
		View.OnCreateContextMenuListener,
		PopupMenu.OnMenuItemClickListener{

	private static final int CM_DELETE_SERIES = 2;

	private List<Series> dataSet = new ArrayList<>();

	private OnItemClickListener onItemClickListener;

	public interface OnItemClickListener
	{
		void onItemClicked(View view, Series series);
		void onMenuAction(MenuItem item, Series series);
	}

	public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
	{
		this.onItemClickListener = onItemClickListener;
	}

	public WatchlistAdapter(@NonNull List<Series> data) {
		this.dataSet = data;
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
	public void onClick(final View view)
	{
		if (this.onItemClickListener != null)
		{
			final RecyclerView recyclerView = (RecyclerView) view.getParent();
			final int position = recyclerView.getChildLayoutPosition(view);
			if (position != RecyclerView.NO_POSITION)
			{
				final Series series = dataSet.get(position);
				this.onItemClickListener.onItemClicked(view, series);
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		PopupMenu popup = new PopupMenu(view.getContext(), view);
		popup.getMenu().add(0, CM_DELETE_SERIES, 0, R.string.DELETE);
		popup.setOnMenuItemClickListener(this);
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		Series series = getItem(info.position);
		this.onItemClickListener.onMenuAction(menuItem, series);
		return false;
	}

	@Override
	public int getItemCount() {
		return (dataSet == null) ? 0 : dataSet.size();
	}

	public Series getItem(int position){
		return (dataSet == null) ? null : dataSet.get(position);
	}

	@Override
	public WatchlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater =
				LayoutInflater.from(parent.getContext());
		WatchlistItemBinding itemBinding =
				WatchlistItemBinding.inflate(layoutInflater, parent, false);
		itemBinding.setHandler(this);
		return new WatchlistViewHolder(itemBinding);
	}

	@Override
	public void onBindViewHolder(WatchlistViewHolder viewHolder, int position) {
		Series series = dataSet.get(position);
		viewHolder.bind(series);
	}

	class WatchlistViewHolder extends RecyclerView.ViewHolder {
		private final WatchlistItemBinding binding;

		WatchlistViewHolder(WatchlistItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(Series item) {
			binding.setSeries(item);
			binding.executePendingBindings();
		}
	}
}
