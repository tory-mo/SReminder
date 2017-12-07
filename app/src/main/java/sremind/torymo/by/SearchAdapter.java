package sremind.torymo.by;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sremind.torymo.by.data.SearchResult;

public class SearchAdapter extends ArrayAdapter<SearchResult> {

    private List<SearchResult> dataSet;
    Context mContext;

    private class ViewHolder{
        TextView nameTextView;
        TextView overviewTextView;
    }

    public SearchAdapter(Context context, List<SearchResult> data) {
        super(context, R.layout.search_result_list_item, data);
        this.dataSet = data;
        this.mContext = context;
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

        return result;
    }
}
