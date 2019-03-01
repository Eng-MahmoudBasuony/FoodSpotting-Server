package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
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
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Shipper;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.ShippersManagementViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ShippersManagement extends AppCompatActivity {

    private FloatingActionButton fabAddShipper;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    private FirebaseDatabase database;
    private DatabaseReference shippersTable;


    private FirebaseRecyclerAdapter<Shipper,ShippersManagementViewHolder>adapter;
    private FirebaseRecyclerOptions<Shipper> allShippers;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shippers_management);

        //-----------------Id-----------------------//
        fabAddShipper=(FloatingActionButton)findViewById(R.id.fab_shippers);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_shippers_management);


        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //-----ini
        database=FirebaseDatabase.getInstance();
        shippersTable= database.getReference(Common.SHIPPER_TABLE);


        //-------------------Event
        fabAddShipper.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                  showCreateShipperLayout();
            }
        });



        //-------Load All Shippers
        loadAllShippers();
    }

    private void loadAllShippers()
    {
         allShippers=new FirebaseRecyclerOptions.Builder<Shipper>()
                                        .setQuery(shippersTable,Shipper.class)
                                        .build();

         adapter=new FirebaseRecyclerAdapter<Shipper, ShippersManagementViewHolder>(allShippers) {
             @Override
             protected void onBindViewHolder(@NonNull ShippersManagementViewHolder holder, final int position, @NonNull final Shipper model)
             {
                 holder.shipperPhone.setText(model.getPhone());
                 holder.shipperName.setText(model.getName());


                 holder.btnEditShipper.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view)
                     {
                         showEditDialog(adapter.getRef(position).getKey(),model);

                     }
                 });

                 holder.btnRemoveShipper.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view)
                     {
                         removeShipper(adapter.getRef(position).getKey());
                     }
                 });

             }

             @NonNull
             @Override
             public ShippersManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                 View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shippers_management,parent,false);

                 return new ShippersManagementViewHolder(view);
             }
         };

         adapter.startListening();
         recyclerView.setAdapter(adapter);
         adapter.notifyDataSetChanged();

    }

    private void removeShipper(String key)
    {
        shippersTable.child(key)
                     .removeValue()
                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid)
                         {
                             Toast.makeText(ShippersManagement.this, "Shipper Remove Success", Toast.LENGTH_SHORT).show();
                         }
                     })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(ShippersManagement.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showEditDialog(String key,Shipper model)
    {
        AlertDialog.Builder  alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Shipper");
        // alertDialog.setMessage("Please Choose Status");


        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.dialog_create_shipper,null);

        //--------------------Id-------------------------//
        final TextInputEditText editName=(TextInputEditText)view.findViewById(R.id.dialog_name_shipper);
        final TextInputEditText editPhone=(TextInputEditText)view.findViewById(R.id.dialog_phone_shipper);
        final TextInputEditText editPassword=(TextInputEditText)view.findViewById(R.id.dialog_password_shipper);

        //set Data
        editName.setText(model.getName());
        editPassword.setText(model.getPassword());
        editPhone.setText(model.getPhone());


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_shipping_black_24dp);


        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                Map<String,Object>update=new HashMap<>();
                                  update.put("name",editName.getText().toString());
                                  update.put("phone",editPhone.getText().toString());
                                  update.put("password",editPassword.getText().toString());




                shippersTable.child(editPhone.getText().toString())
                        .updateChildren(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(ShippersManagement.this, "Shipper Update!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(ShippersManagement.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();


    }


    private void showCreateShipperLayout()
    {
        AlertDialog.Builder  alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Create Shipper");
       // alertDialog.setMessage("Please Choose Status");


        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.dialog_create_shipper,null);

        //--------------------Id-------------------------//
        final TextInputEditText editName=(TextInputEditText)view.findViewById(R.id.dialog_name_shipper);
        final TextInputEditText editPhone=(TextInputEditText)view.findViewById(R.id.dialog_phone_shipper);
        final TextInputEditText editPassword=(TextInputEditText)view.findViewById(R.id.dialog_password_shipper);

       alertDialog.setView(view);
       alertDialog.setIcon(R.drawable.ic_local_shipping_black_24dp);


        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                Shipper shipper=new Shipper();
                        shipper.setName(editName.getText().toString());
                        shipper.setPhone(editPhone.getText().toString());
                        shipper.setPassword(editPassword.getText().toString());

                shippersTable.child(editPhone.getText().toString())
                             .setValue(shipper)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid)
                                 {
                                     Toast.makeText(ShippersManagement.this, "Shipper Create!", Toast.LENGTH_SHORT).show();
                                     adapter.notifyDataSetChanged();

                                 }
                             })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(ShippersManagement.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();


    }


    @Override
    protected void onStop()
    {
        super.onStop();
       //if (adapter!=null)
       // adapter.stopListening();
    }


}
