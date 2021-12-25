package com.samulitfirstproject.supply365.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.samulitfirstproject.supply365.Approval;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.VendorDetails;
import com.samulitfirstproject.supply365.model.VendorInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.NewsViewHolder> {


    private Context vContext;
    private ArrayList<VendorInfo> vendorList;
    private ProgressDialog progressDialog;
    private DatabaseReference uRef,topVendorRef;
    private String type,isApprove,panelAccess;


    public VendorListAdapter(Context context, ArrayList<VendorInfo> vendorLists) {
        vContext = context;
        vendorList = vendorLists;
    }


    @NonNull
    @Override
    public VendorListAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(vContext).inflate(R.layout.vendor_list_item, parent, false);
        return new VendorListAdapter.NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VendorListAdapter.NewsViewHolder holder, int position) {
        final VendorInfo vendor = vendorList.get(position);

        final String key = vendor.getVendorKey();
        String name = vendor.getUserName();
        String phone = vendor.getUserPhone();
        final String isTop = vendor.getIsTopVendor();
        final String isBrand = vendor.getIsBrandShop();


        if(vContext instanceof Approval){

            holder.top.setVisibility(View.GONE);
            holder.brand.setVisibility(View.GONE);
            holder.access.setVisibility(View.GONE);

        }

        if (vendor.getIsApprove().equals("Yes")){
            holder.approve.setText("   Remove Approval   ");
        }else {
            holder.approve.setText("Approve");
        }

        if (vendor.getvPanelAccess().equals("Yes")){
            holder.access.setText(" Remove Panel Access ");
        }else {
            holder.access.setText(" Give Panel Access ");
        }


        uRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    type = String.valueOf(snapshot.child("userType").getValue());

                    holder.vType.setText(type);

                    if (type.equals("Distributor")){
                        holder.top.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.vName.setText(name);
        holder.vPhone.setText(phone);



        if (!(vendor.getUserImage().equals(" "))) {

            Picasso.get().load(vendor.getUserImage()).placeholder(R.drawable.loading_gif__).fit().centerCrop().into(holder.vImage);
        }

        if (isTop.equals("true")){
            holder.top.setText("Added");
            holder.top.setEnabled(false);
        }

        if (isBrand!=null && isBrand.equals("true")){
            holder.brand.setText(" Added Brand ");
            holder.brand.setEnabled(false);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), VendorDetails.class);
                intent.putExtra("person_uid", key);
                vContext.startActivity(intent);
            }
        });

        holder.top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isTop.equals("true")){
                    holder.top.setText("Added");
                }else {
                    progressDialog = new ProgressDialog(vContext);
                    progressDialog.show();
                    progressDialog.setMessage("Adding As Top");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();

                            HashMap add = new HashMap();

                            add.put("vendorUID", key);

                            topVendorRef.push().updateChildren(add);

                            holder.top.setText("Added");

                            uRef.child(key).child("isTopVendor").setValue("true");

                            Toast.makeText(vContext, "Successfully Added As Top", Toast.LENGTH_SHORT).show();

                        }
                    },1500);
                }

            }
        });


        holder.brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(vContext);
                progressDialog.show();
                progressDialog.setMessage("Adding As Brand Shop");

                if (isBrand==null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();

                            updateBrandUser(key);

                            holder.brand.setText(" Added Brand ");
                            holder.brand.setEnabled(false);

                            Toast.makeText(vContext, "Successfully Added As Brand Shop", Toast.LENGTH_SHORT).show();

                        }
                    },1000);

                }else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();

                            uRef.child(key).child("isBrandShop").setValue("true");

                            holder.brand.setText(" Added Brand ");
                            holder.brand.setEnabled(false);

                            Toast.makeText(vContext, "Successfully Added As Brand Shop", Toast.LENGTH_SHORT).show();

                        }
                    },1000);
                }

            }
        });




        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(vContext);
                progressDialog.show();
                progressDialog.setMessage("Please Wait...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        if (vendor.getIsApprove().equals("Yes")){
                            uRef.child(key).child("isApprove").setValue("No");

                            holder.approve.setText("Approve");
                            Toast.makeText(vContext, "Successfully Remove", Toast.LENGTH_SHORT).show();
                        }else {
                            uRef.child(key).child("isApprove").setValue("Yes");

                            holder.approve.setText("   Remove Approval   ");
                            Toast.makeText(vContext, "Approved", Toast.LENGTH_SHORT).show();
                        }


                    }
                },1000);


            }
        });

        holder.access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(vContext);
                progressDialog.show();
                progressDialog.setMessage("Please Wait...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        if (vendor.getvPanelAccess().equals("Yes")){
                            uRef.child(key).child("vPanelAccess").setValue("No");

                            holder.access.setText(" Give Panel Access ");
                            Toast.makeText(vContext, "Remove Access", Toast.LENGTH_SHORT).show();
                        }else {
                            uRef.child(key).child("vPanelAccess").setValue("Yes");

                            holder.access.setText(" Remove Panel Access ");
                            Toast.makeText(vContext, "Given Access", Toast.LENGTH_SHORT).show();
                        }


                    }
                },1000);


            }
        });

        holder.removeBrand.setVisibility(View.GONE);




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
        return vendorList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView vName,vPhone,vType;
        public CircleImageView vImage;
        public Button top,brand,approve,removeBrand,access;

        public FirebaseAuth mfirebaseAuth;
        public String current_user_id = " ";

        public NewsViewHolder(View itemView) {
            super(itemView);

            mfirebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            uRef = FirebaseDatabase.getInstance().getReference("UsersData");
            topVendorRef = FirebaseDatabase.getInstance().getReference("TopVendor");

            vName = itemView.findViewById(R.id.vendor_name);
            vImage = itemView.findViewById(R.id.vendor_profile_image);
            vPhone = itemView.findViewById(R.id.vendor_phone);
            vType = itemView.findViewById(R.id.type);

            top = itemView.findViewById(R.id.add_as_top_vendor);
            brand = itemView.findViewById(R.id.add_as_brand_shop);
            approve = itemView.findViewById(R.id.approve_account);
            removeBrand = itemView.findViewById(R.id.remove_brand_shop);
            access = itemView.findViewById(R.id.panel_access);




        }


    }


    private void updateBrandUser(final String UID) {
        uRef.child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> postValues = new HashMap<String,Object>();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            postValues.put(snapshot.getKey(),snapshot.getValue());
                                                        }
                                                        postValues.put("isBrandShop", "true");

                                                        uRef.child(UID).updateChildren(postValues);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {}
                                                }
                );
    }


}
