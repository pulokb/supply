package com.samulitfirstproject.supply365.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.adapter.BrandShopAdapter;
import com.samulitfirstproject.supply365.model.BrandInfo;

import java.util.ArrayList;


public class BrandShopFragment extends Fragment {

    private RecyclerView bRecyclerView;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference uRef;
    private TextView empty, message;
    private ProgressDialog progressDialog;
    private BrandShopAdapter bAdapter;
    private ArrayList<BrandInfo> brandList;
    private String type;


    public BrandShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brand_shop, container, false);


        empty = view.findViewById(R.id.empty_vendor_list);
        message = view.findViewById(R.id.textView5);
        mfirebaseAuth = FirebaseAuth.getInstance();

        bRecyclerView = view.findViewById(R.id.brand_list_rv);
        bRecyclerView.setHasFixedSize(true);
        bRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        uRef = FirebaseDatabase.getInstance().getReference("UsersData");

        brandList = new ArrayList<>();
        bAdapter = new BrandShopAdapter(getActivity(), brandList);
        bRecyclerView.setAdapter(bAdapter);


        if (mfirebaseAuth.getCurrentUser() != null) {
            uRef.child(mfirebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String user = snapshot.child("userType").getValue().toString();

                        if (user.equals("Customer")) {
                            type = "Vendor";
                        } else if (user.equals("Vendor")) {
                            type = "Distributor";
                        } else {
                            type = "Admin";
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            uRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    brandList.clear();
                    bAdapter.notifyDataSetChanged();

                    if (dataSnapshot.exists()) {

                        boolean found = false;

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            String key = postSnapshot.getKey();

                            if (dataSnapshot.child(key).child("isBrandShop").exists()) {

                                String temp = dataSnapshot.child(key).child("isBrandShop").getValue().toString();
                                String temp2 = dataSnapshot.child(key).child("userType").getValue().toString();

                                if (temp.equals("true") && temp2.equals(type)) {
                                    BrandInfo info = postSnapshot.getValue(BrandInfo.class);
                                    info.setBrandKey(key);
                                    brandList.add(info);
                                    found = true;
                                } else if (temp.equals("true") && type.equals("Admin")) {
                                    BrandInfo info = postSnapshot.getValue(BrandInfo.class);
                                    info.setBrandKey(key);
                                    brandList.add(info);
                                    found = true;
                                }

                            }

                        }
                        bAdapter.notifyDataSetChanged();
                        if (!found) {
                            empty.setVisibility(View.VISIBLE);
                        }

                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        }else {
            message.setVisibility(View.VISIBLE);
            bRecyclerView.setVisibility(View.GONE);
        }



        return view;
    }


}