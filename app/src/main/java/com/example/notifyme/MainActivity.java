package com.example.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_NOTIFICATION_CHANNEL_ID =
            "primary_notification_channel_id";
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.notifyme.ACTION_UPDATE_NOTIFICATION";

    private Button button_notify;
    private Button button_update;
    private Button button_cancel;
    private NotificationManager mNotificationManager;
    private NotificationReceiver mNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        button_update = findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        createNotificationChannel();

        setNotificationButtonState(true, false, false);

        mNotificationReceiver = new NotificationReceiver();
        registerReceiver(mNotificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNotificationReceiver);
        super.onDestroy();
    }

    private void setNotificationButtonState(Boolean isNotifyEnabled
                                            , Boolean isUpdateEnabled
                                            , Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    private void cancelNotification() {
        setNotificationButtonState(true, false, false);

        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateNotification() {
        setNotificationButtonState(false, false, true);

        Bitmap notificationBigImage = BitmapFactory.decodeResource(getResources()
                                        , R.drawable.mascot_1);

        NotificationCompat.Builder notificationBuilder = getNotificationBuilder();
        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(notificationBigImage)
                                        .setBigContentTitle("Notification Updated!"));

        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);

        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this
                , NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        setNotificationButtonState(false, true, true);

        NotificationCompat.Builder notificationBuilder = getNotificationBuilder();

        notificationBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_NOTIFICATION_CHANNEL_ID, "Default Channel"
                    , NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this
                , NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(
                this, PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Notification Successful!")
                .setContentText("This is your Content Text.")
                .setSmallIcon(R.drawable.ic_notification_exclamation)
                .setContentIntent(notificationPendingIntent)
                //.setDeleteIntent()
                .setAutoCancel(true)
                // Backward compatibility
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
