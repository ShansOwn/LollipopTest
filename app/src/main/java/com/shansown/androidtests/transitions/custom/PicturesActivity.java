/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shansown.androidtests.transitions.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.shansown.androidtests.App;
import com.shansown.androidtests.BaseActivity;
import com.shansown.androidtests.R;
import com.squareup.picasso.Picasso;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

/**
 * This example shows how to create a custom activity animation when you want something more
 * than window animations can provide. The idea is to disable window animations for the
 * activities and to instead launch or return from the sub-activity immediately, but use
 * property animations inside the activities to customize the transition.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the androiddevelopers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class PicturesActivity extends BaseActivity {

  private static final String TAG = makeLogTag(PicturesActivity.class);

  protected static float sAnimatorScale = 1;

  private GridView mGridView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pictures);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(view -> finish());

    mGridView = (GridView) findViewById(R.id.gridView);
    mGridView.setAdapter(new GridViewAdapter(this));
    mGridView.setOnItemClickListener(mThumbnailClickListener);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_better_window_animations, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_slow) {
      sAnimatorScale = item.isChecked() ? 1 : 5;
      item.setChecked(!item.isChecked());
    }
    return super.onOptionsItemSelected(item);
  }

  private static class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ColorMatrixColorFilter mGrayScaleFilter;

    private GridViewAdapter(Context context) {
      mContext = context;
      Picasso.with(mContext).setIndicatorsEnabled(true);
      // Grayscale filter used on all thumbnails
      ColorMatrix grayMatrix = new ColorMatrix();
      grayMatrix.setSaturation(0);
      mGrayScaleFilter = new ColorMatrixColorFilter(grayMatrix);
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

    @Override public View getView(int i, View convertView, ViewGroup parent) {

      ViewDataHolder holder;
      View rowView = convertView;

      if (rowView == null) {
        rowView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_pictures, parent, false);
        holder = new ViewDataHolder();
        holder.imageView = (ImageView) rowView.findViewById(R.id.image);
        holder.imageView.setColorFilter(mGrayScaleFilter);
        rowView.setTag(holder);
      } else {
        holder = (ViewDataHolder) rowView.getTag();
      }

      String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
      LOGD(TAG, "imageUrl: " + imageUrl);
      holder.imageUrl = imageUrl;
      holder.description = "This is description for image: " + imageUrl;

      Picasso.with(App.getAppContext()).load(imageUrl).into(holder.imageView);

      return rowView;
    }
  }

  private static class ViewDataHolder {
    public ImageView imageView;
    public String imageUrl;
    public String description;
  }

  /**
   * When the user clicks a thumbnail, bundle up information about it and launch the
   * details activity.
   */
  private AdapterView.OnItemClickListener mThumbnailClickListener =
      (adapterView, view, position, id) -> {
        // Interesting data to pass across are the thumbnail size/location, the
        // resourceId of the source bitmap, the picture description, and the
        // orientation (to avoid returning back to an obsolete configuration if
        // the device rotates again in the meantime)
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        ViewDataHolder info = (ViewDataHolder) view.getTag();
        Intent subActivity = new Intent(PicturesActivity.this, PictureDetailsActivity.class);
        int orientation = getResources().getConfiguration().orientation;
        subActivity.putExtra(PREFIX + ".orientation", orientation)
            .putExtra(PREFIX + ".url", info.imageUrl)
            .putExtra(PREFIX + ".left", screenLocation[0])
            .putExtra(PREFIX + ".top", screenLocation[1])
            .putExtra(PREFIX + ".width", view.getWidth())
            .putExtra(PREFIX + ".height", view.getHeight())
            .putExtra(PREFIX + ".description", info.description);
        startActivity(subActivity);

        // Override transitions: we don't want the normal window animation in addition
        // to our custom one
        overridePendingTransition(0, 0);
      };
}