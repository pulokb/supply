package com.samulitfirstproject.supply365.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;


import com.google.firebase.auth.FirebaseUser;
import com.samulitfirstproject.supply365.MainActivity;
import com.samulitfirstproject.supply365.OrderActivity;
import com.samulitfirstproject.supply365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.samulitfirstproject.supply365.VendorActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private static final String ADMIN_CHANNEL_ID2 = "admin_channel2";
    private static final String ADMIN_CHANNEL_ID3 = "admin_channel3";

    private  String current_user_id = " ",postDP;
    private FirebaseAuth mAuth;
    private String user_id = " ";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseuser = mAuth.getCurrentUser();
        if (mFirebaseuser != null) {
            current_user_id = mAuth.getCurrentUser().getUid();
        }



        String notificationType = remoteMessage.getData().get("notificationType");

                if(notificationType.equals("OrderNotification")){

                    postDP = remoteMessage.getData().get("dp");
                    String sender = remoteMessage.getData().get("sender");
                    String pId = remoteMessage.getData().get("pId");
                    String pTitle = remoteMessage.getData().get("pTitle");
                    String pDescription = remoteMessage.getData().get("pDescription");

                    if((current_user_id).equals(String.valueOf(R.string.admin_UID))){

                        showOrderNotification(""+pId,""+pTitle,""+pDescription);


                    }

                }
                else if (notificationType.equals("MessageNotification")){


                    user_id = remoteMessage.getData().get("user_id");
                    String dp = remoteMessage.getData().get("dp");
                    String pId = remoteMessage.getData().get("pId");
                    String pTitle = remoteMessage.getData().get("pTitle");
                    String pDescription = remoteMessage.getData().get("pDescription");

                    if(current_user_id.equals(pId)){

                        showMessageNotification(""+dp,""+pId,""+pTitle,""+pDescription);

                    }


                }

                else if (notificationType.equals("TransferNotification")){


                    user_id = remoteMessage.getData().get("user_id");
                    String receiver = remoteMessage.getData().get("receiver");
                    String pId = remoteMessage.getData().get("pId");
                    String pTitle = remoteMessage.getData().get("pTitle");
                    String pDescription = remoteMessage.getData().get("pDescription");

                    if(current_user_id.equals(receiver)){

                        showTransferNotification(""+pId,""+pTitle,""+pDescription);


                    }


                }


    }


    private void showMessageNotification(String dp,String pId, String pTitle, String pDescription) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setupMessageNotificationChannel(notificationManager);

        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from","message");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Bitmap bmp = null;
        try {
            URL url = new URL(dp);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception

        }

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this,""+ADMIN_CHANNEL_ID2)

                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(getCircleBitmap(bmp))
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo);
        }


        notificationManager.notify(notificationID,notificationBuilder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupMessageNotificationChannel(NotificationManager notificationManager) {


        CharSequence channelName = "Message Notification";
        String channelDescription = "Device to device review notification";



        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID2, channelName, NotificationManager.IMPORTANCE_HIGH);

        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);

        if(notificationManager!=null){
            notificationManager.createNotificationChannel(adminChannel);
        }


    }

    private void showOrderNotification(String pId, String pTitle, String pDescription) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setupOrderNotificationChannel(notificationManager);

        }

        Intent intent = new Intent(this, OrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Bitmap bmp = null;
        try {
            URL url = new URL(postDP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception

        }

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this,""+ADMIN_CHANNEL_ID)

        .setSmallIcon(R.drawable.logo)
        .setLargeIcon(getCircleBitmap(bmp))
        .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo);
        }


        notificationManager.notify(notificationID,notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupOrderNotificationChannel(NotificationManager notificationManager) {

        CharSequence channelName = "Order Notification";
        String channelDescription = "Device to device review notification";



        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);

        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);

        if(notificationManager!=null){
            notificationManager.createNotificationChannel(adminChannel);
        }



    }

    private void showTransferNotification(String pId, String pTitle, String pDescription) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setupTransferNotificationChannel(notificationManager);

        }

        Intent intent = new Intent(this, VendorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Bitmap bmp = null;
        try {
            URL url = new URL(postDP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception

        }

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this,""+ADMIN_CHANNEL_ID3)

                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(getCircleBitmap(bmp))
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo);
        }


        notificationManager.notify(notificationID,notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupTransferNotificationChannel(NotificationManager notificationManager) {

        CharSequence channelName = "Transfer Notification";
        String channelDescription = "Device to device review notification";



        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID3, channelName, NotificationManager.IMPORTANCE_HIGH);

        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);

        if(notificationManager!=null){
            notificationManager.createNotificationChannel(adminChannel);
        }



    }


    private Bitmap getCircleBitmap(Bitmap bitmap) {

        Bitmap output = null;

        if(bitmap != null){

            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            bitmap.recycle();


        }

        return output;
    }

}
