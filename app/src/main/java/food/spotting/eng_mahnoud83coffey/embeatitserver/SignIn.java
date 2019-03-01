package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.User;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

public class SignIn extends AppCompatActivity {

    private TextView textHeader;
    private TextInputEditText editPhone;
    private TextInputEditText editPassword;
    private Button btnSignIn;
    private CheckBox checkBoxRememberMe;
    private ProgressDialog mDialog;
    //-----------------------
    private FirebaseDatabase database;
    private DatabaseReference Users;
    //---------------------
    private Typeface typeface;


    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

          //-------------------------Id------------------------//
        editPhone=(TextInputEditText)findViewById(R.id.Phone_Number_SignIn);
        editPassword=(TextInputEditText)findViewById(R.id.Password_SignIn);
        btnSignIn=(Button)findViewById(R.id.Button_SignIn);
        textHeader=(TextView)findViewById(R.id.textHeader);
        checkBoxRememberMe=(CheckBox)findViewById(R.id.checkbox_Remember_me);

        //---------Progress Dialog---------------//
        mDialog=new ProgressDialog(SignIn.this);



        sharedPref=getSharedPreferences(Common.USER_DATA,MODE_PRIVATE);



        //-----------------Custom Font-------------------------//
         typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
         textHeader.setTypeface(typeface);


        //-----------------Firebase---------------------------//
        database=FirebaseDatabase.getInstance();
        Users=database.getReference("User");


        //-------------------------------Action--------------------//
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (Common.isConnectedToInternet(getBaseContext()))
                {

                signInUser(editPhone.getText().toString(),editPassword.getText().toString());

                }else
                    {
                        Toast.makeText(SignIn.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                    }

                }
        });



    }




        //Method SignIn User Validation
    private void signInUser( String Phone, String Password)
    {

        //هنعمل شويه   validation
        //هشوف لو فاضى او داس مسطره
        if (Phone.isEmpty()||Phone.equals(" ")){

            //Text Input Edittext
            editPhone.setError("fill here place");//الميثود ديه هتظهر لون احمر كدا فى الجنب
            return;//وبعدين بقولو ارجع ماتعملشى حاجه خالص __فاهيطلع من الميثود كلها
        }
        if (Password.isEmpty()||Password.equals(" ")){
            editPassword.setError("fill here place");
            return;
        }



        mDialog.setMessage("Please Wait...");
        mDialog.show();


        final String localPhone=Phone;
        final String localPassword=Password;



        Users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {


                if (dataSnapshot.child(localPhone).exists())
                {
                    mDialog.dismiss();

                    User user=dataSnapshot.child(localPhone).getValue(User.class);

                    user.setPhone(localPhone);




                    if (Boolean.parseBoolean(user.getIsStaff())) //If IsStuff ==tru
                    {

                        if (user.getPassword().equals(localPassword))
                        {
                            //Login Ok

                            //Save User & Password
                            if (checkBoxRememberMe.isChecked())
                            {
                                //------For Login Automatic--------//
                                sharedPref.edit().putString(Common.USER_KEY, localPhone).apply();
                                sharedPref.edit().putString(Common.PASSWORD_KEY, localPassword).apply();
                            }

                            //-----------Transmission TO Home Activity-----------//
                            Toast.makeText(SignIn.this, "Success full", Toast.LENGTH_SHORT).show();
                            Intent intentHome=new Intent(SignIn.this,Home.class);
                            Common.currentUser=user;
                            startActivity(intentHome);
                            finish();

                        }else
                        {

                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                        }

                    }else
                    {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, "Please Login with Staff account !", Toast.LENGTH_SHORT).show();

                    }

                }else
                {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "This User not exist Database !", Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
