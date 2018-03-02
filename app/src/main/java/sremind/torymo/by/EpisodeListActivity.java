package sremind.torymo.by;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import sremind.torymo.by.data.SReminderDatabase;
import sremind.torymo.by.data.Series;

public class EpisodeListActivity extends AppCompatActivity{

    ImageView imageView;
    ViewPager mViewPager;
    Bundle arguments;
    SearchDetailFragment sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episodes_activity);


        if (savedInstanceState == null) {
            // using a fragment transaction.

            final Callback callBack = new Callback(){
                @Override
                public void onSuccess(){
                    //imageView.setColorFilter(R.color.primary, PorterDuff.Mode.DARKEN);
                }
                @Override
                public void onError(){
                }
            };
            imageView = findViewById(R.id.ivEpisodesHeader);
            String imdbid = getIntent().getStringExtra(EpisodeListFragment.EPISODE_LIST_URI);
            arguments = new Bundle();
            arguments.putString(EpisodeListFragment.EPISODE_LIST_URI, imdbid);

            Series series = SReminderDatabase.getAppDatabase(this).seriesDao().getSeriesByImdbId(imdbid);
            if(series != null){
                getSupportActionBar().setTitle("");
                TextView tv = findViewById(R.id.tvSeriesName);
                TextView tv1 = findViewById(R.id.tvSeriesOriginalName);
                tv.setText(series.getName());
                tv1.setText(series.getOriginalName());

                if(sdf != null)
                    sdf.refresh(series.getMdbId());
                else
                    arguments.putString(SearchDetailFragment.SEARCH_DETAIL_URI, series.getMdbId());

                Picasso.with(getBaseContext())
                        .load(series.getPoster())
                        .error(R.drawable.no_photo)
                        .into(imageView, callBack);
            }
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.episodes)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.overview)));

        mViewPager = findViewById(R.id.ep_view_pager);

        EpisodesPagerAdapter adapter = new EpisodesPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                //chooseType(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setElevation(0f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public class EpisodesPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {getResources().getString(R.string.episodes),
                getResources().getString(R.string.overview)};

        public EpisodesPagerAdapter(FragmentManager fm) {
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
                    EpisodeListFragment fragment = new EpisodeListFragment();
                    fragment.setArguments(arguments);
                    return fragment;
                case 1:
                    sdf = new SearchDetailFragment();
                    sdf.setArguments(arguments);
                    return sdf;
            }
        }
    }
}
