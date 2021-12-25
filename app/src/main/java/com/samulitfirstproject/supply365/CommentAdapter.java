package com.samulitfirstproject.supply365;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.NewsViewHolder> {

    private Context nContext;
    private ArrayList<CommentInfo> cList;
    private OnItemClickListener cListener;
    private static String mdate;
    private String current_user_id,current_user_dp,name;
    private ProgressDialog progressDialog;
    private static int REFRESH = 700;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final int YEAR_MILLIS = 52 * WEEK_MILLIS;


    private static long currentDate() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
        String date = currentDate.format(calFordDate.getTime());
        return getDateInMillis(date);
    }

    public static String getTimeAgo(String date) {
        long time = Long.parseLong(date);
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1m";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "1h";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1d";
        } else if (diff < 168 * HOUR_MILLIS) {
            return diff / DAY_MILLIS + "d";
        }else if (diff <= 52 * DateUtils.WEEK_IN_MILLIS) {
            int week = (int)(diff / (DateUtils.WEEK_IN_MILLIS));
            return  week>1?week+"w":week+"w";
        }else {
            int year = (int) (diff/ DateUtils.YEAR_IN_MILLIS);
            return year>1?year+"y":year+"y";
        }
    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");

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

    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public CommentAdapter(Context context, ArrayList<CommentInfo> cLists) {
        nContext = context;
        cList = cLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cv = LayoutInflater.from(nContext).inflate(R.layout.show_comment, parent, false);
        return new NewsViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        final CommentInfo info = cList.get(position);

        final String comment_key = info.getCoomentKey().toString();
        final String post_comment_key = info.getCommentPostKey().toString();


        final String uid = info.getUid().toString();

        mdate = info.getDate();



        holder.date.setText(getTimeAgo(info.getDate()));

        holder.setLikebtnstatus(comment_key);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                holder.LikeChecker = true;

                holder.LikeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(holder.LikeChecker.equals(true)){

                            if(dataSnapshot.child(comment_key).hasChild(current_user_id)){

                                holder.LikeRef.child(comment_key).child(current_user_id).removeValue();
                                holder.LikeChecker = false;
                            }
                            else {


                                holder.LikeRef.child(comment_key).child(current_user_id).setValue(true);
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

        holder.dbName.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("userName").getValue().toString();

                    holder.fullname.setText(userName );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        holder.dbName.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String url = dataSnapshot.child("userImage").getValue(String.class);

                    if (!url.equals(" ")){
                        Picasso.get().load(url).placeholder(R.drawable.loading_icon).fit().centerCrop().into(holder.profileimage);
                    }

                    name = userName;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        holder.CommentRef.child(post_comment_key).child(comment_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    String des = dataSnapshot.child("comment").getValue(String.class);
                    holder.d_uid = dataSnapshot.child("uid").getValue(String.class);

                    if (holder.d_uid.equals(current_user_id)){

                        holder.deletebtn.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.deletebtn.setVisibility(View.GONE);
                    }

                    holder.comment.setText(des);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        holder.replybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentKey = comment_key;

                Intent intent = new Intent(v.getContext(), Reply.class);
                intent.putExtra("postKey",post_comment_key);
                intent.putExtra("comment_key", commentKey);
                nContext.startActivity(intent);
            }
        });


        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(nContext);
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Deleting");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.CommentRef.child(post_comment_key).child(comment_key).removeValue();
                        holder.LikeRef.child(comment_key).removeValue();

                        cList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                        progressDialog.dismiss();

                    }
                },REFRESH);


            }
        });

        holder.ReplyRef.child(post_comment_key).child(comment_key).child("Reply").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    holder.replyCount = dataSnapshot.getChildrenCount();

                    holder.countReply.setText(String.valueOf(holder.replyCount));

                }
                else {
                    holder.countReply.setText(String.valueOf(holder.replyCount));
                }

                if (holder.replyCount==0) {

                    holder.countReply.setVisibility(View.GONE);
                }
                else {
                    holder.countReply.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return cList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        Boolean LikeChecker = false;
        public CardView like,replybtn,deletebtn;
        private ImageView likebtn;
        public CircleImageView profileimage;
        public TextView date, comment, fullname,numOfLikes,countReply;
        DatabaseReference LikeRef,LikeRef2,dbName,CommentRef,ReplyRef;
        FirebaseAuth mAuth;
        String d_uid;
        int countLike;
        long replyCount = 0;


        public NewsViewHolder(View itemView) {
            super(itemView);

            dbName =FirebaseDatabase.getInstance().getReference().child("UsersData");
            LikeRef = FirebaseDatabase.getInstance().getReference("CommentLikes");
            CommentRef =FirebaseDatabase.getInstance().getReference().child("Comment");

            ReplyRef =FirebaseDatabase.getInstance().getReference().child("Comment");
            LikeRef2 = FirebaseDatabase.getInstance().getReference("ReplyLikes");

            mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();
            numOfLikes = itemView.findViewById(R.id.numofclikes);
            likebtn = itemView.findViewById(R.id.comment_likebtn);
            like = itemView.findViewById(R.id.cv_comment_like);
            fullname = itemView.findViewById(R.id.tv_comment_name);
            date = itemView.findViewById(R.id.tv_comment_date);
            comment = itemView.findViewById(R.id.tv_comment);
            profileimage = itemView.findViewById(R.id.c_pic);
            deletebtn = itemView.findViewById(R.id.cv_delete);
            replybtn = itemView.findViewById(R.id.cv_reply);
            countReply = itemView.findViewById(R.id.noOfReply);


            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }
        @Override
        public void onClick(View v) {
            if (cListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cListener.onItemcClick(position);
                }
            }
        }


        public void setLikebtnstatus(final String comment_key) {

            LikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(comment_key).hasChild(current_user_id)){

                        countLike = (int) dataSnapshot.child(comment_key).getChildrenCount();
                        likebtn.setImageResource(R.drawable.like);
                        numOfLikes.setText(Integer.toString(countLike));
                    }
                    else {

                        countLike = (int) dataSnapshot.child(comment_key).getChildrenCount();
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

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }


    public interface OnItemClickListener {
        void onItemcClick(int position);

    }
}
