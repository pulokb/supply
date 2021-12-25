package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.samulitfirstproject.supply365.adapter.StoreTransferProducts;
import com.samulitfirstproject.supply365.model.vendorName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Admin_Product_Transfer2_Activity extends AppCompatActivity {

    private TextView Name, Email, Number, Location;
    private Button button;
    private RecyclerView recyclerView;
    private StoreTransferProducts transferProducts;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private ImageView back;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    private DatabaseReference transferOrder = FirebaseDatabase.getInstance().getReference("FinalOrder");
    private DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference("AdminEmail");

    private String receive, receive2;
    private ArrayList<String> arrayList;
    private ArrayList<vendorName> list;
    private vendorName vProductID;
    private String address,number,current_user_id,customerUID, TotalWeight,item_weight,vName,vEmail,vNumber,saveCurrentDate,saveCurrentTime,pMethod,pStatus,cName,cEmail;
    private String couponDis,subPrice,totalPrice,customerBalance,vLocation;
    private String fileName,adminEmail,adminPass;
    private boolean isConnectWithInternet = false;

    String orderItem = "";
    String orderQuantity = "";
    String orderItemPrice = "";
    String productID = "";

    private FirebaseAuth mfirebaseAuth;

    //////////////////////////////////////////////////////////////////////////////////

    String file_name_path = "";
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {

            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,

    };

    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_admin__product__transfer2_);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if (!hasPermissions(Admin_Product_Transfer2_Activity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(Admin_Product_Transfer2_Activity.this, PERMISSIONS, PERMISSION_ALL);
        }

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath(), "pdfsdcard_location");
        if (!file.exists()) {
            file.mkdir();
        }




        mfirebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        } else {

        }
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        receive = getIntent().getStringExtra("OderId");
        receive2 = getIntent().getStringExtra("vendorID");

        button = (Button) findViewById(R.id.button3);
        Name = (TextView) findViewById(R.id.Name);
        Email = (TextView) findViewById(R.id.Email);
        Number = (TextView) findViewById(R.id.Number);
        Location = (TextView) findViewById(R.id.Location);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_product_check);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        transferProducts = new StoreTransferProducts(receive, list, Admin_Product_Transfer2_Activity.this);
        recyclerView.setAdapter(transferProducts);

        databaseReference.child("UsersData").child(receive2).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Name.setText("\t"+ snapshot.child("userName").getValue(String.class));
                Email.setText("\t" + snapshot.child("userEmail").getValue(String.class));
                Number.setText("\t" + snapshot.child("userPhone").getValue(String.class));
                Location.setText("\t" + snapshot.child("userLocation").getValue(String.class));

                vName = snapshot.child("userName").getValue(String.class);
                vNumber = snapshot.child("userPhone").getValue(String.class);
                vEmail = snapshot.child("userEmail").getValue(String.class);
                vLocation = snapshot.child("userLocation").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("Order").child(receive).child("productID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                arrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    String id = ds.getValue(String.class);
                    arrayList.add(id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                adminEmail = snapshot.child("email").getValue(String.class);
                adminPass = snapshot.child("password").getValue(String.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("MyProduct").child(receive2).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                list.clear();
                transferProducts.notifyDataSetChanged();

                for (DataSnapshot ds : snapshot.getChildren()){

                        String id = snapshot.child(ds.getKey()).child("productID").getValue(String.class);

                        if (arrayList.contains(id)) {

                            vendorName info = ds.getValue(vendorName.class);
                            info.setNameVendor(id);
                            list.add(info);

                        }

                        if (list.size()<1){
                            button.setVisibility(View.GONE);
                        }else {
                            button.setVisibility(View.VISIBLE);
                        }

                }
                transferProducts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        orderRef.child(receive).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    address = snapshot.child("orderAddress").getValue(String.class);
                    number = snapshot.child("orderNumber").getValue(String.class);
                    customerUID = snapshot.child("customerUID").getValue(String.class);
                    TotalWeight = snapshot.child("TotalWeight").getValue(String.class);
                    //item_weight = snapshot.child("ItemWeights").getValue(String.class);
                    pMethod = snapshot.child("paymentMethod").getValue(String.class);
                    pStatus = snapshot.child("paymentStatus").getValue(String.class);

                    couponDis = snapshot.child("couponDis").getValue(String.class);
                    //subPrice = snapshot.child("orderSubPrice").getValue(String.class);
                    //totalPrice = snapshot.child("orderTotalPrice").getValue(String.class);
                    customerBalance = snapshot.child("customerBalance").getValue(String.class);


                    databaseReference.child("UsersData").child(customerUID).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            cName = snapshot.child("userName").getValue(String.class);
                            cEmail = snapshot.child("userEmail").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_again_Internet_connection(Admin_Product_Transfer2_Activity.this);

                if (isConnectWithInternet) {
                    progressDialog = new ProgressDialog(Admin_Product_Transfer2_Activity.this);
                    progressDialog.show();
                    progressDialog.setMessage("Transferring Order...");


                    int found = 0;
                    for (vendorName pro : list) {
                        if (pro.isSelected()) {

                            orderItem += pro.getProName() + "\n";
                            orderQuantity += pro.getQuantity() + "\n";
                            orderItemPrice += pro.getPrice() + "\n";
                            productID += pro.getProductID() + "\n";
                            item_weight += pro.getWeight() + "\n";

                            found = 1;

                        }
                    }


                    if (found == 1) {


                        Calendar calFordDate = Calendar.getInstance();
                        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                        saveCurrentDate = currentDate.format(calFordDate.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                        saveCurrentTime = currentTime.format(calFordDate.getTime());

                        String myDate = saveCurrentDate + " " + saveCurrentTime;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a");
                        Date date = null;
                        try {
                            date = sdf.parse(myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long millis = date.getTime();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                createpdf();
                                progressDialog.setMessage("Creating Invoice...");
                                button.setEnabled(false);

                            }
                        },1500);


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Admin_Product_Transfer2_Activity.this, "Select at least one item", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });
    }

    private void sendData() {

        HashMap postsMap = new HashMap();

        postsMap.put("orderDate", saveCurrentDate);
        postsMap.put("orderTime", saveCurrentTime);
        postsMap.put("orderItem", orderItem);
        postsMap.put("orderQuantity", orderQuantity);
        postsMap.put("orderItemPrice", orderItemPrice);
        postsMap.put("orderAddress", address);
        postsMap.put("orderNumber", number);
        postsMap.put("customerUID", customerUID);
        postsMap.put("customerOrderID", receive);
        postsMap.put("vendorUID", receive2);
        postsMap.put("productID", productID);
        postsMap.put("TotalWeight", TotalWeight);



        transferOrder.child(receive).updateChildren(postsMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                progressDialog.dismiss();

                prepareNotification(
                        ""+receive,
                        "You have a new order",
                        ""+"Let's have a look",
                        "TransferNotification",
                        "TRANSFER"
                );

                orderRef.child(receive).child("orderStatus").setValue("Processing");

                Toast.makeText(Admin_Product_Transfer2_Activity.this, "Order has been transferred successfully.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(Admin_Product_Transfer2_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    }
                },1500);



            }
        });


    }

    public void  prepareNotification(String pId,String title,String ndescription,String notificationType,String notificationTopic){

        String NOTIFICATION_TOPIC = "/topics/"+notificationTopic;
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_MESSAGE = ndescription;
        String NOTIFICATION_TYPE = notificationType;

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("sender",current_user_id);
            notificationBodyJo.put("receiver",receive2);
            notificationBodyJo.put("pId",pId);
            notificationBodyJo.put("pTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription",NOTIFICATION_MESSAGE);

            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendPostNotification(notificationJo);

    }

    private void sendPostNotification(JSONObject notificationJo) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("FCM_RESPONSE","onResponse: "+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Admin_Product_Transfer2_Activity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> headers = new HashMap<>();

                headers.put("content-Type","application/json");
                headers.put("Authorization","key = AAAAdgRerbo:APA91bGEE9Ay9wCJ6nkXvCUXfsOsdt7kz8nit0ikEiUJJyD63saaBN0EwmoWP1yqdjwF-hjNQE_CRvJA61rbkSG-ClCWDQY15W_jQvfXjXIXam9gtNOtMV81U7adnTXmEkBi6EtleuzV");

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    public void createpdf() {
        Rect bounds = new Rect();
        int pageWidth = 595;
        int pageheight = 842;
        int pathHeight = 2;

        fileName = "invoice"+receive+ ".pdf";
        file_name_path = "/pdfsdcard_location/" + fileName ;
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint iPaint = new Paint();
        iPaint.setTextSize(12f);
        iPaint.setTypeface(Typeface.DEFAULT_BOLD);

        Path path = new Path();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create();
        PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = documentPage.getCanvas();
        int y = 10; // x = 10,
        int x = 10;


        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.logo);
        Bitmap b = (Bitmap.createScaledBitmap(bitmap, 250, 150, false));
        canvas.drawBitmap(b, x, y, paint);

        x += 20;
        y += 180;

        Paint oPaint = new Paint();
        oPaint.setTextSize(24f);
        oPaint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Order Invoice", x, y, oPaint);


        canvas.drawText("Order#"+receive, x+330, y, iPaint);
        y += paint.descent() - paint.ascent()+3;
        canvas.drawText(saveCurrentTime+" | "+saveCurrentDate, x+330, y, paint);

        y += oPaint.descent() - oPaint.ascent();

        canvas.drawText("Bill From:", x+330, y+30, iPaint);
        canvas.drawText(vName, x+330, y+48, paint);
        canvas.drawText(vLocation, x+330, y+62, paint);
        canvas.drawText("Mobile: "+vNumber, x+330, y+76, paint);
        canvas.drawText("Email: "+vEmail, x+330, y+90, paint);



        y += oPaint.descent() - oPaint.ascent();
        x = 30;
        canvas.drawText("Billing Address:", x, y, iPaint);

        y += paint.descent() - paint.ascent()+5;
        x = 30;
        canvas.drawText(cName, x, y, paint);

        y += paint.descent() - paint.ascent()+5;
        x = 30;
        canvas.drawText(address, x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Mobile: "+number, x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Payment Method: "+pMethod, x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Payment Status: "+pStatus, x, y, paint);



        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

//horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);

        canvas.drawLine(30, y, 565, y, paint2);


        y += paint.descent() - paint.ascent()+5;
        x = 120;
        canvas.drawText("Item", x, y, iPaint);

        canvas.drawText("Quantity", x+180, y, iPaint);

        canvas.drawText("Weight", x+280, y, iPaint);

        canvas.drawText("Price", x+380, y, iPaint);


        //blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

        //horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);
        canvas.drawLine(30, y, 565, y, paint2);



        y += paint.descent() - paint.ascent();
        x = 40;


        String item  = orderItem;

        int dis = y;
        int temp = y;

        for (String line: item.split("\n")) {
            canvas.drawText(line, x, dis, paint);

            dis+=17;
            y += paint.descent() - paint.ascent();
        }


        String quantity  = orderQuantity;

        dis = temp;

        for (String line: quantity.split("\n")) {
            canvas.drawText(line, x+280, dis, paint);

            dis+=17;
        }


        String weight  = item_weight.substring(4);

        dis = temp;
        double gw = 0;
        for (String line: weight.split("\n")) {

            gw = Double.parseDouble(line)/1000;

            canvas.drawText(gw+" KG", x+360, dis, paint);

            dis+=17;
        }


        String price  = orderItemPrice;

        dis = temp;

        for (String line: price.split("\n")) {
            canvas.drawText(line, x+460, dis, paint);

            dis+=17;
        }

//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

//horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);
        canvas.drawLine(30, y, 565, y, paint2);


        Paint tPaint = new Paint();
        tPaint.setTextSize(12f);
        tPaint.setTypeface(Typeface.DEFAULT_BOLD);

        y += paint.descent() - paint.ascent()+2;
        x = 120;



        String[] arr = orderQuantity.split("\n");

        final StringBuilder i = new StringBuilder();
        int q = 0;
        for(String s : arr){

            q += Integer.parseInt(s.substring(1));

        }
        i.append("x"+q);

        canvas.drawText("= "+i.toString(), x+190, y, tPaint);

        String[] arr2 = weight.split("\n");

        final StringBuilder j = new StringBuilder();
        double w = 0;
        for(String s : arr2){

            w += Double.parseDouble(s);

        }
       j.append(""+w/1000+" KG");

        canvas.drawText("= "+j.toString(), x+275, y, tPaint);

        String[] arr3 = orderItemPrice.split("\n");

        final StringBuilder k = new StringBuilder();
        int p = 0;
        for(String s : arr3){

            p += Double.parseDouble(s.substring(2));

        }
        k.append("৳ "+p);

        subPrice = k.toString();
        canvas.drawText("= "+k.toString(), x+370, y, tPaint);


//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);


        String Total_Weight_Price = TotalWeight.substring(34, TotalWeight.length());
        String Total_Weight_Price2 = Total_Weight_Price.replaceAll("[^0-9]", "");


        canvas.drawText("Sub Total =  "+subPrice, 360, y+oPaint.descent() - oPaint.ascent()+10, paint);

        canvas.drawText("Delivery Charge =   ৳ "+Total_Weight_Price2, 360, y+paint.descent() - paint.ascent()+40, paint);

        canvas.drawText("Customer Balance =  ৳ "+customerBalance, 360, y+paint.descent() - paint.ascent()+55, paint);

        canvas.drawText("Coupon Discount =  ৳ "+couponDis, 360, y+paint.descent() - paint.ascent()+70, paint);


        //horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(360, y+paint.descent() - paint.ascent()+80);
        canvas.drawLine(340, y+paint.descent() - paint.ascent()+80, 565, y+paint.descent() - paint.ascent()+80, paint2);


        double total =0;
        total= Double.parseDouble(subPrice.substring(2))+Double.parseDouble(Total_Weight_Price2)-Double.parseDouble(couponDis)-Double.parseDouble(customerBalance);

        canvas.drawText("Total Payable = ৳ "+total, 360, y+paint.descent() - paint.ascent()+95, iPaint);


        y += oPaint.descent() - oPaint.ascent()+10;
        x = 30;
        canvas.drawText("Office Address", x, y, iPaint);

        y += paint.descent() - paint.ascent()+5;
        x = 30;
        canvas.drawText(getString(R.string.address_area), x, y, paint);

        y += paint.descent() - paint.ascent();
        x = 30;
        canvas.drawText(getString(R.string.address_dis), x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText(getString(R.string.mobile), x, y, paint);
        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText(getString(R.string.email), x, y, paint);


        //blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);




        myPdfDocument.finishPage(documentPage);

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();

        //viewPdfFile();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sendEmail();
                progressDialog.setMessage("Sending Email...");

            }
        },2500);

    }

    public void viewPdfFile() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void sendEmail() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);

        final String username = adminEmail;
        final String password = adminPass;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(vEmail));
            message.setSubject("Order Invoice");
            message.setText("");

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();
            //String file = this.getExternalFilesDir(null).getAbsolutePath() + file_name_path;
            // String fileName = "attachmentName";
            //DataSource source = new FileDataSource(file);
            //messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.attachFile(file);
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sendData();
                    progressDialog.setMessage("Order Transferred");

                }
            },700);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void Show_dialog_box() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(Admin_Product_Transfer2_Activity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog_box_check_internet,null);
        Button btn_okay = (Button)mView.findViewById(R.id.done);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                check_again_Internet_connection(Admin_Product_Transfer2_Activity.this);
            }
        });
        alertDialog.show();
    }

    private void check_again_Internet_connection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            isConnectWithInternet = true;
        } else {
            if (!isFinishing()) {
                Show_dialog_box();
            }
        }
    }
}