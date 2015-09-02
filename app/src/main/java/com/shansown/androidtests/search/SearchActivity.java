package com.shansown.androidtests.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shansown.androidtests.BaseActivity;
import com.shansown.android.lollipoptest.R;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.LOGV;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class SearchActivity extends BaseActivity {

  private static final String TAG = makeLogTag(SearchActivity.class);

  public static final String EXTRA_SEARCH_DATA = PREFIX + ".extra.SEARCH_DATA";

  private MenuItem mSearchItem;
  private String mQuery;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });

    handleIntent(getIntent());
  }

  @Override protected void onNewIntent(Intent intent) {
    setIntent(intent);
    handleIntent(intent);
  }

  @Override protected void onStop() {
    super.onStop();
    if (mSearchItem.isActionViewExpanded()) {
      mSearchItem.collapseActionView();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_with_search, menu);
    mSearchItem = menu.findItem(R.id.action_search);

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        searchView.setQuery(mQuery, false);
      }
    });
    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          mSearchItem.collapseActionView();
        }
      }
    });
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      public boolean onQueryTextChange(String arg0) {
        // Doesn't used
        LOGV(TAG, "onQueryTextChange");
        return false;
      }

      public boolean onQueryTextSubmit(String arg0) {
        LOGV(TAG, "onQueryTextSubmit");
        Bundle extraSearchData = getIntent().getBundleExtra(SearchManager.APP_DATA);
        searchView.setAppSearchData(extraSearchData);
        return false;
      }
    });
    return true;
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }

  private void handleIntent(Intent intent) {
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      mQuery = intent.getStringExtra(SearchManager.QUERY);
      getSupportActionBar().setTitle(mQuery);
      String extraData = null;
      Bundle extraSearchData = intent.getBundleExtra(SearchManager.APP_DATA);
      if (extraSearchData != null) {
        extraData = extraSearchData.getString(EXTRA_SEARCH_DATA);
      }
      LOGD(TAG, "key: " + extraData);
      doSearch(mQuery, extraData);
    }
  }

  private void doSearch(String query, String extraData) {
    ((TextView) findViewById(R.id.search_query)).setText(query);
    ((TextView) findViewById(R.id.search_data)).setText(extraData);
  }
}
