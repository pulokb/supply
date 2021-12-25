package com.samulitfirstproject.supply365;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddCoupon extends AppCompatActivity {

    private Button add;
    private ImageView back;
    private EditText Code, Value;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CouponRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_coupon);

        add = findViewById(R.id.btn_coupon);
        back = findViewById(R.id.back);
        Code= findViewById(R.id.et_coupon);
        Value= findViewById(R.id.et_value);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mfirebaseAuth = FirebaseAuth.getInstance();

        CouponRef = FirebaseDatabase.getInstance().getReference("CouponCode");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Code.getWindowToken(), 0);

                String code = Code.getText().toString().trim();
                String value = Value.getText().toString().trim();

                if (code.isEmpty()){
                    Toast.makeText(AddCoupon.this, "Missing Code", Toast.LENGTH_SHORT).show();
                }
                else if (value.isEmpty()){
                    Toast.makeText(AddCoupon.this, "Missing Value", Toast.LENGTH_SHORT).show();
                }else {

                    Map c = new HashMap();

                    c.put("value", value);

                    CouponRef.child(code).updateChildren(c);

                    Code.setText("");
                    Value.setText("");

                    Toast.makeText(AddCoupon.this, "Coupon Added Successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}