package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VendorDetails extends AppCompatActivity {

    private TextView name,email,phone,location,tvVendor;
    private String vendorKey;
    private DatabaseReference vendorRef,uRef;
    private FirebaseAuth mFirebaseAuth;
    private CircleImageView profile;
    private ImageView back;

    private RecyclerView cRecyclerView;
    private SubCategoryAdapter cAdapter;
    private ArrayList<CatInfo> cList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_vendor_details);

        name = findViewById(R.id.tv_vendor_name);
        email = findViewById(R.id.tv_vendor_email);
        phone = findViewById(R.id.tv_vendor_phone);
        location = findViewById(R.id.tv_vendor_location);
        profile = findViewById(R.id.vendorDP);
        tvVendor = findViewById(R.id.tv_vendor_details);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        vendorKey = getIntent().getStringExtra("person_uid");

        uRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(vendorKey);
        vendorRef = FirebaseDatabase.getInstance().getReference().child("MyProduct").child(vendorKey);

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String n = snapshot.child("userName").getValue().toString();
                    String url = snapshot.child("userImage").getValue().toString();
                    String e = snapshot.child("userEmail").getValue().toString();
                    String p = snapshot.child("userPhone").getValue().toString();
                    String l = snapshot.child("userLocation").getValue().toString();
                    String t = snapshot.child("userType").getValue().toString();


                    name.setText(n);
                    email.setText(e);
                    phone.setText(p);
                    location.setText(l);

                    if (!url.equals(" ")) {

                        Picasso.get().load(url).placeholder(R.drawable.loading_gif__).fit().centerCrop().into(profile);
                    }

                    if (!t.equals("Vendor")){
                        tvVendor.setText("Distributor Details");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cRecyclerView = findViewById(R.id.available_pro_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        cRecyclerView.setLayoutManager(linearLayoutManager);

        cList = new ArrayList<>();
        cAdapter = new SubCategoryAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        vendorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                        CatInfo info = postSnapshot.getValue(CatInfo.class);
                                info.setKey(ID);
                                cList.add(info);

                    }
                    cAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VendorDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }
}