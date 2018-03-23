package meme.busoton.notify;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import meme.busoton.Main;
import meme.busoton.R;

/**
 * Created by hb on 22/03/18.
 */

public class MyNewIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 12343;

    public MyNewIntentService() {
        super("MyNewIntentService");
    }


    public void setupNotificationChannel(Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "12343";
            //Notification Channel
            CharSequence channelName = "Busoton";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


            NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.print("working?");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupNotificationChannel(intent);
        }
        //get data
        String stopName = intent.getStringExtra("stopName");
        String serviceName = intent.getStringExtra("serviceName");
        String operator = intent.getStringExtra("operator");
        String time = intent.getStringExtra("time");

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "Busoton");
            builder.setChannelId("Busoton");

        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("Hey! get to " + stopName);
        builder.setContentText("The " + operator + " " + stopName + " is leaving soon.");
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);

        Intent notifyIntent = new Intent(this, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 12343, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}