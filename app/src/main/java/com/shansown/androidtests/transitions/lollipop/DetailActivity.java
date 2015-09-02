package com.shansown.androidtests.transitions.lollipop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.shansown.androidtests.App;
import com.shansown.androidtests.BaseActivity;
import com.shansown.androidtests.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends BaseActivity {

  public static final String EXTRA_IMAGE = "DetailActivity:image";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(view -> finish());

    ImageView image = (ImageView) findViewById(R.id.image);
    ViewCompat.setTransitionName(image, EXTRA_IMAGE);
    Picasso.with(App.getAppContext()).load(getIntent().getStringExtra(EXTRA_IMAGE)).into(image);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static void launch(BaseActivity activity, View transitionView, String url) {
    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_IMAGE);
    Intent intent = new Intent(activity, DetailActivity.class);
    intent.putExtra(EXTRA_IMAGE, url);
    ActivityCompat.startActivity(activity, intent, options.toBundle());
  }
}