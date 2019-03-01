package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Banner;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.BannerViewHolder;
import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.CustomAdapterSpinnerBanner;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BannerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton FAB;
    private RelativeLayout rootLayoutFoodList;
    private ProgressDialog mProgressDialog;
    //----------------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference databaseReferenceBanner;
    private DatabaseReference databaseReferenceFoods;
    private FirebaseStorage storage;
    private StorageReference storageReferenceBanner;
    //------------------------------
    private Query query;
    private FirebaseRecyclerOptions<Banner> options;
    private FirebaseRecyclerAdapter<Banner,BannerViewHolder> adapter;
    //-----------View Dialog Add Banner--------
    private Spinner spinnerBanner;
    private Button btnAddBanner;
    private ImageView btnCloase;
    private TextView textFoodId;
    private TextView textFoodName;

  private static String nameFoodS=null;
  private static String imageFoodS=null;
  private static String idFoodS=null;
  private static String menuIdS=null;
    private Banner newBanner;
    //------------------------
    private Uri saveUri;

    private CustomAdapterSpinnerBanner adapterSpinnerBanner;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);


        //--------------------Id--------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_Banner);
        FAB=(FloatingActionButton)findViewById(R.id.fab_BannerActivity);
        rootLayoutFoodList=(RelativeLayout)findViewById(R.id.rootLayout_banner);
        mProgressDialog=new ProgressDialog(this);

        //----------------Firebase-------------------//
        database=FirebaseDatabase.getInstance();
        databaseReferenceBanner=database.getReference(Common.BANNER);
        databaseReferenceFoods=database.getReference(Common.FOODS);
        storage=FirebaseStorage.getInstance();
        storageReferenceBanner=storage.getReference();

        //--------RecyclerView---------------//
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //----------------------Event-------------------------//

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showAddBanner();
            }
        });

        //Load List Banner
        loadBannerList();
    }


    private void loadBannerList()
    {

        options=new  FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(databaseReferenceBanner,Banner.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model)
            {

                holder.textViewBanner.setText(model.getName());

                Picasso.get()
                        .load(model.getImage())
                        .into(holder.imageViewBanner);




                holder.setItemClickListiner(new ItemClickListiner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {

                    }
                });

            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_banner_activity,parent,false);


                return new BannerViewHolder(v);
            }
        };


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    private void showAddBanner()
    {

        final Dialog dialogAddBanner=new Dialog(this);
               dialogAddBanner.requestWindowFeature(Window.FEATURE_NO_TITLE);
               dialogAddBanner.setContentView(R.layout.dialog_add_banner);

        //-------------------Id--------------------//
        btnAddBanner=(Button)dialogAddBanner.findViewById(R.id.btnDialog_add_banner);
        btnCloase=(ImageView)dialogAddBanner.findViewById(R.id.btn_cloase_addBanner);
        textFoodId=(TextView)dialogAddBanner.findViewById(R.id.food_id_banner);
        textFoodName=(TextView)dialogAddBanner.findViewById(R.id.food_name_banner) ;
        spinnerBanner=(Spinner)dialogAddBanner.findViewById(R.id.spinner_banner_item);




        //---------Load All Foods From Firebase-------//
        final List<String> nameFoodList=new ArrayList<>();
        final List<String>imageFoodList=new ArrayList<>();
        final List<String>idFoodLis=new ArrayList<>();
        final List<String>menuIdCategory=new ArrayList<>();

        FirebaseDatabase.getInstance().getReference(Common.FOODS)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                 nameFoodList.clear();
                                                 imageFoodList.clear();
                                                 idFoodLis.clear();
                                                 menuIdCategory.clear();

                                                for (DataSnapshot snapshotFoods:dataSnapshot.getChildren())
                                                {
                                                    nameFoodList.add(String.valueOf(snapshotFoods.child("name").getValue()));
                                                    imageFoodList.add(String.valueOf(snapshotFoods.child("image").getValue()));
                                                    menuIdCategory.add(String.valueOf(snapshotFoods.child("menuId").getValue()));
                                                    idFoodLis.add(snapshotFoods.getKey()); //Id Food

                                                }

                                                if (nameFoodList.size()>0)
                                                {
                                                    adapterSpinnerBanner=new CustomAdapterSpinnerBanner(BannerActivity.this,imageFoodList,nameFoodList);
                                                    spinnerBanner.setAdapter(adapterSpinnerBanner);
                                                }

                                             }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {

                                            }
                                        });
    //---------------------------------------------------------------------//


        spinnerBanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                                 nameFoodS=nameFoodList.get(position);
                                 imageFoodS=imageFoodList.get(position);
                                 idFoodS=idFoodLis.get(position);
                                 menuIdS=menuIdCategory.get(position);

                                 textFoodId.setText(menuIdS);
                                 textFoodName.setText(nameFoodS);

                            newBanner=new Banner(imageFoodS,nameFoodS,idFoodS,menuIdS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                     newBanner=null;

            }
        });



        //----------------Event Button-----------------//

        btnAddBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(newBanner !=null)
                {
                    mProgressDialog.setMessage("Uploading...");
                    mProgressDialog.show();

                    databaseReferenceBanner.child(idFoodS).setValue(newBanner);

                    mProgressDialog.dismiss();
                    Snackbar.make(rootLayoutFoodList,"new Banner "+newBanner.getName()+"was add ",Snackbar.LENGTH_LONG).show();
                    dialogAddBanner.dismiss();
                }else
                {
                    Snackbar.make(rootLayoutFoodList,"Sorry no download, there is a problem ",Snackbar.LENGTH_LONG).show();

                }

            }
        });


        btnCloase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogAddBanner.dismiss();
            }
        });


        dialogAddBanner.show();
    }


/*    //Method Save Image in Firebase Storage and Send (Name ,Description,Discount,Price,UriImage,MenuId) into Model "Food"
    private void uplaodImage()
    {

        if (saveUri !=null)
        {
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();


            String imageName= UUID.randomUUID().toString();


            final StorageReference imageFolder = storageReferenceBanner.child("images/"+imageName);

            imageFolder.putFile(saveUri) //Upload Image
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {

                                    newBanner=new Banner();

                                    newBanner.setId(editId.getText().toString());
                                    newBanner.setName(editName.getText().toString());
                                    newBanner.setImage(uri.toString());

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {

                    mProgressDialog.dismiss();
                    Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    */
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




    //Main Method select item from Context Menu
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {

      /*  if(item.getTitle().equals(Common.UPDATE))
        {
            //Method Update Product
          showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));



        }*/
        if (item.getTitle().equals(Common.DELETE))
        {
            //Method Delete  Product
            deleteCategoryDialog(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

/*
    //Follow Context Menu
    private void showUpdateBannerDialog(final String key, final Banner item)
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Edit Banner");
        builder.setMessage("Please Fill Full Information ");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.dialog_add_banner,null);


        //-------------------Id------------------//
        editId=(TextInputEditText)view.findViewById(R.id.editIdDialog_banner);
        editName=(TextInputEditText)view.findViewById(R.id.editNametDialog_banner);
        btnSelect=(Button)view.findViewById(R.id.btnDialog_Select_banner);
        btnUpload=(Button)view.findViewById(R.id.btnDialog_Uplaod_banner);


        //set Defaulet Value
        editName.setText(item.getName());
        editId.setText(item.getId());



        //-------------Event --------------------//
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
                changeImageItem(item);
            }
        });


        builder.setView(view);
        builder.setIcon(R.drawable.ic_laptop_black_24dp);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                item.setName(editName.getText().toString());
                item.setId(editId.getText().toString());

                 //Or Solution
                //databaseReferenceBanner.child(key).setValue(item);
                Map<String,Object>update=new HashMap<>();
                                  update.put("id",item.getId());
                                  update.put("name",item.getName());
                                  update.put("image",item.getImage());

                databaseReferenceBanner.child(key)
                                       .updateChildren(update)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                               if (task.isSuccessful())
                                               {
                                                   Snackbar.make(rootLayoutFoodList, "Banner " + item.getName() + "was Edit", Snackbar.LENGTH_LONG).show();
                                                   adapter.notifyDataSetChanged();

                                               }
                                           }
                                       });



            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.show();

    }
*/


/*
    //Follow Context Menu Method Save Image in Firebase Storage , get Uri Image From Firebase Storage Then Send Uri to Model "Category"
    private void changeImageItem(final Banner item)
    {

        if (saveUri !=null)
        {

            mProgressDialog =new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();


            String imageName= UUID.randomUUID().toString(); //Generate Random Name

            final StorageReference imageFoldar=storageReferenceBanner.child("images/"+imageName);// Create Name For Folder in Firebase Storage

            imageFoldar.putFile(saveUri)//Save Image IN Firebase Storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();

                            imageFoldar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() // Download  Uri Image From Firebase Storage
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    //SET  Value newFood if image Uploaded can get download Link

                                    item.setImage(uri.toString()); // send Uri for Model Food


                                }
                            });




                        } // Failure
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {

                    mProgressDialog.dismiss();
                    Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

*/

    // Follow Context Menu Method Delete Item use Key Item
    private void deleteCategoryDialog(String key)
    {

        databaseReferenceBanner.child(key).removeValue();
        Toast.makeText(this, "Item Deleted !!!", Toast.LENGTH_SHORT).show();
    }


}
