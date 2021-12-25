package com.samulitfirstproject.supply365;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class CartActivity extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CartRef;

    private RecyclerView cRecyclerView;
    private CartAdapter cAdapter;
    private ArrayList<CatInfo> cList;
    private String current_user_id=" ";

    private ImageView back,empty_cart;
    private Button makeOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_cart);

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
        }

        CartRef = FirebaseDatabase.getInstance().getReference("MyCart").child(current_user_id);

        back = findViewById(R.id.back);
        empty_cart = findViewById(R.id.empty_cart);
        makeOrder = findViewById(R.id.make_an_order);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cRecyclerView = findViewById(R.id.cart_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManagerV);

        cList = new ArrayList<>();
        cAdapter = new CartAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        CartRef.addValueEventListener(new ValueEventListener() {
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
                    empty_cart.setVisibility(View.VISIBLE);
                    makeOrder.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });




        cAdapter.notifyDataSetChanged();
        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cAdapter.notifyDataSetChanged();
                ArrayList<String> itemList = new ArrayList<String>();
                ArrayList<String> priceList = new ArrayList<String>();
                ArrayList<String> qList = new ArrayList<String>();
                ArrayList<String> idList = new ArrayList<String>();

                int found = 0;

                for (CatInfo cat : cList) {
                    if (cat.isSelected()) {
                        itemList.add(cat.getItem()) ;
                        priceList.add(cat.getPrice()) ;
                        qList.add(cat.getQuantity());
                        idList.add(cat.getKey());

                        found =1;
                    }
                }
                cAdapter.notifyDataSetChanged();

                if (found==1){

                    Intent intent = new Intent(CartActivity.this, ProductSummary.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("item", itemList);
                    bundle.putStringArrayList("price", priceList);
                    bundle.putStringArrayList("quantity", qList);
                    bundle.putStringArrayList("product_id", idList);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else {
                    Toast.makeText(CartActivity.this, "Select at least one item", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}