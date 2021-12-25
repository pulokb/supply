package com.samulitfirstproject.supply365.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samulitfirstproject.supply365.R;
import com.samulitfirstproject.supply365.model.ChatMsg;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<ChatMsg> infoList;
    public Context context;
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(List<ChatMsg> infoList, Context context) {
        this.infoList = infoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_sms, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_sms, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatMsg chatMsg = infoList.get(position);

        holder.message.setText(chatMsg.getSms());
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

        TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message_body);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String UserID = user.getUid();
        if (infoList.get(position).getSender().equals(UserID)){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
