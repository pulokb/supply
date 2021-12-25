package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samulitfirstproject.supply365.adapter.ProductAdapter;
import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.samulitfirstproject.supply365.model.ProductInfo;

import java.util.ArrayList;

public class ShowAllActivity extends AppCompatActivity {

    private TextView show_all_text;
    private RecyclerView recyclerView_show_all;
    private ImageView load1, ic_back;

    private ProductAdapter productAdapter;
    private SubCategoryAdapter subCategoryAdapter;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayList<ProductInfo> arrayList;
    private ArrayList<CatInfo> cList;

    private int number;
    private boolean isTrue = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_show_all);

        show_all_text = (TextView) findViewById(R.id.show_all_text);
        recyclerView_show_all = (RecyclerView) findViewById(R.id.show_all_recycle);
        load1 = (ImageView) findViewById(R.id.load1);
        ic_back = (ImageView) findViewById(R.id.back);

        Intent intent = this.getIntent();
        number = intent.getIntExtra("number", 0);

        recyclerView_show_all.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3); // 3 = Number of Columns
        recyclerView_show_all.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(), arrayList);
        recyclerView_show_all.setAdapter(productAdapter);


        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (number == 1) {
            databaseReference = FirebaseDatabase.getInstance().getReference("TopDeals");
            show_all_text.setText("Today Best Deals");

        } else if (number == 2) {
            databaseReference = FirebaseDatabase.getInstance().getReference("BestSelling");
            show_all_text.setText("Best Selling Products");

        } else if (number == 3) {
            databaseReference = FirebaseDatabase.getInstance().getReference("PopularProduct");
            show_all_text.setText("Popular products");

        }else if (number == 4) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Products");
            show_all_text.setText("All products");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_show_all.setLayoutManager(linearLayoutManager);

            cList = new ArrayList<>();
            subCategoryAdapter = new SubCategoryAdapter(this, cList);
            recyclerView_show_all.setAdapter(subCategoryAdapter);
            isTrue = false;

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cList.clear();
                    subCategoryAdapter.notifyDataSetChanged();

                    if(snapshot.exists()){

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                CatInfo info = postSnapshot.getValue(CatInfo.class);
                                info.setKey(postSnapshot.getKey());
                                cList.add(info);

                        }
                        subCategoryAdapter.notifyDataSetChanged();
                        load1.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            isTrue = false;
        }


        if (isTrue) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    arrayList.clear();
                    productAdapter.notifyDataSetChanged();

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            String key = postSnapshot.getKey();

                            String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                            ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                            info.setProductKey(ID);
                            info.setDeleteKey(key);
                            arrayList.add(info);

                        }
                        load1.setVisibility(View.GONE);
                        productAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            if (number != 4){
                Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_SHORT).show();
            }

        }
    }
}