package sremind.torymo.by;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.karim.MaterialTabs;
import sremind.torymo.by.service.EpisodesService;

public class MainActivity extends AppCompatActivity implements SeriesFragment.Callback{

	MaterialTabs mMaterialTabs;
	ViewPager mViewPager;

	SReminderServicesBroadcastReceiver mBroadcastReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMaterialTabs = (MaterialTabs)findViewById(R.id.material_tabs);
		mViewPager = (ViewPager)findViewById(R.id.view_pager);

		SeriesPagerAdapter adapter = new SeriesPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);

		mMaterialTabs.setViewPager(mViewPager);

		getSupportActionBar().setTitle(getString(R.string.app_caption));
		getSupportActionBar().setElevation(0f);

		mBroadcastReceiver = new SReminderServicesBroadcastReceiver();
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

	@Override
	public void onItemSelected(Uri dateUri) {
		if(mViewPager.getCurrentItem()==1){
			Intent intent = new Intent(this, EpisodeListActivity.class)
					.setData(dateUri);
			startActivity(intent);
		}
	}

	public class SeriesPagerAdapter extends FragmentPagerAdapter {
		private final String[] TITLES = {getResources().getString(R.string.calendar),
				getResources().getString(R.string.series),
				getResources().getString(R.string.watchlist)};

		public SeriesPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
				default:
					return new CalendarFragment();
				case 1:
					return new SeriesFragment();
				case 2:
					return new WatchlistFragment();
			}
		}
	}

	public class SReminderServicesBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null || !intent.hasExtra(EpisodesService.EPISODES_RESULT_EXTRA))
				return;
			Toast.makeText(context,intent.getIntExtra(EpisodesService.EPISODES_RESULT_EXTRA,0) + context.getString(R.string.are_updated), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mBroadcastReceiver,
				new IntentFilter(Utility.BROADCAST_ACTION));
	}
}
