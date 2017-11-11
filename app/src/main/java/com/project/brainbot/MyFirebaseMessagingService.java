package com.project.brainbot;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final int NOTIFICATION_ID = 3423;
    NotificationCompat.InboxStyle inboxStyle;
    NotificationCompat.Builder mBuilder;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        inboxStyle = new NotificationCompat.InboxStyle();
        mBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        try {
            JSONObject jsonObject =
                    new JSONObject(remoteMessage.getData().toString()).getJSONObject("data");

            String token = jsonObject.getString("token");
            String savedToken = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            if (Objects.equals(token, savedToken)) {
                Log.d("GOT TOKEN :", token);
                Log.d("SAV TOKEN :", savedToken);
                return;
            }

            String title = jsonObject.getString("title");
            String stringMessage = jsonObject.getString("message");

            Message message = MyJSON.getMessage(stringMessage);

            MyJSON.writeFile(message, true);
            showNotification(title, message);

            Intent data = new Intent("message_update");
            data.putExtra("message_json", stringMessage);
            data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyFirebaseMessagingService.this.sendBroadcast(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showNotification(String title, Message message) {

        Intent resultIntent = new Intent(this, GlobalChatRoomActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent piResult = PendingIntent.getActivity(this,
                (int) Calendar.getInstance().getTimeInMillis(), resultIntent, 0);


        inboxStyle.setBigContentTitle(title);
        inboxStyle.addLine(message.getMessage());

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("You have new messages.")
                .setTicker("BrianBot : You have new message.")
                .setStyle(inboxStyle)
                .setContentIntent(piResult)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

}


