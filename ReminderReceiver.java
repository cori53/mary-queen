package com.mary.wellness;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "mary_reminders_v2";

    @Override public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Promemoria Mary", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Appuntamenti e promemoria personali di Mary");
            nm.createNotificationChannel(channel);
        }
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        int id = intent.getIntExtra("id", (int)(System.currentTimeMillis() & 0x7fffffff));
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.mary.wellness.R.mipmap.ic_launcher)
                .setContentTitle(title == null ? "Mary" : title)
                .setContentText(body == null ? "Hai un promemoria." : body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        try { nm.notify(id, b.build()); } catch (SecurityException ignored) {}
    }
}
