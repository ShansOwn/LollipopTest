package com.shansown.android.lollipoptest.photoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.shansown.android.lollipoptest.BaseActivity;
import com.shansown.android.lollipoptest.facebook.FacebookHelper;
import com.shansown.android.lollipoptest.R;
import com.shansown.android.lollipoptest.facebook.SocialUser;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGD;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class PhotoViewerActivity extends BaseActivity implements FacebookHelper.Callback {

    private static final String TAG = makeLogTag(PhotoViewerActivity.class);

    private final static String STATE_ITEM_KEY = PREFIX + ".state.ITEM";
    private final static String STATE_FULLSCREEN_KEY = PREFIX + ".state.FULLSCREEN";
    private final static String STATE_ACTIONBAR_TITLE_KEY = PREFIX + ".state.ACTIONBAR_TITLE";
    private final static String STATE_ACTIONBAR_SUBTITLE_KEY = PREFIX + ".state.ACTIONBAR_SUBTITLE";

    public static final String EXTRA_PHOTOS_URI = PREFIX + ".extra.PHOTOS_URI";
    public static final String EXTRA_PROJECTION = PREFIX + ".extra.PROJECTION";
    public static final String EXTRA_PHOTO_INDEX = PREFIX + ".extra.PHOTOS_URI";
    public static final String EXTRA_INITIAL_PHOTO_URI = PREFIX + ".extra.INITIAL_PHOTO_URI";
    public static final String EXTRA_MAX_INITIAL_SCALE = PREFIX + ".extra.MAX_INITIAL_SCALE";

    private FacebookHelper mFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoviewer);

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mFacebook = new FacebookHelper(this);
        mFacebook.onCreate(savedInstanceState);

        if (!mFacebook.isLogged()) {
            findViewById(R.id.photo_activity_root_view).setVisibility(View.VISIBLE);
            findViewById(R.id.social).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_facebook_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFacebook.login();
                }
            });
        }

        findViewById(R.id.btn_publish_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFacebook.publishFeed(((EditText) findViewById(R.id.feed_msg)).getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mFacebook.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFacebook.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_facebook_logout) {
            logoutFacebook();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebook.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFacebook.onSaveInstanceState(outState);
    }

    @Override
    public void onLoginFacebook() {
        LOGD(TAG, "onLoginFacebook");
        findViewById(R.id.social).setVisibility(View.GONE);
        mFacebook.requestUserInfo();
    }

    @Override
    public void onGetFacebookUserInfo(SocialUser facebookUser) {
        LOGD(TAG, "onGetFacebookUserInfo");
        String userInfo = buildUserInfoDisplay(facebookUser);
        LOGD(TAG, "User Info: " + userInfo);
    }

    private String buildUserInfoDisplay(SocialUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        userInfo.append(String.format("Link: %s\n\n",
                user.getLink()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
//        userInfo.append(String.format("Location: %s\n\n",
//                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
//        userInfo.append(String.format("Locale: %s\n\n",
//                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        /*JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String>();
            for (int i=0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }*/

        return userInfo.toString();
    }

    private void logoutFacebook() {
        mFacebook.logout();
        finish();
    }
}