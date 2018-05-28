package com.heilov.heilov.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.heilov.heilov.Activities.MainActivity;
import com.heilov.heilov.Activities.SignUpActivity;
import com.heilov.heilov.R;

public class InAppNotifier implements Observer {

    private NotificationChannel channel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void update(Context context, String message) {
        initChannels(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel.getId())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Heilov")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}
