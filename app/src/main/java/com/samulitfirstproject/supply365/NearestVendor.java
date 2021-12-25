package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NearestVendor extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dataRef, databaseReference2;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Circle circle2;

    private String currID;
    private String intent, vendorName, person_id, markers, vendorEmail;
    private boolean isVendor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nearest_vendor);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent().getStringExtra("whichType");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        currID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("UserAddress").child(currID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng CustomerLocation = new LatLng(
                        snapshot.child("latitude").getValue(Double.class),
                        snapshot.child("longitude").getValue(Double.class)
                );
                mMap.clear();
                circle2 = mMap.addCircle(new CircleOptions()
                        .center(CustomerLocation)
                        // if need change radius change there if you fix 8000<n then circle will be continue to get bigger *****
                        .radius(10000)
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

        if (intent.equals("Vendor")) {
            dataRef = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("DistributorLocation");
            isVendor = true;
        }else {
            dataRef = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("VendorLocation");
            isVendor = false;
        }

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        float[] distance = new float[500];
                        vendorName = ds.child("names").getValue(String.class);
                        vendorEmail = snapshot.child("email").getValue(String.class);
                        String vendorID = snapshot.getKey();

                        LatLng vendorLocation = new LatLng(
                                ds.child("latitude").getValue(Double.class),
                                ds.child("longitude").getValue(Double.class)
                        );
                        Location.distanceBetween(vendorLocation.latitude, vendorLocation.longitude, circle2.getCenter().latitude, circle2.getCenter().longitude, distance);

                        if (distance[0] <= circle2.getRadius()) {
                            // Inside The Circle
                            mMap.addMarker(new MarkerOptions()
                                    .position(vendorLocation)
                                    .title(vendorName)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            );
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markers = marker.getTitle();

                if (isVendor){
                    databaseReference2 = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("DistributorLocation");
                }else {
                    databaseReference2 = FirebaseDatabase.getInstance().getReference("ShowOnMap").child("VendorLocation");
                }

                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String Names = ds.child("names").getValue(String.class);
                            String Email = ds.child("email").getValue(String.class);

                            if (!Objects.equals(markers, "Your Location") && Objects.equals(Names, markers)){
                                person_id = ds.getKey();
                                Intent gotoVendorDetails = new Intent(NearestVendor.this, VendorDetails.class);
                                gotoVendorDetails.putExtra("person_uid", person_id);
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