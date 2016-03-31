package sremind.torymo.by;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import sremind.torymo.by.data.SReminderContract;

public class EpisodeListActivity extends ActionBarActivity {
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episodes_activity);
        mToolbar = (Toolbar)findViewById(R.id.episodesToolbar);


        if (savedInstanceState == null) {
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(EpisodeListFragment.EPISODE_LIST_URI, getIntent().getData());

            String imdbid = SReminderContract.EpisodeEntry.getImdbIdFromUri(getIntent().getData());
            Cursor cursor = getContentResolver().query(SReminderContract.SeriesEntry.CONTENT_URI,
                    SReminderContract.SERIES_COLUMNS,
                    SReminderContract.SeriesEntry.COLUMN_IMDB_ID + " like ?",
                    new String[]{imdbid},
                    null);
            if(cursor!=null) {
                if (cursor.moveToFirst()) {
                    mToolbar.setTitle(cursor.getString(SReminderContract.COL_SERIES_NAME));
                }
                cursor.close();
            }

            EpisodeListFragment fragment = new EpisodeListFragment();
            fragment.setArguments(arguments);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_episodes, fragment)
                    .commit();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
