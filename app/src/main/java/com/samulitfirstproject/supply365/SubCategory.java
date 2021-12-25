package com.samulitfirstproject.supply365;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCategory extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference ProRef;

    private RecyclerView cRecyclerView;
    private SubCategoryAdapter cAdapter;
    private ArrayList<CatInfo> cList;
    private String current_user_id = " ",category;
    private TextView textView;
    private ImageView back,empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_sub_category);

        textView = findViewById(R.id.tv_sub_category);

        back = findViewById(R.id.back);
        empty = findViewById(R.id.empty_pro);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        Intent i = getIntent();
        category = i.getStringExtra("category_id");

        textView.setText("Category: "+category);

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

        }
        ProRef = FirebaseDatabase.getInstance().getReference("Products");


        cRecyclerView = findViewById(R.id.sub_category_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManagerV);

        cList = new ArrayList<>();
        cAdapter = new SubCategoryAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        ProRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    long count = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        String check = dataSnapshot.child(key).child("productSubCategory").getValue().toString();

                        if(check.equals(category)){

                            CatInfo info = postSnapshot.getValue(CatInfo.class);
                            info.setKey(postSnapshot.getKey());
                            cList.add(info);

                            count++;
                        }

                    }
                    cAdapter.notifyDataSetChanged();

                    if (count==0){
                            empty.setVisibility(View.VISIBLE);
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubCategory.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }
}