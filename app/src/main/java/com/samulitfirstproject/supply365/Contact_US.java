package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Contact_US extends AppCompatActivity {

    private TextView Name, Email, Address, Number, EditText;
    private ImageView UserImage, back, editButton;
    private Button button;
    private FirebaseAuth mfirebaseAuth;
    private String current_user_id = " ";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(), databaseReference2;
    private FirebaseUser mFirebaseuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_contact__us);

        Name = findViewById(R.id.Name);
        Email = findViewById(R.id.EmailAddress);
        Number = findViewById(R.id.NumberFill);
        Address = findViewById(R.id.addressText);
        EditText = findViewById(R.id.aditText);
        UserImage = findViewById(R.id.UserImage);
        editButton = findViewById(R.id.edit);
        back = findViewById(R.id.back);
        button = findViewById(R.id.chat);


        mfirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

        }


        databaseReference.child("UsersData").child(getString(R.string.admin_UID))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("userName").getValue(String.class);
                String email = snapshot.child("userEmail").getValue(String.class);
                String number = snapshot.child("userPhone").getValue(String.class);
                String address = snapshot.child("userLocation").getValue(String.class);
                String userImage = snapshot.child("userImage").getValue(String.class);

                Name.setText(name);
                Email.setText(email);
                Number.setText(number);
                Address.setText(address);

                if (!userImage.equals(" ")) {
                    Picasso.get().load(userImage).fit().centerInside().placeholder(R.drawable.loading_gif__).into(UserImage);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFirebaseuser != null) {

                    if (current_user_id.equals(getString(R.string.admin_UID))){
                        Toast.makeText(Contact_US.this, "You Can't Message Yourself", Toast.LENGTH_SHORT).show();
                    }else {

                        Intent goToMessageShow = new Intent(Contact_US.this, messageShowActivity.class);
                        goToMessageShow.putExtra("key", getString(R.string.admin_UID));
                        startActivity(goToMessageShow);
                    }


                }else {
                    Toast.makeText(Contact_US.this, "Login First", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (current_user_id.equals(getString(R.string.admin_UID))){
            editButton.setVisibility(View.VISIBLE);
        }else {
            editButton.setVisibility(View.GONE);
        }

        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Details").child("contactDetails");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EditText.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(Contact_US.this);
                input.setText("    "+EditText.getText());

                AlertDialog dialog = new AlertDialog.Builder(Contact_US.this)
                        .setMessage("Enter Text")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = input.getText().toString().trim();

                                if(text.isEmpty()){
                                    Toast.makeText(Contact_US.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("Details").child("contactDetails").setValue(text);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }
}