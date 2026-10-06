package com.mary.wellness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new MaryAndroidBridge(), "MaryAndroid");
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ReminderReceiver.CHANNEL_ID, "Promemoria Mary", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Appuntamenti e promemoria personali di Mary");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private boolean notificationsAllowed() {
        return Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void sendTestNotification() {
        Intent i = new Intent(this, ReminderReceiver.class);
        i.putExtra("title", "Mary 💜");
        i.putExtra("body", "Notifiche attivate correttamente.");
        i.putExtra("id", 900001);
        sendBroadcast(i);
    }

    public class MaryAndroidBridge {
        @JavascriptInterface public void requestNotificationPermission() {
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= 33 && !notificationsAllowed()) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
                } else {
                    sendTestNotification();
                    notifyWeb("nativeNotificationResult", "granted");
                }
            });
        }

        @JavascriptInterface public String notificationStatus() {
            return notificationsAllowed() ? "granted" : "default";
        }

        @JavascriptInterface public void scheduleEventReminder(String title, String date, String time) {
            try {
                if (time == null || time.trim().isEmpty()) return;
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                Date when = f.parse(date + " " + time);
                if (when == null || when.getTime() <= System.currentTimeMillis()) return;
                int id = Math.abs((title + date + time).hashCode());
                Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
                intent.putExtra("title", "Mary · " + title);
                intent.putExtra("body", "È il momento del tuo appuntamento.");
                intent.putExtra("id", id);
                PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                    // Open Android's exact-alarm permission screen once, then keep a safe fallback alarm.
                    try {
                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(settingsIntent);
                    } catch (Exception ignored) {}
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when.getTime(), pi);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when.getTime(), pi);
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, when.getTime(), pi);
                }
            } catch (Exception ignored) {}
        }
    }

    private void notifyWeb(String fn, String value) {
        if (webView != null) webView.evaluateJavascript("if(window." + fn + ") window." + fn + "('" + value + "');", null);
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            boolean ok = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (ok) sendTestNotification();
            notifyWeb("nativeNotificationResult", ok ? "granted" : "denied");
        }
    }

    @Override public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
