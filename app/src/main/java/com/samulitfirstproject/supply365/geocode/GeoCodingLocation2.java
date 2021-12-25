package com.samulitfirstproject.supply365.geocode;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samulitfirstproject.supply365.model.locationInfo;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GeoCodingLocation2 {
    // This function is called from HomeFragment
    public static void getAddressFromLocation2(final String locationAddress, final Context context, final Handler handler, final String ID, final String WhichType, final String names) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                Double Longitude = null;
                Double Latitude = null;

                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 100);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();

                        // For Store Firebase.......................................................
                        Longitude = address.getLongitude();
                        Latitude = address.getLatitude();
                        // End......................................................................

                        sb.append(address.getLatitude()).append(", ");
                        sb.append(address.getLongitude()).append(" ");
                        result = sb.toString();
                    }
                } catch (Exception e) {
                    // Nothing
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        // Store data on Firebase...................................................
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        if (Objects.equals(WhichType, "Vendor")){
                            locationInfo locationInfo = new locationInfo(Latitude, Longitude, names);
                            databaseReference.child("vendorLocation").child(ID).setValue(locationInfo);

                        }else if (Objects.equals(WhichType, "Distributor")){
                            locationInfo locationInfo = new locationInfo(Latitude, Longitude, names);
                            databaseReference.child("DistributorLocation").child(ID).setValue(locationInfo);
                        }else {
                            databaseReference.child("oneTimeAddress").child("latitude").setValue(Latitude);
                            databaseReference.child("oneTimeAddress").child("longitude").setValue(Longitude);
                        }

                        // End store................................................................

                        result = "Address: " + locationAddress +
                                "\n\nLatitude and Longitude :\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);

                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
