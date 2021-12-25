package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.samulitfirstproject.supply365.fragment.BrandShopFragment;
import com.samulitfirstproject.supply365.fragment.HomeFragment;
import com.samulitfirstproject.supply365.fragment.MessageFragment;
import com.samulitfirstproject.supply365.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference slideRef,userRef,referCode;
    private FirebaseAuth mfirebaseAuth;

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private ProfileFragment profileFragment;
    private BrandShopFragment brandShopFragment;
    private String from;
    private boolean isTrue;

    private static final String TOPIC_ORDER_NOTIFICATION = "ORDER";
    private static final String TOPIC_MESSAGE_NOTIFICATION = "MESSAGE";
    private static final String TOPIC_TRANSFER_NOTIFICATION = "TRANSFER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        from = getIntent().getStringExtra("from");

        //Initialization

        bottomNavigationView = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.main_fram);

        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        brandShopFragment = new BrandShopFragment();
        profileFragment = new ProfileFragment();

        //End

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();
        if (mFirebaseuser != null) {
            Menu nav_Menu = bottomNavigationView.getMenu();
            nav_Menu.findItem(R.id.nav_message).setVisible(true);
            nav_Menu.findItem(R.id.nav_user_profile).setVisible(true);
            isTrue = true;

            FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_ORDER_NOTIFICATION);
            FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_MESSAGE_NOTIFICATION);
            FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_TRANSFER_NOTIFICATION);

            referCode = FirebaseDatabase.getInstance().getReference().child("ReferCode").child("user");

            referCode.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child(mFirebaseuser.getUid().substring(7,14)).exists()) {

                        referCode.child(mFirebaseuser.getUid().substring(7,14)).child("value").setValue(mFirebaseuser.getUid());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else {
            Menu nav_Menu = bottomNavigationView.getMenu();
            nav_Menu.findItem(R.id.nav_message).setVisible(false);
            nav_Menu.findItem(R.id.nav_user_profile).setVisible(false);
            isTrue = false;
        }


        if (isTrue) {
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(mFirebaseuser.getUid());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String type = snapshot.child("userType").getValue().toString();

                        if (type.equals("Distributor")) {

                            Menu nav_Menu = bottomNavigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_brand_shop).setVisible(false);

                        }

//                        String t = String.valueOf(snapshot.child("userTotalBalance").getValue());
//                        String u = String.valueOf(snapshot.child("usesBalance").getValue());
//
//                        double temp = Double.parseDouble(t);
//                        double temp2 = Double.parseDouble(u);
//
//                        userRef.child("userBalance").setValue(""+(temp-temp2));

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //set default fragment
        if (from!=null){
            if (from.equals("message")){
                setFragment(messageFragment);
            }

        }else {
            setFragment(homeFragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.nav_home : {
                        //bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;
                    }
                    case R.id.nav_message : {
                        //bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(messageFragment);
                        return true;
                    }

                    case R.id.nav_brand_shop : {
                        //bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(brandShopFragment);
                        return true;
                    }

                    case R.id.nav_user_profile : {
                        //bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(profileFragment);
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });


        


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fram,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure! You want to exit this app?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("no", null)
                .create();
        dialog.show();
    }
}