package com.shansown.androidtests.badge;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shansown.androidtests.BaseActivity;
import com.shansown.androidtests.R;

public class BadgeActivity extends BaseActivity {

  private BadgeView mBadge;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_badge);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(view -> finish());

    View badgeTarget = findViewById(R.id.badge_target);
    mBadge = new BadgeView(this, badgeTarget);
    mBadge.setBadgeMargin(-15);
    mBadge.setText(String.valueOf(0));
    mBadge.show();
  }

  /**
   * Increment number. Called from onclick=""
   */
  public void incrementNumber(View unused) {
    mBadge.increment(10);
  }
}