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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.samulitfirstproject.supply365.ProductDetails;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.WishlistActivity;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<CatInfo> productList;
    private OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String current_user_id,userType=" ";
    private int limit;
    private boolean hasLimitation ;


    public CartAdapter(Context context, ArrayList<CatInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.cart_item, parent, false);
        return new NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        final CatInfo info = productList.get(position);

        if(pContext instanceof WishlistActivity){

            holder.checkBox.setVisibility(View.GONE);
            holder.numberButton.setVisibility(View.GONE);
            holder.quantity.setVisibility(View.GONE);

        }

        final String key = info.getKey();

        if(pContext instanceof WishlistActivity){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ProductDetails.class);
                    intent.putExtra("product_id", key);
                    pContext.startActivity(intent);

                }
            });
        }

        if(getItemCount()<=1){
            info.setSelected(!info.isSelected());
        }


        holder.CartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    long count = snapshot.getChildrenCount();

                    if ((count>1) && !(pContext instanceof WishlistActivity)){
                        holder.checkBox.setVisibility(View.VISIBLE);
                    }

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



                   holder.ProRef.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.child(key).exists()) {

                               String name = snapshot.child(key).child("productName").getValue().toString();
                               String price = snapshot.child(key).child("productPrice").getValue().toString();
                               String wholeSale = snapshot.child(key).child("productWholeSalesPrice").getValue().toString();
                               String des = snapshot.child(key).child("productDescription").getValue().toString();
                               String url = snapshot.child(key).child("productImage").getValue().toString();
                               String discount = snapshot.child(key).child("productDiscount").getValue().toString();
                               String offer = snapshot.child(key).child("isOffer").getValue().toString();
                               String cash = snapshot.child(key).child("productCashBack").getValue().toString();

                               String v_dis = snapshot.child(key).child("vendorDiscount").getValue().toString();
                               String v_cash = snapshot.child(key).child("vendorCashBack").getValue().toString();

                               //limit = Integer.parseInt(snapshot.child(key).child("limit").getValue().toString());

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

                                   if(!discount.equals(" ") || !cash.equals(" ") ){

                                       if (!discount.equals(" ")){
                                           holder.productDis.setVisibility(View.GONE);
                                           holder.percentage.setVisibility(View.VISIBLE);
                                           holder.productPercentage.setText(discount+"%\nOFF");
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
                                               digits = discount.trim();
                                               strikePrice_cut = (Double.parseDouble(price) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(price)));
                                               strikePrice_string =  "৳ " + price;
                                           }

                                           int strikePrice_cut_int = (int) strikePrice_cut;
                                           percentage_string = String.valueOf(strikePrice_cut_int);
                                           holder.productPrice.setText("৳ "+percentage_string);

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
                                   if (!discount.equals(null) && !discount.equals(" ")) {
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
                                               digits = discount.trim();
                                               strikePrice_cut = (Double.parseDouble(price) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(price)));
                                               strikePrice_string =  "৳ " + price;
                                           }

                                           int strikePrice_cut_int = (int) strikePrice_cut;
                                           percentage_string = String.valueOf(strikePrice_cut_int);
                                           holder.productPrice.setText("৳ "+percentage_string);

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



                               info.setItem(name);

                               //set price
                               info.setPrice(holder.productPrice.getText().toString().substring(1));


                               info.setOffer(offer);

                               if (discount.equals(" ")){
                                   info.setDiscount(discount);
                               }


                               holder.productName.setText(name);
                               if (des.length() < 25) {
                                   holder.productDes.setText(des);
                               } else {
                                   holder.productDes.setText(des.substring(0, 24) + " ...");
                               }

                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.numberButton.setOnClickListener((ElegantNumberButton.OnClickListener) view -> {

            //Limitation Product
            holder.ProRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(key).hasChild("Limitation")){

                        hasLimitation = true;
                        limit = Integer.parseInt(snapshot.child(key).child("Limitation").child("limit").getValue().toString());

                        holder.numberButton.setRange(1,limit);
                        String quantity = holder.numberButton.getNumber();

                        if(quantity.isEmpty()){
                            info.setQuantity("1");
                        }else {
                            info.setQuantity(quantity);
                        }

                    }else {
                        hasLimitation = false;
                        String quantity = holder.numberButton.getNumber();

                        if(quantity.isEmpty()){
                            info.setQuantity("1");
                        }else {
                            info.setQuantity(quantity);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        });

        String quantity = holder.numberButton.getNumber();

        if(quantity.isEmpty()){
            info.setQuantity("1");
        }else {
            info.setQuantity(quantity);
        }


        holder.deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Removing...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        if(pContext instanceof WishlistActivity){
                            holder.WishRef.child(key).removeValue();
                            notifyDataSetChanged();
                        }else{
                            holder.CartRef.child(key).removeValue();
                            notifyDataSetChanged();
                        }

                        Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();

                    }
                },1000);

            }
        });


        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    info.setSelected(true);
                }
                else{
                    info.setSelected(false);
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


        public TextView productName,productDes,productPrice,quantity, strikePrice, productDis, productPercentage;
        public ImageView productImage,deleteCart;
        public DatabaseReference ProRef,CartRef,WishRef,userRef,ProLim;
        public FirebaseAuth mfirebaseAuth;
        public ElegantNumberButton numberButton;
        public CheckBox checkBox;
        public CardView percentage;


        public NewsViewHolder(View itemView) {
            super(itemView);


            mfirebaseAuth = FirebaseAuth.getInstance();
            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            productName = itemView.findViewById(R.id.c_product_title);
            productDes = itemView.findViewById(R.id.c_product_des);
            productImage = itemView.findViewById(R.id.c_product_image);

            productPrice = itemView.findViewById(R.id.c_product_price);
            strikePrice = itemView.findViewById(R.id.c_product_price_cut);
            numberButton = itemView.findViewById(R.id.number_button);

            deleteCart = itemView.findViewById(R.id.remove_cart);
            checkBox = itemView.findViewById(R.id.cart_check_box);
            quantity = itemView.findViewById(R.id.tv_quantity);

            productPercentage = itemView.findViewById(R.id.tv_discount2);
            percentage = itemView.findViewById(R.id.cv_discount2);
            productDis = itemView.findViewById(R.id.tv_offer);

            ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
            CartRef = FirebaseDatabase.getInstance().getReference().child("MyCart").child(current_user_id);
            WishRef = FirebaseDatabase.getInstance().getReference().child("WishList").child(current_user_id);
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
