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
import com.samulitfirstproject.supply365.adapter.TopAdapter;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowAllProduct extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference ProRef;

    private RecyclerView vRecyclerView;
    private TopAdapter tAdapter;
    private ArrayList<ProductInfo> tList;
    private String current_user_id;
    private ImageView back;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_show_all_product);


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
        ProRef = FirebaseDatabase.getInstance().getReference("Products");

        vRecyclerView = findViewById(R.id.add_top_rv);
        vRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        vRecyclerView.setLayoutManager(linearLayoutManagerV);

        tList = new ArrayList<>();
        tAdapter = new TopAdapter(this, tList);
        vRecyclerView.setAdapter(tAdapter);


        search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {

                String text ="";
                if(search.getText().toString().trim().length()>0){

                    //sRecyclerView.setVisibility(View.VISIBLE);

                    text = search.getText().toString().trim();

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

    private void searchProduct(Query query) {



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tList.clear();
                tAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                        info.setProductKey(postSnapshot.getKey());
                        tList.add(info);

                    }
                    tAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowAllProduct.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}