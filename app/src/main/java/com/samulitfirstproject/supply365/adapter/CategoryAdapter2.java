package com.samulitfirstproject.supply365.adapter;

import android.annotation.SuppressLint;
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

import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.SubCategory;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.NewsViewHolder>{

    private Context pContext;
    private ArrayList<CatInfo> productList;
    private OnItemClickListener pListener;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";

    public CategoryAdapter2(Context context, ArrayList<CatInfo> productLists) {
        pContext = context;
        productList = productLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.category_item, parent, false);
        return new NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        CatInfo info = productList.get(position);

        final String key = info.getKey();
        final String key2 = info.getItem();

        holder.categoryName.setText(key);
        Picasso.get().load(info.getUrl()).placeholder(R.drawable.loading_icon).fit().centerInside().into(holder.catImage);



        if(current_user_id.equals(pContext.getString(R.string.admin_UID))){         //Admin UID - string.xml
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), SubCategory.class);
                intent.putExtra("category_id", key);
                pContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Deleting...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        holder.CatRef.child(key2).child(key).removeValue();
                        notifyDataSetChanged();

                        Toast.makeText(pContext, "Successfully Deleted", Toast.LENGTH_SHORT).show();

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


        public TextView categoryName;
        public ImageView delete,catImage;
        public DatabaseReference CatRef;
        public FirebaseAuth mfirebaseAuth;


        public NewsViewHolder(View itemView) {
            super(itemView);


            mfirebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            categoryName = itemView.findViewById(R.id.tv_category_name);
            delete = itemView.findViewById(R.id.delete_category);
            catImage =itemView.findViewById(R.id.category_image);

            CatRef = FirebaseDatabase.getInstance().getReference().child("Category");




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
