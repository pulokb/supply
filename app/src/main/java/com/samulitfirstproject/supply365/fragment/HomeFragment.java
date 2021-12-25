package com.samulitfirstproject.supply365.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.AdminActivity;
import com.samulitfirstproject.supply365.BuildConfig;
import com.samulitfirstproject.supply365.CartActivity;
import com.samulitfirstproject.supply365.CategoryActivity;
import com.samulitfirstproject.supply365.Contact_US;
import com.samulitfirstproject.supply365.DistributorActivity;
import com.samulitfirstproject.supply365.LoginActivity;
import com.samulitfirstproject.supply365.My_wallet_activity;
import com.samulitfirstproject.supply365.NearestVendor;
import com.samulitfirstproject.supply365.News;
import com.samulitfirstproject.supply365.OfferProduct;
import com.samulitfirstproject.supply365.Privacy_Policy;
import com.samulitfirstproject.supply365.RefundPolicyActivity;
import com.samulitfirstproject.supply365.SearchActivity;
import com.samulitfirstproject.supply365.ShowAllActivity;
import com.samulitfirstproject.supply365.UserProfileActivity;
import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.geocode.GeoCodingLocation;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.samulitfirstproject.supply365.model.ProductInfo;
import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.RegistrationActivity;
import com.samulitfirstproject.supply365.model.SlideInfo;
import com.samulitfirstproject.supply365.VendorActivity;
import com.samulitfirstproject.supply365.model.VendorInfo;
import com.samulitfirstproject.supply365.WishlistActivity;
import com.samulitfirstproject.supply365.adapter.ProductAdapter;
import com.samulitfirstproject.supply365.adapter.SliderAdapter;
import com.samulitfirstproject.supply365.adapter.VendorAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mdrawerLayout;
    private TextView name, email,nearTV, ShowAll1, ShowAll2, ShowAll3, ShowAll4;
    private ImageView cart,registration,search,load3,profileImage,wish, load1, load2, load4;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private Button place_oder_button, textButton;
    private NestedScrollView nested_scroll_view;

    SliderView sliderView;
    SliderAdapter adapter;

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference SlideRef,topDealRef,bestSellRef,topVendorRef,popularProductRef,userRef,ProRef, forMap,update, order_ref, FinalOrder_Ref;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //some change on version-1.3. Those are tRecyclerView,bRecyclerView, pRecyclerView..............

    private RecyclerView vRecyclerView, cRecyclerView, tRecyclerView, bRecyclerView, pRecyclerView;
    private SubCategoryAdapter cAdapter;
    private ArrayList<CatInfo> cList;
    private ProductAdapter pAdapter,tAdapter,bAdapter;
    private VendorAdapter vAdapter;
    private ArrayList<ProductInfo> pList,tList,bList;
    private ArrayList<VendorInfo> vendorList;

    private String current_user_id, current_id, user_type,url, address;
    public int counter = 0;

    private CardView category,offer,newsPage,contactUs,NearestVendor;
    private LinearLayout adminPannel,vendorPannel,distriPannel;
    private boolean bool = false;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Initialization
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);
        profileImage = headerView.findViewById(R.id.profile_image);

        cart = view.findViewById(R.id.cart);
        registration = view.findViewById(R.id.registration);
        search = view.findViewById(R.id.search_bar);
        wish = view.findViewById(R.id.wishlist);
        textButton = view.findViewById(R.id.textButton);

        ShowAll1 = view.findViewById(R.id.see_all1);
        ShowAll2 = view.findViewById(R.id.see_all2);
        ShowAll3 = view.findViewById(R.id.see_all3);
        ShowAll4 = view.findViewById(R.id.see_all4);

        category = view.findViewById(R.id.cv_category);
        offer = view.findViewById(R.id.cv_offer);
        newsPage = view.findViewById(R.id.cv_new);
        contactUs = view.findViewById(R.id.cv_contact_us);
        NearestVendor = view.findViewById(R.id.cv_nearest_vendor);

        nearTV = view.findViewById(R.id.tv_near_vendor);

        adminPannel = view.findViewById(R.id.admin_pannel);
        vendorPannel = view.findViewById(R.id.vendor_pannel);
        distriPannel = view.findViewById(R.id.distributor_pannel);

        place_oder_button = view.findViewById(R.id.place_oder_button);

        Toolbar toolbar = view.findViewById(R.id.tool_bar);

        SlideRef = FirebaseDatabase.getInstance().getReference().child("SlidingBanner");
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
        ProRef = FirebaseDatabase.getInstance().getReference().child("Products");
        order_ref = FirebaseDatabase.getInstance().getReference().child("Order");
        FinalOrder_Ref = FirebaseDatabase.getInstance().getReference().child("FinalOrder");

        progressDialog2 = new ProgressDialog(getContext());

        mfirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();


        if (mFirebaseuser != null) {

            place_oder_button.setVisibility(View.VISIBLE);
            registration.setVisibility(View.GONE);
            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            if(current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - string.xml
                adminPannel.setVisibility(View.VISIBLE);
                NearestVendor.setVisibility(View.GONE);
                nearTV.setVisibility(View.GONE);

            }else{

                userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            String type = snapshot.child("userType").getValue(String.class);
                            String access = snapshot.child("vPanelAccess").getValue(String.class);

                            if (access.equals("Yes")){

                                if (type.equals("Vendor")){

                                    vendorPannel.setVisibility(View.VISIBLE);
                                    nearTV.setText("Nearest Distributor");

                                }else if(type.equals("Distributor")){
                                    distriPannel.setVisibility(View.VISIBLE);
                                }

                            }else {

                                vendorPannel.setVisibility(View.GONE);
                                distriPannel.setVisibility(View.GONE);

                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);

        } else {
            current_user_id = " ";

            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_signout).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_wallet).setVisible(false);

        }



        load3 = view.findViewById(R.id.load3);
        load1 = view.findViewById(R.id.load1);
        load2 = view.findViewById(R.id.load2);
        load4 = view.findViewById(R.id.load4);


        vRecyclerView = view.findViewById(R.id.top_vendor_rv);
        tRecyclerView = view.findViewById(R.id.todays_best_deals);
        bRecyclerView = view.findViewById(R.id.best_sells);
        pRecyclerView = view.findViewById(R.id.popular_product);

        tRecyclerView.setHasFixedSize(true);
        tRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        bRecyclerView.setHasFixedSize(true);
        bRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        pRecyclerView.setHasFixedSize(true);
        pRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3); // 3 = Number of Columns
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 3); // 3 = Number of Columns
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 3); // 3 = Number of Columns
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        tRecyclerView.setLayoutManager(gridLayoutManager);
        bRecyclerView.setLayoutManager(gridLayoutManager1);
        vRecyclerView.setLayoutManager(linearLayoutManagerV);
        pRecyclerView.setLayoutManager(gridLayoutManager2);

        pList = new ArrayList<>();
        tList = new ArrayList<>();
        bList = new ArrayList<>();
        vendorList = new ArrayList<>();

        pAdapter = new ProductAdapter(getActivity(), pList);// Popular Products
        tAdapter = new ProductAdapter(getActivity(), tList);// Today Best Deals
        bAdapter = new ProductAdapter(getActivity(), bList);// Best Selling Products
        vAdapter = new VendorAdapter(getActivity(), vendorList);

        tRecyclerView.setAdapter(tAdapter);
        bRecyclerView.setAdapter(bAdapter);
        vRecyclerView.setAdapter(vAdapter);
        pRecyclerView.setAdapter(pAdapter);

        topDealRef = FirebaseDatabase.getInstance().getReference("TopDeals");
        bestSellRef = FirebaseDatabase.getInstance().getReference("BestSelling");
        topVendorRef = FirebaseDatabase.getInstance().getReference("TopVendor");
        popularProductRef = FirebaseDatabase.getInstance().getReference("PopularProduct");

        update = FirebaseDatabase.getInstance().getReference().child("CheckUpdate");


        //Call Action Drawer
        mdrawerLayout = view.findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mdrawerLayout, toolbar, R.string.open, R.string.close);
        mdrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //End

        //Navigation View Listener
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String n = snapshot.child("userName").getValue(String.class);
                    String e = snapshot.child("userEmail").getValue(String.class);
                    String url = snapshot.child("userImage").getValue(String.class);

                    name.setText(n);
                    email.setText(e);

                    if(!url.equals(" ")){
                        Picasso.get().load(url).centerCrop().fit().placeholder(R.drawable.loading_gif__).into(profileImage);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Open Search Activity
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), SearchActivity.class);
                startActivity(n);

            }
        });

        //Open Registration Activity
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), RegistrationActivity.class);
                startActivity(n);

            }
        });

        //Open Cart Activity
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), CartActivity.class);
                startActivity(n);

            }
        });

        //Open Wish Activity
        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), WishlistActivity.class);
                startActivity(n);

            }
        });

        //Open Admin Activity
        adminPannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), AdminActivity.class);
                startActivity(n);

            }
        });

        //Open Category Activity
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), CategoryActivity.class);
                startActivity(n);

            }
        });

        //Open Vendor Activity
        vendorPannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), VendorActivity.class);
                startActivity(n);

            }
        });

        //Open Vendor Activity
        distriPannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), DistributorActivity.class);
                startActivity(n);

            }
        });

        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Need to change ..................................................................................................................................
                Intent n = new Intent(getActivity(), OfferProduct.class);
                startActivity(n);

            }
        });

        newsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), News.class);
                startActivity(n);

            }
        });


        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(getActivity(), Contact_US.class);
                startActivity(n);

            }
        });


        NearestVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    current_id = firebaseUser.getUid();

                    if (!Objects.equals(current_id, " ") && !Objects.equals(current_id, getString(R.string.admin_UID))) {

                        progressDialog2.setMessage("Please wait, check your location");
                        progressDialog2.show();
                        forMap = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_id);
                        forMap.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                address = snapshot.child("userLocation").getValue(String.class);
                                user_type = snapshot.child("userType").getValue(String.class);
                                if (!address.equals(" ") && address.length() > 4) {
                                    bool = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (bool) {

                            bool = false;
                            GeoCodingLocation.getAddressFromLocation(address, getContext(), new GeoCoderHandler(), current_id, null, null, null);

                            Handler handle = new Handler(Looper.getMainLooper());
                            handle.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), NearestVendor.class);
                                    intent.putExtra("whichType", user_type);
                                    startActivity(intent);

                                    progressDialog2.dismiss();
                                }
                            }, 1000);
                        }else {
                            progressDialog2.dismiss();
                            Toast.makeText(getContext(), "Please Set Your Location First", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (NullPointerException e){
                    Toast.makeText(getContext(), "Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //End ....................................................................................................................................


        sliderView = view.findViewById(R.id.imageSlider);
        adapter = new SliderAdapter(getActivity());
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(R.color.colorPrimary);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        renewItems(view);

        topDealRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tList.clear();
                tAdapter.notifyDataSetChanged();
                counter = 0;

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        if (counter == 6 || counter > 6 ){
                            counter = 0;
                            break;
                        }else {

                            String key = postSnapshot.getKey();

                            String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                            ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                            info.setProductKey(ID);
                            info.setDeleteKey(key);
                            tList.add(info);

                            counter++;
                        }

                    }
                    load1.setVisibility(View.GONE);
                    tAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        bestSellRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bList.clear();
                bAdapter.notifyDataSetChanged();
                counter = 0;

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        if (counter == 6 || counter > 6 ){
                            counter = 0;
                            break;
                        }else {

                            String key = postSnapshot.getKey();

                            String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                            ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                            info.setProductKey(ID);
                            info.setDeleteKey(key);
                            bList.add(info);

                            counter++;
                        }

                    }
                    load2.setVisibility(View.GONE);
                    bAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        topVendorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                vendorList.clear();
                vAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        String UID = dataSnapshot.child(key).child("vendorUID").getValue().toString();

                        VendorInfo info = postSnapshot.getValue(VendorInfo.class);
                        info.setVendorKey(UID);
                        info.setVendorDkey(key);
                        vendorList.add(info);

                    }
                    load3.setVisibility(View.GONE);
                    vAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        popularProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pList.clear();
                pAdapter.notifyDataSetChanged();
                counter = 0;

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        if (counter == 6 || counter > 6 ){
                            counter = 0;
                            break;
                        }else {

                            String key = postSnapshot.getKey();

                            String ID = dataSnapshot.child(key).child("productID").getValue().toString();

                            ProductInfo info = postSnapshot.getValue(ProductInfo.class);
                            info.setProductKey(ID);
                            info.setDeleteKey(key);
                            pList.add(info);

                            counter++;
                        }

                    }
                    load4.setVisibility(View.GONE);
                    pAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        // for update version-1.3.......
        place_oder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });


        ShowAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                intent.putExtra("number", 1);
                startActivity(intent);
            }
        });

        ShowAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                intent.putExtra("number", 2);
                startActivity(intent);
            }
        });

        ShowAll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                intent.putExtra("number", 3);
                startActivity(intent);
            }
        });

        ShowAll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                intent.putExtra("number", 4);
                startActivity(intent);
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                intent.putExtra("number", 4);
                startActivity(intent);
            }
        });


        return view;

    }



    //Navigation Item Selected Method
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent n;
        switch (item.getItemId()) {

            case R.id.nav_profile:
                n = new Intent(getActivity(), UserProfileActivity.class); startActivity(n);
                break;

            case R.id.nav_login:
                n = new Intent(getActivity(), LoginActivity.class); startActivity(n);
                break;

            case R.id.nav_wish_list:
                n = new Intent(getActivity(),WishlistActivity.class); startActivity(n);
                break;
            case R.id.nav_contact_us:
                n = new Intent(getActivity(),Contact_US.class); startActivity(n);
                break;

            case R.id.nav_privacy:
                n = new Intent(getActivity(), Privacy_Policy.class); startActivity(n);
                break;

            case R.id.nav_refund:
                n = new Intent(getActivity(), RefundPolicyActivity.class); startActivity(n);
                break;


            case R.id.nav_cart_list:
                n = new Intent(getActivity(), CartActivity.class); startActivity(n);
                break;

            case R.id.nav_my_wallet:

                n = new Intent(getActivity(), My_wallet_activity.class);
                startActivity(n);

                break;

            case R.id.nav_signout:

                signOut();

                break;

            case R.id.nav_check_update:

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Checking...");
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        update.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){


                                    String version = dataSnapshot.child("version").getValue(String.class);
                                    url = dataSnapshot.child("url").getValue(String.class);

                                    String VersionName = BuildConfig.VERSION_NAME;

                                    if (version.equals(VersionName)) {

                                        Toast.makeText(getActivity(), "Supply365 Is Up To Date", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    } else{
                                        progressDialog.dismiss();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("New Version Available");
                                        builder.setIcon(R.drawable.logo);
                                        builder.setCancelable(true);
                                        builder.setMessage("Update Supply365 For Better Experience")
                                                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(url));
                                                        startActivity(intent);

                                                        getActivity().finish();

                                                    }
                                                }).setNegativeButton("Not Now",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.cancel();
                                            }

                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }

                                }else {
                                    Toast.makeText(getActivity(), "Supply365 Is Up To Date", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },1500);

                break;

        }
        return true;
    }

    private void signOut() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Are you sure you want to log out?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.show();
                        progressDialog.setMessage("Signing Out...");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                progressDialog.dismiss();
                                getActivity().finishAffinity();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        },1700);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

//        Window view=((AlertDialog)alert11).getWindow();
//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        view.setBackgroundDrawableResource(R.drawable.dialog_shape);

        Button btnPositive = alert11.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alert11.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
    }


    public void renewItems(View view) {
        final List<SlideInfo> sliderItemList = new ArrayList<>();

        SlideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        String key = postSnapshot.getKey();
                        String description = " ";

                            SlideInfo posts = postSnapshot.getValue(SlideInfo.class);
                            posts.setImageKey(postSnapshot.getKey());
                            posts.setDescription(description);
                            sliderItemList.add(posts);

                }
                    adapter.renewItems((ArrayList<SlideInfo>) sliderItemList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cRecyclerView = view.findViewById(R.id.all_product_rv);
        cRecyclerView.setHasFixedSize(true);
        cRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cList = new ArrayList<>();
        cAdapter = new SubCategoryAdapter(getActivity(), cList);
        cRecyclerView.setAdapter(cAdapter);

        ProRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();
                counter = 0;

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        if (counter == 40 || counter > 40 ){
                            counter = 0;
                            break;
                        }else {
                            CatInfo info = postSnapshot.getValue(CatInfo.class);
                            info.setKey(postSnapshot.getKey());
                            cList.add(info);

                            counter++;
                        }

                    }
                    Collections.shuffle(cList,new Random());
                    cAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        

    }


    //Don't Call or Remove (updateUser Method) without Akash's Permission
    private void updateUser2() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MyProduct");
        final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("UsersData");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String UID = dataSnapshot.getKey();
                        databaseReference2.child(UID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String Name = String.valueOf(snapshot.child("userName").getValue());
                                    String Email = String.valueOf(snapshot.child("userEmail").getValue());
                                    String UserType = String.valueOf(snapshot.child("userType").getValue());
                                    String UserID = String.valueOf(snapshot.child("userUID").getValue());
                                    String Location = String.valueOf(snapshot.child("userLocation").getValue());

                                    if (!Location.equals(" ")) {
                                        GeoCodingLocation.getAddressFromLocation(Location, getContext(), new GeoCoderHandler(), UserID, UserType, Name, Email);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //update Product field
    private void update() {

        ProRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    String key = postSnapshot.getKey();

                    ProRef.child(key).child("Limitation")
                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Map<String, Object> postValues = new HashMap<String,Object>();
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        postValues.put(snapshot.getKey(),snapshot.getValue());
                                                                    }

                                                                    postValues.put("limit", "100");
                                                                    ProRef.child(key).child("Limitation").updateChildren(postValues);

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {}
                                                            }
                            );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    // Akash ......................................................................................
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
        }
    }



    //End
}