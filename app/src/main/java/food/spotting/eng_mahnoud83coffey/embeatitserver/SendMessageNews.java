package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.DataMessage;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.MyResponse;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Remote.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageNews extends AppCompatActivity {

    private Button btnSendMessage;
    private TextInputEditText titleMessage;
    private TextInputEditText contentMessage;
    private ImageView imageSendMessage;


    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_news);


        //--------------------Id-------------------------//
        btnSendMessage=(Button)findViewById(R.id.btn_send_message);
        titleMessage=(TextInputEditText)findViewById(R.id.title_message);
        contentMessage=(TextInputEditText)findViewById(R.id.content_message);
        imageSendMessage=(ImageView)findViewById(R.id.iamge_sendmessage_activity);


        //---Init
        apiService= Common.getFCMClinet();


        //---------------Event----------------------//
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
             {

                 imageSendMessage.setImageResource(R.drawable.hmessage);

            /*     //Create Raw payload to send
                 Notification notification=new Notification(titleMessage.getText().toString(),contentMessage.getText().toString());

                   Sender toTopic=new Sender();

                  toTopic.setTo(new StringBuilder("/topics/").append(Common.toopicName).toString());
                  toTopic.setNotification(notification);*/
                 //animationView.setAnimation("hello-world.json");

                 Map<String,String> dataSend = new HashMap<>();
                 dataSend.put("title",titleMessage.getText().toString());
                 dataSend.put("body",contentMessage.getText().toString());


                 DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(Common.toopicName).toString(),dataSend);

                 apiService.sendNotification(dataMessage)
                                     .enqueue(new Callback<MyResponse>()
                                     {
                                         @Override
                                         public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                                         {
                                             if (response.isSuccessful())
                                                 Toast.makeText(SendMessageNews.this, "Message sent!", Toast.LENGTH_SHORT).show();

                                                  imageSendMessage.setImageResource(R.drawable.sendmessage);
                                         }


                                                 @Override
                                                 public void onFailure(Call<MyResponse> call, Throwable t)
                                                 {
                                                     Toast.makeText(SendMessageNews.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                                 }
                                     });

             }

                });


   }




 }




