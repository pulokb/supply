package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditProduct extends AppCompatActivity {

    private String current_user_id,proName,proDes,proPrice, proDis,proWholePrice,url, Product_discount_vendor, Product_cashBack_vendor, pID,cat,cat2,inStock,isBestDeal,isBestSelling,isPopular,isOffer, weight_String;
    private DatabaseReference ProductRef;
    private RadioButton stockIn,stockOut;
    private EditText pName,pDes,pPrice,pDis,wholesalesPrice,pCash, product_discount_vendor, product_cashBack_vendor, product_weight;
    private TextView category,category2;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private ImageView showImage;
    private CardView UpdateProductButton;

    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri mImageUri,resultUri;
    private StorageReference productImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_edit_product);

        product_weight = findViewById(R.id.et_product_weight);
        showImage = findViewById(R.id.show_product_imageE);
        pName = findViewById(R.id.et_product_nameE);
        pDes = findViewById(R.id.et_product_desE);
        pPrice = findViewById(R.id.et_product_priceE);
        pDis = findViewById(R.id.et_product_disE);
        product_discount_vendor = findViewById(R.id.et_product_dis_vendor);
        wholesalesPrice = findViewById(R.id.et_whole_sales_priceE);
        category = findViewById(R.id.tv_category);
        category2 = findViewById(R.id.tv_category2);
        stockIn = findViewById(R.id.stock_in);
        stockOut = findViewById(R.id.stock_out);
        pCash = findViewById(R.id.et_product_cashBackE);
        product_cashBack_vendor = findViewById(R.id.et_product_cashBack_vendor);

        UpdateProductButton = findViewById(R.id.cv_btn_update_product);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Intent i = getIntent();
        pID = i.getStringExtra("product_id");

        ProductRef  = FirebaseDatabase.getInstance().getReference().child("Products").child(pID);

        loadingBar = new ProgressDialog(EditProduct.this);


        ProductRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("productName").getValue().toString();
                String price = snapshot.child("productPrice").getValue().toString();
                String des = snapshot.child("productDescription").getValue().toString();
                url = snapshot.child("productImage").getValue().toString();
                String wSprice = snapshot.child("productWholeSalesPrice").getValue().toString();
                String discount = snapshot.child("productDiscount").getValue().toString();
                String Product_discount_vendor1 = snapshot.child("vendorDiscount").getValue().toString();
                String cash = snapshot.child("productCashBack").getValue().toString();
                String Product_cashBack_vendor1 = snapshot.child("vendorCashBack").getValue().toString();
                cat = snapshot.child("productCategory").getValue().toString();
                cat2 = snapshot.child("productSubCategory").getValue().toString();
                inStock = snapshot.child("productInStock").getValue().toString();
                String weight = snapshot.child("weight").getValue().toString();

                isBestDeal = snapshot.child("isBestDeal").getValue().toString();
                isBestSelling = snapshot.child("isBestSelling").getValue().toString();
                isPopular = snapshot.child("isPopular").getValue().toString();



                Picasso.get().load(url).fit().centerInside().placeholder(R.drawable.add_product).into(showImage);
                pName.setText(name);
                pDes.setText(des);
                pDis.setText(discount);
                product_discount_vendor.setText(Product_discount_vendor1);
                pCash.setText(cash);
                product_cashBack_vendor.setText(Product_cashBack_vendor1);
                pPrice.setText(price);
                wholesalesPrice.setText(wSprice);
                category.setText("Category: "+cat);
                category2.setText("Sub Category: "+cat2);
                product_weight.setText(weight);

                if(inStock.equals("Yes")){

                    stockIn.setChecked(true);

                }else {
                    stockIn.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        UpdateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                weight_String = product_weight.getText().toString();
                proName = pName.getText().toString();
                proDes = pDes.getText().toString();
                proPrice = pPrice.getText().toString();
                proDis = pDis.getText().toString();
                proWholePrice = wholesalesPrice.getText().toString();
                Product_cashBack_vendor = product_cashBack_vendor.getText().toString();
                Product_discount_vendor = product_discount_vendor.getText().toString();

                if (TextUtils.isEmpty(proName)) {
                    pName.setError("Enter Product Name");
                    pName.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(proPrice)) {
                    pPrice.setError("Enter Product Price");
                    pPrice.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(weight_String)) {
                    pPrice.setError("Enter Product Weight");
                    pPrice.requestFocus();
                    return;
                }
                else if(!isNetworkAvaliable(EditProduct.this)){
                    Toast.makeText(EditProduct.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                else {

                    loadingBar.setMessage("Updating Product");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(false);

                   // SavingProductInformationToDatabase();
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
                    .start(EditProduct.this);

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

            SavingProductInformationToDatabase();

        }


    }

    private void SavingProductInformationToDatabase()
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

        if (stockIn.isChecked()){
            inStock = "Yes";
        }else if (stockOut.isChecked()){
            inStock = "No";
        }

        if (pCash.getText().toString().isEmpty()){
            cash = " ";
            isOffer = "No";
        }else {
            cash = pCash.getText().toString();
            isOffer = "Yes";
        }

        if (Product_cashBack_vendor.isEmpty()){
            Product_cashBack_vendor = " ";
            isOffer = "No";
        }else {
            isOffer = "Yes";
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
        postsMap.put("productCategory", cat);
        postsMap.put("productInStock", inStock);
        postsMap.put("isBestDeal", isBestDeal);
        postsMap.put("isBestSelling", isBestSelling);
        postsMap.put("isPopular", isPopular);
        postsMap.put("isOffer", isOffer);
        postsMap.put("vendorDiscount", Product_discount_vendor);
        postsMap.put("vendorCashBack", Product_cashBack_vendor);
        postsMap.put("weight", weight_String);


        ProductRef.updateChildren(postsMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {

                        Toast.makeText(EditProduct.this, "Product Details Updated Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                        Intent i = new Intent(EditProduct.this, AdminActivity.class);
                        startActivity(i);
                        finish();


                    }
                    else
                    {
                        Toast.makeText(EditProduct.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });

    }


    public void uploadImage(){

        StorageReference fileReference = productImagesRef.child(pID
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
        }).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!urlTask.isSuccessful()) ;
            Uri downloadUrl = urlTask.getResult();

            url = downloadUrl.toString();

            SavingProductInformationToDatabase();
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
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