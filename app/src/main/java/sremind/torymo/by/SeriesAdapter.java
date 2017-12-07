package sremind.torymo.by;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sremind.torymo.by.data.Series;

public class SeriesAdapter extends ArrayAdapter<Series> {

    private List<Series> dataSet;
    Context mContext;

    public SeriesAdapter(Context context, List<Series> data) {
        super(context, R.layout.series_elem, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public class ViewHolder{
        TextView seriesName;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Series series = getItem(i);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.series_elem, parent, false);
            viewHolder.seriesName = convertView.findViewById(R.id.tvName);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.seriesName.setText(series.getName());

        return result;
    }
}
