package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class SliderActivity extends AppCompatActivity {

    private CardView first, second, third, fourth, fifth;
    private ImageView back;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private ProgressDialog progressDialog;
    private Uri contentURI,resultUri;
    private String imageUri, position;
    private static final int PICK_FROM_GALLERY = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data.getData() != null) {
            contentURI = data.getData();

            CropImage.activity(contentURI)
                    .start(SliderActivity.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                //Picasso.get().load(resultUri).fit().centerInside().into(UserImage);
                SaveChange();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void SaveChange() {
        if (resultUri!= null) {

            switch (position) {
                case "1":
                    storageReference = FirebaseStorage.getInstance().getReference().child("Slider").child("1st");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingBanner").child("1st");
                    break;
                case "2":
                    storageReference = FirebaseStorage.getInstance().getReference().child("Slider").child("2nd");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingBanner").child("2nd");
                    break;
                case "3":
                    storageReference = FirebaseStorage.getInstance().getReference().child("Slider").child("3rd");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingBanner").child("3rd");
                    break;
                case "4":
                    storageReference = FirebaseStorage.getInstance().getReference().child("Slider").child("4th");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingBanner").child("4th");
                    break;
                case "5":
                    storageReference = FirebaseStorage.getInstance().getReference().child("Slider").child("5th");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingBanner").child("5th");
                    break;
                default:
                    // Nothing
                    break;
            }
            progressDialog.setMessage("Updating Slider Image");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            StorageReference fileReference = storageReference.child("SliderImage");
            storageTask = fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri uri = urlTask.getResult();
                    imageUri = uri.toString();

                    databaseReference.child("imageUrl").setValue(imageUri);
                    Toast.makeText(getApplicationContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        first = findViewById(R.id.first_slider);
        second = findViewById(R.id.second_slider);
        third = findViewById(R.id.third_slider);
        fourth = findViewById(R.id.four_slider);
        fifth = findViewById(R.id.five_slider);

        progressDialog = new ProgressDialog(SliderActivity.this);


        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = "1";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = "2";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = "3";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = "4";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

        fifth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = "5";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });
    }
}