package com.samulitfirstproject.supply365.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.VendorDetails;
import com.samulitfirstproject.supply365.model.BrandInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BrandShopAdapter extends RecyclerView.Adapter<BrandShopAdapter.ViewHolder> {
    private Context bContext;
    private ArrayList<BrandInfo> brandList;
    private ProgressDialog progressDialog;


    public BrandShopAdapter(Context context, ArrayList<BrandInfo> brandLists) {
        bContext = context;
        brandList = brandLists;
    }

    @NonNull
    @Override
    public BrandShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(bContext).inflate(R.layout.vendor_list_item, parent, false);
        return new BrandShopAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BrandShopAdapter.ViewHolder holder, final int position) {
        final BrandInfo info = brandList.get(position);

        final String key = info.getBrandKey();

        String name = info.getUserName();
        String phone = info.getUserPhone();


        holder.bName.setText(name);
        holder.bPhone.setText(phone);


            holder.vType.setVisibility(View.GONE);
            holder.access.setVisibility(View.GONE);



        if (!info.getUserImage().equals(" ")) {

            Picasso.get().load(info.getUserImage()).placeholder(R.drawable.loading_gif__).fit().centerCrop().into(holder.bImage);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), VendorDetails.class);
                intent.putExtra("person_uid", key);
                bContext.startActivity(intent);
            }
        });

        holder.removeBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.uRef.child(key).child("isBrandShop").setValue("false");
                notifyItemChanged(position);

                Toast.makeText(bContext, "Removed Successfully", Toast.LENGTH_SHORT).show();
            }
        });


        holder.top.setVisibility(View.GONE);
        holder.brand.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);

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
        return brandList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bName,bPhone,vType;
        public CircleImageView bImage;
        public Button top,brand,delete,removeBrand,access;

        private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        public DatabaseReference uRef, databaseReference;
        private String mFirebaseID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bName = itemView.findViewById(R.id.vendor_name);
            bImage = itemView.findViewById(R.id.vendor_profile_image);
            bPhone = itemView.findViewById(R.id.vendor_phone);
            vType = itemView.findViewById(R.id.type);
            access = itemView.findViewById(R.id.panel_access);

            top = itemView.findViewById(R.id.add_as_top_vendor);
            brand = itemView.findViewById(R.id.add_as_brand_shop);
            delete = itemView.findViewById(R.id.approve_account);
            removeBrand = itemView.findViewById(R.id.remove_brand_shop);
            mFirebaseID = firebaseUser.getUid();

            uRef = FirebaseDatabase.getInstance().getReference("UsersData");
            databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(mFirebaseID);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String userType = snapshot.child("userType").getValue().toString();
                        if (!userType.equals("Admin")){
                            removeBrand.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

}
