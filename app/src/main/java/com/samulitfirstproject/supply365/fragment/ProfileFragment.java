package com.samulitfirstproject.supply365.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samulitfirstproject.supply365.MyOrder;
import com.samulitfirstproject.supply365.R;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.samulitfirstproject.supply365.geocode.GeoCodingLocation;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    TextView Name, Email, Address, Number,myBalace,refer,type;
    ImageView UserImage,editLocation,back,chooseImage,editPhone,copy;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String getKey;

    StorageTask storageTask;
    Uri contentURI,resultUri;
    String valueAddress, ImageURL, imageUri,valueNumber, whichType_user, valueName, valueEmail, vPanelAccess;
    private ProgressDialog progressDialog;
    private Button myOrder;

    private static final int PICK_FROM_GALLERY = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);


        Name = view.findViewById(R.id.Name);
        Email = view.findViewById(R.id.EmailAddress);
        Number = view.findViewById(R.id.NumberFill);
        Address = view.findViewById(R.id.addressText);
        UserImage = view.findViewById(R.id.UserImage);
        chooseImage = view.findViewById(R.id.edit_profile);
        editLocation = view.findViewById(R.id.edit_location);
        editPhone = view.findViewById(R.id.edit_phone);
        back = view.findViewById(R.id.back);
        myBalace = view.findViewById(R.id.my_balance);
        myOrder = view.findViewById(R.id.my_order);
        refer = view.findViewById(R.id.referText);
        copy = view.findViewById(R.id.refer_copy);
        type = view.findViewById(R.id.type);

        progressDialog = new ProgressDialog(getActivity());

        getKey = user.getUid();

        storageReference = FirebaseStorage.getInstance().getReference().child("UserImage").child(getKey);;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersData").child(getKey);

        refer.setText("Refer Code : "+getKey.substring(7,14));

        copy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                ClipboardManager cb=(ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cd=ClipData.newPlainText("Label",getKey.substring(7,14));
                cb.setPrimaryClip(cd);
                Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    valueName = String.valueOf(snapshot.child("userName").getValue());
                    valueNumber = String.valueOf(snapshot.child("userPhone").getValue());
                    valueEmail = String.valueOf(snapshot.child("userEmail").getValue());
                    valueAddress = String.valueOf(snapshot.child("userLocation").getValue());
                    String balance = String.valueOf(snapshot.child("userBalance").getValue());
                    whichType_user = String.valueOf(snapshot.child("userType").getValue());
                    vPanelAccess = String.valueOf(snapshot.child("vPanelAccess").getValue());

                    String t = String.valueOf(snapshot.child("userTotalBalance").getValue());
                    String u = String.valueOf(snapshot.child("usesBalance").getValue());

                    ImageURL = String.valueOf(snapshot.child("userImage").getValue());

                    Name.setText(valueName);
                    Email.setText(valueEmail);
                    Number.setText(valueNumber);

                    if(whichType_user.equals("Customer")){
                        type.setText("");
                    }else {
                        type.setText(whichType_user);
                    }

                    double temp = Double.parseDouble(t);
                    double temp2 = Double.parseDouble(u);

                    myBalace.setText((temp-temp2) + " Tk");

                    if (!valueAddress.equals(" ")) {
                        Address.setText(valueAddress);
                    }

                    if (!ImageURL.equals(" ")) {
                        Picasso.get().load(ImageURL).fit().centerInside().placeholder(R.drawable.loading_gif__).into(UserImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });




        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(getActivity());
                input.setText("    "+valueAddress);


                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("Enter Location")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String location = input.getText().toString().trim();

                                if(location.isEmpty()){
                                    Toast.makeText(getActivity(), "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("userLocation").setValue(location);

                                    if (vPanelAccess.equals("Yes")) {
                                        GeoCodingLocation.getAddressFromLocation(location, getContext(), new GeoCoderHandler(), getKey, whichType_user, valueName, valueEmail);
                                    }

                                    /*GeoCodingLocation2 geoCodingLocation2 = new GeoCodingLocation2();
                                    geoCodingLocation2.getAddressFromLocation2(location, getActivity(), new GeoCoderHandler(),
                                            getKey, whichType_user, valueName);*/

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });



        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(getActivity());
                input.setText("    "+valueNumber);


                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("Enter Number")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String num = input.getText().toString().trim();

                                if(num.isEmpty()){
                                    Toast.makeText(getActivity(), "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    databaseReference.child("userPhone").setValue(num);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });



        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MyOrder.class);
                intent.putExtra("person_id","customer");
                startActivity(intent);
            }
        });



        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY
                && resultCode == RESULT_OK && data.getData() != null) {

            contentURI = data.getData();

            CropImage.activity(contentURI)
                    .setAspectRatio(1,1)
                    .start(getActivity(),this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                Picasso.get().load(resultUri).fit().centerInside().into(UserImage);
                SaveChange();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void SaveChange() {
        if (resultUri!= null) {

            progressDialog.setMessage("Updating Profile Pic");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            StorageReference fileReference = storageReference.child("ProfilePic");
            storageTask = fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri uri = urlTask.getResult();
                    imageUri = uri.toString();

                    databaseReference.child("userImage").setValue(imageUri);
                    Toast.makeText(getActivity(), "Update Successfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    chooseImage.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
        }
    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
        }
    }

}