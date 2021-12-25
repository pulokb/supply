package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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
import com.samulitfirstproject.supply365.geocode.GeoCodingLocation2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDetails extends AppCompatActivity {

    private TextView  Total_delivery_charge,item,price,subPrice,totalPrice,quantity,myBalance,orderID,time,date,status,address,contact,vd,pMethod,pStatus,pNum,pID,markAsPaid;
    private  TextView v1,v2,v3;
    private View v4;
    private ImageView back;
    private String current_user_id="", key, user_type, Total_Weight_Price, Total_Weight_Price2,from;
    private Button nearByVendor,delivered,cancel;

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference orderRef, databaseReference;
    private ArrayList<String> idList;

    private String customerAddress, Name,OderUID;

    private ProgressDialog progressDialog;
    private LinearLayout ldc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_order_details);

        key = getIntent().getStringExtra("order_id");
        user_type = getIntent().getStringExtra("user_type");
        from = getIntent().getStringExtra("from");


        Total_delivery_charge = findViewById(R.id.t_delivery_charge);
        item = findViewById(R.id.item);
        price = findViewById(R.id.item_price);
        back  = findViewById(R.id.back);
        totalPrice = findViewById(R.id.total_price);
        quantity = findViewById(R.id.quantity);
        myBalance = findViewById(R.id.my_balance);
        subPrice = findViewById(R.id.sub_price);

        orderID = findViewById(R.id.orderID);
        time = findViewById(R.id.orderTime);
        date = findViewById(R.id.orderDate);
        address = findViewById(R.id.orderAddress);
        contact = findViewById(R.id.orderNumber);
        status = findViewById(R.id.orderStatus);
        vd = findViewById(R.id.selectedVD);

        ldc = findViewById(R.id.l_d_c);


        pMethod = findViewById(R.id.paymentMethod);
        pStatus = findViewById(R.id.paymentStatus);
        pNum = findViewById(R.id.paymentNumber);
        pID = findViewById(R.id.paymentID);

        markAsPaid = findViewById(R.id.markAsPaid);
        delivered = findViewById(R.id.markAsDelivered);
        cancel = findViewById(R.id.cancelDelivered);

        v1 = findViewById(R.id.s_price);
        v2 = findViewById(R.id.tv_my_balance);
        v3 = findViewById(R.id.t_price);
        v4 = findViewById(R.id.view6);

        nearByVendor =findViewById(R.id.search_nearby_vendor);
        progressDialog = new ProgressDialog(this);

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            if(current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - string.xml
                nearByVendor.setVisibility(View.VISIBLE);
                markAsPaid.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);

            }

        } else {

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (from.equals("my_order")){
            ldc.setVisibility(View.GONE);
        }


        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(key);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){


                    String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());

                    status.setText("Order Status: "+orderStatus);

                    if (orderStatus.equals("Delivered")){
                        delivered.setText("Product Delivered");
                        delivered.setEnabled(false);
                        cancel.setVisibility(View.GONE);
                    }else if (orderStatus.equals("Canceled")){
                        cancel.setText("Order Canceled");
                        cancel.setEnabled(false);
                        delivered.setVisibility(View.GONE);
                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (user_type.equals("vendor")){
            orderRef = FirebaseDatabase.getInstance().getReference("FinalOrder").child(key);

            getProductList();

            orderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        OderUID = String.valueOf(snapshot.child("customerUID").getValue());
                        String orderItem = String.valueOf(snapshot.child("orderItem").getValue());
                        String orderItemPrice = String.valueOf(snapshot.child("orderItemPrice").getValue());
                        customerAddress = String.valueOf(snapshot.child("orderAddress").getValue());
                        String orderTime = String.valueOf(snapshot.child("orderTime").getValue());
                        String orderDate = String.valueOf(snapshot.child("orderDate").getValue());
                        String orderQuantity = String.valueOf(snapshot.child("orderQuantity").getValue());
                        String customerNumber = String.valueOf(snapshot.child("orderNumber").getValue());
                        String TotalWeight_Price = String.valueOf(snapshot.child("TotalWeight").getValue());
                        //String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());


                        orderID.setText("Order ID: "+key);
                        time.setText("Order Time: "+orderTime);
                        date.setText("Order Date: "+orderDate);
                        //status.setText("Order Status: "+orderStatus);
                        address.setText("Billing Address: "+customerAddress);
                        contact.setText("Contact Number: "+customerNumber);
                        status.setVisibility(View.VISIBLE);
                        vd.setVisibility(View.GONE);

                        pMethod.setVisibility(View.GONE);
                        pStatus.setVisibility(View.GONE);

                        markAsPaid.setVisibility(View.GONE);

                        pNum.setVisibility(View.GONE);
                        pID.setVisibility(View.GONE);

                        String[] arr = orderItem.split("\n");

                        final StringBuilder i = new StringBuilder();
                        for(String s : arr){

                            if (s.length() > 30) {
                                i.append(s.substring(0, 28) + "..." + "\n");
                            } else {
                                i.append(s + "\n");
                            }


                        }

                        item.setText(i.toString());

                        subPrice.setVisibility(View.GONE);
                        price.setText(orderItemPrice);
                        totalPrice.setVisibility(View.VISIBLE);

                        String[] arr2 = orderItemPrice.split("\n");

                        final StringBuilder j = new StringBuilder();

                        double tp = 0;
                        for(String s : arr2){

                            if (s.length()>0){
                                tp+= Double.parseDouble(s.substring(2));
                            }

                        }

                        if (!TotalWeight_Price.equals("0")){
                            Total_delivery_charge.setText(TotalWeight_Price);
                            Total_Weight_Price = TotalWeight_Price.substring(34, TotalWeight_Price.length());
                            Total_Weight_Price2 = Total_Weight_Price.replaceAll("[^0-9]", "");
                            tp = tp + Double.parseDouble(Total_Weight_Price2);
                        }


                        totalPrice.setText("= ৳ "+tp);
                        quantity.setText(orderQuantity);
                        myBalance.setVisibility(View.GONE);

                        v1.setVisibility(View.GONE);
                        v2.setVisibility(View.GONE);
                        v3.setVisibility(View.GONE);
                        v4.setVisibility(View.GONE);

                        ldc.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if (user_type.equals("distributor")){

            orderRef = FirebaseDatabase.getInstance().getReference("FinalOrder").child(key);

            getProductList();

            orderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        OderUID = String.valueOf(snapshot.child("customerUID").getValue());
                        String orderItem = String.valueOf(snapshot.child("orderItem").getValue());
                        String orderItemPrice = String.valueOf(snapshot.child("orderItemPrice").getValue());
                        customerAddress = String.valueOf(snapshot.child("orderAddress").getValue());
                        String orderTime = String.valueOf(snapshot.child("orderTime").getValue());
                        String orderDate = String.valueOf(snapshot.child("orderDate").getValue());
                        String orderQuantity = String.valueOf(snapshot.child("orderQuantity").getValue());
                        String customerNumber = String.valueOf(snapshot.child("orderNumber").getValue());
                        String TotalWeight_Price = String.valueOf(snapshot.child("TotalWeight").getValue());
                        //String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());


                        orderID.setText("Order ID: "+key);
                        time.setText("Order Time: "+orderTime);
                        date.setText("Order Date: "+orderDate);
                        //status.setText("Order Status: "+orderStatus);
                        address.setText("Billing Address: "+customerAddress);
                        contact.setText("Contact Number: "+customerNumber);
                        status.setVisibility(View.VISIBLE);
                        vd.setVisibility(View.GONE);

                        pMethod.setVisibility(View.GONE);
                        pStatus.setVisibility(View.GONE);

                        markAsPaid.setVisibility(View.GONE);

                        pNum.setVisibility(View.GONE);
                        pID.setVisibility(View.GONE);


                        String[] arr = orderItem.split("\n");

                        final StringBuilder i = new StringBuilder();
                        for(String s : arr){

                            if (s.length() > 16) {
                                i.append(s.substring(0, 15) + "..." + "\n");
                            } else {
                                i.append(s + "\n");
                            }


                        }

                        item.setText(i.toString());

                        subPrice.setVisibility(View.GONE);
                        price.setText(orderItemPrice);
                        totalPrice.setVisibility(View.VISIBLE);

                        String[] arr2 = orderItemPrice.split("\n");

                        final StringBuilder j = new StringBuilder();

                        double tp = 0;
                        for(String s : arr2){

                            if (s.length()>0){
                                tp+= Double.parseDouble(s.substring(2));
                            }

                        }

                        if (!TotalWeight_Price.equals("0")){
                            Total_delivery_charge.setText(TotalWeight_Price);
                            Total_Weight_Price = TotalWeight_Price.substring(34, TotalWeight_Price.length());
                            Total_Weight_Price2 = Total_Weight_Price.replaceAll("[^0-9]", "");
                            tp = tp + Double.parseDouble(Total_Weight_Price2);
                        }

                        totalPrice.setText("= ৳ "+tp);

                        quantity.setText(orderQuantity);
                        myBalance.setVisibility(View.GONE);

                        v1.setVisibility(View.GONE);
                        v2.setVisibility(View.GONE);
                        v3.setVisibility(View.GONE);
                        v4.setVisibility(View.GONE);

                        ldc.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {
            orderRef = FirebaseDatabase.getInstance().getReference("Order").child(key);

            getProductList();

            orderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        OderUID = String.valueOf(snapshot.child("customerUID").getValue());
                        String orderItem = String.valueOf(snapshot.child("orderItem").getValue());
                        String orderItemPrice = String.valueOf(snapshot.child("orderItemPrice").getValue());
                        String balance = String.valueOf(snapshot.child("customerBalance").getValue());
                        customerAddress = String.valueOf(snapshot.child("orderAddress").getValue());
                        String orderTime = String.valueOf(snapshot.child("orderTime").getValue());
                        String orderDate = String.valueOf(snapshot.child("orderDate").getValue());
                        String orderQuantity = String.valueOf(snapshot.child("orderQuantity").getValue());
                        String customerNumber = String.valueOf(snapshot.child("orderNumber").getValue());
                        String tPrice = String.valueOf(snapshot.child("orderTotalPrice").getValue());
                        String sPrice = String.valueOf(snapshot.child("orderSubPrice").getValue());
                        String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());
                        String select = String.valueOf(snapshot.child("selectedVD").getValue());
                        String method = String.valueOf(snapshot.child("paymentMethod").getValue());
                        String payStatus = String.valueOf(snapshot.child("paymentStatus").getValue());
                        String num = String.valueOf(snapshot.child("paymentNumber").getValue());
                        String id = String.valueOf(snapshot.child("paymentID").getValue());
                        String TotalWeight_Price = String.valueOf(snapshot.child("TotalWeight").getValue());


                        if (!TotalWeight_Price.equals("0")){
                            Total_delivery_charge.setText(TotalWeight_Price);
                        }

                        orderID.setText("Order ID: "+key);
                        time.setText("Order Time: "+orderTime);
                        date.setText("Order Date: "+orderDate);
                        address.setText("Billing Address: "+customerAddress);
                        contact.setText("Contact Number: "+customerNumber);
                        status.setText("Order Status: "+orderStatus);
                        vd.setText("Selected V/D: "+select);

                        pMethod.setText("Payment Method: "+method);
                        pStatus.setText("Payment Status: "+payStatus);

                        if (payStatus.equals("Paid")){
                            markAsPaid.setVisibility(View.GONE);
                        }

                        markAsPaid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderRef.child("paymentStatus").setValue("Paid");
                            }
                        });

                        if (!num.equals(" ") || !id.equals(" ")){
                            pNum.setText("Payment Number: "+num);
                            pID.setText("Payment TrxID: "+id);
                        }else {
                            pNum.setText(" ");
                            pID.setText(" ");
                        }


                        String[] arr = orderItem.split("\n");

                        final StringBuilder i = new StringBuilder();
                        for(String s : arr){

                            if (s.length() > 30) {
                                i.append(s.substring(0, 28) + "..." + "\n");
                            } else {
                                i.append(s + "\n");
                            }


                        }

                        item.setText(i.toString());

                        subPrice.setText(sPrice);
                        price.setText(orderItemPrice);
                        totalPrice.setText(tPrice);
                        quantity.setText(orderQuantity);
                        myBalance.setText("৳ "+balance);

                        // Some Change *************************************************************************
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("UsersData").child(OderUID);
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String WhichType = snapshot.child("userType").getValue(String.class);

                                if (Objects.equals(WhichType, "Vendor")){

                                    nearByVendor.setText("Search Nearby Distributor");

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        nearByVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nearby Vendor Code
                nearByVendor.setBackgroundResource(R.drawable.button2);
                progressDialog.setMessage("Running");
                progressDialog.show();

                GeoCodingLocation2.getAddressFromLocation2(customerAddress, getApplicationContext(), new GeoCoderHandler(), null, null, null);

                Handler handle = new Handler(Looper.getMainLooper());
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        nearByVendor.setBackgroundResource(R.drawable.button);
                        Intent intent = new Intent(OrderDetails.this, MapsActivity.class);
                        intent.putExtra("OderID", key);
                        intent.putExtra("OderUID", OderUID);
                        startActivity(intent);
                    }
                }, 1000);
            }
        });

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderRef = FirebaseDatabase.getInstance().getReference("Order");

                Map<String, Object> updates = new HashMap<String,Object>();

                updates.put("orderStatus", "Delivered");

                orderRef.child(key).updateChildren(updates);

                delivered.setText("Product Delivered");
                delivered.setEnabled(false);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderRef = FirebaseDatabase.getInstance().getReference("Order");

                Map<String, Object> updates = new HashMap<String,Object>();

                updates.put("orderStatus", "Canceled");

                orderRef.child(key).updateChildren(updates);

                cancel.setText("Order Canceled");
                cancel.setEnabled(false);

            }
        });

    }

    private void getProductList() {

        orderRef.child("productID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    idList = new ArrayList<String>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        idList.add(childSnapshot.getValue().toString());
                    }

                    StringBuilder i = new StringBuilder();
                    for(String s : idList){
                        i.append(s+"\n");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


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
}