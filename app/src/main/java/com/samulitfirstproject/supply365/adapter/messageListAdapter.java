package com.samulitfirstproject.supply365.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.messageShowActivity;
import com.samulitfirstproject.supply365.model.UserDataInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class messageListAdapter extends RecyclerView.Adapter<messageListAdapter.ViewHolder> {

    private DatabaseReference databaseReference;
    private String userName, userNumber, userType, userImage;
    private List<UserDataInfo> infoList;
    private Context context;

    public messageListAdapter(List<UserDataInfo> infoList, Context context) {
        this.infoList = infoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_show_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final UserDataInfo userDataInfo = infoList.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersData");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (Objects.equals(userDataInfo.getReceiveID(), ds.getKey())) {
                                userName = String.valueOf(ds.child("userName").getValue());
                                userNumber = String.valueOf(ds.child("userPhone").getValue());
                                userType = String.valueOf(ds.child("userType").getValue());
                                userImage = String.valueOf(ds.child("userImage").getValue());
                            }
                        }
                        holder.Name.setText(userName);
                holder.Number.setText(userNumber);
                holder.whichType.setText(userType);

                if (userImage != null && !userImage.equals(" ")) {
                    Picasso.get().load(userImage).placeholder(R.drawable.loading_gif__).fit().into(holder.Image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), messageShowActivity.class);
                intent.putExtra("keys", infoList.get(position).getReceiveID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return infoList.size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView Image;
        TextView Name, Number, whichType;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.UserImage);
            Name = itemView.findViewById(R.id.Name);
            Number = itemView.findViewById(R.id.Number);
            whichType = itemView.findViewById(R.id.WhichType);
            linearLayout = itemView.findViewById(R.id.Linear_layout);
        }
    }
}
