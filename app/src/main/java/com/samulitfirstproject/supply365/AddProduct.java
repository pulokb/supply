package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddProduct extends AppCompatActivity {

    final ArrayList<String> cat = new ArrayList<String>();
    final ArrayList<String> cat2 = new ArrayList<String>();
    private String String_weight ,category,url,current_user_id,proName,proDes,proPrice, Product_discount_vendor, Product_cashBack_vendor , proDis,proWholePrice,inStock = " ",category2,isOffer;
    private DatabaseReference categoryRef,MyPro,ProductRef;
    private RadioButton stockIn,stockOut;
    private static final int PICK_IMAGE_REQUEST = 101;
    private EditText EditText_Weight ,pName,pDes,pPrice,pDis,wholesalesPrice,cashBack, product_discount_vendor, product_cashBack_vendor;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private ImageView addImage,showImage;
    private Uri mImageUri,resultUri;
    private StorageReference productImagesRef;
    private CardView AddProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_product);


        addImage = findViewById(R.id.add_product_image);
        showImage = findViewById(R.id.show_product_image);
        EditText_Weight = findViewById(R.id.et_product_weight);
        pName = findViewById(R.id.et_product_name);
        pDes = findViewById(R.id.et_product_des);
        pPrice = findViewById(R.id.et_product_price);
        pDis = findViewById(R.id.et_product_dis);
        product_discount_vendor = findViewById(R.id.et_product_dis_vendor);
        wholesalesPrice = findViewById(R.id.et_whole_sales_price);
        stockIn = findViewById(R.id.stock_in);
        stockOut = findViewById(R.id.stock_out);
        cashBack = findViewById(R.id.et_product_cashBack);
        product_cashBack_vendor = findViewById(R.id.et_product_cashBack_vendor);

        AddProductButton = findViewById(R.id.cv_btn_add_product);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product").child(current_user_id);
        ProductRef  = FirebaseDatabase.getInstance().getReference().child("Products");

        categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");

        loadingBar = new ProgressDialog(AddProduct.this);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        String c = postSnapshot.getKey();
                        cat.add(c);

                        for (DataSnapshot postSnapshot2 : snapshot.child(c).getChildren()) {

                            String c2 = postSnapshot2.getKey();
                            if (!c2.equals("url")){
                                cat2.add(c2);
                            }


                        }

                    }

                    final Spinner spin = findViewById(R.id.spinner1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProduct.this, android.R.layout.simple_spinner_item, cat);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin.setAdapter(adapter);
                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            category = spin.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    final Spinner spin2 = findViewById(R.id.spinner2);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddProduct.this, android.R.layout.simple_spinner_item, cat2);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adapter2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            category2 = spin2.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        AddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String_weight = EditText_Weight.getText().toString();
                proName = pName.getText().toString();
                proDes = pDes.getText().toString();
                proPrice = pPrice.getText().toString();
                proDis = pDis.getText().toString();
                Product_discount_vendor = product_discount_vendor.getText().toString();
                proWholePrice = wholesalesPrice.getText().toString();
                Product_cashBack_vendor = product_cashBack_vendor.getText().toString();

                if (stockIn.isChecked()){
                    inStock = "Yes";
                }
                if (stockOut.isChecked()) {
                    inStock = "No";
                }


                if (TextUtils.isEmpty(proName)) {
                    pName.setError("Enter Product Name");
                    pName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(proPrice)) {
                    pPrice.setError("Enter Product Price");
                    pPrice.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(String_weight)) {
                    pPrice.setError("Enter Product Weight");
                    pPrice.requestFocus();
                    return;
                } else if (inStock.equals(" ")) {
                    Toast.makeText(AddProduct.this, "Select is product stock or not", Toast.LENGTH_SHORT).show();
                } else if(!isNetworkAvaliable(AddProduct.this)){
                    Toast.makeText(AddProduct.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                } else {

                    loadingBar.setMessage("Adding New Product");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(false);

                    uploadFile();
                }

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
                    .start(AddProduct.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.get().load(resultUri).fit().centerInside().into(showImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void uploadFile() {

        if(resultUri!=null) {

            uploadImage();

        }

        else {

            Toast.makeText(this, "Add Image First", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }


    }


    private void SavingProductInformationToDatabase( String postKey)
    {

        String cash = " ";

        if (proDes.isEmpty()){
            proDes = " ";
        }
       if (proDis.isEmpty()){
            proDis = " ";
           isOffer = "No";
        }else {
           isOffer = "Yes";
       }

        if (Product_discount_vendor.isEmpty()){
            Product_discount_vendor = " ";
            isOffer = "No";
        }else {
            isOffer = "Yes";
        }

       if (proWholePrice.isEmpty()){
            proWholePrice = " ";
        }

        if (cashBack.getText().toString().isEmpty()){
            cash = " ";
            isOffer = "No";
        }else {
            cash = cashBack.getText().toString();
            isOffer = "Yes";
        }

        if (Product_cashBack_vendor.isEmpty()){
            Product_cashBack_vendor = " ";
            isOffer = "No";
        }else {
            isOffer = "Yes";
        }

       if (stockIn.isChecked()){
           inStock = "Yes";
       }
       if (stockOut.isChecked()){
        inStock = "No";
       }


        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());



        HashMap postsMap = new HashMap();

        postsMap.put("date", saveCurrentDate);
        postsMap.put("time", saveCurrentTime);
        postsMap.put("productDescription", proDes);
        postsMap.put("productImage", url);
        postsMap.put("productName", proName);
        postsMap.put("productPrice", proPrice);
        postsMap.put("productDiscount", proDis);
        postsMap.put("productCashBack", cash);
        postsMap.put("productWholeSalesPrice", proWholePrice);
        postsMap.put("productCategory", category);
        postsMap.put("productSubCategory", category2);
        postsMap.put("productInStock", inStock);
        postsMap.put("isBestDeal", "No");
        postsMap.put("isBestSelling", "No");
        postsMap.put("isPopular", "No");
        postsMap.put("isOffer", isOffer);
        postsMap.put("vendorDiscount", Product_discount_vendor);
        postsMap.put("vendorCashBack", Product_cashBack_vendor);
        postsMap.put("weight", String_weight);

        //vendorCashBack
        //vendorDiscount





        ProductRef.child(postKey).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(AddProduct.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent i = new Intent(AddProduct.this, AdminActivity.class);
                            startActivity(i);
                            finish();

                        }
                        else
                        {
                            Toast.makeText(AddProduct.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }

    public void uploadImage(){

        String proID = ProductRef.push().getKey();
        final String postKey = proID;

        StorageReference fileReference = productImagesRef.child(postKey
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

                SavingProductInformationToDatabase(postKey);
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
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

}