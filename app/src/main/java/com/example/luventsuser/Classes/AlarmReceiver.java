package com.example.luventsuser.Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.luventsuser.R;
import com.example.luventsuser.activities.BottomNavigation;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationID=intent.getIntExtra("notificaitonId",0);
        String message1=intent.getStringExtra("message1");
        String message2=intent.getStringExtra("message2");
        long timemillis=intent.getLongExtra("timemillis",0);

        Intent mainIntent=new Intent(context, BottomNavigation.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,mainIntent,0);


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel("mynotification","mynotification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }



        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"mynotification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(message1)
                .setContentText(message2)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

       // builder.setSmallIcon(R.drawable.ic_notifications_white_24dp)
//                .setContentTitle(message1)
//                .setContentText("G")
//                .setWhen(timemillis)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setPriority(Notification.PRIORITY_MAX)
//                .setDefaults(Notification.DEFAULT_ALL);
       // NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(context);
        managerCompat.notify(notificationID,builder.build());

      //  notificationManager.notify(notificationID,builder.build());


    }
}
