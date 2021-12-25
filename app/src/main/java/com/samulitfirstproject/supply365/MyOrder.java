package com.samulitfirstproject.supply365;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.samulitfirstproject.supply365.model.OrderInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyOrder extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference orderRef;
    private ImageView back;
    private TextView empty;
    private FirebaseAuth mAuth;
    private String current_user_id,key;
    FirebaseRecyclerAdapter<OrderInfo, MyOrder.ViewHolder> mAdapter = null;
    private DatabaseReference orderRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        key = getIntent().getStringExtra("person_id");

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        empty = findViewById(R.id.empty_order);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.my_order_rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);mRecyclerView.setLayoutManager(linearLayoutManager);

        if(key.equals("customer")){

            orderRef = FirebaseDatabase.getInstance().getReference("Order");

            FirebaseRecyclerOptions<OrderInfo> options =
                    new FirebaseRecyclerOptions.Builder<OrderInfo>()
                            .setQuery(orderRef.orderByChild("customerUID").equalTo(current_user_id),OrderInfo.class)
                            .build();

            mAdapter = new FirebaseRecyclerAdapter<OrderInfo, MyOrder.ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final MyOrder.ViewHolder holder, final int i, @NonNull OrderInfo order) {

                    final String id = mAdapter.getRef(i).getKey();
                    String date = order.getOrderDate();
                    String time = order.getOrderTime();
                    String status = order.getOrderStatus();



                    holder.orderStatus.setText(status);
                    holder.orderID.setText(id);
                    holder.orderDate.setText(time+" | "+date);


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), OrderDetails.class);
                            intent.putExtra("order_id", id);
                            intent.putExtra("user_type", key);
                            intent.putExtra("from", "my_order");
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public MyOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.order_item, parent, false);

                    return new MyOrder.ViewHolder(view);
                }

                @Override
                public void onDataChanged() {
                    // if there are no chat messages, show a view that invites the user to add a message
                    empty.setVisibility(
                            mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                    );
                }
            };

        }else {

            orderRef = FirebaseDatabase.getInstance().getReference("FinalOrder");
            orderRef2 = FirebaseDatabase.getInstance().getReference("Order");

            FirebaseRecyclerOptions<OrderInfo> options =
                    new FirebaseRecyclerOptions.Builder<OrderInfo>()
                            .setQuery(orderRef.orderByChild("vendorUID").equalTo(current_user_id),OrderInfo.class)
                            .build();

            mAdapter = new FirebaseRecyclerAdapter<OrderInfo, MyOrder.ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final MyOrder.ViewHolder holder, final int i, @NonNull OrderInfo order) {

                    final String id = mAdapter.getRef(i).getKey();
                    String date = order.getOrderDate();
                    String time = order.getOrderTime();
                    String status = order.getOrderStatus();


                    orderRef2.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){

                                String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());

                                holder.orderStatus.setText(orderStatus);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    holder.orderID.setText(id);
                    holder.orderDate.setText(time+" | "+date);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), OrderDetails.class);
                            intent.putExtra("order_id", id);
                            intent.putExtra("user_type", key);
                            intent.putExtra("from", "my_order");
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public MyOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.order_item, parent, false);

                    return new MyOrder.ViewHolder(view);
                }

                @Override
                public void onDataChanged() {
                    // if there are no chat messages, show a view that invites the user to add a message
                    empty.setVisibility(
                            mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                    );
                }
            };
        }


        mAdapter.startListening();
        mRecyclerView.setAdapter(mAdapter);
    }


    //view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView orderID,orderDate,orderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderID = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);

        }
    }
}