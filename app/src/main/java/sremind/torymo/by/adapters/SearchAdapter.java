package sremind.torymo.by.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sremind.torymo.by.R;
import sremind.torymo.by.data.SearchResult;

public class SearchAdapter extends ArrayAdapter<SearchResult> {

    private List<SearchResult> dataSet = new ArrayList<>();
    Context mContext;

    private class ViewHolder{
        ImageView posterImageView;
        TextView nameTextView;
        TextView overviewTextView;
    }

    public SearchAdapter(Context context, @NonNull List<SearchResult> data) {
        super(context, R.layout.search_result_list_item, data);
        this.dataSet.addAll(data);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return (dataSet == null)?0:dataSet.size();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        SearchResult searchResult = getItem(i);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_result_list_item, parent, false);
            viewHolder.nameTextView = convertView.findViewById(R.id.tvName);
            viewHolder.overviewTextView = convertView.findViewById(R.id.tvDate);
            viewHolder.posterImageView = convertView.findViewById(R.id.ivPoster);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.nameTextView.setText(searchResult.getName());
        String overview = searchResult.getOverview();
        if(overview.length()>140)
            overview = overview.substring(0, 140)+"...";
        viewHolder.overviewTextView.setText(overview);

        Picasso.with(mContext)
                .load(searchResult.getPoster())
                .error(R.drawable.no_photo)
                .into(viewHolder.posterImageView);

        return result;
    }
}
