package com.samulitfirstproject.supply365;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.samulitfirstproject.supply365.adapter.VendorProductAdapter;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageVendorProduct extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference VendorRef,ProRef;

    private RecyclerView vRecyclerView;
    private VendorProductAdapter vAdapter;
    private ArrayList<ProductInfo> vList;
    private ArrayList<String> temp = new ArrayList<>();
    private String current_user_id,type;
    private ImageView back;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_manage_vendor_product);


        type = getIntent().getStringExtra("type");


        back = findViewById(R.id.back);
        search = findViewById(R.id.search_field);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        mfirebaseAuth = FirebaseAuth.getInstance();
        current_user_id = mfirebaseAuth.getCurrentUser().getUid();
        VendorRef = FirebaseDatabase.getInstance().getReference("MyProduct").child(current_user_id);
        ProRef = FirebaseDatabase.getInstance().getReference("Products");

        vRecyclerView = findViewById(R.id.manage_product_rv);
        vRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        vRecyclerView.setLayoutManager(linearLayoutManagerV);

        vList = new ArrayList<>();
        vAdapter = new VendorProductAdapter(this, vList);
        vRecyclerView.setAdapter(vAdapter);

        if (type.equals("vendor")){


            search.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void afterTextChanged(Editable mEdit)
                {

                    if(search.getText().toString().trim().length()>0){


                        String text = search.getText().toString().trim();

                        text = text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();

                        Query query = ProRef.orderByChild("productName").startAt(text).endAt(text+"\uf8ff");
                        vendorSearchProduct(query);


                    }else {

                        Query query = ProRef.orderByChild("productName");
                        vendorSearchProduct(query);

                    }



                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after){}

                public void onTextChanged(CharSequence s, int start, int before, int count){}
            });


            Query query = ProRef.orderByChild("productName");
            vendorSearchProduct(query);


        }
        else if(type.equals("admin")){


            search.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void afterTextChanged(Editable mEdit)
                {

                    if(search.getText().toString().trim().length()>0){


                        String text = search.getText().toString().trim();

                        text = text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();

                        Query query = ProRef.orderByChild("productName").startAt(text).endAt(text+"\uf8ff");
                        searchProduct(query);


                    }else {

                        Query query = ProRef.orderByChild("productName");
                        searchProduct(query);
                    }



                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after){}

                public void onTextChanged(CharSequence s, int start, int before, int count){}
            });


            Query query = ProRef.orderByChild("productName");
            searchProduct(query);


        }




    }

    private void vendorSearchProduct(Query query) {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                temp.clear();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        temp.add(key);

                    }

                    VendorRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            vList.clear();
                            vAdapter.notifyDataSetChanged();

                            if(dataSnapshot.exists()){

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    String key = postSnapshot.getKey();

                                    String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                                    if (temp.contains(ID)){

                                        ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                                        info.setProductKey(ID);
                                        info.setDeleteKey(key);
                                        vList.add(info);

                                    }


                                }
                                vAdapter.notifyDataSetChanged();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ManageVendorProduct.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageVendorProduct.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void searchProduct(Query query) {



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                vList.clear();
                vAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                        info.setProductKey(postSnapshot.getKey());
                        vList.add(info);

                    }
                    vAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageVendorProduct.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}