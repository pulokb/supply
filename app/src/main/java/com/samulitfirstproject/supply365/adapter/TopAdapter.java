package com.samulitfirstproject.supply365.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.ProductDetails;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.ShowAllProduct;
import com.samulitfirstproject.supply365.geocode.GeoCodingLocation;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<ProductInfo> productList;
    private TopAdapter.OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String userType=" ", userLocation = "";


    public TopAdapter(Context context, ArrayList<ProductInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public TopAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.add_top_product, parent, false);
        return new TopAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final TopAdapter.NewsViewHolder holder, int position) {
        ProductInfo info = productList.get(position);

        final String key = info.getProductKey();




        if(holder.current_user_id.equals(pContext.getString(R.string.admin_UID))){         //Admin UID - string.xml
            holder.L1.setVisibility(View.VISIBLE);
            holder.L2.setVisibility(View.GONE);
        }else{
            holder.L1.setVisibility(View.GONE);
            holder.L2.setVisibility(View.VISIBLE);
        }

        holder.userRef.child(holder.current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userType = snapshot.child("userType").getValue().toString();
                    userLocation = snapshot.child("userLocation").getValue().toString();
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
                    String des = snapshot.child("productDescription").getValue().toString();
                    String url = snapshot.child("productImage").getValue().toString();

                    String isBestDeal = snapshot.child("isBestDeal").getValue().toString();
                    String isBestSelling = snapshot.child("isBestSelling").getValue().toString();
                    String isPopular = snapshot.child("isPopular").getValue().toString();

                    if (isBestDeal.equals("Yes")){
                        holder.tDeals.setEnabled(false);
                        holder.tDeals.setText("Added");
                    }
                    if (isBestSelling.equals("Yes")){
                        holder.bestSell.setEnabled(false);
                        holder.bestSell.setText("Added");
                    }
                    if (isPopular.equals("Yes")){
                        holder.popularPro.setEnabled(false);
                        holder.popularPro.setText("Added");
                    }


                    holder.productTitle.setText(name);
                    if (des.length()<25){
                        holder.productDes.setText(des);
                    }
                    else {
                        holder.productDes.setText(des.substring(0,24)+" ...");
                    }

                    if (userType.equals("Vendor")&& !(pContext instanceof ShowAllProduct)){
                        holder.productPrice.setText("৳ "+wholeSale);
                    }
                    else {
                        holder.productPrice.setText("৳ "+price);
                    }

                    if (!url.isEmpty()) {

                        Picasso.get().load(url).placeholder(R.drawable.loading_icon).fit().centerCrop().into(holder.productImage);
                    }
                }

                if (userType.equals("Vendor")){
                    if (snapshot.child("Vendor").hasChild(holder.current_user_id)){
                        holder.addToShop.setText("Added");
                        holder.addToShop.setEnabled(false);
                    }else {
                        holder.addToShop.setText("Add");
                        holder.addToShop.setEnabled(true);
                    }
                }
                else {
                    if (snapshot.child("Distributor").hasChild(holder.current_user_id)){
                        holder.addToShop.setText("Added");
                        holder.addToShop.setEnabled(false);
                    }else {
                        holder.addToShop.setText("Add");
                        holder.addToShop.setEnabled(true);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ProductDetails.class);
                intent.putExtra("product_id", key);
                pContext.startActivity(intent);
            }
        });







        holder.tDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Adding...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        HashMap add = new HashMap();

                        add.put("productID", key);

                        holder.TopRef.push().updateChildren(add);

                        holder.tDeals.setEnabled(false);
                        holder.tDeals.setText("Added");

                        holder.ProRef.child(key).child("isBestDeal").setValue("Yes");
                        Toast.makeText(pContext, "Successfully Added", Toast.LENGTH_SHORT).show();

                    }
                },1500);


            }
        });

        holder.bestSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Adding...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        HashMap add = new HashMap();

                        add.put("productID", key);

                        holder.BestSellRef.push().updateChildren(add);

                        holder.bestSell.setEnabled(false);
                        holder.bestSell.setText("Added");

                        holder.ProRef.child(key).child("isBestSelling").setValue("Yes");
                        Toast.makeText(pContext, "Successfully Added", Toast.LENGTH_SHORT).show();

                    }
                },1500);


            }
        });

        holder.popularPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Adding");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        HashMap add = new HashMap();

                        add.put("productID", key);

                        holder.PProRef.push().updateChildren(add);

                        holder.popularPro.setEnabled(false);
                        holder.popularPro.setText("Added");

                        holder.ProRef.child(key).child("isPopular").setValue("Yes");
                        Toast.makeText(pContext, "Successfully Added", Toast.LENGTH_SHORT).show();

                    }
                },1500);


            }
        });


        holder.addToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.userRef.child(holder.current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            userType = snapshot.child("userType").getValue().toString();
                            userLocation = snapshot.child("userLocation").getValue().toString();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (!userLocation.equals(" ") && userLocation.length() > 4) {

                    progressDialog = new ProgressDialog(pContext);
                    progressDialog.show();
                    progressDialog.setMessage("Adding");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            boolean isEnter = false;

                            HashMap add = new HashMap();

                            add.put("productID", key);

                            holder.MyProRef.child(holder.current_user_id).push().updateChildren(add);

                            holder.addToShop.setEnabled(false);

                            if (userType.equals("Vendor")) {
                                holder.ProRef.child(key).child("Vendor").child(holder.current_user_id).child("uid").setValue(holder.current_user_id);
                            } else {
                                holder.ProRef.child(key).child("Distributor").child(holder.current_user_id).child("uid").setValue(holder.current_user_id);
                            }

                            Toast.makeText(pContext, "Successfully Added to the Shop", Toast.LENGTH_SHORT).show();

                            GeoCodingLocation.getAddressFromLocation(userLocation, pContext, new GeoCoderHandler(), holder.current_user_id, null, null, null);

                        }
                    }, 1500);

                }else {

                    Toast.makeText(pContext, "Please give your location first in Profile!", Toast.LENGTH_LONG).show();

                }
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

        public TextView productDes,productTitle,productPrice;
        public ImageView productImage;
        public FirebaseAuth mfirebaseAuth;
        public DatabaseReference ProRef,TopRef,BestSellRef,PProRef,MyProRef,userRef, userAddress;
        private Button tDeals,bestSell,popularPro,addToShop,removeFromShop;
        String current_user_id;

        private LinearLayout L1,L2;

        public NewsViewHolder(View itemView) {
            super(itemView);

            productDes = itemView.findViewById(R.id.ar_product_des);
            productTitle = itemView.findViewById(R.id.ar_product_title);
            productPrice = itemView.findViewById(R.id.ar_product_price);
            productImage = itemView.findViewById(R.id.ar_product_image);

            tDeals = itemView.findViewById(R.id.add_as_best_deal);
            bestSell = itemView.findViewById(R.id.add_as_best_selling);
            popularPro = itemView.findViewById(R.id.add_as_popular_product);

            L1 = itemView.findViewById(R.id.btn);
            L2 = itemView.findViewById(R.id.btn2);

            addToShop = itemView.findViewById(R.id.add_to_shop);



            mfirebaseAuth = FirebaseAuth.getInstance();
            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
            TopRef = FirebaseDatabase.getInstance().getReference().child("TopDeals");
            BestSellRef = FirebaseDatabase.getInstance().getReference().child("BestSelling");
            PProRef = FirebaseDatabase.getInstance().getReference().child("PopularProduct");
            MyProRef = FirebaseDatabase.getInstance().getReference().child("MyProduct");
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
            userAddress = FirebaseDatabase.getInstance().getReference().child("UsersAddress");


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

    // Akash ......................................................................................
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
        }
    }

}
