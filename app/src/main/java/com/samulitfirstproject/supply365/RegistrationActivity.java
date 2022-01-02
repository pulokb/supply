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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userName,userEmail,userPass,userNumber,refer_code;
    private TextView Login;
    private Button signUp;
    private ImageView vPass;
    private RadioButton Customer, Vendor, Distributor;
    private RelativeLayout relativeLayout;
    private ScrollView scrollView;
    private ProgressDialog loadingBar;
    private boolean isShowPassword = false;
    private DatabaseReference userRef,rRef;
    private String userType, user_id, sendCode, email, password, PhoneNumber;
    private String balance = "0";
    private int count = 0;

    private EditText text;
    private Button button;

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

        text = findViewById(R.id.text);
        button = findViewById(R.id.button2);

        scrollView = findViewById(R.id.scroll);
        relativeLayout = findViewById(R.id.rl2);

        loadingBar = new ProgressDialog(RegistrationActivity.this);

        button.setOnClickListener(v -> {
            verifyVerificationCode(text.getText().toString().trim());
        });

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

            email = userEmail.getText().toString().trim();
            password = userPass.getText().toString().trim();



            // Create Account
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        user_id = mAuth.getCurrentUser().getUid();
                        loadingBar.dismiss();
                        relativeLayout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        PhoneNumber = "+88" + userNumber.getText().toString();
                        sendPhoneVerificationCode(PhoneNumber);
                    }
                }
            });

        }
        // End
    }

    private void sendPhoneVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sendCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String Code = phoneAuthCredential.getSmsCode();
            Log.i("Code SMS Code", Code);
            Toast.makeText(RegistrationActivity.this, Code, Toast.LENGTH_SHORT).show();

            if (Code != null) {
                text.setText(Code);
                verifyVerificationCode(Code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RegistrationActivity.this, "Check your internet!", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sendCode, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserData();

                        } else {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Your entered code are wrong!", Toast.LENGTH_SHORT).show();
                                                relativeLayout.setVisibility(View.GONE);
                                                scrollView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }
                            });
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }


    /*private void sendVerificationEmail() {
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
    }*/

    private void sendUserData() {
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
        rRef = FirebaseDatabase.getInstance().getReference().child("ReferCode");

        final String name = userName.getText().toString();
        email = userEmail.getText().toString();
        password = userPass.getText().toString();
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
                            }, 300);
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

        HashMap reg = new HashMap();

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

        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}