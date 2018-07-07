package com.example.eng_mahnoud83coffey.embeatitserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button btnSignIn;
    private TextView sLogin;
    //----------------------
    private Typeface typeface;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //--------------------------------Id-----------------------//
        btnSignIn=(Button)findViewById(R.id.btnMainSignIn);

        sLogin=(TextView)findViewById(R.id.txtslogn);

        //---------------Custom Font----------------//
        typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        sLogin.setTypeface(typeface);




        //------------------------Action----------------------//

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intentSignIn=new Intent(MainActivity.this,SignIn.class);
                      startActivity(intentSignIn);


            }
        });


    }
}
