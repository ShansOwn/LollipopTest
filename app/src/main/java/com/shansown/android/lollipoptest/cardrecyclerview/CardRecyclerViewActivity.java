package com.shansown.android.lollipoptest.cardrecyclerview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.shansown.android.lollipoptest.BaseActivity;
import com.shansown.android.lollipoptest.R;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGD;
import static com.shansown.android.lollipoptest.util.LogUtils.LOGV;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class CardRecyclerViewActivity extends BaseActivity
        implements RecyclerView.OnItemTouchListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = makeLogTag(CardRecyclerViewActivity.class);

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private CountryAdapter mAdapter;
    private GestureDetectorCompat mGestureDetector;
    private FrameLayout mAddCountryFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_recycler_view);
        EventBus.getDefault().register(this);

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3
        );
        mSwipeRefresh.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CountryAdapter(CountryManager.getInstance().getCountries(), R.layout.list_item_card, this);
        mRecyclerView.setAdapter(mAdapter);
        // onClickDetection is done in this Activity's onItemTouchListener
        // with the help of a GestureDetector;
        mRecyclerView.addOnItemTouchListener(this);

        mGestureDetector = new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());

        mAddCountryFab = (FrameLayout) findViewById(R.id.add_country_button);
        mAddCountryFab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.country, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_country_button:
                animateAddCountryFab();
                break;
            case R.id.countryCard:
                int position = mRecyclerView.getChildPosition(view);
                Country country = mAdapter.getItem(position);
                CardDetailActivity.launch(this, view.findViewById(R.id.image), country, position);
                break;
        }
    }

    @Override
    public void onRefresh() {
        LOGV(TAG, "Refresh");
        mSwipeRefresh.setRefreshing(true);
        List<Country> newData = CountryManager.getInstance().getCountries();
        mAdapter.refreshData(newData);
        mSwipeRefresh.setRefreshing(false);
    }

    private void animateAddCountryFab() {
        /**
         * Animation View
         */
//                Animation animation = AnimationUtils.loadAnimation(CardRecyclerViewActivity.this, R.anim.add_country_anim);
//                v.startAnimation(animation);

        /**
         * Property Animation
         */
        View addCountryIcon = mAddCountryFab.findViewById(R.id.add_country_icon);

        ValueAnimator scaleInAnimX = ObjectAnimator.ofFloat(mAddCountryFab, "scaleX", 1f, 1.1f);
        ValueAnimator scaleInAnimY = ObjectAnimator.ofFloat(mAddCountryFab, "scaleY", 1f, 1.1f);
        ValueAnimator rotateAnim = ObjectAnimator.ofFloat(addCountryIcon, "rotation", 0f, 180f);
        ValueAnimator scaleOutAnimX = ObjectAnimator.ofFloat(mAddCountryFab, "scaleX", 1.1f, 1f);
        ValueAnimator scaleOutAnimY = ObjectAnimator.ofFloat(mAddCountryFab, "scaleY", 1.1f, 1f);

        AnimatorSet inAnimator = new AnimatorSet();
        inAnimator.setDuration(150);
        inAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet outAnimator = new AnimatorSet();
        outAnimator.setDuration(150);
        outAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        outAnimator.play(scaleOutAnimX).with(scaleOutAnimY);
        inAnimator.play(scaleInAnimX).with(scaleInAnimY).with(rotateAnim).before(outAnimator);
        inAnimator.start();
        int firstVisibleItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        mAdapter.insertRandomItem(firstVisibleItemPosition);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        /**
         * Only if used on item click
         */
//        mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {}

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(CountryEvent event) {
        int position = event.position;
        if (position < 0) {
            LOGD(TAG, "Item view: " + event.countryView);
            position = mRecyclerView.getChildPosition(event.countryView);
            LOGD(TAG, "Item position: " + position);
        }

        switch (event.type) {
            case VIEW:
                Country country = mAdapter.getItem(position);
                CardDetailActivity
                        .launch(this, event.countryView.findViewById(R.id.image), country, position);
                break;
            case DELETE:
                LOGD(TAG, "Item position to delete: " + position);
                deleteItem(position);
                break;
        }
    }

    private void deleteItem(int position) {
        mAdapter.removeItem(position);
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }
    }
}