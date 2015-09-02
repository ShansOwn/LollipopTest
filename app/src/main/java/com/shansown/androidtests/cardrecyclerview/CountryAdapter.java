package com.shansown.androidtests.cardrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shansown.androidtests.App;
import com.shansown.androidtests.R;
import com.shansown.androidtests.util.UIUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder>
    implements View.OnClickListener {

  private static final String TAG = makeLogTag(CountryAdapter.class);

  private List<Country> countries;

  private int rowLayout;
  private Context mContext;

  public CountryAdapter(List<Country> countries, int rowLayout, Context context) {
    this.countries = countries;
    this.rowLayout = rowLayout;
    this.mContext = context;
  }

  /**
   * Adds and item into the underlying data set
   * at the position passed into the method.
   *
   * @param country The item to add to the data set.
   * @param position The index of the item to remove.
   */
  public void addData(Country country, int position) {
    countries.add(position, country);
    notifyItemInserted(position);
  }

  public void insertRandomItem(int position) {
    position++;
    Country newData = CountryManager.getInstance().getRandomCountry();
    countries.add(position, newData);
    notifyItemInserted(position);
  }

  public Country getItem(int position) {
    return countries.get(position);
  }

  /**
   * Removes the item that currently is at the passed in position from the
   * underlying data set.
   *
   * @param position The index of the item to remove.
   */
  public void removeItem(int position) {
    countries.remove(position);
    notifyItemRemoved(position);
  }

  public void refreshData(List<Country> newData) {
    countries.clear();
    countries.addAll(newData);
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
    v.setOnClickListener(this);
    v.findViewById(R.id.delete).setOnClickListener(this);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    Country country = countries.get(i);
    viewHolder.countryName.setText(country.name);
    Picasso.with(App.getAppContext())
        .load(country.getImageResourceId(mContext))
        .into(viewHolder.countryImage);
  }

  @Override public int getItemCount() {
    return countries == null ? 0 : countries.size();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.delete:
        LOGD(TAG, "Click on DELETE");
        EventBus.getDefault()
            .post(new CountryEvent(CountryEvent.Type.DELETE,
                UIUtils.findParentRecursively(v, R.id.countryCard)));
        break;
      case R.id.countryCard:
        LOGD(TAG, "Click on ITEM");
        EventBus.getDefault().post(new CountryEvent(CountryEvent.Type.VIEW, v));
        break;
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView countryName;
    public ImageView countryImage;

    public ViewHolder(View itemView) {
      super(itemView);
      countryName = (TextView) itemView.findViewById(R.id.countryName);
      countryImage = (ImageView) itemView.findViewById(R.id.countryImage);
    }
  }
}