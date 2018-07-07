package com.example.eng_mahnoud83coffey.embeatitserver;

import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.MyResponse;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Notification;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Request;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Sender;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Token;
import com.example.eng_mahnoud83coffey.embeatitserver.Remote.ApiService;
import com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder.OrderStatusViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialSpinner spinner;
    //---------------------------
    private FirebaseDatabase db;
    private DatabaseReference request;
    //------------Firebase Ui-------------//
    private Query query;
    private FirebaseRecyclerOptions<Request>options;
    private FirebaseRecyclerAdapter<Request,OrderStatusViewHolder>adapter;
    //-------------------------------------
    private ApiService mService;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        //-------------------------Id-------------------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_OrderStatus);


        //-----------------------Firebase------------------------//
          db=FirebaseDatabase.getInstance();
          request=db.getReference("Requests");


        //Init "Retrofit" Service  Notification
         mService=Common.getFCMClinet();

        //------------------RecyclerView--------------//
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //-------------------Event-----------------

        loadOrderStatus(Common.currentUser.getPhone());//Load All Status


    }


    //Load All Status and show Request into Recylerview
    private void loadOrderStatus(String phone)
    {


        //---Using Firebase UI to populate a RecyclerView--------//
        query= FirebaseDatabase.getInstance()
                .getReference()
                .child("Requests");

        //.orderByChild("phone").equalTo(phone)

        query.keepSynced(true);//Load Data OffLine

        options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request,OrderStatusViewHolder>(options) {
            @Override
            public OrderStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_status_item, parent, false);

                return new OrderStatusViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final OrderStatusViewHolder holder, final int position, final Request model) {
                // Bind the Chat object to the ChatHolder

                //Send Image Name to Recyclerview
                holder.textOrderId.setText(adapter.getRef(position).getKey());
                holder.textOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                holder.textOrderPhone.setText(model.getPhone());
                holder.textOrderAddress.setText(model.getAddress());


                 //New event Button
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                       showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));
                    }
                });


                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        deleteStatusDialog(adapter.getRef(position).getKey());
                    }
                });

                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        Intent orderDetailsActivity= new Intent(OrderStatus.this,OrderDetails.class);
                        Common.currentRequest=model;
                        orderDetailsActivity.putExtra("orderId",adapter.getRef(position).getKey());
                        startActivity(orderDetailsActivity);

                    }
                });

                holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        Intent trackingOrderIntent=new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest=model;
                        startActivity(trackingOrderIntent);

                    }
                });
              /*  //لما المستخدم يضغط على اى صف
                holder.setItemClickListener(new ItemClickListiner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Get CategoryId and send to new Activity

                        if (!isLongClick)
                        {
                                 Intent trackingOrderIntent=new Intent(OrderStatus.this,TrackingOrder.class);
                                            Common.currentRequest=model;
                                            startActivity(trackingOrderIntent);
                        }
                        *//*else
                            {
                                Intent orderDetailsActivity= new Intent(OrderStatus.this,OrderDetails.class);
                                       Common.currentRequest=model;
                                       orderDetailsActivity.putExtra("orderId",adapter.getRef(position).getKey());
                                       startActivity(orderDetailsActivity);
                            }
                          *//*
                    }
                     });*/


            }//end OnBind

        };

        //end Adapter
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }



    //Start Adapter
    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();

    }

    //Stop Adapter
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

  /*
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {

        if (item.getTitle().equals(Common.UPDATE))
        {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteStatusDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

  */
    //Follow Context Menu ==> Update Status
    private void deleteStatusDialog(String key)
    {

        request.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }


    //Follow Context Menu ==> Delete Status
    private void showUpdateDialog(final String key, final Request item)
    {
        AlertDialog.Builder  alertDialog=new AlertDialog.Builder(this);
                             alertDialog.setTitle("Update Order");
                             alertDialog.setMessage("Please Choose Status");


        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.update_order_status_item,null);

        //--------------------Id-------------------------//
        spinner=(MaterialSpinner)view.findViewById(R.id.OrderstatusSpinner_item);


        spinner.setItems("placed","On My Way",",shipped");

        alertDialog.setView(view);

        final String LocalKey=key;

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                request.child(key).setValue(item);
                adapter.notifyDataSetChanged(); //add to update item size

                sendOrderStatusToUser(LocalKey,item);

            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();

    }

     //Send Notification
    private void sendOrderStatusToUser(final String key,Request item)
    {
        final DatabaseReference referenceToken=db.getReference("Tokens");

       referenceToken.orderByKey().equalTo(item.getPhone())
                       .addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                           {
                               for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                               {
                                   Token token=postSnapshot.getValue(Token.class);


                                   //MAKE RAW PAYLOAD
                                   Notification notification=new Notification("BasuonyFood","Your Order"+key+"was Update");
                                   Sender content= new Sender(token.getToken(),notification);

                                   mService.sendNotification(content)
                                                         .enqueue(new Callback<MyResponse>()
                                                         {
                                                             @Override
                                                             public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                                                             {
                                                                   if (response.body().success==1)
                                                                   {
                                                                       Toast.makeText(OrderStatus.this, "Order Was Update", Toast.LENGTH_SHORT).show();
                                                                   }else 
                                                                       {
                                                                           Toast.makeText(OrderStatus.this, "Order Was Update but failed send Notification !", Toast.LENGTH_SHORT).show();
                                                                       }
                                                             }

                                                             @Override
                                                             public void onFailure(Call<MyResponse> call, Throwable t)
                                                             {

                                                                 Log.e("Error", t.getMessage());
                                                             }
                                                         });
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });


    }


}
