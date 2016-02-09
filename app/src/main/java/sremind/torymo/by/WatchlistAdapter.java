package sremind.torymo.by;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class WatchlistAdapter extends ArrayAdapter<SRSeries> {
	
	private List<SRSeries> seriesList;
	private List<SRSeries> listlist;
	private Activity context;

	public WatchlistAdapter(Activity context, int textViewResourceId, List<SRSeries> seriesList) {
		super(context, textViewResourceId, seriesList);
		this.seriesList = new ArrayList<SRSeries>();
		this.seriesList.addAll(seriesList);
		this.listlist = new ArrayList<SRSeries>();
		this.listlist.addAll(this.seriesList);
		this.context = context;
	}
	
	private class ViewHolder {
		TextView name;
		CheckBox watchlist;
	}
	
	@Override
    public int getCount() {
        return seriesList.size();
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater vi = context.getLayoutInflater();
			convertView = vi.inflate(R.layout.item_for_choosing, null);
			 
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.product_name);
			holder.watchlist = (CheckBox) convertView.findViewById(R.id.cb1);
			convertView.setTag(holder);
			 
			/*holder.watchlist.setOnClickListener( new View.OnClickListener() {  
			     public void onClick(View v) {  
			      CheckBox cb = (CheckBox) v ;  
			      SRSeries country = (SRSeries) cb.getTag();  
			      //Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() +  " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
			      country.WatchList(cb.isChecked());
			     }  
			    }); */ 
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		SRSeries series = this.seriesList.get(position);
		holder.name.setText(series.Name());
		holder.watchlist.setChecked(series.WatchList());
			
		return convertView;
	}
	
	public void setListData(List<SRSeries> data){
	    this.seriesList = data;
	}
	
	// Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        seriesList.clear();
        if (charText.length() == 0) {
        	seriesList.addAll(listlist);
        } 
        else 
        {
            for (SRSeries series : listlist) 
            {
                if (series.Name().toLowerCase(Locale.getDefault()).contains(charText)) 
                {
                	seriesList.add(series);
                }
            }
        }
        notifyDataSetChanged();
    }


}
