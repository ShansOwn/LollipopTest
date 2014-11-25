package com.shansown.android.lollipoptest.facebook;

import com.facebook.model.GraphUser;

public class FacebookUser implements SocialUser {

    private GraphUser mDelegate;

    public FacebookUser(GraphUser delegate) {
        mDelegate = delegate;
    }

    @Override
    public String getId() {
        return mDelegate.getId();
    }

    @Override
    public void setId(String id) {
        mDelegate.setId(id);
    }

    @Override
    public String getName() {
        return mDelegate.getName();
    }

    @Override
    public void setName(String name) {
        mDelegate.setName(name);
    }

    @Override
    public String getFirstName() {
        return mDelegate.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        mDelegate.setFirstName(firstName);
    }

    @Override
    public String getMiddleName() {
        return mDelegate.getMiddleName();
    }

    @Override
    public void setMiddleName(String middleName) {
        mDelegate.setMiddleName(middleName);
    }

    @Override
    public String getLastName() {
        return mDelegate.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        mDelegate.setLastName(lastName);
    }

    @Override
    public String getLink() {
        return mDelegate.getLink();
    }

    @Override
    public void setLink(String link) {
        mDelegate.setLink(link);
    }

    @Override
    public String getUsername() {
        return mDelegate.getUsername();
    }

    @Override
    public void setUsername(String username) {
        mDelegate.setUsername(username);
    }

    @Override
    public String getBirthday() {
        return mDelegate.getBirthday();
    }

    @Override
    public void setBirthday(String birthday) {
        mDelegate.setBirthday(birthday);
    }
}