package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DistributorActivity extends AppCompatActivity {

    private CardView manageProduct,addProduct,orderRequest;
    private TextView vedorName;
    private DatabaseReference vendorRef;
    private FirebaseAuth mAuth;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor);

        addProduct = findViewById(R.id.cv_add_product);
        manageProduct = findViewById(R.id.cv_manage_product);
        vedorName = findViewById(R.id.tv_welcome_vendor);
        back = findViewById(R.id.back);
        orderRequest = findViewById(R.id.cv_dis_order_request);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        vendorRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(uid);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        vendorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String name = snapshot.child("userName").getValue().toString();
                    vedorName.setText ("Welcome "+name);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(DistributorActivity.this, ShowAllProduct.class);
                startActivity(n);

            }
        });


        manageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(DistributorActivity.this, ManageVendorProduct.class);
                n.putExtra("type", "vendor");
                startActivity(n);

            }
        });

        orderRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(DistributorActivity.this, MyOrder.class);
                n.putExtra("person_id","distributor");
                startActivity(n);

            }
        });
    }
}