package com.samulitfirstproject.supply365.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.ProductDetails;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.VendorDetails;
import com.samulitfirstproject.supply365.model.VendorInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.NewsViewHolder> {

    private Context vContext;
    private ArrayList<VendorInfo> vendorList;
    private ProgressDialog progressDialog;


    public VendorAdapter(Context context, ArrayList<VendorInfo> vendorLists) {
        vContext = context;
        vendorList = vendorLists;
    }

    @NonNull
    @Override
    public VendorAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(vContext).inflate(R.layout.vendor_item, parent, false);
        return new VendorAdapter.NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VendorAdapter.NewsViewHolder holder, int position) {
        VendorInfo info = vendorList.get(position);

        final String key = info.getVendorKey();
        final String dKey = info.getVendorDkey();



        if(holder.current_user_id.equals(vContext.getString(R.string.admin_UID)) && !(vContext instanceof ProductDetails)){         //Admin UID - D2sMhPLPzaOiY3z21F4tvP0JAih1
            holder.delete.setVisibility(View.VISIBLE);
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }



        holder.UserRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String name = snapshot.child("userName").getValue().toString();
                    String url = snapshot.child("userImage").getValue().toString();


                    holder.vendorName.setText(name);

                    if (!url.equals(" ")) {

                        Picasso.get().load(url).placeholder(R.drawable.loading_gif__).fit().centerCrop().into(holder.vendorImage);
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

                String persons_uid = key;

                Intent intent = new Intent(v.getContext(), VendorDetails.class);
                intent.putExtra("person_uid", persons_uid);
                vContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(vContext);
                progressDialog.show();
                progressDialog.setMessage("Removing");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();

                        holder.vendorRef.child(dKey).removeValue();

                        holder.UserRef.child(key).child("isTopVendor").setValue("false");
                        notifyDataSetChanged();

                        Toast.makeText(vContext, "Successfully Removed", Toast.LENGTH_SHORT).show();

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
        return vendorList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView vendorName;
        public ImageView vendorImage,delete;
        public FirebaseAuth mfirebaseAuth;
        public String current_user_id = " ";
        public DatabaseReference UserRef,vendorRef;

        public NewsViewHolder(View itemView) {
            super(itemView);

            mfirebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            UserRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
            vendorRef = FirebaseDatabase.getInstance().getReference().child("TopVendor");

            vendorName = itemView.findViewById(R.id.vendor_name);
            vendorImage = itemView.findViewById(R.id.vendor_profile_image);
            delete = itemView.findViewById(R.id.delete_top_vendor);




        }
        @Override
        public void onClick(View v) {

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
