package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWeight extends AppCompatActivity {

    private CardView first, second, third, four;
    private DatabaseReference databaseReference;
    private String First, Second, Third, Four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        four = findViewById(R.id.four);

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

        first.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(AddWeight.this);
                input.setText("    "+ First);


                AlertDialog dialog = new AlertDialog.Builder(AddWeight.this)
                        .setMessage("Enter Weight Amount For 0 to 5 kg")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AddWeight.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("first").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(AddWeight.this);
                input.setText("    "+ Second);


                AlertDialog dialog = new AlertDialog.Builder(AddWeight.this)
                        .setMessage("Enter Weight Amount For 6 to 10 kg")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AddWeight.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("second").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(AddWeight.this);
                input.setText("    "+ Third);


                AlertDialog dialog = new AlertDialog.Builder(AddWeight.this)
                        .setMessage("Enter Weight Amount For 11 to 25 kg")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AddWeight.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("third").setValue(value2);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(AddWeight.this);
                input.setText("    "+ Four);


                AlertDialog dialog = new AlertDialog.Builder(AddWeight.this)
                        .setMessage("Enter Weight Amount For 25+ kg")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value2 = input.getText().toString().trim();

                                if(value2.isEmpty()){
                                    Toast.makeText(AddWeight.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("four").setValue(value2);
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