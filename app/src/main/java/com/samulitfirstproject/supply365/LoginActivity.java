package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private TextView forgotPass;
    private EditText userEmail, userPassword;
    private ProgressDialog progressDialog;
    private Button btnLogin;
    private FirebaseAuth mfirebaseAuth;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private ImageView vPass;
    private boolean isShowPassword = false;
    private String useremail,userpassword,current_user_id;
    private DatabaseReference uRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        signUp = findViewById(R.id.tv_signup);
        mfirebaseAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.et_email);
        userPassword =  findViewById(R.id.et_pass);
        forgotPass = findViewById(R.id.tv_forgotpass) ;
        btnLogin = findViewById(R.id.btnLogin);
        vPass = findViewById(R.id.imgLogPass);


        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            userEmail.setText(loginPreferences.getString("useremail", ""));
            userPassword.setText(loginPreferences.getString("userpassword", ""));
            saveLoginCheckBox.setChecked(true);
        }



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);startActivity(i) ;
                finish();
            }
        });


        vPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPassword) {
                    userPassword.setTransformationMethod(new PasswordTransformationMethod());
                    vPass.setImageDrawable(getResources().getDrawable(R.drawable.ic_pass_not_visible));
                    isShowPassword = false;
                }else{
                    userPassword.setTransformationMethod(null);
                    vPass.setImageDrawable(getResources().getDrawable(R.drawable.ic_pass_visibility));
                    isShowPassword = true;
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userEmail.getWindowToken(), 0);

                useremail = userEmail.getText().toString();
                userpassword = userPassword.getText().toString();

                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("useremail", useremail);
                    loginPrefsEditor.putString("userpassword", userpassword);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }



                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();

                if (email.isEmpty()) {
                    userEmail.setError("invalid email");
                    userEmail.requestFocus();
                } else if (password.isEmpty()) {
                    userPassword.setError("invalid password");
                    userPassword.requestFocus();
                } else if (email.isEmpty() && password.isEmpty()) {

                    Toast.makeText(LoginActivity.this, "Enter a valid Email & Password", Toast.LENGTH_SHORT).show();
                }else if (!(isNetworkAvaliable(LoginActivity.this))) {
                    Toast.makeText(LoginActivity.this, "Check Your Connection", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && password.isEmpty())) {

                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCanceledOnTouchOutside(false);


                    mfirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            //checkIfEmailVerified();
                            successful_login();
                        }

                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }

            }

            /*private void checkIfEmailVerified() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user.isEmailVerified()) {

                    successful_login();

                } else {

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please use verified Email", Toast.LENGTH_SHORT).show();

                    for (int i=0; i<1; i++){
                        sendVerificationEmail();
                    }

                }
            }

            private void sendVerificationEmail() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        final Dialog dialog = new Dialog(LoginActivity.this);
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
                            }
                        });

                        dialog.show();


                    } else {

                        Toast.makeText(LoginActivity.this, "Something wrong! Please try again.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                    }
                });
            }*/

            private void successful_login() {
                progressDialog.dismiss();

                uRef = FirebaseDatabase.getInstance().getReference().child("UsersData");

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

                uRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            String approve = String.valueOf(snapshot.child("isApprove").getValue());
                            String type = String.valueOf(snapshot.child("userType").getValue());

                            if (!(type.equals("Customer") || type.equals("Admin"))){

                                if (approve.equals("Yes")){

                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();

                                }else {

                                    Toast.makeText(LoginActivity.this, "Please Wait For Admin Approval", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                    finish();

                                }

                            }else {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(LoginActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Reset Password")
                        .setMessage("\nEnter your registered email")
                        .setView(input)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String resetemail = input.getText().toString();

                                if(resetemail.isEmpty()){
                                    Toast.makeText(LoginActivity.this, "Empty Email!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    mfirebaseAuth.sendPasswordResetEmail(resetemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "An Email Sent To Your Registered Email", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });
    }


    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please connect to the Internet")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
    }
}