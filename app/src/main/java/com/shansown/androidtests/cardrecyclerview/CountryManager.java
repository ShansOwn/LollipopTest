package com.shansown.androidtests.cardrecyclerview;

import java.util.ArrayList;
import java.util.List;

public class CountryManager {

  private static final String[] countryArray =
      { "Australia", "China", "Italy", "Japan", "United Kingdom", "United States" };
  private static final String loremIpsum =
      "Lorem ipsum dolor sit amet, consectetur adipisicing elit, "
          + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
          + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi "
          + "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit "
          + "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
          + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia "
          + "deserunt mollit anim id est laborum.";

  private static CountryManager sInstance;
  private List<Country> countries;

  public static CountryManager getInstance() {
    if (sInstance == null) {
      sInstance = new CountryManager();
    }
    return sInstance;
  }

  public Country getRandomCountry() {
    int position = (int) (Math.random() * (countryArray.length - 1));
    Country result = new Country();
    String countryName = countryArray[position];
    result.name = countryName;
    result.description = loremIpsum;
    result.imageName = countryName.replaceAll("\\s+", "").toLowerCase();
    return result;
  }

  public List<Country> getCountries() {
    if (countries == null) {
      countries = new ArrayList<>(countryArray.length);

      for (String countryName : countryArray) {
        Country country = new Country();
        country.name = countryName;
        country.description = loremIpsum;
        country.imageName = countryName.replaceAll("\\s+", "").toLowerCase();
        countries.add(country);
      }
    }
    return new ArrayList<>(countries);
  }
}