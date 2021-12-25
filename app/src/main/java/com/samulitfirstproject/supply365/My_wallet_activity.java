package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class My_wallet_activity extends AppCompatActivity {

    private ImageView back, profileImage;
    private TextView myBalace,totalBalance,usesBalance, textView, UserName;
    private LinearLayout linearLayout;
    private CardView profileCard;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;
    String getKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_my_wallet_activity);

        back = findViewById(R.id.back);
        profileImage = findViewById(R.id.profileImage);
        myBalace = findViewById(R.id.current_balance);
        usesBalance = findViewById(R.id.uses_balance);
        totalBalance = findViewById(R.id.total_balance);
        linearLayout = findViewById(R.id.linear);
        textView = findViewById(R.id.textView6);
        profileCard = findViewById(R.id.profileCard);
        UserName = findViewById(R.id.UserName);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (user != null) {
            getKey = user.getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersData").child(getKey);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String balance = String.valueOf(snapshot.child("userBalance").getValue());
                        String t = String.valueOf(snapshot.child("userTotalBalance").getValue());
                        String u = String.valueOf(snapshot.child("usesBalance").getValue());
                        String name = String.valueOf(snapshot.child("userName").getValue());
                        String image = String.valueOf(snapshot.child("userImage").getValue());

                        double temp = Double.parseDouble(t);
                        double temp2 = Double.parseDouble(u);

                        databaseReference.child("userBalance").setValue(""+(temp-temp2));

                        myBalace.setText(""+(temp-temp2) + "  BDT");
                        totalBalance.setText(t + "  BDT");
                        usesBalance.setText(u + "  BDT");
                        UserName.setText(name);

                        if (!image.equals(" ")){
                            Picasso.get().load(image).fit().centerInside().placeholder(R.drawable.loading_gif__).into(profileImage);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            linearLayout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            profileCard.setVisibility(View.GONE);
        }
    }
}