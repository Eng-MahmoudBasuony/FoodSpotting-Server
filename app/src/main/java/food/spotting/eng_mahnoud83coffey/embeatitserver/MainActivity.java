package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.User;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button btnSignIn;
    private TextView sLogin;
    //----------------------
    private Typeface typeface;
    //-----------------------------
    private SharedPreferences sharedPref;
    private String userNamePre,passwordPre;
    private ProgressDialog progressDialog;
    //------------------------------
    private DatabaseReference table_User;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //--------------------------------Id-----------------------//
        btnSignIn=(Button)findViewById(R.id.btnMainSignIn);

        sLogin=(TextView)findViewById(R.id.txtslogn);


        //---------------Init---------------//
        firebaseDatabase=FirebaseDatabase.getInstance();
        table_User=firebaseDatabase.getReference(Common.USER);


        progressDialog=new ProgressDialog(this);
        sharedPref=getSharedPreferences(Common.USER_DATA,MODE_PRIVATE);


        userNamePre=sharedPref.getString(Common.USER_KEY,null);
        passwordPre=sharedPref.getString(Common.PASSWORD_KEY,null);

        if (userNamePre!=null&&passwordPre!=null)
        {

            if (!userNamePre.isEmpty()&&!passwordPre.isEmpty())
            {
                login(userNamePre,passwordPre);
            }

        }





        //---------------Custom Font----------------//
        typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        sLogin.setTypeface(typeface);





        //------------------------Action----------------------//

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (Common.isConnectedToInternet(MainActivity.this))
                {

                Intent intentSignIn=new Intent(MainActivity.this,SignIn.class);
                      startActivity(intentSignIn);
                }else
                    {
                        Toast.makeText(MainActivity.this, "PLease Check Internet Connection !", Toast.LENGTH_SHORT).show();
                    }

            }
        });


    }






  private void login(final String phone, final String pwd)
    {
        if (Common.isConnectedToInternet(getBaseContext()))
        {
            progressDialog.setMessage("Please Wating...");
            progressDialog.show();

            table_User.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    //Check if user not exist in database بمعنى هتأكد هل المستخدم موجود فى قاعده البيانات ولا لا الاول
                    if (dataSnapshot.child(phone).exists()) {//لو موجود هجيب البيانات الخاصه بيه
                        //Get User Information
                        progressDialog.dismiss();
                        //Get Data From Database by Key
                        User user = dataSnapshot.child(phone).getValue(User.class);//Phone Number Is Primary Key
                        user.setPhone(phone); //set Phone

                        if (user.getPassword().equals(pwd))//I am sure of the password
                        {
                           Toast.makeText(MainActivity.this, "Sign iN Successfull", Toast.LENGTH_SHORT).show();
                            Intent homeIntent=new Intent(MainActivity.this,Home.class);
                            Common.currentUser=user;
                            startActivity(homeIntent);
                           finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Sign In Failed !", Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Not Exists on Database !", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else
        {
            Toast.makeText(MainActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }

















}
