package com.samulitfirstproject.supply365.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.samulitfirstproject.supply365.EditProduct;
import com.samulitfirstproject.supply365.ManageVendorProduct;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.samulitfirstproject.supply365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VendorProductAdapter extends RecyclerView.Adapter<VendorProductAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<ProductInfo> productList;
    private ProductAdapter.OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String userType=" ",d_url;


    public VendorProductAdapter(Context context, ArrayList<ProductInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public VendorProductAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.vendor_product_item, parent, false);
        return new VendorProductAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final VendorProductAdapter.NewsViewHolder holder, int position) {
        ProductInfo info = productList.get(position);

        final String key = info.getProductKey();
        final String dKey = info.getDeleteKey();

        if(!holder.current_user_id.equals(pContext.getString(R.string.admin_UID))){         //Admin UID - string.xml

            holder.edit.setVisibility(View.GONE);
        }

        holder.userRef.child(holder.current_user_id).addValueEventListener(new ValueEventListener() {
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
                    String des = snapshot.child("productDescription").getValue().toString();
                    String url = snapshot.child("productImage").getValue().toString();


                    holder.productTitle.setText(name);
                    if (des.length()<25){
                        holder.productDes.setText(des);
                    }
                    else {
                        holder.productDes.setText(des.substring(0,24)+" ...");
                    }

                    if (userType.equals("Vendor")&& !(pContext instanceof ManageVendorProduct)){
                        holder.productPrice.setText("৳ "+wholeSale);
                    }
                    else {
                        holder.productPrice.setText("৳ "+price);
                    }


                    if (!url.isEmpty()) {

                        Picasso.get().load(url).placeholder(R.drawable.loading_icon).fit().centerCrop().into(holder.productImage);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(v.getContext(), ProductDetails.class);
//                intent.putExtra("product_id", key);
//                pContext.startActivity(intent);
//            }
//        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), EditProduct.class);
                intent.putExtra("product_id", key);
                pContext.startActivity(intent);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                holder.userRef.child(holder.current_user_id).addValueEventListener(new ValueEventListener() {
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

                            d_url = snapshot.child("productImage").getValue().toString();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Deleting...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        if(holder.current_user_id.equals(pContext.getString(R.string.admin_UID))){         //Admin UID - string.xml

                            StorageReference imageRef = holder.firebaseStorage.getReferenceFromUrl(d_url);
                            imageRef.delete();
                            holder.ProRef.child(key).removeValue();
                            notifyDataSetChanged();

                            Toast.makeText(pContext, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                        }else{

                            if(userType.equals("Vendor")){

                                holder.MyProRef.child(dKey).removeValue();
                                holder.ProRef.child(key).child("Vendor").child(holder.current_user_id).removeValue();
                                notifyDataSetChanged();

                                Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();

                            }else {
                                holder.MyProRef.child(dKey).removeValue();
                                holder.ProRef.child(key).child("Distributor").child(holder.current_user_id).removeValue();
                                notifyDataSetChanged();

                                Toast.makeText(pContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                            }

                        }



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

        public LinearLayout manage,edit,delete;
        public TextView productDes,productTitle,productPrice;
        public ImageView productImage;
        public FirebaseAuth mfirebaseAuth;
        public DatabaseReference MyProRef,ProRef,userRef;
        public String current_user_id;
        public FirebaseStorage firebaseStorage;

        public NewsViewHolder(View itemView) {
            super(itemView);

            manage = itemView.findViewById(R.id.ll_manage);
            productDes = itemView.findViewById(R.id.v_product_des);
            productTitle = itemView.findViewById(R.id.v_product_title);
            productPrice = itemView.findViewById(R.id.v_product_price);
            productImage = itemView.findViewById(R.id.v_product_image);

            edit = itemView.findViewById(R.id.v_edit_product);
            delete = itemView.findViewById(R.id.v_delete_product);


            mfirebaseAuth = FirebaseAuth.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            current_user_id = mfirebaseAuth.getCurrentUser().getUid();
            MyProRef = FirebaseDatabase.getInstance().getReference().child("MyProduct").child(current_user_id);
            ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");


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
