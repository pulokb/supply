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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransferOrder extends AppCompatActivity {

    private RecyclerView oRecyclerView;
    private DatabaseReference orderRef,orderRef2;
    private ImageView back;
    private TextView empty;
    private String orderid ;
    FirebaseRecyclerAdapter<OrderInfo, TransferOrder.ViewHolder> oAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_transfer_order);

        empty = findViewById(R.id.empty_order);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        oRecyclerView = findViewById(R.id.final_order_rv);
        oRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        oRecyclerView.setLayoutManager(linearLayoutManager);


        orderRef = FirebaseDatabase.getInstance().getReference("FinalOrder");
        orderRef2 = FirebaseDatabase.getInstance().getReference("Order");



        FirebaseRecyclerOptions<OrderInfo> options =
                new FirebaseRecyclerOptions.Builder<OrderInfo>()
                        .setQuery(orderRef,OrderInfo.class)
                        .build();



        oAdapter = new FirebaseRecyclerAdapter<OrderInfo, TransferOrder.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TransferOrder.ViewHolder holder, final int i, @NonNull OrderInfo order) {

                final String id = oAdapter.getRef(i).getKey();
                String date = order.getOrderDate();
                String time = order.getOrderTime();
                String status = order.getOrderStatus();




                holder.orderID.setText(id);
                holder.orderDate.setText(time+" | "+date);



                orderRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        orderid = String.valueOf(snapshot.child("customerOrderID").getValue());


                        orderRef2.child(orderid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String orderStatus = String.valueOf(snapshot.child("orderStatus").getValue());

                                holder.orderStatus.setText(orderStatus);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), TransferDetails.class);
                        intent.putExtra("order_id", id);
                        intent.putExtra("cOrder_id", orderid);
                        intent.putExtra("order_status", holder.orderStatus.getText().toString());
                        startActivity(intent);
                    }
                });



            }

            @NonNull
            @Override
            public TransferOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_item, parent, false);

                return new TransferOrder.ViewHolder(view);
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