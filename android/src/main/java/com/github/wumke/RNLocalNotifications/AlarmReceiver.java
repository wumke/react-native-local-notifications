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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
            String sound = intent.getExtras().getString("sound", "default");
            String hiddendata = intent.getExtras().getString("hiddendata", "");
            String largeIconName = intent.getExtras().getString("largeIconName", "ic_launcher");
            String largeIconType = intent.getExtras().getString("largeIconType", "mipmap");
            String smallIconName = intent.getExtras().getString("smallIconName", "notification_small");
            String smallIconType = intent.getExtras().getString("smallIconType", "drawable");

            if(!this.isAppOnForeground(context)) {
                // Set the icon, scrolling text and timestamp
                Resources res = context.getResources();
                String packageName = context.getPackageName();
                ApplicationInfo appInfo = context.getApplicationInfo();
                String appName = context.getPackageManager().getApplicationLabel(appInfo).toString();

                int largeIconResId;
                largeIconResId = res.getIdentifier("ic_launcher", "mipmap", packageName);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, largeIconResId);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(res.getIdentifier("notification_small", "drawable", packageName))
                                .setLargeIcon(largeIconBitmap)
                                .setContentTitle(appName)
                                .setContentText(text)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                                .setAutoCancel(true);

                // Set alarm sound
                if(sound.equals("default")){
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(alarmSound);
                }
                else if (sound.equals("silence")) {
                    //Do not set a sound for silence
                }
                else {
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String soundName = sound;
                    if (soundName != null) {
                        if (!"default".equalsIgnoreCase(soundName)) {

                            // sound name can be full filename, or just the resource name.
                            // So the strings 'my_sound.mp3' AND 'my_sound' are accepted
                            // The reason is to make the iOS and android javascript interfaces compatible

                            int resId;
                            if (context.getResources().getIdentifier(soundName, "raw", context.getPackageName()) != 0) {
                                resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
                            } else {
                                soundName = soundName.substring(0, soundName.lastIndexOf('.'));
                                resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
                            }

                            soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
                        }
                    }
                    mBuilder.setSound(soundUri);
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
                openIntent.putExtra("hiddendata", hiddendata);

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
