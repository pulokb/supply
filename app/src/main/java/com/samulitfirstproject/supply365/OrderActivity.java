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

import com.samulitfirstproject.supply365.model.OrderInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView oRecyclerView;
    private DatabaseReference orderRef;
    private ImageView back;
    private TextView empty;
    FirebaseRecyclerAdapter<OrderInfo, OrderActivity.ViewHolder> oAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        empty = findViewById(R.id.empty_order);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        oRecyclerView = findViewById(R.id.admin_order_rv);
        oRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        oRecyclerView.setLayoutManager(linearLayoutManager);

        orderRef = FirebaseDatabase.getInstance().getReference("Order");

        FirebaseRecyclerOptions<OrderInfo> options =
                new FirebaseRecyclerOptions.Builder<OrderInfo>()
                        .setQuery(orderRef,OrderInfo.class)
                        .build();



        oAdapter = new FirebaseRecyclerAdapter<OrderInfo, OrderActivity.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderActivity.ViewHolder holder, final int i, @NonNull OrderInfo order) {

                final String id = oAdapter.getRef(i).getKey();
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
                        intent.putExtra("user_type", "admin");
                        intent.putExtra("from", "my_order");
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public OrderActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_item, parent, false);

                return new OrderActivity.ViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                // if there are no chat messages, show a view that invites the user to add a message
                empty.setVisibility(
                        oAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }
        };


        oAdapter.startListening();
        oRecyclerView.setAdapter(oAdapter);

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