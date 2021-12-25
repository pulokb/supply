package com.samulitfirstproject.supply365;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.samulitfirstproject.supply365.model.NewsInfo;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class News extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private TextView Post,empty;
    private ImageView back,view_image;
    private Button add_image;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseuser = mAuth.getCurrentUser();
    private DatabaseReference NewsRef;

    private FirebaseStorage firebaseStorage;
    private EditText nText,video;
    private RecyclerView nRecyclerView;
    private ProgressDialog progressDialog;
    private String current_user_id = " ",image_url;
   private FirebaseRecyclerAdapter<NewsInfo, News.ViewHolder> nAdapter = null;
   private LinearLayout editText,l_video;

    StorageReference storageReference;

    StorageTask storageTask;
    Uri contentURI,resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_news);

        nText = findViewById(R.id.et_news);
        video = findViewById(R.id.et_video);
        empty = findViewById(R.id.empty_news);
        Post = findViewById(R.id.tv_news_post);
        editText = findViewById(R.id.ll_news);
        l_video = findViewById(R.id.ll_video);

        add_image = findViewById(R.id.add_image);
        view_image = findViewById(R.id.view_image);

        back = findViewById(R.id.back);


        if (mFirebaseuser != null) {

            current_user_id = mAuth.getCurrentUser().getUid();

        } else {

        }

        if(!current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - string.xml
          Post.setVisibility(View.GONE);
          editText.setVisibility(View.GONE);
          add_image.setVisibility(View.GONE);
          view_image.setVisibility(View.GONE);
          l_video.setVisibility(View.GONE);


        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Post.setVisibility(View.GONE);

        nRecyclerView = findViewById(R.id.news_rv);
        //nRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
        nRecyclerView.setLayoutManager(linearLayoutManager);

        NewsRef = FirebaseDatabase.getInstance().getReference("News");
        firebaseStorage = FirebaseStorage.getInstance();


        nText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {

                if(nText.getText().toString().trim().length()>0){

                    Post.setVisibility(View.VISIBLE);
                }else {
                    Post.setVisibility(View.GONE);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });


        showNews();

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(News.this);
                progressDialog.show();
                progressDialog.setMessage("Posting...");

                SaveChange();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();



                    }
                },1500);




            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            contentURI = data.getData();

            CropImage.activity(contentURI)
                    .start(News.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        view_image.setImageURI(resultUri);

    }


    private void showNews() {


        FirebaseRecyclerOptions<NewsInfo> options =
                new FirebaseRecyclerOptions.Builder<NewsInfo>()
                        .setQuery(NewsRef,NewsInfo.class)
                        .build();



        nAdapter = new FirebaseRecyclerAdapter<NewsInfo, News.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int i, @NonNull final NewsInfo news) {

                if(!current_user_id.equals(getString(R.string.admin_UID))){         //Admin UID - string.xml
                    holder.delete.setVisibility(View.GONE);
                }

                final String key = nAdapter.getRef(i).getKey();

                String t = news.getTime();
                String d = news.getDate();
                String n = news.getNews();



                holder.time.setText(t);
                holder.date.setText(d);
                holder.news.setText(n);

                if (!news.getImage().equals(" ")){
                    Picasso.get().load(news.getImage()).placeholder(R.drawable.loading_icon).fit().centerCrop().into(holder.postimage);
                }

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(News.this);
                        progressDialog.show();
                        progressDialog.setMessage("Deleting...");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressDialog.dismiss();

                                if (!news.getImage().equals(" ")){
                                    StorageReference imageRef = firebaseStorage.getReferenceFromUrl(news.getImage());
                                    imageRef.delete();
                                }
                                NewsRef.child(key).removeValue();
                                notifyDataSetChanged();



                            }
                        },1500);

                    }
                });


                holder.setLikebtnstatus(key,current_user_id);

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        holder.LikeChecker = true;

                        holder.LikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(holder.LikeChecker.equals(true)){

                                    if(dataSnapshot.child(key).hasChild(current_user_id)){

                                        holder.LikeRef.child(key).child(current_user_id).removeValue();
                                        holder.LikeChecker = false;
                                    }
                                    else {

                                        holder.LikeRef.child(key).child(current_user_id).child("uid").setValue(current_user_id);
                                        holder.LikeChecker = false;
                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(v.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



                holder.ComRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            long replyCount = 0;

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                String key = postSnapshot.getKey();

                                if (dataSnapshot.child(key).child("Reply").exists()){

                                    long count = dataSnapshot.child(key).child("Reply").getChildrenCount();
                                    replyCount+=count;
                                }
                            }

                            holder.comcount = dataSnapshot.getChildrenCount();

                            holder.commentCount.setText(String.valueOf(holder.comcount+replyCount));

                        }
                        else {
                            holder.commentCount.setText(String.valueOf(holder.comcount));
                        }



                        if (holder.comcount==0) {

                            holder.commentCount.setVisibility(View.GONE);
                        }
                        else {

                            holder.commentCount.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.cmntbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String comment_key = key;

                        Intent intent = new Intent(v.getContext(), Comments.class);
                        intent.putExtra("comment_key", comment_key);
                        startActivity(intent);
                    }
                });


                getLifecycle().addObserver(holder.youTubePlayerView);

                if (news.getVideo().equals(" ")){
                    holder.youTubePlayerView.setVisibility(View.GONE);
                }else {
                    holder.youTubePlayerView.setVisibility(View.VISIBLE);
                }

                holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        String videoId = news.getVideo();
                        if (!videoId.equals(" ")){
                            youTubePlayer.loadVideo(videoId, 0);
                        }

                    }
                });


                if (mFirebaseuser == null) {

                    holder.like.setVisibility(View.GONE);
                    holder.cmntbtn.setVisibility(View.GONE);

                }

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_item, parent, false);


                return new ViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                // if there are no chat messages, show a view that invites the user to add a message
                empty.setVisibility(
                        nAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }
        };


       nAdapter.startListening();
        nRecyclerView.setAdapter(nAdapter);

    }

    //view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView time,date,news;
        public ImageView delete;

        Boolean LikeChecker = false;
        public CardView like,cmntbtn;
        private ImageView likebtn,postimage;
        public TextView numOfLikes,commentCount;
        DatabaseReference LikeRef,ComRef;
        int countLike;
        private  long comcount = 0;
        YouTubePlayerView youTubePlayerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            news = itemView.findViewById(R.id.news);
            delete = itemView.findViewById(R.id.delete_news);

            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);

            LikeRef = FirebaseDatabase.getInstance().getReference("Likes");
            ComRef = FirebaseDatabase.getInstance().getReference().child("Comment");


            numOfLikes = itemView.findViewById(R.id.numoflikes);
            likebtn = itemView.findViewById(R.id.likebtn);
            like = itemView.findViewById(R.id.cv_like);
            cmntbtn = itemView.findViewById(R.id.cv_comment);
            postimage = itemView.findViewById(R.id.post_image);
            commentCount = itemView.findViewById(R.id.commentCount);

        }

        public void setLikebtnstatus(final String postkey, final String current_user_uid) {

            LikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(postkey).hasChild(current_user_uid)){

                        countLike = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.like);
                        numOfLikes.setText(Integer.toString(countLike));
                    }
                    else {

                        countLike = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.dislike);
                        numOfLikes.setText(Integer.toString(countLike));
                    }

                    if (countLike==0) {
                        numOfLikes.setVisibility(View.GONE);
                    }
                    else {
                        numOfLikes.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    public void SaveChange() {
        if (resultUri!= null) {

            UUID uuid = UUID.randomUUID();
            storageReference = FirebaseStorage.getInstance().getReference().child("NewsPic");
            StorageReference fileReference = storageReference.child(uuid.toString());
            storageTask = fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri uri = urlTask.getResult();
                    image_url = uri.toString();

                    SaveData();




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            image_url = " ";
            SaveData();
        }
    }

    private void SaveData() {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());

        String nn = nText.getText().toString().trim();
        String v = video.getText().toString().trim();

        if (nn.isEmpty()){
            nn = " ";
        }

        if (v.isEmpty()){
            v = " ";
        }else {
            v = v.substring(17);
        }

        Map n = new HashMap();

        n.put("news", nn);
        n.put("video", v);
        n.put("image", image_url);
        n.put("date", saveCurrentDate);
        n.put("time", saveCurrentTime);



        NewsRef.push().updateChildren(n);

        Toast.makeText(News.this, "News Updated", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(News.this,News.class);
        startActivity(intent);

    }


}