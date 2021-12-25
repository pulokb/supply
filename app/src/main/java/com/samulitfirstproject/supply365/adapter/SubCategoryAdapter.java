package com.samulitfirstproject.supply365.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.samulitfirstproject.supply365.CartActivity;
import com.samulitfirstproject.supply365.ProductDetails;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.WishlistActivity;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<CatInfo> productList;
    private CategoryAdapter.OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String current_user_id = " ",userType=" ", getIntentKey=" ";
    private int limit;
    private boolean hasLimitation = false;




    public SubCategoryAdapter(Context context, ArrayList<CatInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.sub_category_item, parent, false);
        return new SubCategoryAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final SubCategoryAdapter.NewsViewHolder holder, int position) {
        CatInfo info = productList.get(position);

        final String key = info.getKey();

        holder.userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    userType = snapshot.child("userType").getValue().toString();
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
                        if(!v_dis.equals(" ") || !v_cash.equals(" ") && !(pContext instanceof CartActivity) && !(pContext instanceof WishlistActivity)){

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

                        if(!dis.equals(" ") || !cash.equals(" ") && !(pContext instanceof CartActivity) && !(pContext instanceof WishlistActivity)){

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

                    Toast.makeText(pContext, "Added to cart successfully", Toast.LENGTH_SHORT).show();

                    //holder.add_to_card_button.setEnabled(false);
                } else {

                    Toast.makeText(pContext, "Please Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ProductDetails.class);
                intent.putExtra("product_id", key);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pContext.startActivity(intent);

            }
        });

        holder.numberButton.setOnClickListener((ElegantNumberButton.OnClickListener) view -> {

            //Limitation Product
            holder.ProRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(key).hasChild("Limitation")){

                        limit = Integer.parseInt(snapshot.child(key).child("Limitation").child("limit").getValue().toString());
                        holder.numberButton.setRange(1,limit);
                        String quantity = holder.numberButton.getNumber();

                    }else {
                        String quantity = holder.numberButton.getNumber();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {


        public TextView productName,productDes,productPrice,productDis, productPercentage, strikePrice;
        public CardView percentage;
        public ImageView productImage, add_to_card_button;
        public DatabaseReference ProRef,userRef, CartRef;
        public FirebaseAuth mfirebaseAuth;
        public FirebaseUser mFirebaseuser;
        public String user_id = " ";
        public ElegantNumberButton numberButton;


        public NewsViewHolder(View itemView) {
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
        @Override
        public void onClick(View v) {
            if (pListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    pListener.onItemCClick(position);
                }
            }
        }



        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }

    public interface OnItemClickListener {
        void onItemCClick(int position);

    }

}
