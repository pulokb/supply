package com.samulitfirstproject.supply365;

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
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class Invoice extends AppCompatActivity {

    Button btnCreatePdf;


    String file_name_path = "";
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {

            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if (!hasPermissions(Invoice.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(Invoice.this, PERMISSIONS, PERMISSION_ALL);
        }

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath(), "pdfsdcard_location");
        if (!file.exists()) {
            file.mkdir();
        }

        btnCreatePdf = findViewById(R.id.btnCreatePdf);


        btnCreatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createpdf();
            }
        });

    }


    public void createpdf() {
        Rect bounds = new Rect();
        int pageWidth = 595;
        int pageheight = 842;
        int pathHeight = 2;

        final String fileName = "invoice";
        file_name_path = "/pdfsdcard_location/" + fileName + ".pdf";
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint paint2 = new Paint();

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


        canvas.drawText("#Order No", x+400, y, paint);
        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Date of issue", x+400, y, paint);


        y += oPaint.descent() - oPaint.ascent();
        x = 30;
        canvas.drawText("Billing Address", x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Contact Number", x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Payment Status", x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText("Payment Method", x, y, paint);




//        paint.getTextBounds(tv_title.getText().toString(), 0, tv_title.getText().toString().length(), bounds);
//        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
//        canvas.drawText(tv_title.getText().toString(), x, y, paint);
//
//        paint.getTextBounds(tv_sub_title.getText().toString(), 0, tv_sub_title.getText().toString().length(), bounds);
//        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
//        y += paint.descent() - paint.ascent();
//        canvas.drawText(tv_sub_title.getText().toString(), x, y, paint);

        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

//horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);

        canvas.drawLine(30, y, 565, y, paint2);


        Paint iPaint = new Paint();
        iPaint.setTextSize(12f);
        iPaint.setTypeface(Typeface.DEFAULT_BOLD);

        y += paint.descent() - paint.ascent()+5;
        x = 120;
        canvas.drawText("Item", x, y, iPaint);

        canvas.drawText("Quantity", x+200, y, iPaint);

        canvas.drawText("Weight", x+300, y, iPaint);

        canvas.drawText("Price", x+400, y, iPaint);


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


        String item  ="With A sized paper, however many times\nyou double the short edge or\nhalf the long edge, it will always have the";

        int dis = y;
        int temp = y;

        for (String line: item.split("\n")) {
            canvas.drawText(line, x, dis, paint);

            dis+=17;
            y += paint.descent() - paint.ascent();
        }


        String quantity  ="x1\nx2\nx3";

        dis = temp;

        for (String line: quantity.split("\n")) {
            canvas.drawText(line, x+300, dis, paint);

            dis+=17;
        }


        String weight  ="1 KG\n2 KG\n3 KG";

        dis = temp;

        for (String line: weight.split("\n")) {
            canvas.drawText(line, x+385, dis, paint);

            dis+=17;
        }


        String price  ="100 TK\n200 TK\n300 TK";

        dis = temp;

        for (String line: price.split("\n")) {
            canvas.drawText(line, x+480, dis, paint);

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

        canvas.drawText("= x6", x+210, y, tPaint);

        canvas.drawText("= 6 KG", x+295, y, tPaint);

        canvas.drawText("= 600 TK", x+390, y, tPaint);


//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);




        y += oPaint.descent() - oPaint.ascent()+10;
        x = 30;
        canvas.drawText("Office Address", x, y, iPaint);

        y += paint.descent() - paint.ascent()+5;
        x = 30;
        canvas.drawText(String.valueOf(R.string.address_area), x, y, paint);

        y += paint.descent() - paint.ascent();
        x = 30;
        canvas.drawText(String.valueOf(R.string.address_dis), x, y, paint);

        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText(String.valueOf(R.string.mobile), x, y, paint);
        y += paint.descent() - paint.ascent()+3;
        x = 30;
        canvas.drawText(String.valueOf(R.string.email), x, y, paint);


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

        sendEmail();
    }

    public void viewPdfFile() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);

    }

    public void sendPdfFile() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        String[] mailto = {"me@gmail.com"};
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, mailto);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Someone Needs Blood Nearby You - Team BackBenchers");
        intent.putExtra(Intent.EXTRA_TEXT, "Blood Group : " +"\nQuantity : " +"\nLocation : " +"\nContact : " +"\nDeadline : " +"\n\n" + "\n\nIf you cannot give blood yourself, share it with your friends. Maybe one of your shares will save someone's life.\n" +
                "Thank you.");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void sendEmail() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        String[] mailto = {"me@gmail.com"};
        Uri uri = Uri.fromFile(file);

        final String username = String.valueOf(R.string.admin_username);
        final String password = String.valueOf(R.string.admin_password);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("niloy64529@gmail.com"));
            message.setSubject("Order Invoice");
            message.setText("Dear Mail Crawler,"
                    + "\n\n No spam to my email, please!");

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();
            //String file = this.getExternalFilesDir(null).getAbsolutePath() + file_name_path;
           // String fileName = "attachmentName";
            //DataSource source = new FileDataSource(file);
            //messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.attachFile(file);
            messageBodyPart.setFileName(file_name_path);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Done");
            viewPdfFile();

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}