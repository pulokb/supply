package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OfferProduct extends AppCompatActivity {

    private ImageView productImage,back,empty;

    private DatabaseReference ProRef,userRef;
    private FirebaseAuth mfirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private SubCategoryAdapter cAdapter;
    private RecyclerView cRecyclerView;
    private ArrayList<CatInfo> cList;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_offer_product);


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ProRef = FirebaseDatabase.getInstance().getReference().child("Products");


        if (firebaseUser != null) {

            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(mfirebaseAuth.getCurrentUser().getUid());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        type = snapshot.child("userType").getValue().toString();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {
            type = " ";
        }

        cRecyclerView = findViewById(R.id.offer_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cList = new ArrayList<>();
        cAdapter = new SubCategoryAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        ProRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        String dis = dataSnapshot.child(key).child("productDiscount").getValue().toString();
                        String cash = dataSnapshot.child(key).child("productCashBack").getValue().toString();
                        String v_dis = dataSnapshot.child(key).child("vendorDiscount").getValue().toString();
                        String v_cash = dataSnapshot.child(key).child("vendorCashBack").getValue().toString();

                        if (type.equals("Vendor")){

                            if (!v_dis.equals(" ") || !v_cash.equals(" ") ){
                                CatInfo info = postSnapshot.getValue(CatInfo.class);
                                info.setKey(postSnapshot.getKey());
                                cList.add(info);
                            }

                        }else {
                            if (!dis.equals(" ") || !cash.equals(" ") ){
                                CatInfo info = postSnapshot.getValue(CatInfo.class);
                                info.setKey(postSnapshot.getKey());
                                cList.add(info);
                            }

                        }

                    }
                    cAdapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OfferProduct.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });



    }
}