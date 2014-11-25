package com.shansown.android.lollipoptest.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

import java.util.List;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGD;
import static com.shansown.android.lollipoptest.util.LogUtils.LOGV;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class FacebookHelper {

    private static final String TAG = makeLogTag(FacebookHelper.class);

    private static final String PATH_MY_PHOTOS = "me/photos";
    private static final String PATH_MY_FEED = "/me/feed";

    private static final String PERMISSION_PHOTOS = "user_photos";
    private static final String PERMISSION_PUBLISH = "publish_actions";

    private static final String PARAM_FEED_MSG = "message";

    private Session.StatusCallback mStatusCallback = new SessionStatusCallback();
    private Callback mCallback;

//    private PendingAction mPendingAction = PendingAction.NONE;

    private Activity mActivity;
    private Fragment mFragment;
    private boolean mIsFromFragment;

    public interface Callback {
        void onLoginFacebook();
        void onGetFacebookUserInfo(SocialUser user);
    }

    /*private enum PendingAction {
        NONE,
        LOGIN,
        GET_USER_INFO,
        GET_USER_PHOTOS,
        POST_STATUS_UPDATE
    }*/

    public FacebookHelper(Activity activity) {
        mActivity = activity;
        mCallback = (Callback) activity;
    }

    public FacebookHelper(Fragment fragment) {
        mFragment = fragment;
        mActivity = fragment.getActivity();
        mIsFromFragment = true;
        mCallback = (Callback) fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        LOGV(TAG, "onCreate");
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = getSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(mActivity, null, mStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(mActivity);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                if (mIsFromFragment) {
                    session.openForRead(new Session.OpenRequest(mFragment).setCallback(mStatusCallback));
                } else {
                    session.openForRead(new Session.OpenRequest(mActivity).setCallback(mStatusCallback));
                }
            }
        }
    }

    public void onStart() {
        LOGV(TAG, "onStart");
        getSession().addCallback(mStatusCallback);
    }

    public void onStop() {
        LOGV(TAG, "onStop");
        getSession().removeCallback(mStatusCallback);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOGV(TAG, "onActivityResult");
        getSession().onActivityResult(mActivity, requestCode, resultCode, data);
    }

    public void onSaveInstanceState(Bundle outState) {
        LOGV(TAG, "onSaveInstanceState");
        Session session = getSession();
        Session.saveSession(session, outState);
    }

    public void login() {
        LOGD(TAG, "login");
//        mPendingAction = PendingAction.LOGIN;
        if (isLogged()) {
            LOGD(TAG, "Already logged in");
        } else {
            Session session = getSession();
            if (!session.isOpened() && !session.isClosed()) {
                LOGD(TAG, "TRUE: !session.isOpened() && !session.isClosed()");
                if (mIsFromFragment) {
                    session.openForPublish(new Session.OpenRequest(mFragment).setCallback(mStatusCallback));
                } else {
                    session.openForPublish(new Session.OpenRequest(mActivity).setCallback(mStatusCallback));
                }
            } else {
                LOGD(TAG, "FALSE: !session.isOpened() && !session.isClosed()");
                if (mIsFromFragment) {
                    Session.openActiveSession(mActivity, mFragment, true, mStatusCallback);
                } else {
                    Session.openActiveSession(mActivity, true, mStatusCallback);
                }
            }
        }
    }

    public void logout() {
        LOGD(TAG, "logout");
        if (isLogged()) {
            getSession().closeAndClearTokenInformation();
        } else {
            LOGD(TAG, "Already logged out");
        }
    }

    public boolean isLogged() {
        Session session = getSession();
        boolean result = (session != null && session.isOpened());
        LOGD(TAG, "isLogged: " + result);
        return result;
    }

    public void requestUserInfo() {
        LOGD(TAG, "requestUserInfo");
//        mPendingAction = PendingAction.GET_USER_INFO;
        Request.newMeRequest(getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser graphUser, Response response) {
                if (graphUser != null) {
                    LOGD(TAG, "User Info: " + response.getRawResponse());
                    mCallback.onGetFacebookUserInfo(new FacebookUser(graphUser));
                }
            }
        }).executeAsync();
    }

    public void requestUserPhotos() {
        LOGD(TAG, "requestUserPhotos");
//        mPendingAction = PendingAction.GET_USER_PHOTOS;
        getPermissions();
        new Request(
                getSession(),
                PATH_MY_PHOTOS,
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        LOGD(TAG, "User Photos: " + response.getRawResponse());
                    }
                }
        ).executeAsync();
    }

    public String getAccessToken() {
        return getSession().getAccessToken();
    }

    /*private void handlePendingAction() {
        LOGD(TAG, "handlePendingAction");
        PendingAction previouslyPendingAction = mPendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        mPendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case LOGIN:
                LOGD(TAG, "Action: LOGIN");
                mCallback.onLoginFacebook();
                break;
            case GET_USER_INFO:
                LOGD(TAG, "Action: LOGIN");
                mCallback.onGetFacebookUserInfo();
            case POST_STATUS_UPDATE:
                LOGD(TAG, "Action: POST_STATUS_UPDATE");
                break;
        }
    }*/

    private List<String> getPermissions() {
        List<String> permissions = getSession().getPermissions();
        LOGD(TAG, "Permissions: " + permissions);
        return permissions;
    }

    public void publishFeed(String msg) {
        Session session = getSession();
        if (hasPublishPermission()) {
            Bundle params = new Bundle();
            params.putString(PARAM_FEED_MSG, msg);
            new Request(
                    session,
                    PATH_MY_FEED,
                    params,
                    HttpMethod.POST,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            LOGD(TAG, "publishFeed: " + response.getRawResponse());

                        }
                    }
            ).executeAsync();
        } else if (session.isOpened()) {
            // We need to get new permissions, then complete the action when we get called back.
            requestNewPermission(PERMISSION_PUBLISH);
        }
    }

    private void requestNewPermission(String permission) {
        Session session = getSession();
        switch (permission) {
            case PERMISSION_PUBLISH:
                if (mIsFromFragment) {
                    session.requestNewPublishPermissions(
                            new Session.NewPermissionsRequest(mFragment, PERMISSION_PUBLISH));
                } else {
                    session.requestNewPublishPermissions(
                            new Session.NewPermissionsRequest(mActivity, PERMISSION_PUBLISH));
                }
                break;
        }
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains(PERMISSION_PUBLISH);
    }

    private Session getSession() {
        return Session.getActiveSession();
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            LOGV(TAG, "SessionStatusCallback. Session: " + session + " SessionState: "
                    + state + " Exception: " + exception);
            if (state.isOpened()) {
                LOGV(TAG, "Session is OPENED");
                LOGD(TAG, "access token: " + session.getAccessToken());
                mCallback.onLoginFacebook();
            }
        }
    }
}