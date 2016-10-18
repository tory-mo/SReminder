package sremind.torymo.by;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.karim.MaterialTabs;
import sremind.torymo.by.data.SReminderContract;

public class EpisodeListActivity extends ActionBarActivity{

    ImageView imageView;
    MaterialTabs mMaterialTabs;
    ViewPager mViewPager;
    Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episodes_activity);


        if (savedInstanceState == null) {
            // using a fragment transaction.

            Callback callBack = new Callback(){
                @Override
                public void onSuccess(){
                    //imageView.setColorFilter(R.color.primary, PorterDuff.Mode.DARKEN);
                }
                @Override
                public void onError(){
                }
            };
            imageView = (ImageView)findViewById(R.id.ivEpisodesHeader);
            arguments = new Bundle();
            arguments.putParcelable(EpisodeListFragment.EPISODE_LIST_URI, getIntent().getData());

            String imdbid = SReminderContract.EpisodeEntry.getImdbIdFromUri(getIntent().getData());

            Cursor cursor = getContentResolver().query(SReminderContract.SeriesEntry.CONTENT_URI,
                    SReminderContract.SERIES_COLUMNS,
                    SReminderContract.SeriesEntry.COLUMN_IMDB_ID + " like ?",
                    new String[]{imdbid},
                    null);
            if(cursor!=null) {
                if (cursor.moveToFirst()) {
                    getSupportActionBar().setTitle("");
                    TextView tv = (TextView) findViewById(R.id.tvSeriesName);
                    tv.setText(cursor.getString(SReminderContract.COL_SERIES_NAME));

                    arguments.putParcelable(SearchDetailFragment.SEARCH_DETAIL_URI, SReminderContract.SearchResultEntry.buildSearchResultId(cursor.getString(SReminderContract.COL_SERIES_MDBID)));
                    Picasso.with(this)
                            .load(cursor.getString(SReminderContract.COL_SERIES_POSTER))
                            .error(R.drawable.no_photo)
                            .into(imageView, callBack);


                }
                cursor.close();
            }

//            EpisodeListFragment fragment = new EpisodeListFragment();
//            fragment.setArguments(arguments);


//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_episodes, fragment)
//                    .commit();
        }

        mMaterialTabs = (MaterialTabs)findViewById(R.id.ep_material_tabs);
        mViewPager = (ViewPager)findViewById(R.id.ep_view_pager);

        EpisodesPagerAdapter adapter = new EpisodesPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mMaterialTabs.setViewPager(mViewPager);

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
                    SearchDetailFragment fr = new SearchDetailFragment();
                    fr.setArguments(arguments);
                    return fr;
            }
        }
    }
}
