package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userName,userEmail,userPass,userNumber,refer_code;
    private TextView Login;
    private Button signUp;
    private ImageView vPass;
    private RadioButton Customer, Vendor, Distributor;
    private ProgressDialog loadingBar;
    private boolean isShowPassword = false;
    private DatabaseReference userRef,rRef;
    private String userType,user_id;
    private String balance = "0";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        //Initialization
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.et_name);
        userEmail = findViewById(R.id.et_remail);
        userPass = findViewById(R.id.et_pass);
        userNumber = findViewById(R.id.et_phone);
        Login = findViewById(R.id.tv_login);
        refer_code = findViewById(R.id.et_refer_code);

        vPass = findViewById(R.id.imgRegPass);

        signUp = findViewById(R.id.registration);

        Customer = findViewById(R.id.radioButton);
        Vendor = findViewById(R.id.radioButton2);
        Distributor = findViewById(R.id.radioButton3);

        loadingBar = new ProgressDialog(RegistrationActivity.this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(RegistrationActivity.this,LoginActivity.class); startActivity(n);

            }
        });

        vPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPassword) {
                    userPass.setTransformationMethod(new PasswordTransformationMethod());
                    vPass.setImageDrawable(getResources().getDrawable(R.drawable.ic_pass_not_visible));
                    isShowPassword = false;
                }else{
                    userPass.setTransformationMethod(null);
                    vPass.setImageDrawable(getResources().getDrawable(R.drawable.ic_pass_visibility));
                    isShowPassword = true;
                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check internet connection
                if (!isConnected(RegistrationActivity.this)){
                    Toast.makeText(RegistrationActivity.this, "Check Your Connection", Toast.LENGTH_SHORT).show();
                }else {
                    CreateAccountWithRegistration();
                }

            }

            private boolean isConnected(RegistrationActivity registrationActivity) {
                ConnectivityManager connectivityManager = (ConnectivityManager) registrationActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo WIFIinfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
                NetworkInfo Mobileinfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

                if ((WIFIinfo != null && WIFIinfo.isConnected()) || (Mobileinfo != null && Mobileinfo.isConnected())){
                    return true;
                }else {
                    return false;
                }
            }


        });
    }


    private void CreateAccountWithRegistration() {

        String name = userName.getText().toString().trim();
        String email =  userEmail.getText().toString().trim();
        String number = userNumber.getText().toString().trim();
        String password = userPass.getText().toString().trim();

        userType = "";

        if (Customer.isChecked()){
            userType = "Customer";
        }else if (Vendor.isChecked()){
            userType = "Vendor";
        }else if (Distributor.isChecked()){
            userType = "Distributor";
        }

        // Give a message if user can't give one field empty
        if (name.isEmpty()){
            userName.setError("Enter a Name");
            userName.requestFocus();
            return;
        }
        else if (email.isEmpty()){
            userEmail.setError("Enter an Email");
            userEmail.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Enter a valid Email Address");
            userEmail.requestFocus();
            return;
        }
        else if (password.isEmpty()){
            userPass.setError("Enter a Password");
            userPass.requestFocus();
            return;
        }
        else if (password.length() < 6){
            userPass.setError("Minimum length of a Password should be 6");
            userPass.requestFocus();
            return;
        }
        else if (number.isEmpty()){
            userNumber.setError("Enter a Phone Number");
            userNumber.requestFocus();
            return;
        }
        else if (number.length() != 11){
            userNumber.setError("Minimum length of a Number should be 11");
            userNumber.requestFocus();
            return;
        }
        else if (!Patterns.PHONE.matcher(number).matches()){
            userNumber.setError("Enter a valid Phone Number");
            userNumber.requestFocus();
            return;
        }
        else if (userType.isEmpty()){
            Toast.makeText(getApplicationContext(), "Choose which type of user you are ?", Toast.LENGTH_LONG).show();
            return;
        }else {

            // For show loading when click the registration Button
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait, we are creating your account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            // End


            // Create Account
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        sendVerificationEmail();
                        sendUserData();

                    }
                }
            });

        }
        // End

    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    sendUserData();

                    final Dialog dialog = new Dialog(RegistrationActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.email_sent_dialog);

                    String email = userEmail.getText().toString();

                    TextView text = (TextView) dialog.findViewById(R.id.text_dialog);

                    text.setText("Verification Email sent successfully! Please check your Email\n"+email);

                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            finish();

                        }
                    });

                    dialog.show();




                } else {

                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());

                }
            }
        });
    }

    private void sendUserData() {

        user_id = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
        rRef = FirebaseDatabase.getInstance().getReference().child("ReferCode");

        final String name = userName.getText().toString();
        final String email = userEmail.getText().toString();
        final String phone = userNumber.getText().toString();
        final String refer = refer_code.getText().toString();


            rRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (userType.equals("Customer")) {
                        balance = String.valueOf(snapshot.child("customerValue").getValue());
                    }else if (userType.equals("Distributor")) {
                        balance = String.valueOf(snapshot.child("distributorValue").getValue());
                    }else if (userType.equals("Vendor")) {
                        balance = String.valueOf(snapshot.child("vendorValue").getValue());
                    }


                    if (!refer.isEmpty()) {

                        if (snapshot.child("user").child(refer).exists()) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //balance = String.valueOf(snapshot.child("value").getValue());
                                    final String uid = String.valueOf(snapshot.child("user").child(refer).child("value").getValue());

                                    userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.exists()){

                                                String value = String.valueOf(snapshot.child("userTotalBalance").getValue());
                                                String type = String.valueOf(snapshot.child("userType").getValue());

                                                if (type.equals(userType) && count==0){

                                                    count++;
                                                    double temp = Double.parseDouble(value);
                                                    double temp2 = Double.parseDouble(balance);
                                                    // double temp3 = (Double.parseDouble(balance));

                                                    //balance = ""+temp3*2;

                                                    Map bal = new HashMap();

                                                    bal.put("userTotalBalance", ""+(temp+temp2));

                                                    userRef.child(uid).updateChildren(bal);

                                                    SaveUserInfo(name,email,phone);

                                                }else {
                                                    SaveUserInfo(name,email,phone);
                                                }


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
                    else {
                        SaveUserInfo(name,email,phone);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    private void SaveUserInfo(String name, String email, String phone) {

        String saveCurrentDate, saveCurrentTime;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        Map reg = new HashMap();

        reg.put("userName", name);
        reg.put("userEmail", email);
        reg.put("userPhone",phone);
        reg.put("userType",userType);
        reg.put("userImage"," ");
        reg.put("userUID", user_id);
        reg.put("userLocation", " ");
        reg.put("userBalance", "0");
        reg.put("userTotalBalance", "0");
        reg.put("usesBalance", "0");
        reg.put("isTopVendor","false");
        reg.put("isBrandShop","false");
        reg.put("userMembershipTime",saveCurrentTime);
        reg.put("userMembershipDate",saveCurrentDate);
        reg.put("isApprove", "No");
        reg.put("vPanelAccess", "No");

        userRef.child(user_id).updateChildren(reg);
        rRef.child("user").child(user_id.substring(7,14)).child("value").setValue(user_id);

        loadingBar.dismiss();
        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();

    }

}