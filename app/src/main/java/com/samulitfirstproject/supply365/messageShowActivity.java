package com.samulitfirstproject.supply365;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.samulitfirstproject.supply365.adapter.MessageAdapter;
import com.samulitfirstproject.supply365.model.ChatMsg;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class messageShowActivity extends AppCompatActivity {

    TextView name;
    private EditText editText;
    private ImageView sendMessage, receiverImage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMsg> msgList;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat");
    private DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("ChatList");
    private DatabaseReference databaseReference2,userRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String UserId, receiveId,current_user_dp,current_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_message_show);

        name = (TextView) findViewById(R.id.Name);
        receiverImage = (ImageView) findViewById(R.id.UserImage);
        editText = (EditText) findViewById(R.id.editText);
        sendMessage = (ImageView) findViewById(R.id.send);

        recyclerView = (RecyclerView) findViewById(R.id.RecycleView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.isSmoothScrollbarEnabled();
        recyclerView.setLayoutManager(linearLayoutManager);

        UserId = user.getUid();
        if (UserId.equals(getString(R.string.admin_UID))){
            receiveId = getIntent().getStringExtra("keys");
        }else {
            receiveId = getIntent().getStringExtra("key");
        }

        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("UsersData").child(receiveId);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("userName").getValue(String.class);
                String Image = snapshot.child("userImage").getValue(String.class);

                name.setText(Name);
                if (Image != null && !Image.equals(" ")) {
                    Picasso.get().load(Image).placeholder(R.drawable.loading_gif__).fit().into(receiverImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(UserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                current_user_dp = snapshot.child("userImage").getValue(String.class);
                current_user_name = snapshot.child("userName").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Toast.makeText(getApplicationContext(), receiveId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), UserId, Toast.LENGTH_SHORT).show();

        ReadMessage(UserId, receiveId);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if (msg == null) {
                    editText.setText("");
                }else if (!msg.equals(" ") && !msg.equals("")){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", UserId);
                    hashMap.put("receiver", receiveId);
                    hashMap.put("sms", msg);

                    databaseReference.push().setValue(hashMap);

                    if (UserId.equals(getString(R.string.admin_UID))) {
                        databaseReference3.child(receiveId).child("ID").setValue(receiveId);
                    }else {
                        databaseReference3.child(UserId).child("ID").setValue(UserId);
                    }

                    Calendar calFordDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    String saveCurrentDate = currentDate.format(calFordDate.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    String saveCurrentTime = currentTime.format(calFordDate.getTime());

                    String myDate = saveCurrentDate+" "+saveCurrentTime;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a");
                    Date date = null;
                    try {
                        date = sdf.parse(myDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long millis = date.getTime();

                    databaseReference3.child(UserId).child("currentTime").setValue(millis/1000);

                    FirebaseMessaging.getInstance().subscribeToTopic("MESSAGE");

                    prepareNotification(
                            ""+receiveId,
                            ""+current_user_name+" sent a message",
                            ""+"Let's have a look",
                            "MessageNotification",
                            "MESSAGE"
                    );


                }else {
                    // Nothing happen
                }

                editText.setText("");

            }
        });
    }

    public void ReadMessage(final String myId, final String otherId){
        msgList = new ArrayList<>();

        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Chat");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String receive = ds.child("receiver").getValue(String.class);
                    String sender = ds.child("sender").getValue(String.class);
                    String sms = ds.child("sms").getValue(String.class);
                    ChatMsg chatMsg;

                    try {
                        if (receive == null && sender == null && sms == null) {
                            chatMsg = new ChatMsg(null, null, null);
                        } else {
                            chatMsg = new ChatMsg(receive, sender, sms);
                        }

                        if (chatMsg.getSender().equals(myId) && chatMsg.getReceiver().equals(otherId) || chatMsg.getSender().equals(otherId) && chatMsg.getReceiver().equals(myId)) {
                            msgList.add(chatMsg);
                            messageAdapter = new MessageAdapter(msgList, messageShowActivity.this);
                            recyclerView.setAdapter(messageAdapter);
                        }
                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            notificationBodyJo.put("sender",UserId);
            notificationBodyJo.put("dp",current_user_dp);
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

                        Toast.makeText(messageShowActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
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
}