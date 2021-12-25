package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.samulitfirstproject.supply365.model.Search;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText eSearch;
    private RecyclerView sRecyclerView;
    private DatabaseReference ProRef;
    private ImageView empty,back;
    private String userType;
    private boolean checkProKey;
    private int limit;

    FirebaseRecyclerAdapter<Search,SearchViewHolder> firebaseRecyclerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_search);

        eSearch = findViewById(R.id.search_field);
        empty = findViewById(R.id.empty_product);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sRecyclerView = findViewById(R.id.search_rv);
        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProRef = FirebaseDatabase.getInstance().getReference("Products");


        eSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {

                if(eSearch.getText().toString().trim().length()>0){

                    sRecyclerView.setVisibility(View.VISIBLE);

                    String text = eSearch.getText().toString().trim();

                    text = text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();

                    searchProduct(text);

//                    if (text.equals(text.toUpperCase())){
//                        searchProduct(text);
//                    }else {
//                        searchProduct(text.toUpperCase());
//                    }

                }else {
                    sRecyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);
                }



            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });



    }

    private void searchProduct(String search) {


        FirebaseRecyclerOptions<Search> options =
                new FirebaseRecyclerOptions.Builder<Search>()
                        .setQuery(ProRef.orderByChild("productName").startAt(search).endAt(search+"\uf8ff"),Search.class)
                        .build();



            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder holder, final int i, @NonNull Search search) {

                /*
                String name = search.getProductName();
                String des = search.getProductDescription();
                String price =search.getProductPrice();
                String url = search.getProductImage();
                 */

                final String key = firebaseRecyclerAdapter.getRef(i).getKey();

                holder.userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            userType = snapshot.child("userType").getValue().toString();
                        }else {
                            userType = " ";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.ProRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String name = snapshot.child("productName").getValue().toString();
                            String price = snapshot.child("productPrice").getValue().toString();
                            String wholeSale = snapshot.child("productWholeSalesPrice").getValue().toString();
                            String des = snapshot.child("productDescription").getValue().toString();
                            String url = snapshot.child("productImage").getValue().toString();
                            String dis = snapshot.child("productDiscount").getValue().toString();
                            String cash = snapshot.child("productCashBack").getValue().toString();

                            String v_dis = snapshot.child("vendorDiscount").getValue().toString();
                            String v_cash = snapshot.child("vendorCashBack").getValue().toString();


                            boolean isTrue;
                            double strikePrice_cut;
                            String strikePrice_string, percentage_string;
                            String digits;

                            if (userType.equals("Vendor")){
                                if(!v_dis.equals(" ") || !v_cash.equals(" ") ){

                                    if (!v_dis.equals(" ")){
                                        holder.productDis.setVisibility(View.GONE);
                                        holder.percentage.setVisibility(View.VISIBLE);
                                        holder.productPercentage.setText(v_dis+"%\nOFF");
                                    }else {
                                        holder.percentage.setVisibility(View.GONE);
                                        holder.productDis.setVisibility(View.VISIBLE);
                                        holder.productDis.setText(v_cash+" % "+" Cash Back");
                                    }

                                }else {
                                    holder.productDis.setVisibility(View.GONE);
                                    holder.percentage.setVisibility(View.GONE);
                                }

                            }else {

                                if(!dis.equals(" ") || !cash.equals(" ") ){

                                    if (!dis.equals(" ")){
                                        holder.productDis.setVisibility(View.GONE);
                                        holder.percentage.setVisibility(View.VISIBLE);
                                        holder.productPercentage.setText(dis+"%\nOFF");
                                    }else {
                                        holder.percentage.setVisibility(View.GONE);
                                        holder.productDis.setVisibility(View.VISIBLE);
                                        holder.productDis.setText(cash+" % "+" Cash Back");
                                    }

                                }else {
                                    holder.productDis.setVisibility(View.GONE);
                                    holder.percentage.setVisibility(View.GONE);
                                }
                            }



                            if (name.length() > 23) {
                                holder.productName.setText(name.substring(0, 21) + "...");
                            } else {
                                holder.productName.setText(name);

                            }

                            if (des.length() < 27) {
                                holder.productDes.setText(des);
                            } else {
                                holder.productDes.setText(des.replace("\n", " ").substring(0, 25) + "...");
                            }


                            if (!url.isEmpty()) {

                                Picasso.get().load(url).placeholder(R.drawable.loading_icon).fit().centerCrop().into(holder.productImage);
                            }

                            if (userType.equals("Vendor")){

                                if (!v_dis.equals(null) && !v_dis.equals(" ")) {
                                    if (userType.equals("Vendor")){
                                        isTrue = true;
                                    } else {
                                        isTrue = false;
                                    }
                                    holder.strikePrice.setVisibility(View.VISIBLE);

                                    try {


                                        if (isTrue) {
                                            digits = v_dis.trim();
                                            strikePrice_cut = (Double.parseDouble(wholeSale) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(wholeSale)));
                                            strikePrice_string =  "৳ " + wholeSale;
                                        } else {
                                            digits = dis.trim();
                                            strikePrice_cut = (Double.parseDouble(price) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(price)));
                                            strikePrice_string =  "৳ " + price;
                                        }

                                        int strikePrice_cut_int = (int) strikePrice_cut;
                                        percentage_string = String.valueOf(strikePrice_cut_int);
                                        holder.productPrice.setText("৳ " +percentage_string);

                                        SpannableString spannableString = new SpannableString(strikePrice_string);
                                        spannableString.setSpan(new StrikethroughSpan(), 0, strikePrice_string.length() - 1, 0);
                                        holder.strikePrice.setText(spannableString);
                                    }catch (Exception e){
                                        // Nothing now
                                    }

                                }else {
                                    if (userType.equals("Vendor")){
                                        holder.productPrice.setText("৳ "+wholeSale);
                                    } else {
                                        holder.productPrice.setText("৳ "+price);
                                    }
                                }

                            }else {
                                if (!dis.equals(null) && !dis.equals(" ")) {
                                    if (userType.equals("Vendor")){
                                        isTrue = true;
                                    } else {
                                        isTrue = false;
                                    }
                                    holder.strikePrice.setVisibility(View.VISIBLE);

                                    try {


                                        if (isTrue) {
                                            digits = v_dis.trim();
                                            strikePrice_cut = (Double.parseDouble(wholeSale) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(wholeSale)));
                                            strikePrice_string =  "৳ " + wholeSale;
                                        } else {
                                            digits = dis.trim();
                                            strikePrice_cut = (Double.parseDouble(price) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(price)));
                                            strikePrice_string =  "৳ " + price;
                                        }

                                        int strikePrice_cut_int = (int) strikePrice_cut;
                                        percentage_string = String.valueOf(strikePrice_cut_int);
                                        holder.productPrice.setText("৳ " +percentage_string);

                                        SpannableString spannableString = new SpannableString(strikePrice_string);
                                        spannableString.setSpan(new StrikethroughSpan(), 0, strikePrice_string.length() - 1, 0);
                                        holder.strikePrice.setText(spannableString);
                                    }catch (Exception e){
                                        // Nothing now
                                    }

                                }else {
                                    if (userType.equals("Vendor")){
                                        holder.productPrice.setText("৳ "+wholeSale);
                                    } else {
                                        holder.productPrice.setText("৳ "+price);
                                    }
                                }
                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





                holder.add_to_card_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.mFirebaseuser != null) {

                            Map cart = new HashMap();

                            cart.put("productID",key);

                            holder.CartRef.child(holder.user_id).child(key).updateChildren(cart);

                            Toast.makeText(SearchActivity.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();

                            //holder.add_to_card_button.setEnabled(false);
                        } else {

                            Toast.makeText(SearchActivity.this, "Please Login First", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), ProductDetails.class);
                        intent.putExtra("product_id", key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                holder.numberButton.setOnClickListener((ElegantNumberButton.OnClickListener) view -> {


                    //Limitation Product

                    holder.ProRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.hasChild("Limitation")){

                                limit = Integer.parseInt(dataSnapshot.child("Limitation").child("limit").getValue().toString());

                                holder.numberButton.setRange(1,limit);
                                String quantity = holder.numberButton.getNumber();

                            }else {

                                String quantity = holder.numberButton.getNumber();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                });

            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sub_category_item, parent, false);

                return new SearchViewHolder(view);
            }

                @Override
                public void onDataChanged() {
                    // if there are no chat messages, show a view that invites the user to add a message
                    empty.setVisibility(
                            firebaseRecyclerAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                    );
                }
        };


        firebaseRecyclerAdapter.startListening();
        sRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    //view holder
    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        public TextView productName,productDes,productPrice,productDis, productPercentage, strikePrice;
        public CardView percentage;
        public ImageView productImage, add_to_card_button;
        public DatabaseReference ProRef,userRef, CartRef;
        public FirebaseAuth mfirebaseAuth;
        public FirebaseUser mFirebaseuser;
        public String user_id, current_user_id = " ";
        public ElegantNumberButton numberButton;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mfirebaseAuth = FirebaseAuth.getInstance();

            mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();
                user_id = current_user_id;

            }

            productName = itemView.findViewById(R.id.c_product_title);
            productDes = itemView.findViewById(R.id.c_product_des);
            productImage = itemView.findViewById(R.id.c_product_image);
            productPrice = itemView.findViewById(R.id.c_product_price);
            productDis = itemView.findViewById(R.id.tv_offer);
            productPercentage = itemView.findViewById(R.id.tv_discount2);
            percentage = itemView.findViewById(R.id.cv_discount2);
            strikePrice = itemView.findViewById(R.id.c_product_price_cut);
            add_to_card_button = itemView.findViewById(R.id.add_to_card_button);
            numberButton = itemView.findViewById(R.id.number_button);


            CartRef = FirebaseDatabase.getInstance().getReference("MyCart");
            ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_user_id);


        }
    }
}