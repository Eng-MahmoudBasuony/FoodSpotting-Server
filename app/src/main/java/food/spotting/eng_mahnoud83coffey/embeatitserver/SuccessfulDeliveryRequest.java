package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.SuccessfulRequest;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.SuccessfulDeliveryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//فى حاله ان الShipper اللى وصل الطلب بنجاح هيجى ال
public class SuccessfulDeliveryRequest extends AppCompatActivity
{

    private FloatingActionButton fabRemoveAllItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    public DatabaseReference successfulRequestTable;

    private FirebaseRecyclerAdapter<SuccessfulRequest,SuccessfulDeliveryViewHolder> adapter;
    private FirebaseRecyclerOptions<SuccessfulRequest> allSuccessfulRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_delivery_request);

            iniView();

        //--------intiFirebase-------------//
        database=FirebaseDatabase.getInstance();
        successfulRequestTable=database.getReference(Common.SUCCESSFUL_RREQUEST_TO_CLIENT);




        //-----------Event--------------//
     fabRemoveAllItems.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view)
         {

             final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(SuccessfulDeliveryRequest.this);
             alertBuilder.setTitle("Remove AlL");
             alertBuilder.setMessage("Are you sure from Delete All Items ");
             alertBuilder.setIcon(R.drawable.ic_info_black_24dp);

             alertBuilder.setPositiveButton("sure", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i)
                 {
                     dialogInterface.dismiss();

                     successfulRequestTable.removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
                     {
                         @Override
                         public void onSuccess(Void aVoid)
                         {
                             Toast.makeText(SuccessfulDeliveryRequest.this, "Success!!", Toast.LENGTH_SHORT).show();
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e)
                         {

                         }
                     });
                 }
             });


             alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i)
                 {
                 dialogInterface.dismiss();
                 }
             });

             alertBuilder.show();

         }
     });

       //-------------Load All Orders which done send Successful Request To Client---//
        loadAllOrdersSendToClint();


    }


    private void iniView()
    {
        fabRemoveAllItems=(FloatingActionButton)findViewById(R.id.fab_SuccessfulDeliveryRequest);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_SuccessfulDeliveryRequest);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


    }


    private void loadAllOrdersSendToClint()
    {
        allSuccessfulRequest=new FirebaseRecyclerOptions.Builder<SuccessfulRequest>()
                .setQuery(successfulRequestTable,SuccessfulRequest.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<SuccessfulRequest, SuccessfulDeliveryViewHolder>(allSuccessfulRequest) {
            @Override
            protected void onBindViewHolder(@NonNull SuccessfulDeliveryViewHolder holder, final int position, @NonNull SuccessfulRequest model)
            {

                holder.shipperName.setText(model.getNameShipper());
                holder.shipperPhone.setText(model.getPhoneShipper());
                holder.shipperDateRequestSend.setText(Common.getData(Long.parseLong(adapter.getRef(position).getKey())));//or model.getDtaRequestShipper();
                holder.feedBackClint.setText(model.getCommentClientForShipper());
                holder.ratingBarShipper.setRating(Float.parseFloat(model.getRatingClientforShipper()));

                holder.idOrder.setText(model.getOrderId());
                holder.clintName.setText(model.getClientName());
                holder.clintAddress.setText(model.getAddressClient());
                holder.paymentMode.setText(model.getPaymentMode());
                holder.totalPrice.setText(model.getTotalPrice());
                holder.stateOrder.setText(model.getStateOrder());
                holder.clintDateRequest.setText(Common.getData(Long.parseLong(model.getDateRequestClient())));
                holder.clintPhone.setText(model.getClientPhone());

                holder.btnRemoveitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        successfulRequestTable.child(adapter.getRef(position).getKey()).removeValue()
                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid)
                                                  {
                                                      Toast.makeText(SuccessfulDeliveryRequest.this, "Success Remove", Toast.LENGTH_SHORT).show();
                                                      adapter.notifyDataSetChanged();

                                                  }
                                                   })
                                              .addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                             public void onFailure(@NonNull Exception e)
                                                 {
                                                     Toast.makeText(SuccessfulDeliveryRequest.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                 }
                                                 });


                    }
                });

            }

            @NonNull
            @Override
            public SuccessfulDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.successful_delivery_request_item,parent,false);

                return new SuccessfulDeliveryViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onStart()
    {
            adapter.startListening();
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        adapter.stopListening();

        super.onStop();
    }
}

