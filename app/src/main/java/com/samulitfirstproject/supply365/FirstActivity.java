package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirstActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private boolean isConnectWithInternet = false;
    private String  url;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference update;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setProgress(i);

        update = FirebaseDatabase.getInstance().getReference().child("CheckUpdate");

        check_again_Internet_connection(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Check Auto Update
                update.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && isConnectWithInternet){

                            String version = dataSnapshot.child("version").getValue().toString();
                            url = dataSnapshot.child("url").getValue(String.class);

                            String VersionName = BuildConfig.VERSION_NAME;

                            if (version.equals(VersionName)) {

                                Intent goToMain = new Intent(FirstActivity.this, MainActivity.class);
                                startActivity(goToMain);
                                finish();
                                //progressBar.setVisibility(View.INVISIBLE);

                            } else{

                                if (isConnectWithInternet) {

                                    progressBar.setVisibility(View.INVISIBLE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                                    builder.setTitle("New Version Available");
                                    builder.setIcon(R.drawable.logo);
                                    builder.setCancelable(false);
                                    builder.setMessage("Update SpeedBazar For Better Experience")
                                            .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(url));
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(FirstActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        },1500);
    }

    private void Show_dialog_box() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FirstActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog_box_check_internet,null);
        Button btn_okay = (Button)mView.findViewById(R.id.done);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                check_again_Internet_connection(FirstActivity.this);

                if(isConnectWithInternet){
                    Intent goToMain = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(goToMain);
                    finish();
                }

            }
        });
        alertDialog.show();
    }

    private void check_again_Internet_connection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            isConnectWithInternet = true;
        } else {

            progressBar.setVisibility(View.INVISIBLE);
            Show_dialog_box();

        }
    }
}