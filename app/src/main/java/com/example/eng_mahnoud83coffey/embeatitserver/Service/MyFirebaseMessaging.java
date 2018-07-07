package com.example.eng_mahnoud83coffey.embeatitserver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.eng_mahnoud83coffey.embeatitserver.MainActivity;
import com.example.eng_mahnoud83coffey.embeatitserver.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService
{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage);
    }


    private void sendNotification(RemoteMessage remoteMessage)
    {
        RemoteMessage.Notification remoNotification=remoteMessage.getNotification();

        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri uriDefaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.imagetest);
        builder.setContentTitle(remoNotification.getTitle());
        builder.setContentText(remoNotification.getBody());
        builder.setAutoCancel(true);
        builder.setSound(uriDefaultSound);
        builder.setContentIntent(pendingIntent);

        Notification notification =builder.build();

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,notification);

    }


}
