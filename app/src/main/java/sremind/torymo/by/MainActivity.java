package sremind.torymo.by;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class MainActivity extends TabActivity {

	TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabHost = getTabHost();

		TabHost.TabSpec tabSpec;

		tabSpec = tabHost.newTabSpec("tag1");
		tabSpec.setIndicator(getResources().getString(R.string.calendar));
		tabSpec.setContent(new Intent(this, CalendarActivity.class));
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tag2");
		tabSpec.setIndicator(getResources().getString(R.string.series));
		tabSpec.setContent(new Intent(this, SeriesActivity.class));
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tag3");
		tabSpec.setIndicator(getResources().getString(R.string.watchlist));
		tabSpec.setContent(new Intent(this, WatchlistActivity.class));
		tabHost.addTab(tabSpec);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            default:
                return super.onOptionsItemSelected(item);
        }
    }
	


}
