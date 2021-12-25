package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.adapter.SubCategoryAdapter;
import com.samulitfirstproject.supply365.adapter.VendorAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.samulitfirstproject.supply365.model.ReviewInfo;
import com.samulitfirstproject.supply365.model.VendorInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {

    private RecyclerView rRecyclerView;
    private ImageView productImage,back;
    private TextView productName, price, productCatagory, details, discount, bestSell , reviewTV, relatedProduct, availableVendor, wishList, addReview, Strike_price, textView4;
    private Button addToCart;
    private CardView cv_bestSell;
    private FirebaseAuth mfirebaseAuth;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private View view4, view2;
    private FirebaseRecyclerAdapter<ReviewInfo, ProductDetails.ViewHolder> rAdapter = null;


    // Get Something
    String current_user_id = " ",getIntentKey,vendorUID,category,valueWholeSell,ImageURL,userType= " ";

    //FireBase DataBase
    DatabaseReference databaseReference,vendorRef,ProRef,CartRef,WishRef,reviewRef,userRef,disRef;

    private RecyclerView cRecyclerView,vRecyclerView;
    private SubCategoryAdapter cAdapter;
    private VendorAdapter vAdapter;
    private ArrayList<CatInfo> cList;
    private ArrayList<VendorInfo> vendorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_product_details);


        productName = (TextView) findViewById(R.id.productName);
        price = (TextView) findViewById(R.id.Price);
        details = (TextView) findViewById(R.id.details);
        textView4 = (TextView) findViewById(R.id.textView4);
        productCatagory = (TextView) findViewById(R.id.productCatagory);
        discount = (TextView) findViewById(R.id.Discount);
        bestSell = (TextView) findViewById(R.id.BestSell);
        cv_bestSell = (CardView) findViewById(R.id.cv_bestSell);

        back = findViewById(R.id.back);
        relatedProduct = findViewById(R.id.related_product);
        availableVendor = findViewById(R.id.available_vendor);
        addToCart =findViewById(R.id.AddToCart);
        wishList = findViewById(R.id.add_wish_list);
        Strike_price = findViewById(R.id.Strike_price);
        addReview = findViewById(R.id.add_review);
        reviewTV = findViewById(R.id.tv_review);

        view2 = findViewById(R.id.view2);
        view4 = findViewById(R.id.view4);


        productImage = (ImageView) findViewById(R.id.productImage);


        getIntentKey = getIntent().getStringExtra("product_id");

        //Toast.makeText(this, ""+getIntentKey, Toast.LENGTH_SHORT).show();


        mfirebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

            addReview.setVisibility(View.GONE);

        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rRecyclerView = findViewById(R.id.review_rv);
        //rRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerR = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rRecyclerView.setLayoutManager(linearLayoutManagerR);

        vRecyclerView = findViewById(R.id.available_vendor_rv);
        vRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        vRecyclerView.setLayoutManager(linearLayoutManagerV);
        vendorList = new ArrayList<>();
        vAdapter = new VendorAdapter(this, vendorList);
        vRecyclerView.setAdapter(vAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(getIntentKey);
        vendorRef = FirebaseDatabase.getInstance().getReference().child("Products").child(getIntentKey).child("Vendor");
        disRef = FirebaseDatabase.getInstance().getReference().child("Products").child(getIntentKey).child("Distributor");
        ProRef = FirebaseDatabase.getInstance().getReference("Products");
        CartRef = FirebaseDatabase.getInstance().getReference("MyCart");
        WishRef = FirebaseDatabase.getInstance().getReference("WishList");
        reviewRef = FirebaseDatabase.getInstance().getReference().child("Products").child(getIntentKey).child("RateReview");
        userRef = FirebaseDatabase.getInstance().getReference("UsersData");


        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    userType = snapshot.child("userType").getValue().toString();

                    if (userType.equals("Vendor")){

                        availableVendor.setText("Available Distributor");

                        disRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                vendorList.clear();
                                vAdapter.notifyDataSetChanged();

                                if(dataSnapshot.exists()){

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        VendorInfo info = postSnapshot.getValue(VendorInfo.class);
                                        info.setVendorKey(postSnapshot.getKey());
                                        vendorList.add(info);

                                    }
                                    vAdapter.notifyDataSetChanged();

                                    if (vendorList.size() == 0){
                                        view4.setVisibility(View.GONE);
                                    }

                                }
                                else {
                                    availableVendor.setVisibility(View.GONE);
                                    view4.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ProductDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });

                    }else {

                        vendorRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                vendorList.clear();
                                vAdapter.notifyDataSetChanged();

                                if(dataSnapshot.exists()){

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        VendorInfo info = postSnapshot.getValue(VendorInfo.class);
                                        info.setVendorKey(postSnapshot.getKey());
                                        vendorList.add(info);

                                    }
                                    vAdapter.notifyDataSetChanged();

                                    if (vendorList.size() == 0){
                                        view4.setVisibility(View.GONE);
                                    }

                                }
                                else {
                                    availableVendor.setVisibility(View.GONE);
                                    view4.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ProductDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String valueName = String.valueOf(snapshot.child("productName").getValue());
                    String valuePrice = String.valueOf(snapshot.child("productPrice").getValue());
                    vendorUID = String.valueOf(snapshot.child("vendorUID").getValue());
                    String valueDetails = String.valueOf(snapshot.child("productDescription").getValue());
                    ImageURL = String.valueOf(snapshot.child("productImage").getValue());
                    category = String.valueOf(snapshot.child("productCategory").getValue());
                    String valueDiscount = String.valueOf(snapshot.child("productDiscount").getValue());
                    String valueBestSell = String.valueOf(snapshot.child("isBestSelling").getValue());
                    valueWholeSell = String.valueOf(snapshot.child("productWholeSalesPrice").getValue());
                    String cash = String.valueOf(snapshot.child("productCashBack").getValue());

                    String v_cash = String.valueOf(snapshot.child("vendorCashBack").getValue());
                    String v_dis = String.valueOf(snapshot.child("vendorDiscount").getValue());

                    boolean isTrue;
                    double strikePrice_cut;
                    String strikePrice_string, percentage_string;
                    String digits;

                    productName.setText(valueName);



                    if (valueDetails == null || valueDetails.equals(" ")){
                        view2.setVisibility(View.GONE);
                        textView4.setVisibility(View.GONE);
                    }


                    details.setText(valueDetails);
                    productCatagory.setText("Product Category :\t"+ category);
                    Picasso.get().load(ImageURL).placeholder(R.drawable.loading_gif__).fit().centerInside().into(productImage);


                    if (userType.equals("Vendor")){

                        if (!v_dis.equals(" ") || !v_cash.equals(" ") ){
                            discount.setVisibility(View.VISIBLE);

                            if (!v_dis.equals(" ")){
                                discount.setText("Offer : \t" + v_dis + " % OFF");
                            }else {
                                discount.setText("Offer : \t" + v_cash + " % Cash Back");
                            }

                        }
                        else{
                            discount.setVisibility(View.GONE);
                            discount.setText(" ");
                        }

                    }else {

                        if (!valueDiscount.equals(" ") || !cash.equals(" ") ){
                            discount.setVisibility(View.VISIBLE);

                            if (!valueDiscount.equals(" ")){
                                discount.setText("Offer : \t" + valueDiscount + " % OFF");
                            }else {
                                discount.setText("Offer : \t" + cash + " % Cash Back");
                            }

                        }
                        else{
                            discount.setVisibility(View.GONE);
                            discount.setText(" ");
                        }

                    }



                    if (valueBestSell.equals("Yes")){
                        //bestSell.setVisibility(View.VISIBLE);
                        cv_bestSell.setVisibility(View.VISIBLE);
                    }


                    if (userType.equals("Vendor")){

                        if (!v_dis.equals(null) && !v_dis.equals(" ")) {
                            if (userType.equals("Vendor")){
                                isTrue = true;
                            }
                            else {
                                isTrue = false;
                            }
                            Strike_price.setVisibility(View.VISIBLE);
                            try {
                                digits = v_dis.trim();

                                if (isTrue) {
                                    strikePrice_cut = (Double.parseDouble(valueWholeSell) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(valueWholeSell)));
                                    strikePrice_string =  "৳ " + valueWholeSell;
                                } else {
                                    strikePrice_cut = (Double.parseDouble(valuePrice) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(valuePrice)));
                                    strikePrice_string =  "৳ " + valuePrice;
                                }

                                int strikePrice_cut_int = (int) strikePrice_cut;
                                percentage_string = String.valueOf(strikePrice_cut_int);
                                price.setText("৳ " + percentage_string);

                                SpannableString spannableString = new SpannableString(strikePrice_string);
                                spannableString.setSpan(new StrikethroughSpan(), 0, strikePrice_string.length() - 1, 0);
                                Strike_price.setText(spannableString);

                            }catch (Exception e){
                                // Nothing now
                            }

                        }else {
                            if (userType.equals("Vendor")){
                                price.setText("৳ " + valueWholeSell);
                            }
                            else {
                                price.setText("৳ " + valuePrice);
                            }
                        }

                    }else {
                        if (!valueDiscount.equals(null) && !valueDiscount.equals(" ")) {
                            if (userType.equals("Vendor")){
                                isTrue = true;
                            }
                            else {
                                isTrue = false;
                            }
                            Strike_price.setVisibility(View.VISIBLE);
                            try {
                                digits = valueDiscount.trim();

                                if (isTrue) {
                                    strikePrice_cut = (Double.parseDouble(valueWholeSell) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(valueWholeSell)));
                                    strikePrice_string =  "৳ " + valueWholeSell;
                                } else {
                                    strikePrice_cut = (Double.parseDouble(valuePrice) - ((Double.parseDouble(digits) / 100) * Double.parseDouble(valuePrice)));
                                    strikePrice_string =  "৳ " + valuePrice;
                                }

                                int strikePrice_cut_int = (int) strikePrice_cut;
                                percentage_string = String.valueOf(strikePrice_cut_int);
                                price.setText("৳ " + percentage_string);

                                SpannableString spannableString = new SpannableString(strikePrice_string);
                                spannableString.setSpan(new StrikethroughSpan(), 0, strikePrice_string.length() - 1, 0);
                                Strike_price.setText(spannableString);

                            }catch (Exception e){
                                // Nothing now
                            }

                        }else {
                            if (userType.equals("Vendor")){
                                price.setText("৳ " + valueWholeSell);
                            }
                            else {
                                price.setText("৳ " + valuePrice);
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        cRecyclerView = findViewById(R.id.related_product_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cList = new ArrayList<>();
        cAdapter = new SubCategoryAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        ProRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    int count = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        String check = dataSnapshot.child(key).child("productCategory").getValue().toString();

                        if(check.equals(category)){

                            if (!key.equals(getIntentKey)){

                                CatInfo info = postSnapshot.getValue(CatInfo.class);
                                info.setKey(postSnapshot.getKey());
                                cList.add(info);

                                count++;
                            }

                        }



                    }
                    cAdapter.notifyDataSetChanged();
                    if (count==0){
                        relatedProduct.setVisibility(View.GONE);
                    }

                }else {
                    relatedProduct.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProductDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mFirebaseuser != null) {

                    Map cart = new HashMap();

                    cart.put("productID",getIntentKey );

                    CartRef.child(current_user_id).child(getIntentKey).updateChildren(cart);


                    Toast.makeText(ProductDetails.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();

                    addToCart.setEnabled(false);
                } else {

                    Toast.makeText(ProductDetails.this, "Please Login First", Toast.LENGTH_SHORT).show();
                }


            }
        });

        wishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wishList.setEnabled(false);

                if (mFirebaseuser != null) {

                    Map cart = new HashMap();

                    cart.put("productID",getIntentKey );

                    WishRef.child(current_user_id).child(getIntentKey).updateChildren(cart);


                    Toast.makeText(ProductDetails.this, "Added to wish list successfully", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(ProductDetails.this, "Please Login First", Toast.LENGTH_SHORT).show();
                }

            }
        });


        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(ProductDetails.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.rating_review);

                final TextView rateTV = (TextView)dialog.findViewById(R.id.rateTV);
                Button submitBtn = (Button)dialog.findViewById(R.id.submitRateBtn);
                Button cancelBtn = (Button)dialog.findViewById(R.id.cancelRateBtn);
                RatingBar rating = (RatingBar)dialog.findViewById(R.id.ratingsBar);

                rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        rateTV.setText(""+v);
                    }
                });

                final EditText reviewED = (EditText)dialog.findViewById(R.id.reviewED);
                dialog.show();

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(ProductDetails.this);
                        progressDialog.show();
                        progressDialog.setMessage("Please Wait...");

                        dialog.cancel();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressDialog.dismiss();

                                String text = reviewED.getText().toString().trim();


                                HashMap add = new HashMap();

                                add.put("reviewer", current_user_id);
                                add.put("review", text);
                                add.put("rating", rateTV.getText().toString());


                                databaseReference.child("RateReview").push().updateChildren(add);

                                Toast.makeText(ProductDetails.this, "Review Added", Toast.LENGTH_SHORT).show();


                            }
                        },2000);


                    }
                });


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        if (getIntentKey.equals("-MYzhOOsvNT_IxuqrQky")){

            ProRef.child("-MYzhOOsvNT_IxuqrQky").child("Exception").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()){
                        if(dataSnapshot.hasChild(current_user_id)){

                            addToCart.setVisibility(View.GONE);
                        }
                        else {

                            addToCart.setVisibility(View.VISIBLE);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        //User Review
        FirebaseRecyclerOptions<ReviewInfo> options =
                new FirebaseRecyclerOptions.Builder<ReviewInfo>()
                        .setQuery(reviewRef,ReviewInfo.class)
                        .build();


        rAdapter = new FirebaseRecyclerAdapter<ReviewInfo, ProductDetails.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ProductDetails.ViewHolder holder, final int i, @NonNull ReviewInfo review) {

                String uid = review.getReviewer();
                String rate = review.getRating();
                String review1 = review.getReview();

                final String key = rAdapter.getRef(i).getKey();

                holder.Rate.setText(rate);
                holder.Review.setText(review1);

                userRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            String name = snapshot.child("userName").getValue().toString();
                            String url = snapshot.child("userImage").getValue().toString();


                            holder.Name.setText(name);

                            if (!url.equals(" ")) {

                                Picasso.get().load(url).placeholder(R.drawable.loading_gif__).fit().centerCrop().into(holder.Image);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public ProductDetails.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_item, parent, false);

                return new ProductDetails.ViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                // if there are no chat messages, show a view that invites the user to add a message
                reviewTV.setVisibility(
                        rAdapter.getItemCount() == 0 ? View.INVISIBLE : View.VISIBLE
                );
            }
        };


        rAdapter.startListening();
        rRecyclerView.setAdapter(rAdapter);

    }

    //view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Name,Rate,Review;
        public ImageView Image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.nameR);
            Image = itemView.findViewById(R.id.proImage);
            Rate = itemView.findViewById(R.id.tv_rating);
            Review = itemView.findViewById(R.id.reviewText);


        }
    }

}