package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.adapter.CategoryAdapter;
import com.samulitfirstproject.supply365.model.CatInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CatRef;

    private RecyclerView cRecyclerView;
    private CategoryAdapter cAdapter;
    private ArrayList<CatInfo> cList;
    private String current_user_id = " ",url,name;
    private Button addCat,choose;
    private ImageView back,showImage;

    private static final int PICK_IMAGE_REQUEST = 101;
    private EditText cName;

    private ProgressDialog loadingBar;
    private Uri mImageUri,resultUri;
    private StorageReference productImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_category);

        addCat = findViewById(R.id.add_category);
        choose =findViewById(R.id.choose_cat);
        showImage = findViewById(R.id.show_category_image);
        cName = findViewById(R.id.et_category_name);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        mfirebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

        }

        if(current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - Show on string.xml
            addCat.setVisibility(View.VISIBLE);
            cName.setVisibility(View.VISIBLE);
            choose.setVisibility(View.VISIBLE);

        }

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Category");
        CatRef = FirebaseDatabase.getInstance().getReference("Category");

        loadingBar = new ProgressDialog(CategoryActivity.this);

        cRecyclerView = findViewById(R.id.add_category_rv);
        cRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerV = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManagerV);

        cList = new ArrayList<>();
        cAdapter = new CategoryAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        CatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            CatInfo info = postSnapshot.getValue(CatInfo.class);
                            info.setKey(postSnapshot.getKey());
                            cList.add(info);


                    }
                    cAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFile();

            }
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .start(CategoryActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        showImage.setVisibility(View.VISIBLE);
        Picasso.get().load(resultUri).fit().centerInside().into(showImage);

    }

    private void uploadFile() {

        loadingBar.setMessage("Please wait...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        name = cName.getText().toString();

        if(resultUri==null ) {

            Toast.makeText(this, "Add Image First", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }
        else if(name.isEmpty()){
            Toast.makeText(CategoryActivity.this, "Empty Field!", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }

        else if(resultUri!=null && !name.isEmpty()) {

            uploadImage();

        }


    }

    public void uploadImage(){

        StorageReference fileReference = productImagesRef.child(System.currentTimeMillis()
                + ".jpg");

        showImage.setDrawingCacheEnabled(true);
        showImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)showImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();

                url = downloadUrl.toString();

                CatRef.child(name).child("url").setValue(url);

                Toast.makeText(CategoryActivity.this, "Category Successfully Added", Toast.LENGTH_SHORT).show();

                Intent n = new Intent(CategoryActivity.this,CategoryActivity.class);
                startActivity(n);
                finish();
                loadingBar.dismiss();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }
}