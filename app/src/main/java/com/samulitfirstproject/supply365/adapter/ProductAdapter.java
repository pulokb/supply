package com.samulitfirstproject.supply365.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.ProductDetails;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.samulitfirstproject.supply365.R;
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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<ProductInfo> productList;
    private OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String userType = " ";
    private boolean checkProKey = true;


    public ProductAdapter(Context context, ArrayList<ProductInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.product_item, parent, false);
        return new NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        ProductInfo info = productList.get(position);

        final String key = info.getProductKey();
        final String dKey = info.getDeleteKey();

        if(holder.current_user_id.equals(pContext.getString(R.string.admin_UID))){         //Admin UID - string.xml
           holder.delete.setVisibility(View.GONE);
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductDetails.class);
                intent.putExtra("product_id", key);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pContext.startActivity(intent);
            }
        });


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

                if (snapshot.exists()){

                    String name = snapshot.child("productName").getValue().toString();
                    String price = snapshot.child("productPrice").getValue().toString();
                    String wholeSale = snapshot.child("productWholeSalesPrice").getValue().toString();
                    String dis = snapshot.child("productDiscount").getValue().toString();
                    String url = snapshot.child("productImage").getValue().toString();
                    String cashBack = snapshot.child("productCashBack").getValue().toString();

                    String v_dis = snapshot.child("vendorDiscount").getValue().toString();
                    String v_cashBack = snapshot.child("vendorCashBack").getValue().toString();


                    boolean isTrue;
                    double strikePrice_cut;
                    String strikePrice_string, percentage_string;
                    String digits;

                    if (name.length() > 15) {
                        holder.productTitle.setText(name.substring(0, 13) + "...");
                    } else {
                        holder.productTitle.setText(name);
                    }

                    if (userType.equals("Vendor")){

                        if (!v_dis.equals(" ") || !v_cashBack.equals(" ")){

                            if (!v_dis.equals(" ")){
                                holder.cash_bach_box.setVisibility(View.GONE);
                                holder.offer_LinearLayout.setVisibility(View.VISIBLE);
                                holder.tDiscount.setText(v_dis+"%\nOFF");
                            }else {
                                holder.offer_LinearLayout.setVisibility(View.GONE);
                                holder.cash_bach_box.setVisibility(View.VISIBLE);
                                holder.cash_back_text.setText(v_cashBack+" %");
                            }

                        }else {
                            holder.cash_bach_box.setVisibility(View.GONE);
                            holder.offer_LinearLayout.setVisibility(View.GONE);
                        }
                    }else {

                        if (!dis.equals(" ") || !cashBack.equals(" ")){

                            if (!dis.equals(" ")){
                                holder.cash_bach_box.setVisibility(View.GONE);
                                holder.offer_LinearLayout.setVisibility(View.VISIBLE);
                                holder.tDiscount.setText(dis+"%\nOFF");
                            }else {
                                holder.offer_LinearLayout.setVisibility(View.GONE);
                                holder.cash_bach_box.setVisibility(View.VISIBLE);
                                holder.cash_back_text.setText(cashBack+" %");
                            }

                        }else {
                            holder.cash_bach_box.setVisibility(View.GONE);
                            holder.offer_LinearLayout.setVisibility(View.GONE);
                        }
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
                else {
                    checkProKey = false;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.add_to_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mFirebaseuser != null) {

                    Map cart = new HashMap();

                    cart.put("productID",key);

                    holder.CartRef.child(holder.current_user_id).child(key).updateChildren(cart);

                    Toast.makeText(pContext, "Added to cart successfully", Toast.LENGTH_SHORT).show();

                    //holder.add_to_card_button.setEnabled(false);
                } else {

                    Toast.makeText(pContext, "Please Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Removing...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        holder.TopRef.child(dKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    holder.TopRef.child(dKey).removeValue();
                                    if (checkProKey){
                                        holder.ProRef.child(key).child("isBestDeal").setValue("No");
                                    }

                                    notifyDataSetChanged();

                                    Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.BestSellRef.child(dKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    holder.BestSellRef.child(dKey).removeValue();

                                    if (checkProKey){
                                        holder.ProRef.child(key).child("isBestSelling").setValue("No");
                                    }

                                    notifyDataSetChanged();

                                    Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.PProRef.child(dKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    holder.PProRef.child(dKey).removeValue();

                                    if (checkProKey){
                                        holder.ProRef.child(key).child("isPopular").setValue("No");
                                    }
                                    notifyDataSetChanged();

                                    Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                },1500);

            }
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

        public CardView cDiscount;
        public TextView tDiscount, productTitle, productPrice, cash_back_text, strikePrice;
        public ImageView productImage, delete, add_to_card;
        public LinearLayout offer_LinearLayout, cash_bach_box;

        public DatabaseReference ProRef, TopRef, BestSellRef, PProRef, userRef, CartRef;
        public FirebaseAuth mfirebaseAuth;
        public FirebaseUser mFirebaseuser;
        public String current_user_id = " ";

        public NewsViewHolder(View itemView) {
            super(itemView);

            cDiscount = itemView.findViewById(R.id.cv_discount);
            tDiscount = itemView.findViewById(R.id.tv_discount);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.c_product_price);
            strikePrice = itemView.findViewById(R.id.c_product_price_cut);
            productImage = itemView.findViewById(R.id.product_image);
            offer_LinearLayout = itemView.findViewById(R.id.offer_LinearLayout);
            cash_bach_box = itemView.findViewById(R.id.cash_bach_box);
            cash_back_text = itemView.findViewById(R.id.cash_back_text);
            add_to_card = itemView.findViewById(R.id.add_to_card);

            delete = itemView.findViewById(R.id.delete_top);

            mfirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseuser = mfirebaseAuth.getCurrentUser();


            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
            CartRef = FirebaseDatabase.getInstance().getReference("MyCart");


            TopRef = FirebaseDatabase.getInstance().getReference().child("TopDeals");
            BestSellRef = FirebaseDatabase.getInstance().getReference().child("BestSelling");
            PProRef = FirebaseDatabase.getInstance().getReference().child("PopularProduct");
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
