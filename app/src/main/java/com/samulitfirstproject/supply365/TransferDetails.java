package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransferDetails extends AppCompatActivity {

    private TextView item,price,quantity,orderID,time,date,status,address,contact,transfer,totalPrice;
    private ImageView back;
    private String current_user_id="", key,key2;
    private Button nearByVendor;

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference orderRef,orderRef2,vendorRef;
    private ArrayList<String> idList;

    private String customerAddress, orderStatus;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_transfer_details);

        key = getIntent().getStringExtra("order_id");
        key2 = getIntent().getStringExtra("cOrder_id");
        orderStatus = getIntent().getStringExtra("order_status");

        item = findViewById(R.id.item);
        price = findViewById(R.id.item_price);
        back  = findViewById(R.id.back);
        quantity = findViewById(R.id.quantity);
        totalPrice = findViewById(R.id.total_price);


        orderID = findViewById(R.id.orderID);
        time = findViewById(R.id.orderTime);
        date = findViewById(R.id.orderDate);
        address = findViewById(R.id.orderAddress);
        contact = findViewById(R.id.orderNumber);
        status = findViewById(R.id.orderStatus);

        transfer = findViewById(R.id.transferTo);

        nearByVendor =findViewById(R.id.search_nearby_vendor);
        progressDialog = new ProgressDialog(this);

        mfirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            if(current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - string.xml
                nearByVendor.setVisibility(View.GONE);
            }

        } else {

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        orderRef = FirebaseDatabase.getInstance().getReference("FinalOrder").child(key);
        orderRef2 = FirebaseDatabase.getInstance().getReference("Order").child(key2);
        vendorRef = FirebaseDatabase.getInstance().getReference().child("UsersData");


        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String orderItem = String.valueOf(snapshot.child("orderItem").getValue());
                    String orderItemPrice = String.valueOf(snapshot.child("orderItemPrice").getValue());
                    customerAddress = String.valueOf(snapshot.child("orderAddress").getValue());
                    String orderTime = String.valueOf(snapshot.child("orderTime").getValue());
                    String orderDate = String.valueOf(snapshot.child("orderDate").getValue());
                    String orderQuantity = String.valueOf(snapshot.child("orderQuantity").getValue());
                    String customerNumber = String.valueOf(snapshot.child("orderNumber").getValue());

                    String tranferTo = String.valueOf(snapshot.child("vendorUID").getValue());


                    vendorRef.child(tranferTo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                String name = snapshot.child("userName").getValue().toString();
                                transfer.setText("Transfer To : "+name);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    

                    orderID.setText("Order ID: "+key);
                    time.setText("Order Time: "+orderTime);
                    date.setText("Order Date: "+orderDate);
                    address.setText("Billing Address: "+customerAddress);
                    contact.setText("Contact Number: "+customerNumber);
                    status.setText("Order Status: "+orderStatus);

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

                    price.setText(orderItemPrice);
                    quantity.setText(orderQuantity);

                    String[] arr2 = orderItemPrice.split("\n");

                    final StringBuilder j = new StringBuilder();

                    double tp = 0;
                    for(String s : arr2){

                        if (s.length()>0){
                            tp+= Double.parseDouble(s.substring(2));
                        }


                    }

                    totalPrice.setText("= ৳ "+tp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        nearByVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearByVendor.setBackgroundResource(R.drawable.button2);
                progressDialog.setMessage("Delivering...");
                progressDialog.show();


                Handler handle = new Handler(Looper.getMainLooper());
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        orderRef2.child("orderStatus").setValue("Delevered");

                        nearByVendor.setBackgroundResource(R.drawable.button);
                        Intent intent = new Intent(TransferDetails.this, VendorActivity.class);
                        startActivity(intent);

                        progressDialog.dismiss();
                    }
                }, 1500);
            }
        });
    }


}