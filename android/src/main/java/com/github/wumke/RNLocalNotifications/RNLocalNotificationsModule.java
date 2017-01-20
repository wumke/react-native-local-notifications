package com.github.wumke.RNLocalNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RNLocalNotificationsModule extends ReactContextBaseJavaModule {

    ReactApplicationContext reactContext;
    AlarmManager alarmManager;

    public RNLocalNotificationsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        alarmManager = (AlarmManager) reactContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public String getName() {
        return "RNLocalNotifications";
    }

    @ReactMethod
    public void createNotification(Integer id, String text, String datetime, String sound) {
        this.createAlarm(id, text, datetime, sound, false);
    }

    @ReactMethod
    public void deleteNotification(Integer id) {
        this.deleteAlarm(id);
    }

    @ReactMethod
    public void updateNotification(Integer id, String text, String datetime, String sound) {
        this.createAlarm(id, text, datetime, sound, true);
    }

    public void createAlarm(Integer id, String text, String datetime, String sound, boolean update) {
        if(update){
            this.deleteAlarm(id);
        }

        final SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        Date dateToMillis = null;
        try {
            dateToMillis = desiredFormat.parse(datetime);
        } catch (ParseException e) {
            //TODO: if you want feedback...
            e.printStackTrace();
        }
        Long timeInMillis = dateToMillis.getTime();

        Intent intent = new Intent(reactContext, AlarmReceiver.class);
        intent.setAction("com.github.wumke.RNLocalNotifications.showAlarm");
        intent.putExtra("id", id);
        intent.putExtra("text", text);
        intent.putExtra("datetime", datetime);
        intent.putExtra("sound", sound);

        PendingIntent mAlarmSender = PendingIntent.getBroadcast(reactContext, id, intent, 0);

        Calendar date = Calendar.getInstance();
        if(timeInMillis > date.getTimeInMillis()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, mAlarmSender);
        }
    }

    public void deleteAlarm(Integer id) {
        Intent intent = new Intent(reactContext, AlarmReceiver.class);
        intent.setAction("com.github.wumke.RNLocalNotifications.showAlarm");

        // cancel the alarm!
        PendingIntent pi = PendingIntent.getBroadcast(reactContext, id, intent, PendingIntent.FLAG_NO_CREATE);
        if(pi != null){
            pi.cancel();
        }
        alarmManager.cancel(pi);
    }
}
