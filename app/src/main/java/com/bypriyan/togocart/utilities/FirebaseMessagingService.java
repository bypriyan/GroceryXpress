package com.bypriyan.togocart.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.register.SplashScreen;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null) return;
        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoNotification(remoteMessage);
        } else {
            sendNotification(remoteMessage);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendOreoNotification(RemoteMessage remoteMessage){
        String icon=" ", title=" ", body=" ";
        try{
            icon = remoteMessage.getData().get("icon");
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }catch (NullPointerException e){

        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreaNotification notification1 = new OreaNotification(this);

        Notification.Builder builder = notification1.getNotification(title, body, pendingIntent ,defaultSound, icon);

        int i = 100;

        notification1.getManager().notify(i, builder.build());

    }

    public void sendNotification(RemoteMessage remoteMessage) {
        String icon=" ", title=" ", body=" ";
        try{
            icon = remoteMessage.getData().get("icon");
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }catch (NullPointerException e){

        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.togo_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 100;

        noti.notify(i, builder.build());
    }

}


