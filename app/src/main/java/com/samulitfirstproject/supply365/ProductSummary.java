package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductSummary extends AppCompatActivity {

    private TextView item,price,subPrice,totalPrice,quantity,myBalance,selectedVendor,mNum,tvC,C,pro_cash, TotalWeight, total_weight_price, Item_weights;
    private EditText orderAddress,orderNumber,orderVendor,pNum,pID,coupon;
    private ImageView back,edit;
    private String i,p,current_user_id="",balance,pMethod,current_user_dp,number,method = " ",couponCode =" ",value = " ",userType, Weight_String, First, Second, Third, Four, Total_Weight_String;
    private Button confirmButton,apply;
    private StringBuilder stringBuilder;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference uRef,orderRef,ProRef,PayNumRef,CouponRef, databaseReference,ExRef;


    private double  sPrice = 0,tPrice = 0, tt, extraBalance =0, usesBalance=0,t, without_offer, tUsesBalance=0, kg,cc=0;
    private long  tCash = 0,num, Weight_Long = 0, num2;
    private int Weight_int = 0;
    private ArrayList<String> idList, itemList, priceList, qList;
    private Boolean isOffer = false;
    private ProgressDialog progressDialog;
    private RadioButton cod, bkash, rocket, nogod;
    private LinearLayout lNum,lID,lPay;
    private boolean check = false;
    private boolean isConnectWithInternet = false;
    private static DecimalFormat decimalFormat = new DecimalFormat("#.###");


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_product_summary);

        item = findViewById(R.id.item);
        price = findViewById(R.id.item_price);
        back  = findViewById(R.id.back);
        totalPrice = findViewById(R.id.total_price);
        quantity = findViewById(R.id.quantity);
        myBalance = findViewById(R.id.my_balance);
        subPrice = findViewById(R.id.sub_price);
        orderAddress = findViewById(R.id.order_address);
        orderNumber = findViewById(R.id.order_number);
        confirmButton =findViewById(R.id.confirm_order);
        selectedVendor = findViewById(R.id.tv_selected_vendor);
        orderVendor = findViewById(R.id.selected_vendor);
        pro_cash = findViewById(R.id.pro_cash);
        TotalWeight = findViewById(R.id.weight);
        total_weight_price = findViewById(R.id.total_weight_price);
        Item_weights = findViewById(R.id.Item_weights);

        C = findViewById(R.id.my_coupon);
        tvC = findViewById(R.id.tv_my_coupon);

        apply = findViewById(R.id.btn_coupon_apply);
        coupon = findViewById(R.id.et_coupon_code);

        lNum = findViewById(R.id.lNum);
        lID = findViewById(R.id.lID);
        lPay = findViewById(R.id.lpay);

        pNum = findViewById(R.id.paymentNumber);
        pID = findViewById(R.id.paymentID);
        mNum = findViewById(R.id.merchantNum);
        edit = findViewById(R.id.edit_merchant_number);

        cod = findViewById(R.id.cod);
        bkash = findViewById(R.id.bkash);
        rocket = findViewById(R.id.rocket);
        nogod = findViewById(R.id.nogod);

        stringBuilder = new StringBuilder();

        //check_again_Internet_connection(ProductSummary.this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProductsWeightCost");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    First = String.valueOf(snapshot.child("first").getValue());
                    Second = String.valueOf(snapshot.child("second").getValue());
                    Third = String.valueOf(snapshot.child("third").getValue());
                    Four = String.valueOf(snapshot.child("four").getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

        }

        uRef = FirebaseDatabase.getInstance().getReference("UsersData").child(current_user_id);
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        ProRef = FirebaseDatabase.getInstance().getReference("Products");
        PayNumRef = FirebaseDatabase.getInstance().getReference("PaymentNumber");
        CouponRef = FirebaseDatabase.getInstance().getReference("CouponCode");
        ExRef = FirebaseDatabase.getInstance().getReference("Products").child("-MYzhOOsvNT_IxuqrQky");


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coupon.getWindowToken(), 0);

                if(coupon.getText().toString().trim().isEmpty()){
                    Toast.makeText(ProductSummary.this, "Missing Coupon", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(ProductSummary.this);
                    progressDialog.show();
                    progressDialog.setMessage("Checking...");

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();

                            couponCode = coupon.getText().toString().trim();

                            CouponRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.child(couponCode).exists()){

                                                if (!snapshot.child(couponCode).child("user").child(current_user_id).exists()){

                                                    value = snapshot.child(couponCode).child("value").getValue().toString();
                                                    double c = Double.parseDouble(value);

                                                    cc = (sPrice*c)/100;

                                                    tPrice -= Math.round(cc);

                                                    totalPrice.setText("=  ৳ "+tPrice);

                                                    C.setText("  ৳ "+(int) Math.round(cc));

                                                    apply.setEnabled(false);

                                                    tvC.setVisibility(View.VISIBLE);
                                                    C.setVisibility(View.VISIBLE);

                                                    check = true;



                                                }else {
                                                    if (!check)
                                                    Toast.makeText(ProductSummary.this, "Coupon Already Used", Toast.LENGTH_SHORT).show();
                                                }

                                    }else {
                                        Toast.makeText(ProductSummary.this, "Invalid Coupon", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }, 1000);
                }


            }
        });

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    userType = String.valueOf(snapshot.child("userType").getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        itemList = new ArrayList<String>();
        priceList = new ArrayList<String>();
        qList = new ArrayList<String>();
        idList = new ArrayList<String>();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            itemList = b.getStringArrayList("item");
            priceList = b.getStringArrayList("price");
            qList = b.getStringArrayList("quantity");
            idList = b.getStringArrayList("product_id");
            ForLoopFunction();
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String address = String.valueOf(snapshot.child("userLocation").getValue());
                    String number = String.valueOf(snapshot.child("userPhone").getValue());
                    balance = String.valueOf(snapshot.child("userBalance").getValue());
                    String uB = String.valueOf(snapshot.child("usesBalance").getValue());
                    String type = String.valueOf(snapshot.child("userType").getValue());
                    current_user_dp = String.valueOf(snapshot.child("userImage").getValue());

                    NextFunction();

                    if(type.equals("Vendor")){
                        selectedVendor.setText("Selected Distributor :");
                    }

                    if(type.equals("Admin")){
                        edit.setVisibility(View.VISIBLE);
                    }else {
                        edit.setVisibility(View.GONE);
                    }

                    double temp = without_offer*.25;

                    tt = Math.round(Double.parseDouble(balance));
                    tUsesBalance = Math.round(Double.parseDouble(uB));

                    if (tt<temp){
                        myBalance.setText("৳ "+tt);
                        usesBalance = tt;
                        tPrice = sPrice - tt;
                    }else {
                        myBalance.setText("৳ "+temp);
                        extraBalance = tt-temp;
                        usesBalance = temp;
                        tPrice = sPrice - temp;
                    }

                    pro_cash.setText("৳ "+tCash);



                    totalPrice.setText("=  ৳ "+tPrice);


                    orderAddress.setText(address);
                    orderNumber.setText(number);
                    orderVendor.setText("None");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lNum.setVisibility(View.GONE);
                lID.setVisibility(View.GONE);
                lPay.setVisibility(View.GONE);

            }
        });


        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lNum.setVisibility(View.VISIBLE);
                lID.setVisibility(View.VISIBLE);
                lPay.setVisibility(View.VISIBLE);

                if (bkash.isChecked()){
                    method = "bKash";
                }
                else if (rocket.isChecked()){
                    method = "rocket";
                }
                else if (nogod.isChecked()){
                    method = "nogod";
                }

                checkPayment(method);

            }
        });


        rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lNum.setVisibility(View.VISIBLE);
                lID.setVisibility(View.VISIBLE);
                lPay.setVisibility(View.VISIBLE);

                if (bkash.isChecked()){
                    method = "bKash";
                }
                else if (rocket.isChecked()){
                    method = "rocket";
                }
                else if (nogod.isChecked()){
                    method = "nogod";
                }

                checkPayment(method);

            }
        });

        nogod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lNum.setVisibility(View.VISIBLE);
                lID.setVisibility(View.VISIBLE);
                lPay.setVisibility(View.VISIBLE);

                if (bkash.isChecked()){
                    method = "bKash";
                }
                else if (rocket.isChecked()){
                    method = "rocket";
                }
                else if (nogod.isChecked()){
                    method = "nogod";
                }

                checkPayment(method);


            }
        });




        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(ProductSummary.this);
                input.setText("    "+number);


                AlertDialog dialog = new AlertDialog.Builder(ProductSummary.this)
                        .setMessage("Enter Merchant Number")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String num = input.getText().toString().trim();

                                if(num.isEmpty()){
                                    Toast.makeText(ProductSummary.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{

                                    PayNumRef.child(method).setValue(num);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String payNum ;
                String payID ;

                String orderItem = item.getText().toString();
                String orderQuantity = quantity.getText().toString();
                String orderItemPrice = price.getText().toString();
                String orderTotalPrice = totalPrice.getText().toString();
                String orderSubPrice = subPrice.getText().toString();
                String address = orderAddress.getText().toString();
                String number = orderNumber.getText().toString();
                String selectedVD = orderVendor.getText().toString();
                Weight_String = TotalWeight.getText().toString();
                String item_weight = Item_weights.getText().toString();

                payNum = pNum.getText().toString();
                payID = pID.getText().toString();

                if (address.isEmpty() || address.length()<4){
                    Toast.makeText(ProductSummary.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog = new ProgressDialog(ProductSummary.this);
                    progressDialog.show();
                    progressDialog.setMessage("Making Order...");

                    confirmButton.setEnabled(false);

                    if (cod.isChecked()){
                        pMethod = "Cash On Delivery";
                    }else if (bkash.isChecked()){
                        pMethod = "bKash";
                    }
                    else if (rocket.isChecked()){
                        pMethod = "Rocket";
                    }
                    else if (nogod.isChecked()){
                        pMethod = "Nogod";
                    }else {
                        pMethod = " ";
                    }


                    if (payNum.isEmpty()){
                        payNum = " ";
                    }
                    if (payID.isEmpty()){
                        payID = " ";
                    }

                    Calendar calFordDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    String saveCurrentDate = currentDate.format(calFordDate.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    String saveCurrentTime = currentTime.format(calFordDate.getTime());

                    String myDate = saveCurrentDate+" "+saveCurrentTime;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a");
                    Date date = null;
                    try {
                        date = sdf.parse(myDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long millis = date.getTime();

                    HashMap postsMap = new HashMap();

                    postsMap.put("orderDate", saveCurrentDate);
                    postsMap.put("orderTime", saveCurrentTime);
                    postsMap.put("orderItem", orderItem);
                    postsMap.put("orderQuantity", orderQuantity);
                    postsMap.put("orderItemPrice", orderItemPrice);
                    postsMap.put("orderTotalPrice", orderTotalPrice);
                    postsMap.put("orderSubPrice", orderSubPrice);
                    postsMap.put("orderAddress", address);
                    postsMap.put("orderNumber", number);
                    postsMap.put("selectedVD", selectedVD);
                    postsMap.put("customerUID", current_user_id);
                    postsMap.put("orderStatus", "Pending");
                    postsMap.put("customerBalance", ""+usesBalance);
                    postsMap.put("customerCashBack", ""+tCash);
                    postsMap.put("productID", idList);
                    postsMap.put("paymentMethod", pMethod);
                    postsMap.put("paymentStatus", "Pending");
                    postsMap.put("paymentNumber", payNum);
                    postsMap.put("paymentID", payID);
                    postsMap.put("couponCode", couponCode);
                    postsMap.put("couponDis", ""+cc);
                    postsMap.put("TotalWeight", Total_Weight_String);//**********************************************
                    postsMap.put("ItemWeights", item_weight);


                    final String orderID = String.valueOf(millis/1000);

                    orderRef.child(orderID).updateChildren(postsMap);

                    uRef.child("userBalance").setValue(""+(extraBalance+tCash));
                    uRef.child("usesBalance").setValue(""+(usesBalance+tUsesBalance));
                    uRef.child("userTotalBalance").setValue(""+(tUsesBalance+usesBalance+extraBalance+tCash));
                    uRef.child("userLocation").setValue(address);

                    if(!couponCode.equals(" ")){
                        CouponRef.child(couponCode).child("user").child(current_user_id).setValue("true");
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            FirebaseMessaging.getInstance().subscribeToTopic("ORDER");

                            prepareNotification(
                                    ""+orderID,
                                    "You have a new order",
                                    ""+"Let's have a look",
                                    "OrderNotification",
                                    "ORDER"
                            );

                            Toast.makeText(ProductSummary.this, "Order has been confirmed.\n\tHappy Shopping", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductSummary.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 1500);
                }


            }
        });


    }



    private void NextFunction() {
        final StringBuilder i = new StringBuilder();
        for(String s : itemList){

            if (s.length() > 16) {
                i.append(s.substring(0, 15) + "..." + "\n");
            } else {
                i.append(s + "\n");
            }

        }

        item.setText(i.toString());

        StringBuilder p = new StringBuilder();

        for (int j = 0; j < priceList.size(); j++){

            num = (int) Math.round(Double.parseDouble(qList.get(j)));

            t = num* Double.parseDouble(priceList.get(j));

            p.append("৳ "+String.valueOf(t)+"\n");


            sPrice+= num*Double.parseDouble(priceList.get(j));
        }

        price.setText(p.toString());

        sPrice = sPrice + Double.parseDouble(Weight_String);
        subPrice.setText("৳ "+ sPrice);


        StringBuilder q = new StringBuilder();
        for(String s : qList){
            q.append("x"+s+"\n");
        }

        quantity.setText(q.toString());
    }


    private void ForLoopFunction() {

        for(int i = 0; i<idList.size(); i++){

            final ArrayList<String> finalPriceList = priceList;
            final int finalI = i;

            final ArrayList<String> finalQList = qList;
            final int finalI1 = i;
            ProRef.child(idList.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){

                        String dis = String.valueOf(snapshot.child("productDiscount").getValue());
                        String cash = String.valueOf(snapshot.child("productCashBack").getValue());
                        String v_dis = String.valueOf(snapshot.child("vendorDiscount").getValue());
                        String v_cash = String.valueOf(snapshot.child("vendorCashBack").getValue());
                        String Weight = String.valueOf(snapshot.child("weight").getValue());

                        stringBuilder.append(Weight + "\n");
                        num2 = (int) Math.round(Integer.parseInt(qList.get(finalI)));
                        Weight_int = (Integer.parseInt(Weight) * (int) num2) + Weight_int;

                        kg = Double.parseDouble(String.valueOf(Weight_int)) / 1000;
                        TotalWeight.setText(String.valueOf(decimalFormat.format(kg)));
                        Item_weights.setText(stringBuilder.toString());

                        if (kg <= 5){
                            total_weight_price.setText("  ৳ "+ First);
                            Weight_String = First;
                            Total_Weight_String = "Total weight " + kg + " kg, So delivery charge  ৳ " + First;
                        }else if (kg <= 10){
                            total_weight_price.setText("  ৳ "+ Second);
                            Weight_String = Second;
                            Total_Weight_String = "Total weight " + kg + " kg, So delivery charge  ৳ " + Second;
                        }else if (kg <= 25){
                            total_weight_price.setText("  ৳ "+ Third);
                            Weight_String = Third;
                            Total_Weight_String = "Total weight " + kg + " kg, So delivery charge  ৳ " + Third;
                        }else {
                            total_weight_price.setText("  ৳ "+ Four);
                            Weight_String = Four;
                            Total_Weight_String = "Total weight " + kg + " kg, So delivery charge  ৳ " + Four;
                        }

                        String price = String.valueOf(finalPriceList.get(finalI));
                        double w_p = Double.parseDouble(finalPriceList.get(finalI));

                        if (userType.equals("Vendor")){

                            if (!v_dis.equals(" ") || !v_cash.equals(" ")){

                                isOffer = true;

                                if (!v_cash.equals(" ")){

                                    double temp = Double.parseDouble(price);
                                    double temp2 = Double.parseDouble(v_cash);
                                    double temp3 = (temp2/100)*temp;

                                    tCash+= (int) Math.round(temp3)*(int) Math.round(Double.parseDouble(finalQList.get(finalI1)));

                                }

                            }else{

                                //without offer product price
                                without_offer += w_p*(int) Math.round(Double.parseDouble(finalQList.get(finalI1)));

                            }

                        }else {
                            if (!dis.equals(" ") || !cash.equals(" ")){

                                isOffer = true;

                                if (!cash.equals(" ")){

                                    double temp = Double.parseDouble(price);
                                    double temp2 = Double.parseDouble(cash);
                                    double temp3 = (temp2/100)*temp;

                                    tCash+= (int) Math.round(temp3)*(int) Math.round(Double.parseDouble(finalQList.get(finalI1)));

                                }

                            }else{

                                //without offer product price
                                without_offer += w_p*(int) Math.round(Double.parseDouble(finalQList.get(finalI1)));

                            }
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    private void checkPayment(final String m) {

        PayNumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    number = String.valueOf(snapshot.child(m).getValue());
                    mNum.setText("Merchant Number : "+number);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean isNetworkAvaliable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void  prepareNotification(String pId,String title,String ndescription,String notificationType,String notificationTopic){

        String NOTIFICATION_TOPIC = "/topics/"+notificationTopic;
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_MESSAGE = ndescription;
        String NOTIFICATION_TYPE = notificationType;

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("sender",current_user_id);
            notificationBodyJo.put("dp",current_user_dp);
            notificationBodyJo.put("pId",pId);
            notificationBodyJo.put("pTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription",NOTIFICATION_MESSAGE);

            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendPostNotification(notificationJo);

    }

    private void sendPostNotification(JSONObject notificationJo) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("FCM_RESPONSE","onResponse: "+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ProductSummary.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> headers = new HashMap<>();

                headers.put("content-Type","application/json");
                headers.put("Authorization","key = AAAAdgRerbo:APA91bGEE9Ay9wCJ6nkXvCUXfsOsdt7kz8nit0ikEiUJJyD63saaBN0EwmoWP1yqdjwF-hjNQE_CRvJA61rbkSG-ClCWDQY15W_jQvfXjXIXam9gtNOtMV81U7adnTXmEkBi6EtleuzV");

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void Show_dialog_box() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog_box_check_internet,null);
        Button btn_okay = (Button)mView.findViewById(R.id.done);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                check_again_Internet_connection(ProductSummary.this);
            }
        });
        alertDialog.show();
    }

    private void check_again_Internet_connection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            isConnectWithInternet = true;
        } else {

            if (!isFinishing()) {
                Show_dialog_box();
            }

        }
    }
}