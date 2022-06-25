package com.lifepharmacy.application.ui.address;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lifepharmacy.application.R;
import com.lifepharmacy.application.model.address.AddressTypeModel;

import java.util.ArrayList;

/**
 * Created by Zahid Ali
 */
public class AddressSpinnerAdapter extends ArrayAdapter<AddressTypeModel> {

  Context context;

  public AddressSpinnerAdapter(Context context,
                               ArrayList<AddressTypeModel> algorithmList) {
    super(context, 0, algorithmList);
    this.context = context;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable
      View convertView, @NonNull ViewGroup parent) {
    return initView(position, convertView, parent);
  }

  @Override
  public View getDropDownView(int position, @Nullable
      View convertView, @NonNull ViewGroup parent) {
    return initDropDownView(position, convertView, parent);
  }

  private View initDropDownView(int position, View convertView,
                                ViewGroup parent) {
    // It is used to set our custom view.
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_address_spinner_drop_down, parent, false);
    }

    TextView textViewName = convertView.findViewById(R.id.ed_type);
    AddressTypeModel currentItem = getItem(position);

    // It is used the name to the TextView when the
    // current item is not null.
    if (currentItem != null) {
      Drawable drawable;
      textViewName.setText(currentItem.getName());

      if (currentItem.getAddressId() == 1) {
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_location);
      } else if (currentItem.getAddressId() == 2) {
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_work_location);
      } else {
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_fav_location);

      }
      textViewName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }
    return convertView;
  }

  private View initView(int position, View convertView,
                        ViewGroup parent) {
    // It is used to set our custom view.
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_address_spinner, parent, false);
    }

    TextView textViewName = convertView.findViewById(R.id.ed_type);

    AddressTypeModel currentItem = getItem(position);

    // It is used the name to the TextView when the
    // current item is not null.
    if (currentItem != null) {

      textViewName.setText(currentItem.getName());
    }
    return convertView;
  }
}