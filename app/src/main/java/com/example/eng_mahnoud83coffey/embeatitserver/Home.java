package com.example.eng_mahnoud83coffey.embeatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Category;
import com.example.eng_mahnoud83coffey.embeatitserver.Model.Token;
import com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder.MenuViewHolder;
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
import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView fullName;
    private RecyclerView recyclerViewMenu;
    private RecyclerView.LayoutManager layoutManager;
    //--------Add new Menu use Dialog--------
    private TextInputEditText editName;
    private Button btnSelect;
    private Button btnUpload;
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
    private ProgressDialog mprogressDialog;
    //------------------------------------
    private DrawerLayout drawer;



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


        //---------------------Firebase-----------------------//
        database=FirebaseDatabase.getInstance();
        categories=database.getReference("Category");//Reference Database for TABLE Name Category
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();





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


    //"FAB" Method add Product In Firebase
    //add new menu ,we will let user enter name ,and Select picture  from their library after that  we will upload picture to firebase storage
    private void showDialog()
    {

        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setTitle("Add New Category");
        alertBuilder.setMessage("Please Fill Full information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View addMenuLayout=layoutInflater.inflate(R.layout.add_new_menu_dialog_layout,null);

        //-----------Id
        editName =(TextInputEditText)addMenuLayout.findViewById(R.id.edit_name_Dialog);
        btnSelect=(Button)addMenuLayout.findViewById(R.id.btn_Dialog_Select);
        btnUpload=(Button)addMenuLayout.findViewById(R.id.btn_Dialog_Upload);

        //--------Event Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                chooseImage();// let user Select Image from Gallery and save Uri this Image
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                uploadImage();

            }
        });


        alertBuilder.setView(addMenuLayout);//Show Dialog
        alertBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp); //add icon for Dialog

        //set Button
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                //just new Category
                if (newCategory !=null)
                {
                    categories.push().setValue(newCategory);// push  بتعمل key عشوائيه
                    Snackbar.make(drawer,"new Category "+newCategory.getName()+"was added",Snackbar.LENGTH_SHORT )
                            .show();

                }


            }
        });

        alertBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

            }
        });

        alertBuilder.show();


    }

    //"FAB" Method let user Select Image from Gallery and save Uri this Image
    private void  chooseImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),Common.PICK_IMAGE_REQUESTO);

    }

    //"FAB" Method Save Image in Firebase Storage and Send (Name ,Uri) into Model "Category"
    private void uploadImage()
    {

        if (saveUri !=null)
        {

            mprogressDialog =new ProgressDialog(this);
            mprogressDialog.setMessage("Uploading...");
            mprogressDialog.show();


            String imageName= UUID.randomUUID().toString();

            final StorageReference imageFoldar=storageReference.child("images/"+imageName);

            imageFoldar.putFile(saveUri)
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
                                public void onSuccess(Uri uri)
                                {
                                    //SET  Value newCategory if image Uploaded can get download Link

                                    newCategory=new Category(editName.getText().toString(),uri.toString());



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


                }
            });


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);




        if (requestCode == Common.PICK_IMAGE_REQUESTO && resultCode==RESULT_OK && data!=null &&data.getData()!= null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected !");

        }

    }


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


    // Follow Context Menu Method Delete Item use Key Item
    private void deleteCategoryDialog(String key)
    {


        //Delete Category and All Food in Category by menuId
        //حذف الصف من الفئات وكمان يتحذف معاه اللى واخد رقم الId بتاعه من قائمه الطعام فى الاكتيفيتى التانيه

        // first we need get all Foods
        DatabaseReference foods=database.getReference("Foods");

                                   //هيرجع بكل عمود ال menuId بتاعته بتساوى الkey
        Query foodInCategory=foods.orderByChild("menuId").equalTo(key);//ارجع ب الصف اللى الMenuId بتاعه بيساوى الKey


             foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                 {


                     for (DataSnapshot snapshot:dataSnapshot.getChildren())
                     {
                         snapshot.getRef().removeValue();
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });



        //وده هيحذف الفئه Category  اللى اختار حذفها
         categories.child(key).removeValue();
        Toast.makeText(this, "Item Deleted !!!", Toast.LENGTH_SHORT).show();
    }

     //Follow Context Menu Method Select new Image , get new Name Product From  Editext and send Reference Model "Category" into Table "Category" for Firebase Database
    private void showUpdateDialog(final String key, final Category item)
    {

        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setTitle("Update Category");
        alertBuilder.setMessage("Please Fill Full information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View addMenuLayout=layoutInflater.inflate(R.layout.add_new_menu_dialog_layout,null);

        //-----------Id--------------//
        editName =(TextInputEditText)addMenuLayout.findViewById(R.id.edit_name_Dialog);
        btnSelect=(Button)addMenuLayout.findViewById(R.id.btn_Dialog_Select);
        btnUpload=(Button)addMenuLayout.findViewById(R.id.btn_Dialog_Upload);


        //set Defaulet Name
        editName.setText(item.getName());


        //--------Event Button-------------------//
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                chooseImage();// let user Select Image from Gallery and save Uri this Image
            }
        });


        //-----Event Button-----------------//
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changeImageItem(item); //The intended item

            }
        });


        alertBuilder.setView(addMenuLayout);//Show Dialog
        alertBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp); //add icon for Dialog

        //set Button
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                //Update Information
                item.setName(editName.getText().toString());//get new name item send into  name for Model Category
                categories.child(key).setValue(item); //send model referance  into Firebase database


            }
        });

        alertBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

            }
        });

        alertBuilder.show();


    }


     //follow Context Menu" Method Save Image in Firebase Storage , get Uri Image From Firebase Storage Then Send Uri to Model "Category"
    private void changeImageItem(final Category item)
    {

        if (saveUri !=null)
        {

            mprogressDialog =new ProgressDialog(this);
            mprogressDialog.setMessage("Uploading...");
            mprogressDialog.show();


            String imageName= UUID.randomUUID().toString(); //Generate Random Name

            final StorageReference imageFoldar=storageReference.child("images/"+imageName);// Create Name For Folder in Firebase Storage

            imageFoldar.putFile(saveUri)//Save Image IN Firebase Storage
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

                                     item.setImage(uri.toString()); // send Uri for Model Category


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


                }
            });


        }

    }






    //----------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_orders)
        {

            Intent order=new Intent(Home.this,OrderStatus.class);
                startActivity(order);


        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_logout) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
