package com.samulitfirstproject.supply365;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Reply extends AppCompatActivity {

    private String comment_key,current_user_id,postKey,uid,fullname,current_user_dp;
    private EditText writeReply;
    private ImageView sendReply,backspace;
    private RecyclerView rrecyclerView;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference Reply,UserRef,Comment_uid;
    private ArrayList<ReplyInfo> rList;
    private ReplyAdapter rAdapter;
    private SwipeRefreshLayout swipe;
    private static int REFRESH = 700;
    private CircleImageView cp;

    private TextView reply2,cn,crc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_reply);


        writeReply = findViewById(R.id.write_reply);
        sendReply = findViewById(R.id.send_reply);
        rrecyclerView = findViewById(R.id.reply_recycler);

        backspace = findViewById(R.id.backpress_reply);
        swipe =findViewById(R.id.rswipe);
        cp = findViewById(R.id.cr_pic);
        cn = findViewById(R.id.tv_cr_name);
        crc = findViewById(R.id.tv_cr_comment);

        reply2 = findViewById(R.id.reply_to);

        rrecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        rrecyclerView.setLayoutManager(linearLayoutManager);

        rList = new ArrayList<>();

        rAdapter = new ReplyAdapter(this, rList);

        rrecyclerView.setAdapter(rAdapter);

        mfirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        comment_key = getIntent().getExtras().get("comment_key").toString();
        postKey = getIntent().getExtras().get("postKey").toString();

        Reply = firebaseDatabase.getReference().child("Comment").child(postKey).child(comment_key).child("Reply");
        UserRef = firebaseDatabase.getReference().child("UsersData");

        Comment_uid = firebaseDatabase.getReference().child("Comment").child(postKey).child(comment_key);


        Comment_uid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                   uid = dataSnapshot.child("uid").getValue().toString();
                    String comment = dataSnapshot.child("comment").getValue().toString();

                    crc.setText(comment);

                    UserRef.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){

                                String fname = dataSnapshot.child("userName").getValue().toString();

                                cn.setText(fname);
                                String url = dataSnapshot.child("userImage").getValue(String.class);

                                Picasso.get().load(url).placeholder(R.drawable.loading_icon).fit().centerCrop().into(cp);

                                reply2.setText("Reply to "+fname);
                                writeReply.setHint("Reply to "+fname);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Reply.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    rList.clear();
                                    rAdapter.notifyDataSetChanged();

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        ReplyInfo reply = postSnapshot.getValue(ReplyInfo.class);
                                        reply.setKey(postSnapshot.getKey());
                                        rList.add(reply);
                                    }
                                    rAdapter.notifyDataSetChanged();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(Reply.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });
                        swipe.setRefreshing(false);
                    }
                },REFRESH);
            }
        });

        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(writeReply.getWindowToken(), 0);

                UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String fname = dataSnapshot.child("userFirstName").getValue(String.class);
                            String lname = dataSnapshot.child("userLastName").getValue(String.class);
                            String name = fname+" "+lname;

                            fullname = fname+" "+lname;

                            setReply(name);
                            writeReply.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        Reply.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    rList.clear();
                    rAdapter.notifyDataSetChanged();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ReplyInfo reply = postSnapshot.getValue(ReplyInfo.class);
                        reply.setKey(postSnapshot.getKey());
                        rList.add(reply);
                    }
                    rAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Reply.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });



    }

    private void setReply(String name) {

        String replyText = writeReply.getText().toString().trim();

        if(TextUtils.isEmpty(replyText)){
            Toast.makeText(this, "write a reply first!", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
            String date = currentDate.format(calFordDate.getTime());

            String saveCurrentDate = String.valueOf(getDateInMillis(date));

            HashMap reply = new HashMap();
            reply.put("name",name);
            reply.put("reply",replyText);
            reply.put("date",saveCurrentDate);
            reply.put("uid",current_user_id);
            reply.put("commentKey",comment_key);
            reply.put("postKey",postKey);

            final String replyKey = Reply.push().getKey();

            Reply.child(replyKey).updateChildren(reply)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                                Toast.makeText(Reply.this, "Reply Added", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Reply.this, "something went wrong. try again!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "d MMMM yyyy, hh:mm aa");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
