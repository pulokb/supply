package com.samulitfirstproject.supply365;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.adapter.CartAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference WishRef;

    private RecyclerView cRecyclerView;
    private CartAdapter cAdapter;
    private ArrayList<CatInfo> cList;
    private String current_user_id=" ";

    private ImageView back,empty_wish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_wishlist);

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
        }

        WishRef = FirebaseDatabase.getInstance().getReference("WishList").child(current_user_id);

        back = findViewById(R.id.back);
        empty_wish = findViewById(R.id.empty_wish);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cRecyclerView = findViewById(R.id.wish_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManagerV);

        cList = new ArrayList<>();
        cAdapter = new CartAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        WishRef.addValueEventListener(new ValueEventListener() {
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

                }else {
                    empty_wish.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WishlistActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }

}