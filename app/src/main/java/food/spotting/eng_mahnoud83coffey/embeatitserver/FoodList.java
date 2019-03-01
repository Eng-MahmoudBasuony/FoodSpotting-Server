package food.spotting.eng_mahnoud83coffey.embeatitserver;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Food;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.UUID;

public class FoodList extends AppCompatActivity {



   private RecyclerView recyclerView;
   private RecyclerView.LayoutManager layoutManager;
   private FloatingActionButton FAB;
   private RelativeLayout rootLayoutFoodList;
   //-----------View Dialog--------
   private TextInputEditText editName;
   private TextInputEditText editDescription;
   private TextInputEditText editDiscount;
   private TextInputEditText editPrice;
   private Button btnSelect;
   private Button btnUpload;
   private Dialog dialogAddNewFood;
   //-----------Update-----------
   private TextInputEditText editNameUpdate;
    private TextInputEditText editDescriptionUpdate;
    private TextInputEditText editDiscountUpdate;
    private TextInputEditText editPriceUpdate;
    private Button btnSelectUpdate;
    private Button btnUploadUpdate;
    private Dialog dialogUpdateFood;
   //------------------------------
   private FirebaseDatabase database;
   private DatabaseReference databaseReferenceFoodList;
   private FirebaseStorage storage;
   private StorageReference storageReferenceFoodList;
   //------------------------------
    private  Query query;
    private FirebaseRecyclerOptions<Food>options;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder>adapter;
   //------------------------------
    private String categoryId;
    private Uri saveUri;
    private Food newFood;
    private ProgressDialog mProgressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //--------------Init Id--------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_foodList);
        FAB=(FloatingActionButton)findViewById(R.id.fab_foodlist);
        mProgressDialog=new ProgressDialog(this);
        rootLayoutFoodList=(RelativeLayout)findViewById(R.id.rootLayout_foodList);

        //----------------Firebase-------------------//
        database=FirebaseDatabase.getInstance();
        databaseReferenceFoodList=database.getReference("Foods");
        storage=FirebaseStorage.getInstance();
        storageReferenceFoodList=storage.getReference();



        //--------RecyclerView---------------//
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //------Get Id Category-------//
        if (getIntent()!=null)
        {
            categoryId=getIntent().getStringExtra("CategoryId");

            if (!categoryId.isEmpty() && categoryId !=null)
            {
                    loadFoodList(categoryId);
            }
        }




        //----------------------Event-------------------------//

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showAddFoodDialog();
            }
        });


    }


    //Method load Foods Table From Firebase DataBase send To Recyclerview
    private void loadFoodList(String categoryId)
    {

        Query listFoodsByCategory=databaseReferenceFoodList.orderByChild("menuId").equalTo(categoryId);

        //query=FirebaseDatabase.getInstance().getReference().child("Foods");

         options=new  FirebaseRecyclerOptions.Builder<Food>()
                       .setQuery(listFoodsByCategory,Food.class)
                        .build();
         adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model)
             {

                 holder.textViewFoodList.setText(model.getName());
                 Picasso.get()
                         .load(model.getImage())
                         .into(holder.imageViewFoodList);




                 holder.setItemClickListiner(new ItemClickListiner() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick)
                     {

                     }
                 });

             }

             @NonNull
             @Override
             public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                 View v=LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.food_list_item,parent,false);


                 return new FoodViewHolder(v);
             }
         };


         adapter.notifyDataSetChanged();
         recyclerView.setAdapter(adapter);

    }

    //Shaw Dialog Add new Food
    private void showAddFoodDialog()
    {

        dialogAddNewFood=new Dialog(this);
        dialogAddNewFood.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAddNewFood.setContentView(R.layout.add_new_foodlist_dialog);

      //-------------------Id--------------------//
        editName=(TextInputEditText)dialogAddNewFood.findViewById(R.id.editnameDialog_addFoods);
        editDescription=(TextInputEditText)dialogAddNewFood.findViewById(R.id.editDescriptionDialog_addFoods);
        editDiscount=(TextInputEditText)dialogAddNewFood.findViewById(R.id.editDiscountDialog_addFoods);
        editPrice=(TextInputEditText)dialogAddNewFood.findViewById(R.id.editPriceDialog_addFoods);
        btnSelect=(Button)dialogAddNewFood.findViewById(R.id.btnDialog_Select_Foodlist);
        btnUpload=(Button)dialogAddNewFood.findViewById(R.id.btnDialog_Uplaod_Foodlist);
        ImageView btnCloase=(ImageView)dialogAddNewFood.findViewById(R.id.btn_cloase_addfoodlist);



        //----------------Event Button-----------------//

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                    chooseImage();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    uplaodImage();
            }
        });


        btnCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogAddNewFood.dismiss();
            }
        });

        dialogAddNewFood.show();
    }

    //Method Choose Image From Gallery
    private void chooseImage()
    {
        Intent intent=new Intent();

               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent,"Select Image"), Common.PICK_IMAGE_REQUESTO);

    }

    //Upload New Food
    private void uplaodImage()
    {


        if (saveUri !=null)
        {
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();


            String imageName= UUID.randomUUID().toString();


             final StorageReference imageFolder = storageReferenceFoodList.child("images/"+imageName);

             imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                mProgressDialog.dismiss();
                                Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();

                               imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri)
                                   {
                                       newFood=new Food();

                                       newFood.setName(editName.getText().toString());
                                       newFood.setDescription(editDescription.getText().toString());
                                       newFood.setDiscount(editDiscount.getText().toString());
                                       newFood.setPrice(editPrice.getText().toString());
                                       newFood.setImage(uri.toString());
                                       newFood.setMenuId(categoryId);

                                       if(newFood !=null)
                                       {
                                           databaseReferenceFoodList.push().setValue(newFood);
                                           dialogAddNewFood.dismiss();
                                           Snackbar.make(rootLayoutFoodList,"new Food "+newFood.getName()+"was add ",Snackbar.LENGTH_LONG).show();
                                       }

                                   }
                               });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e)
                 {

                     mProgressDialog.dismiss();
                     Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                 {
                     //Do not worry about this error
                     double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                     mProgressDialog.setMessage("Uploaded"+progress+"%");


                 }
             });

        }
    }

    //-----------------------Context Menu---------------------//
    //Main Method select item from Context Menu
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {


        if(item.getTitle().equals(Common.UPDATE))
        {

            //Method Update Product
          showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));



        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            //Method Delete  Product
            deleteCategoryDialog(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }
    //Shaw Update Dialog
    private void showUpdateFoodDialog(final String key, final Food item)
    {

          dialogUpdateFood=new Dialog(this);
        dialogUpdateFood.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogUpdateFood.setContentView(R.layout.update_food_list);

             //-------------------Id------------------//
            editNameUpdate=(TextInputEditText)dialogUpdateFood.findViewById(R.id.editnameDialog_addFoods_update);
            editDescriptionUpdate=(TextInputEditText)dialogUpdateFood.findViewById(R.id.editDescriptionDialog_addFoods_update);
            editPriceUpdate=(TextInputEditText)dialogUpdateFood.findViewById(R.id.editPriceDialog_addFoods_update);
            editDiscountUpdate=(TextInputEditText)dialogUpdateFood.findViewById(R.id.editDiscountDialog_addFoods_update);
            btnSelectUpdate=(Button)dialogUpdateFood.findViewById(R.id.btnDialog_Select_Foodlist_update);
            btnUploadUpdate=(Button)dialogUpdateFood.findViewById(R.id.btnDialog_Uplaod_Foodlist_update);
            ImageView btnCloase=(ImageView)dialogUpdateFood.findViewById(R.id.btn_cloase_addfoodlist_update);

        //set Defaulet Value
        editNameUpdate.setText(item.getName());
        editDescriptionUpdate.setText(item.getDescription());
        editDiscountUpdate.setText(item.getDiscount());
        editPriceUpdate.setText(item.getPrice());

            //-------------Event --------------------//
        btnSelectUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
              chooseImageItem();
            }
        });

        btnUploadUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changeImageItem(item,key);
            }
        });

        btnCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogUpdateFood.dismiss();
            }
        });

        dialogUpdateFood.show();
    }
   //Update Food
  private void changeImageItem(final Food item, final String key)
    {

        if (saveUri !=null)
        {

            mProgressDialog =new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            String imageName= UUID.randomUUID().toString(); //Generate Random Name

            final StorageReference imageFoldar=storageReferenceFoodList.child("images/"+imageName);// Create Name For Folder in Firebase Storage

            imageFoldar.putFile(saveUri)//Save Image IN Firebase Storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                           mProgressDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();

                            imageFoldar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() // Download  Uri Image From Firebase Storage
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    //SET  Value newFood if image Uploaded can get download Link

                                    item.setImage(uri.toString()); // send Uri for Model Food
                                    item.setName(editNameUpdate.getText().toString());
                                    item.setDescription(editDescriptionUpdate.getText().toString());
                                    item.setDiscount(editDiscountUpdate.getText().toString());
                                    item.setPrice(editPriceUpdate.getText().toString());

                                    databaseReferenceFoodList.child(key).setValue(item);
                                    Snackbar.make(rootLayoutFoodList,"Food "+item.getName()+"was Edit",Snackbar.LENGTH_LONG).show();
                                    dialogUpdateFood.dismiss();
                                    saveUri=null;
                                }
                            });


                        } // Failure
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {

                    mProgressDialog.dismiss();
                    Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveUri=null;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                {
                    //Do not worry about this error
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                     mProgressDialog.setMessage("Uploaded"+progress+"%");
                     saveUri=null;
                }
            });


        }

    }

    //Method Choose Image From Gallery
    private void chooseImageItem()
    {
        Intent intent=new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Update Image"), 47855);

    }


    // Follow Context Menu Method Delete Item use Key Item
    private void deleteCategoryDialog(String key)
    {
        databaseReferenceFoodList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted !!!", Toast.LENGTH_SHORT).show();

        //--If exist in Table "Banner" it Remove
        DatabaseReference banner=database.getReference(Common.BANNER);
        Query foodInBanner= banner.orderByChild("id").equalTo(key);
        foodInBanner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    //Remove From Data equal Sam MenuId
                    snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(FoodList.this, "Success Remove From Banner", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //---------------------------------------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==Common.PICK_IMAGE_REQUESTO&&resultCode==RESULT_OK && data !=null &&data.getData()!=null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected");
        }

        if (requestCode==47855&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            saveUri=data.getData();
            btnSelectUpdate.setText("Image Selected");
        }



    }


    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();

    }
    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }


}
