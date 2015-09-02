package com.shansown.androidtests.cardrecyclerview;

import android.view.View;

public class CountryEvent {

  public Type type;
  public View countryView;
  public int position = -1;

  public enum Type {
    VIEW, DELETE
  }

  public CountryEvent(Type type, int position) {
    this.type = type;
    this.position = position;
  }

  public CountryEvent(Type type, View countryView) {
    this.type = type;
    this.countryView = countryView;
  }
}
