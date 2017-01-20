package com.github.wumke.RNLocalNotifications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.github.wumke.RNLocalNotifications.showAlarm")) {
            Integer id = intent.getExtras().getInt("id", 0);
            String text = intent.getExtras().getString("text", "");
            String datetime = intent.getExtras().getString("datetime", "");
            String sound = intent.getExtras().getString("sound", "");

            if(!this.isAppOnForeground(context)) {
                // Set the icon, scrolling text and timestamp
                Resources res = context.getResources();
                String packageName = context.getPackageName();
                ApplicationInfo appInfo = context.getApplicationInfo();
                String appName = context.getPackageManager().getApplicationLabel(appInfo).toString();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(res.getIdentifier("notification_small", "drawable", packageName)) //TODO: add the icon yourself!
                                .setContentTitle(appName)
                                .setContentText(text)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                                .setAutoCancel(true);

                // Set alarm sound
                if (!sound.equals("") && !sound.equals("silence")) {
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //use default sound TODO: use custom sound by name!
                    mBuilder.setSound(alarmSound);
                }

                //set vibration
                mBuilder.setVibrate(new long[]{0, 1000});
                Class cl = null;
                try {
                    cl = Class.forName(packageName + ".MainActivity");
                } catch (ClassNotFoundException e) {
                    //TODO: if you want feedback
                }
                Intent openIntent = new Intent(context, cl);

                // The PendingIntent to launch our activity if the user selects this notification
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(contentIntent);

                String shortenedDatetime = datetime.replace(":", "").replace("-", "").replace("/", "").replace("\\", "").replace(" ", "").substring(2);
                Integer mId = Integer.parseInt(shortenedDatetime);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(mId, mBuilder.build());
            }
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
