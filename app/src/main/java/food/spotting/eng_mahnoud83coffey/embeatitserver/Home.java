package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Category;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Token;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.MenuViewHolder;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView fullName;
    private RecyclerView recyclerViewMenu;
    private RecyclerView.LayoutManager layoutManager;
    //--------Add new Menu use Dialog--------
    private TextInputEditText editName;
    private TextInputEditText editMenuId;
    private Button btnSelect;
    private Button btnUpload;
    private Dialog dialogAddNewFood;
    //--------Update-------------//
    private TextInputEditText editTextNameUpdate;
    private TextView textMenuIdUpdate;
    private Button btnSelectUPdate;
    private Button btnUploadUpdate;
    private Dialog dialogUpdateItem;
    //----------------------
    private FirebaseDatabase database;
    private DatabaseReference categories;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    //--------Firebase UI--------//
    private Query query;
    private FirebaseRecyclerOptions<Category> options;
    private FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    //---------------------------
    private Category newCategory;
    private Uri saveUri;
    private Bitmap thmbBitmap=null;
    private ProgressDialog mprogressDialog;
    private Bitmap thump_bitmap=null;
    //------------------------------------
    private DrawerLayout drawer;
    //------------------------------
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        //-----------------------Id------------------//
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_home);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        recyclerViewMenu=(RecyclerView)findViewById(R.id.recyclerView_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        //-------------RecyclerView-------------------//
        recyclerViewMenu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewMenu.setLayoutManager(layoutManager);
        mprogressDialog=new ProgressDialog(this);

        //---------------------Firebase-----------------------//
        database=FirebaseDatabase.getInstance();
        categories=database.getReference(Common.CATEGORY);//Reference Database for TABLE Name Category
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


         sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
         sharedPref=getSharedPreferences(Common.USER_DATA,MODE_PRIVATE);

         //---Action Navigation Drawer---------//
         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //set Full Name User

       View headerView=navigationView.getHeaderView(0);
       fullName=(TextView)headerView.findViewById(R.id.text_Full_Name_User);
       fullName.setText(Common.currentUser.getName());

        //-------------------------Event--------------------------------//

        if (Common.isConnectedToInternet(this))
        {
        loadMenu();
        }else
            {
                Toast.makeText(this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
            }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (Common.isConnectedToInternet(getBaseContext()))
                {
                showDialog();

                }else
                    {
                        Toast.makeText(Home.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        //Storage your Token app to FirebaseDatabse
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }


    //Update Token
    private void updateToken(String token)
    {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Tokens");

        Token token1=new Token(token,true);

        reference.child(Common.currentUser.getPhone()).setValue(token1);
    }

    //Method Load Data From FirebaseDatabse AND send Data to RecyclerView
    private void loadMenu()
    {


        //---Using Firebase UI to populate a RecyclerView--------//
        query= FirebaseDatabase.getInstance()
                .getReference()
                .child("Category");

        query.keepSynced(true);//Load Data OffLine

        options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final MenuViewHolder holder, final int position, final Category model) {
                // Bind the Chat object to the ChatHolder

                //Send Image Name to Recyclerview
                holder.textMenuName.setText(model.getName());

                //Send Image  to Recyclerview
                Picasso.get()
                        .load(model.getImage())//Url
                        //  .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        // .placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(holder.imageView);


                final Category clickItem=model;


                //لما المستخدم يضغط على اى صف
                holder.setItemClickListener(new ItemClickListiner()
                {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //sen Category Id and Start new Activity

                       Intent foodsListIntent=new Intent(Home.this,FoodList.class);

                        foodsListIntent.putExtra("CategoryId",adapter.getRef(position).getKey());//Just Get Key Of item
                         startActivity(foodsListIntent);


                    }
                });



            }//end OnBind


        };//end Adapter

        //هيعمل تحديث للبيانات لو حصل تغيرفيها
        adapter.notifyDataSetChanged(); //Refresh Data if data changed

        recyclerViewMenu.setAdapter(adapter);
    }

   //---------------------------------------------
    //Dialog Shaw Add New Category
    private void showDialog()
    {
        dialogAddNewFood=new Dialog(this);
        dialogAddNewFood.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAddNewFood.setContentView(R.layout.add_new_menu_dialog_layout);

        //-----------Id------------------------//
        editName =(TextInputEditText)dialogAddNewFood.findViewById(R.id.edit_name_Dialog);
        editMenuId=(TextInputEditText)dialogAddNewFood.findViewById(R.id.edit_menuId_Dialog);
        btnSelect=(Button)dialogAddNewFood.findViewById(R.id.btn_Dialog_Select);
        btnUpload=(Button)dialogAddNewFood.findViewById(R.id.btn_Dialog_Upload);
        ImageView btnCloase=(ImageView)dialogAddNewFood.findViewById(R.id.close_dialog_category);


        //--------Event Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                chooseImage();// let user Select Image from Gallery and save Uri this Image
                sharedPreferences.edit().putBoolean("btnSelectUPload",true).apply();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                uploadImage();

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
    private void  chooseImage()
    {

      /* Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),Common.PICK_IMAGE_REQUESTO);
*/

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Select Image")
                .start(this);
    }

    private void  chooseImageItem()
    {
      /*  Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Update picture"),14556);
     */
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Update Image")
                .start(this);



    }

    //Upload New Category
    private void uploadImage()
    {

        if (saveUri !=null)
        {
            mprogressDialog.setMessage("Uploading...");
            mprogressDialog.show();

            //SET  Value newCategory if image Uploaded can get download Link
            final String menuId =editMenuId.getText().toString().trim();
            if (!editName.getText().toString().isEmpty()&&!editMenuId.getText().toString().isEmpty())
            {
                categories.orderByChild("menuId").equalTo(menuId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.exists()) //State In Menu  Id Exist
                        {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                if (data.getKey().equals(menuId))
                                {
                                    //do ur stuff
                                    Toast.makeText(Home.this, "Menu Id is Exist !", Toast.LENGTH_LONG).show();
                                    editMenuId.setError("Menu Id is Exist Please Change ");
                                    mprogressDialog.dismiss();


                                } else {
                                    Toast.makeText(Home.this, "  مش موجود ", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else //  in State in Menu Id Not Exist
                        {
                            Toast.makeText(Home.this, "Please Wait !", Toast.LENGTH_SHORT).show();



                            String imageName= UUID.randomUUID().toString();

                            final StorageReference imageFoldar=storageReference.child("images/"+imageName);


                            //-------------------------------Compress Thumb Image--------------------------------------------//
                            //1-فى البدايه انا هضغط الصوره بمكتبه ضغط الصور وهحط الناتج فى الBitmap
                            //2-ثم هضغط الصوره كا Bitmap وهاخد الناتج كا Byte فابالتالى هحتاج حاجه اخزن فيها عمليه التحويل فاهستخدم ByteArrayOutPUTStream
                            //3-هخزن الByteArrayOutPutStream فى مصفوفه بايت عشان ابعتها للFirebasestorage
                            //ملحوظه هيا بتتحول لByte ليه لان عشان انقل حاجه عن طريق الانترنت فا لازم تنقل كا بايت
                            //هنحط الصوره فى File عشان نضغطها عن طريق مكتبه لضغط الصور لتقليل حجمها
                            final File thump_filepathUri=new File(saveUri.getPath());

                            try
                            {
                                thump_bitmap= new Compressor(Home.this)
                                        .setMaxWidth(200)
                                        .setMaxHeight(200)
                                        .setQuality(50)
                                        .compressToBitmap(thump_filepathUri);//بديله مسار الصوره الحقيقيه اللى هيضغطها

                            }catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

                            thump_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

                            final byte[] thump_byte=byteArrayOutputStream.toByteArray();//كده معايا الصوره الحقيقيه مضغوطه والدقه بتاعتها 50%

                            //--------------------------------------------------------------------

                            imageFoldar.putBytes(thump_byte)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                    {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            mprogressDialog.dismiss();
                                            Toast.makeText(Home.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();

                                            imageFoldar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                            {
                                                @Override
                                                public void onSuccess(final Uri uri)
                                                {

                                                    newCategory=new Category(editName.getText().toString(),uri.toString(),editMenuId.getText().toString());
                                                    categories.child(editMenuId.getText().toString()).setValue(newCategory);
                                                    dialogAddNewFood.dismiss(); //Finish Dialog
                                                    Snackbar.make(drawer,"new Category "+newCategory.getName()+"was added",Snackbar.LENGTH_SHORT )
                                                            .show();

                                                }
                                            });

                                        } // Failure
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {

                                    mprogressDialog.dismiss();
                                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    //Do not worry about this error
                                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                                    mprogressDialog.setMessage("Uploaded"+progress+"%");
                                    //Clear Value From saveUri
                                    saveUri=null;
                                 }
                            });

                            //------------------------------------------------------
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                       //Clear Value From saveUri
                        saveUri=null;
                        Toast.makeText(Home.this, "No data uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }else //state someEdit is empty
                {
                    Toast.makeText(this, "Please Fill Information", Toast.LENGTH_SHORT).show();
                  //  editMenuId.setError("Please Fill Information");
                   // editMenuId.setError("Please Fill Information");
                    mprogressDialog.dismiss();
                }
        }else // state SaveUri=null
            {
                Toast.makeText(this, "First Select Image Category", Toast.LENGTH_SHORT).show();
            }

            
    }

   //----------------Context Menu----------------------
     //Main Method select item from Context Menu
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {


        if(item.getTitle().equals(Common.UPDATE))
        {
            //Method Update Product
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

          //  Toast.makeText(this, "getOrder+="+item.getOrder()+"---adapter.getItem"+adapter.getItem(item.getOrder()), Toast.LENGTH_LONG).show();
        }
        else if (item.getTitle().equals(Common.DELETE))
            {
                //Method Delete  Product
                deleteCategoryDialog(adapter.getRef(item.getOrder()).getKey());
            }

        return super.onContextItemSelected(item);
    }
    // Delete Item Category
    private void deleteCategoryDialog(final String key)
    {
        //Delete Category and All Food in Category by menuId
        //حذف الصف من الفئات وكمان يتحذف معاه اللى واخد رقم الId بتاعه من قائمه الطعام فى الاكتيفيتى التانيه
        // first we need get all Foods
        DatabaseReference foods=database.getReference(Common.FOODS);
        DatabaseReference categoriesDelete=database.getReference(Common.CATEGORY);
        DatabaseReference banner=database.getReference(Common.BANNER);

        //هيرجع بكل عمود ال menuId بتاعته بتساوى الkey
        Query foodInCategory=foods.orderByChild("menuId").equalTo(key);//ارجع ب الصف اللى الMenuId بتاعه بيساوى الKey
        Query foodInBanner= banner.orderByChild("menuId").equalTo(key);


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
                            Toast.makeText(Home.this, "Success Remove From DB", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Table "Food"
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Toast.makeText(Home.this, "Success Remove From DB", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        categoriesDelete.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    mprogressDialog.setMessage("Please Wait...");
                    mprogressDialog.show();

                    Category category=new Category();
                    category=snapshot.getValue(Category.class);


                    //---Split Name from Link-------//
                    String [] linkNameImage=category.getImage().split("images%2F");
                    String [] towString=linkNameImage[1].split("\\?alt");
                       String nameImage=towString[0];

                     //------------------Delete Image From Firebase----------//
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReference();
                    // Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child("images/"+nameImage);
                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(Home.this, "Delete Success From Storage", Toast.LENGTH_SHORT).show();
                            //Table "Category"
                            categories.child(key).removeValue(); //Delete Item From Firebase
                            mprogressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Item Deleted DB!!!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            Toast.makeText(Home.this, "Failure"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                          mprogressDialog.dismiss();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                mprogressDialog.dismiss();
            }
        });



    }

    //Dialog Shaw Update Item Category
    private void showUpdateDialog(final String key, final Category item)
    {

       dialogUpdateItem=new Dialog(this);
        dialogUpdateItem.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogUpdateItem.setContentView(R.layout.update_category_dialog);

        //-----------Id--------------//
        editTextNameUpdate =(TextInputEditText)dialogUpdateItem.findViewById(R.id.edit_name_Dialog_category_update);
        btnSelectUPdate=(Button)dialogUpdateItem.findViewById(R.id.btn_Dialog_Select_update);
        btnUploadUpdate=(Button)dialogUpdateItem.findViewById(R.id.btn_Dialog_Upload_update);
        textMenuIdUpdate=(TextView) dialogUpdateItem.findViewById(R.id._text_menu_id_category_update);
        ImageView btnClose=(ImageView)dialogUpdateItem.findViewById(R.id.close_dialog_category_update);

        //set Defaulet Name
        editTextNameUpdate.setText(item.getName());
        textMenuIdUpdate.setText(key);

        //--------Event Button-------------------//
        btnSelectUPdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                chooseImageItem();// let user Select Image from Gallery and save Uri this Image
                sharedPreferences.edit().putBoolean("btnSelectUPdate",true).apply();
            }
        });

        //-----Event Button-----------------//
        btnUploadUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changeImageItem(item); //The intended item

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogUpdateItem.dismiss();
            }
        });


        dialogUpdateItem.show();
    }

    //Upload "Update Category"
    private void changeImageItem(final Category item)
    {

        if (saveUri !=null)
        {


            mprogressDialog.setMessage("Uploading...");
            mprogressDialog.show();

            if (!editTextNameUpdate.getText().toString().isEmpty())
            {

            String imageName= UUID.randomUUID().toString(); //Generate Random Name

            final StorageReference imageFoldar=storageReference.child("images/"+imageName);// Create Name For Folder in Firebase Storage

                //-------------------------------Compress Thumb Image--------------------------------------------//
                //1-فى البدايه انا هضغط الصوره بمكتبه ضغط الصور وهحط الناتج فى الBitmap
                //2-ثم هضغط الصوره كا Bitmap وهاخد الناتج كا Byte فابالتالى هحتاج حاجه اخزن فيها عمليه التحويل فاهستخدم ByteArrayOutPUTStream
                //3-هخزن الByteArrayOutPutStream فى مصفوفه بايت عشان ابعتها للFirebasestorage
                //ملحوظه هيا بتتحول لByte ليه لان عشان انقل حاجه عن طريق الانترنت فا لازم تنقل كا بايت
                //هنحط الصوره فى File عشان نضغطها عن طريق مكتبه لضغط الصور لتقليل حجمها
                final File thump_filepathUri=new File(saveUri.getPath());

                try
                {
                    thump_bitmap= new Compressor(Home.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thump_filepathUri);//بديله مسار الصوره الحقيقيه اللى هيضغطها

                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

                thump_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

                final byte[] thump_byte=byteArrayOutputStream.toByteArray();//كده معايا الصوره الحقيقيه مضغوطه والدقه بتاعتها 50%

                //---------------------------------------------------------------------//
            imageFoldar.putBytes(thump_byte)//Save Image IN Firebase Storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mprogressDialog.dismiss();
                            Toast.makeText(Home.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();

                            imageFoldar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() // Download  Uri Image From Firebase Storage
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    //SET  Value newCategory if image Uploaded can get download Link

                                   /* Category category=new Category();
                                    category.setImage(uri.toString()); // send Uri for Model Category
                                    category.setMenuId(textMenuIdUpdat)e.getText().toString());
                                    category.setName(editTextNameUpdate.getText().toString().trim();//get new name item send into  name for Model Category
                                  */
                                    Map updateData=new HashMap();
                                      updateData.put("image",uri.toString());
                                      updateData.put("menuId",textMenuIdUpdate.getText().toString());
                                      updateData.put("name",editTextNameUpdate.getText().toString().trim());

                                    //Update Information
                                    categories.child(textMenuIdUpdate.getText().toString())
                                            .updateChildren(updateData)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o)
                                                {
                                                    Toast.makeText(Home.this, "Success Update", Toast.LENGTH_SHORT).show();
                                                    btnSelectUPdate.setText("Select Image");
                                                    dialogUpdateItem.dismiss();
                                                    saveUri=null;

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(Home.this, "Failure Update", Toast.LENGTH_LONG).show();
                                            btnSelectUPdate.setText("select Image");
                                            saveUri=null;
                                            mprogressDialog.dismiss();
                                        }
                                    }); //send model referance  into Firebase database
                                }
                            });


                        } // Failure
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    mprogressDialog.dismiss();
                    //Clear Value From saveUri
                    saveUri=null;
                    btnSelectUPdate.setText("Select Image");
                    Toast.makeText(Home.this, "No data Update !", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                {
                    //Do not worry about this error
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    mprogressDialog.setMessage("Uploaded"+progress+"%");
                    //Clear Value From saveUri
                    saveUri=null;

                }
            });

            }else
                {
                   editTextNameUpdate.setError("Please Enter Name Category !");
                   mprogressDialog.dismiss();
                }

        }else
            {
                Toast.makeText(this, "Please Select First Image", Toast.LENGTH_SHORT).show();
            }

    }


    //------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

     /*   //Return for Method "chooseImage"
        if (requestCode == Common.PICK_IMAGE_REQUESTO && resultCode==RESULT_OK && data!=null &&data.getData()!= null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected ");


        }
        //Return for Method "chooseImageItem"
        if (requestCode==14556&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            saveUri=data.getData();
            btnSelectUPdate.setText("Image Selected ");

        }
*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                saveUri = result.getUri();

                //Update Image

                boolean clickbtnUpdate=sharedPreferences.getBoolean("btnSelectUPdate",false);
                boolean clickbtnUpload=sharedPreferences.getBoolean("btnSelectUPload",false);


                if (clickbtnUpdate==true)
                {
                    btnSelectUPdate.setText("Image Selected ");
                    btnSelectUPdate.setTextColor(Color.parseColor("#0d52bf"));
                    sharedPreferences.edit().putBoolean("btnSelectUPdate",false).apply();

                }

                if (clickbtnUpload==true)
                {
                    btnSelect.setText("Image Selected ");
                    btnSelect.setTextColor(Color.parseColor("#0d52bf"));
                    sharedPreferences.edit().putBoolean("btnSelectUPload",false).apply();
                }

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }




        }

    }





    //----------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu)
        {

            Intent intentMenuActivity=new Intent(Home.this,MenuActivity.class);
                startActivity(intentMenuActivity);

            // Handle the camera action
        } else if (id == R.id.nav_orders)
        {

            Intent order=new Intent(Home.this,OrderStatus.class);
                startActivity(order);


        }  else if (id == R.id.nav_logout)
        {
            sharedPref.edit().putString(Common.USER_KEY, null).apply();
            sharedPref.edit().putString(Common.PASSWORD_KEY, null).apply();
            Intent intentMainActivity=new Intent(Home.this,MainActivity.class);
                   startActivity(intentMainActivity);
                   finish();

        }else if (id == R.id.nav_banner) {

            Intent banner=new Intent(Home.this,BannerActivity.class);
            startActivity(banner);
        }else if (id == R.id.nav_message) {

            Intent sendMessage=new Intent(Home.this,SendMessageNews.class);
            startActivity(sendMessage);
        }else if (id == R.id.nav_ShippersManagement) {

            Intent shippersManagement=new Intent(Home.this,ShippersManagement.class);
            startActivity(shippersManagement);
        }else if (id == R.id.nav_success_request) {

            Intent successfulShipperRequest=new Intent(Home.this,SuccessfulDeliveryRequest.class);
            startActivity(successfulShipperRequest);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Private_police)
        {


            Intent intent;
            Toast.makeText(Home.this, "privacy-policy", Toast.LENGTH_LONG).show();

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wonderful-goldberg-f3ff4e.netlify.com/"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
