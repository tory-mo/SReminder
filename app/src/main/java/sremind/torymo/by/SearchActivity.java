package sremind.torymo.by;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import sremind.torymo.by.data.SReminderContract;
import sremind.torymo.by.service.SearchService;

public class SearchActivity extends ActionBarActivity implements SearchFragment.Callback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        if (savedInstanceState == null) {
            // using a fragment transaction.

            SearchFragment fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment, fragment)
                    .commit();
        }

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        handleIntent(getIntent());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.onActionViewExpanded();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            getContentResolver().delete(SReminderContract.SearchResultEntry.CONTENT_URI,null,null);
            if(query.length()>1) {
                Intent searchIntent = new Intent(this, SearchService.class);
                searchIntent.putExtra(SearchService.SEARCH_QUERY_EXTRA, query);
                startService(searchIntent);
            }
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
            Intent intent = new Intent(this, SearchDetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
    }
}
