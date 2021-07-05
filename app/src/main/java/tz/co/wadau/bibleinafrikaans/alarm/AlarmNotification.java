package tz.co.wadau.bibleinafrikaans.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

import tz.co.wadau.bibleinafrikaans.fragment.SettingsFragment;

public class AlarmNotification {

    private final String TAG = AlarmNotification.class.getSimpleName();

    public void setAlarm(Context context) {

        //Reading user's preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDailyVerseNotfnEnabled = sharedPrefs.getBoolean(SettingsFragment.KEY_PREFS_DAILY_VERSES, true);

        if (isDailyVerseNotfnEnabled) {
            //Creating verse notification at specified time and repeat daily
            String verseNotfnTime = sharedPrefs.getString(SettingsFragment.KEY_PREFS_VERSE_NOTFN_TIME, "06:00");
            String[] split = verseNotfnTime.split(":");
            int hour = Integer.valueOf(split[0]);
            int minutes = Integer.valueOf(split[1]);

            Calendar nextAlarm = Calendar.getInstance(Locale.getDefault());
            nextAlarm.setTimeInMillis(System.currentTimeMillis());
            nextAlarm.set(Calendar.HOUR_OF_DAY, hour);
            nextAlarm.set(Calendar.MINUTE, minutes);

            Intent notificationIntent = new Intent(context, AlarmReceiver.class);
            notificationIntent.setAction("tz.co.wadau.bibleinafrikaans.DISPLAY_NOTIFICATION");

            PendingIntent broadcast = PendingIntent.getBroadcast(context, 88889, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);

            Log.d(TAG, "Creating verse notification daily at " + verseNotfnTime);
        } else {
            if (alarmUp(context)) {
                cancelAlarm(context);
            }
            Log.d(TAG, "Can't create alarm daily notification not enabled");
        }
    }

    public boolean alarmUp(Context context) {

        Intent notificationIntent = new Intent("tz.co.wadau.bibleinafrikaans.DISPLAY_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 88889, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        boolean isAlarmUp = (broadcast != null);
        Log.d(TAG, "Alarm is up? " + isAlarmUp);
        return isAlarmUp;
    }

    public void cancelAlarm(Context context) {
        Intent notificationIntent = new Intent("tz.co.wadau.bibleinafrikaans.DISPLAY_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 88889, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(broadcast);
        broadcast.cancel();
        Log.d(TAG, "Cancelling daily verse notification");
    }
}
