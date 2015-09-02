package com.shansown.androidtests.cardrecyclerview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shansown.androidtests.App;
import com.shansown.androidtests.BaseActivity;
import com.shansown.android.lollipoptest.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class CardDetailActivity extends BaseActivity {

  public static final String EXTRA_IMAGE = "CardDetailActivity:image";
  public static final String EXTRA_DESCRIPTION = "CardDetailActivity:description";
  public static final String EXTRA_POSITION = "CardDetailActivity:position";

  private int mCardPosition;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_card_detail);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });

    Bundle bundle = getIntent().getExtras();

    mCardPosition = bundle.getInt(EXTRA_POSITION);
    int imageResource = bundle.getInt(EXTRA_IMAGE, 0);

    Bitmap bm = BitmapFactory.decodeResource(getResources(), imageResource);
    colorizeToolBar(bm);

    ImageView image = (ImageView) findViewById(R.id.image);
    ViewCompat.setTransitionName(image, EXTRA_IMAGE);
    Picasso.with(App.getAppContext()).load(imageResource).into(image);

    TextView description = (TextView) findViewById(R.id.description);
    String descriptionText = bundle.getString(EXTRA_DESCRIPTION) + " POSITION: " + mCardPosition;
    description.setText(descriptionText);
  }

  private void colorizeToolBar(Bitmap bitmap) {
    Palette palette = Palette.generate(bitmap);
    if (palette.getDarkVibrantSwatch() != null) {
      /**
       * Toolbar version
       */
      getActionBarToolbar().setBackground(
          new ColorDrawable(palette.getDarkVibrantSwatch().getRgb()));

      /**
       * ActionBar version
       */
      //            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(palette.getDarkVibrantSwatch().getRgb()));
    }

    if (palette.getLightVibrantSwatch() != null) {
      /**
       * Toolbar version
       */
      getActionBarToolbar().setTitleTextColor(palette.getLightVibrantSwatch().getRgb());

      /**
       * ActionBar version
       */
      //            int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
      //            if (actionBarTitleId > 0) {
      //                TextView title = (TextView) findViewById(actionBarTitleId);
      //                if (title != null) {
      //                    title.setTextColor(palette.getLightVibrantSwatch().getRgb());
      //                }
      //            }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.country_detail, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_remove) {
      EventBus.getDefault().post(new CountryEvent(CountryEvent.Type.DELETE, mCardPosition));
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static void launch(BaseActivity activity, View transitionView, Country country,
      int position) {
    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_IMAGE);
    Intent intent = new Intent(activity, CardDetailActivity.class);
    intent.putExtra(EXTRA_IMAGE, country.getImageResourceId(activity));
    intent.putExtra(EXTRA_DESCRIPTION, country.description);
    intent.putExtra(EXTRA_POSITION, position);
    ActivityCompat.startActivity(activity, intent, options.toBundle());
  }
}
