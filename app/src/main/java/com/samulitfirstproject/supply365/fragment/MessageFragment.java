package com.samulitfirstproject.supply365.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.adapter.messageListAdapter;
import com.samulitfirstproject.supply365.messageShowActivity;
import com.samulitfirstproject.supply365.model.UserDataInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MessageFragment extends Fragment {

    private TextView Name, Number,welcomeText;
    private ImageView UserImage;
    private CardView cardView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private messageListAdapter adapter;

    private String receiveID, store, UserID, userName, userNumber, userType, userImage;
    private List<UserDataInfo> list;
    private UserDataInfo info;
    private List<String> list2;

    private DatabaseReference databaseReference, databaseReference2,userRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialize
        UserImage = view.findViewById(R.id.UserImage);
        Name = view.findViewById(R.id.Name);
        Number = view.findViewById(R.id.Number);
        cardView = view.findViewById(R.id.CardView);
        linearLayout = view.findViewById(R.id.LinearLayout);
        relativeLayout = view.findViewById(R.id.Relative);
        welcomeText = view.findViewById(R.id.welcomeMessage);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);


            UserID = user.getUid();

            if (UserID.equals(getString(R.string.admin_UID))) {
                linearLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
            }

            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
            userRef.child(UserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String n = snapshot.child("userName").getValue().toString();

                        welcomeText.setText("Welcome "+n);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // For retrieve Chat User ID...........................................................................................
            databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatList");
            Query query = databaseReference.orderByChild("currentTime");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list2 = new ArrayList<>();
                    list2.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (!Objects.equals(ds.getKey(), getString(R.string.admin_UID))) {
                            receiveID = ds.child("ID").getValue(String.class);
                            list2.add(receiveID);
                        }
                    }

                    list = new ArrayList<>();
                    list.clear();
                    for (final String d : list2){
                        store = d;
                        // Restore Data on model class name UserDataInfo.....................................................
                        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("UsersData");
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (Objects.equals(store, ds.getKey())) {
                                        userName = String.valueOf(ds.child("userName").getValue());
                                        userNumber = String.valueOf(ds.child("userPhone").getValue());
                                        userType = String.valueOf(ds.child("userType").getValue());
                                        userImage = String.valueOf(ds.child("userImage").getValue());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // End .............................................................................................

                        info = new UserDataInfo(userImage, userType, userNumber, userName, store);
                        list.add(info);
                        adapter = new messageListAdapter(list, getContext());
                        recyclerView.setAdapter(adapter);
                        Collections.reverse(list);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            // End .....................................................................................................................


            // If he is a User not an admin .............................................................................................................................
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersData").child(getString(R.string.admin_UID));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String Image = String.valueOf(snapshot.child("userImage").getValue());
                    String valueName = String.valueOf(snapshot.child("userName").getValue());
                    String ValueNumber = String.valueOf(snapshot.child("userPhone").getValue());

                    if (!Image.equals(" ")) {
                        Picasso.get().load(Image).placeholder(R.drawable.loading_gif__).fit().into(UserImage);
                    }
                    Name.setText(valueName);
                    Number.setText(ValueNumber);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardView.setBackgroundResource(R.drawable.button2);
                    Intent goToMessageShow = new Intent(getActivity(), messageShowActivity.class);
                    goToMessageShow.putExtra("key", getString(R.string.admin_UID));
                    startActivity(goToMessageShow);
                }
            });
            // End ......................................................................................................................................................


        return view;
    }
}