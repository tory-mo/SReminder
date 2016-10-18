package sremind.torymo.by;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;

import sremind.torymo.by.data.SReminderContract;

public class SearchDetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail_activity);

        if(savedInstanceState == null){
            SearchDetailFragment fragment = new SearchDetailFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(SearchDetailFragment.SEARCH_DETAIL_URI, getIntent().getData());
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_detail_fragment, fragment)
                    .commit();

            String id = SReminderContract.SearchResultEntry.getIdfromUri(getIntent().getData());
            Cursor cursor = getContentResolver().query(SReminderContract.SearchResultEntry.CONTENT_URI,
                    SReminderContract.SEARCH_RESULT_COLUMNS,
                    SReminderContract.SearchResultEntry.COLUMN_ID + " like ?",
                    new String[]{id},
                    null);
            if(cursor!=null) {
                if (cursor.moveToFirst()) {
                    getSupportActionBar().setTitle(cursor.getString(SReminderContract.COL_SEARCH_RESULT_NAME));
                }
                cursor.close();
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
    }
}
