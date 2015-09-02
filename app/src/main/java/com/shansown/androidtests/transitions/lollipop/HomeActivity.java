package com.shansown.androidtests.transitions.lollipop;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.shansown.androidtests.App;
import com.shansown.android.lollipoptest.R;
import com.shansown.androidtests.BaseActivity;
import com.squareup.picasso.Picasso;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class HomeActivity extends BaseActivity {

  private static final String TAG = makeLogTag(HomeActivity.class);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });

    GridView gridView = (GridView) findViewById(R.id.gridView);
    gridView.setAdapter(new GridViewAdapter(this));
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String url = (String) view.getTag();
        DetailActivity.launch(HomeActivity.this, view.findViewById(R.id.image), url);
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private static class GridViewAdapter extends BaseAdapter {

    private Context mContext;

    private GridViewAdapter(Context context) {
      mContext = context;
      Picasso.with(mContext).setIndicatorsEnabled(true);
    }

    @Override public int getCount() {
      return 10;
    }

    @Override public Object getItem(int i) {
      return "Item " + String.valueOf(i + 1);
    }

    @Override public long getItemId(int i) {
      return i;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup) {
      if (view == null) {
        view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, viewGroup, false);
      }

      String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
      LOGD(TAG, "imageUrl: " + imageUrl);
      view.setTag(imageUrl);

      ImageView image = (ImageView) view.findViewById(R.id.image);
      Picasso.with(App.getAppContext()).load(imageUrl).into(image);

      TextView text = (TextView) view.findViewById(R.id.text);
      text.setText(getItem(i).toString());

      return view;
    }
  }
}