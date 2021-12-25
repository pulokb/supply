package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private Circle circle;
    private ProgressDialog progressDialog;


    private String OderId, vendorID, OderUID, markers, vendorName, vendorEmail;
    private double Longitude, Latitude;
    private boolean isVendor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
        progressDialog.show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Customer Location .......................................................................
        databaseReference = FirebaseDatabase.getInstance().getReference().child("oneTimeAddress");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng CustomerLocation = new LatLng(
                        snapshot.child("latitude").getValue(Double.class),
                        snapshot.child("longitude").getValue(Double.class)
                );
                mMap.clear();
                circle = mMap.addCircle(new CircleOptions()
                        .center(CustomerLocation)
                        // if need change radius change there if you fix 8000<n then circle will be continue to get bigger *****
                        .radius(8000)
                        .strokeWidth(3)
                        .strokeColor(Color.RED)
                        .fillColor(Color.GRAY)
                );
                mMap.addMarker(new MarkerOptions().position(CustomerLocation).title("Customer Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomerLocation, 12));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // End .....................................................................................


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //final String[] Name = new String[1];
                OderUID = getIntent().getStringExtra("OderUID");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersData").child(OderUID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String type = snapshot.child("userType").getValue(String.class);
                        //Log.i("DATA DATA DATA : " , type);

                        if (type.equals("Vendor")) {
                            databaseReference2 = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("DistributorLocation");
                            isVendor = true;
                        }else {
                            databaseReference2 = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("VendorLocation");
                            isVendor = false;
                        }

                        databaseReference2.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                try {
                                    float[] distance = new float[500];
                                    vendorName = snapshot.child("names").getValue(String.class);
                                    vendorEmail = snapshot.child("email").getValue(String.class);
                                    vendorID = snapshot.getKey();

                                    LatLng vendorLocation = new LatLng(
                                            snapshot.child("latitude").getValue(Double.class),
                                            snapshot.child("longitude").getValue(Double.class)
                                    );
                                    Location.distanceBetween(vendorLocation.latitude, vendorLocation.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                    if (distance[0] <= circle.getRadius()) {
                                        // Inside The Circle
                                        mMap.addMarker(new MarkerOptions()
                                                .position(vendorLocation)
                                                .title(vendorEmail)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        );

                                    } else {
                                        // Outside The Circle
                                    }

                                    progressDialog.dismiss();

                                }catch (Exception e){
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        // End .....................................................................................
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        },3500);



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markers = marker.getTitle();
                OderId = getIntent().getStringExtra("OderID");

                if (isVendor){
                    databaseReference = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("DistributorLocation");
                }else {
                    databaseReference = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("VendorLocation");
                }

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String Names = ds.child("names").getValue(String.class);
                            String Email = ds.child("email").getValue(String.class);

                            if (!Objects.equals(markers, "Customer Location") && Objects.equals(Email, markers)){
                                vendorID = ds.getKey();

                                Intent gotoVendorDetails = new Intent(MapsActivity.this, Admin_Product_Transfer2_Activity.class);
                                gotoVendorDetails.putExtra("vendorID", vendorID);
                                gotoVendorDetails.putExtra("OderId", OderId);
                                startActivity(gotoVendorDetails);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return false;
            }
        });


    }

}