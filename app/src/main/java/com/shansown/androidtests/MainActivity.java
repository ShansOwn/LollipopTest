package com.shansown.androidtests;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.shansown.androidtests.cardflip.CardFlipActivity;
import com.shansown.androidtests.cardrecyclerview.CardRecyclerViewActivity;
import com.shansown.androidtests.badge.BadgeActivity;
import com.shansown.androidtests.rx.RxDemoActivity;
import com.shansown.androidtests.search.SearchActivity;
import com.shansown.androidtests.synctest.EntryListActivity;
import com.shansown.androidtests.transitions.custom.PicturesActivity;
import com.shansown.androidtests.transitions.lollipop.HomeActivity;

import static com.shansown.androidtests.util.LogUtils.LOGV;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity {

  private static final String TAG = makeLogTag(MainActivity.class);

  private MenuItem mSearchItem;

  private boolean mIsSearchTransition;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onPause() {
    super.onPause();
    if (mIsSearchTransition) {
      mIsSearchTransition = false;
      overridePendingTransition(0, 0);
    }
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

    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
      if (!hasFocus) {
        mSearchItem.collapseActionView();
      }
    });
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      public boolean onQueryTextChange(String arg0) {
        // Doesn't used
        LOGV(TAG, "onQueryTextChange");
        return false;
      }

      public boolean onQueryTextSubmit(String query) {
        LOGV(TAG, "onQueryTextSubmit");
        Bundle searchData = new Bundle();
        searchData.putString(SearchActivity.EXTRA_SEARCH_DATA, "Extra search DATA");
        searchView.setAppSearchData(searchData);
        mIsSearchTransition = true;
        return false;
      }
    });
    return true;
  }

  /**
   * Launch a class by name.
   */
  private void launchClass(Class<?> clazz) {
    final Intent intent = new Intent(this, clazz);
    startActivity(intent);
  }

  /**
   * Launch Transitions Lollipop demo. Called from onclick=""
   */
  public void transitionsLollipopDemo(final View unused) {
    launchClass(HomeActivity.class);
  }

  /**
   * Launch Transitions Custom demo. Called from onclick=""
   */
  public void transitionsCustomDemo(View unused) {
    launchClass(PicturesActivity.class);
  }

  /**
   * Launch CardView and RecyclerView demo. Called from onclick=""
   */
  public void cardRecyclerDemo(View unused) {
    launchClass(CardRecyclerViewActivity.class);
  }

  /**
   * Launch Budge demo. Called from onclick=""
   */
  public void badgeDemo(View unused) {
    launchClass(BadgeActivity.class);
  }

  /**
   * Launch Card Flip demo. Called from onclick=""
   */
  public void cardFlipDemo(View unused) {
    launchClass(CardFlipActivity.class);
  }

  /**
   * Launch Sync demo. Called from onclick=""
   */
  public void SyncDemo(View unused) {
    launchClass(EntryListActivity.class);
  }

  public void rxDemo(View view) {
    launchClass(RxDemoActivity.class);
  }
}