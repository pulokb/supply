package com.samulitfirstproject.supply365.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.model.saveKeyList;
import com.samulitfirstproject.supply365.model.vendorName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoreTransferProducts extends RecyclerView.Adapter<StoreTransferProducts.ViewHolder> {

    private saveKeyList keyList;
    private List<String> list = new ArrayList<>();
    private String orderID,name;
    private List<vendorName> arrayList;
    private int List;
    private Context context;


    public StoreTransferProducts(String oderID, List<vendorName> arrayList, Context context) {
        this.orderID = oderID;
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_show_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final vendorName vName = arrayList.get(position);

        holder.databaseReference.child("Products").child(vName.getNameVendor()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name = snapshot.child("productName").getValue(String.class);
                holder.checkBox.setText(name);
                vName.setProName(name);
                vName.setProductID(snapshot.getKey());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.orderRef.child(orderID).child("productID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int C = 0;

                for (DataSnapshot ds : snapshot.getChildren()){
                    String id = ds.getValue(String.class);
                    if (id.equals(vName.getProductID())){
                        List = C;
                        break;
                    }
                    C++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.orderRef.child(orderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String quantity = snapshot.child("orderQuantity").getValue(String.class);
                String n = snapshot.child("orderItem").getValue(String.class);
                String p = snapshot.child("orderItemPrice").getValue(String.class);
                String item_weight = snapshot.child("ItemWeights").getValue(String.class);


                String[] arr2 = quantity.split("\n");

                vName.setQuantity(arr2[List]);



                String[] arr3 = p.split("\n");

                vName.setPrice(arr3[List]);


                String[] arr4 = item_weight.split("\n");

                vName.setWeight(arr4[List]);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    vName.setSelected(true);
                }
                else{
                    vName.setSelected(false);
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
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
