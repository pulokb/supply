package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {


    private CardView approval,uploadProduct,showAll,manageProduct,vendorList,distributorList,orderReq,transferOrder,uploadSlide,addCoupon,cRefValue,vRefValue,dRefValue, Weight,pLimit;
    private ImageView back;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String c_value,d_value,v_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_admin);

        uploadProduct = findViewById(R.id.cv_upload_product);
        showAll = findViewById(R.id.cv_show_all_product);
        manageProduct = findViewById(R.id.cv_admin_manage_product);
        vendorList = findViewById(R.id.cv_show_all_vendor);
        distributorList = findViewById(R.id.cv_show_all_distributor);
        orderReq = findViewById(R.id.cv_order_request);
        transferOrder = findViewById(R.id.cv_transfer_order);
        uploadSlide = findViewById(R.id.cv_upload_slide);
        addCoupon = findViewById(R.id.cv_add_coupon);
        cRefValue = findViewById(R.id.cv_c_ref_value);
        vRefValue = findViewById(R.id.cv_v_ref_value);
        dRefValue = findViewById(R.id.cv_d_ref_value);
        Weight = findViewById(R.id.Weight);
        pLimit = findViewById(R.id.cv_add_limitation);

        approval = findViewById(R.id.cv_approval);

        back = findViewById(R.id.back);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ReferCode");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    c_value = String.valueOf(snapshot.child("customerValue").getValue());
                    d_value = String.valueOf(snapshot.child("distributorValue").getValue());
                    v_value = String.valueOf(snapshot.child("vendorValue").getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        uploadSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, SliderActivity.class);
                startActivity(n);

            }
        });

        uploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, AddProduct.class);
                startActivity(n);

            }
        });


        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, ShowAllProduct.class);
                startActivity(n);

            }
        });

        manageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, ManageVendorProduct.class);
                n.putExtra("type", "admin");
                startActivity(n);

            }
        });

        vendorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, VendorList.class);
                n.putExtra("type","Vendor");
                startActivity(n);

            }
        });

        distributorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, VendorList.class);
                n.putExtra("type","Distributor");
                startActivity(n);

            }
        });

        orderReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, OrderActivity.class);
                startActivity(n);

            }
        });

        transferOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, TransferOrder.class);
                startActivity(n);

            }
        });

        addCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, AddCoupon.class);
                startActivity(n);

            }
        });

        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, Approval.class);
                startActivity(n);

            }
        });

        cRefValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(AdminActivity.this);
                input.setText("    "+c_value);


                AlertDialog dialog = new AlertDialog.Builder(AdminActivity.this)
                        .setMessage("Enter Refer Amount")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AdminActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("customerValue").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        vRefValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(AdminActivity.this);
                input.setText("    "+v_value);


                AlertDialog dialog = new AlertDialog.Builder(AdminActivity.this)
                        .setMessage("Enter Refer Amount")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AdminActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("vendorValue").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });


        dRefValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(AdminActivity.this);
                input.setText("    "+d_value);


                AlertDialog dialog = new AlertDialog.Builder(AdminActivity.this)
                        .setMessage("Enter Refer Amount")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AdminActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("distributorValue").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        Weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminActivity.this, AddWeight.class);
                startActivity(n);

            }
        });

        pLimit.setOnClickListener(view -> {

            Intent n = new Intent(AdminActivity.this, ProductLimitation.class);
            startActivity(n);

        });


    }
}