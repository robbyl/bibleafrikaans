package tz.co.wadau.bibleinafrikaans.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.TodayVerseActivity;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.model.SpecialVerse;
import tz.co.wadau.bibleinafrikaans.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String KEY_PREFS_DAILY_VERSES_ID = "prefs_daily_verses_ids";
    public static final String DAILY_VERSE_NO = "tz.co.wadau.bibleinafrikaans.DAILY_VERSE_NO";
    public static final String DAILY_VERSE_TEXT = "tz.co.wadau.bibleinafrikaans.DAILY_VERSE_TEXT";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("tz.co.wadau.bibleinafrikaans.DISPLAY_NOTIFICATION")) {

            Log.d(TAG, "Broadcast received with action " + intent.getAction());
            final String NOTIFICATION_CHANNEL_ID = "daily_verses";

            SpecialVerse todayVerse = getRandomDailyVerse(context);

            String notificationTitle = context.getString(R.string.verse_of_the_day);
            String notificationContent = todayVerse.getVerseName();
            String dailyVerseText = todayVerse.getVerseText();

            Intent notificationIntent = new Intent(context, TodayVerseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DAILY_VERSE_NO, notificationContent);
            bundle.putString(DAILY_VERSE_TEXT, dailyVerseText);

            notificationIntent.putExtras(bundle);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TodayVerseActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(99881, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && notificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.daily_verse), NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(context.getString(R.string.daily_verses_pref));
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder.setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(notificationTitle)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSound(soundUri)
//                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            Notification notification = builder.build();

            if (notificationManager != null)
                notificationManager.notify(99882, notification);
        }

    }

    private SpecialVerse getRandomDailyVerse(Context context){
        DbFileHelper db = new DbFileHelper(context);
        ArrayList<Integer> dailyVersesIds = Utils.getIntegerArrayPref(context, KEY_PREFS_DAILY_VERSES_ID);

        if(dailyVersesIds.size() == 0){
            for (int i=1; i<=874; i++){
                dailyVersesIds.add(i);
            }

            Utils.setStringArrayPref(context, KEY_PREFS_DAILY_VERSES_ID, dailyVersesIds);
        }

        ArrayList<Integer> remainDailyVersesIds = Utils.getIntegerArrayPref(context, KEY_PREFS_DAILY_VERSES_ID);
        Collections.shuffle(remainDailyVersesIds);
        int randomVerseId = remainDailyVersesIds.get(0);
        remainDailyVersesIds.remove(0); //Remove showed verses
        Utils.setStringArrayPref(context, KEY_PREFS_DAILY_VERSES_ID, remainDailyVersesIds);
        Log.d(TAG, "Today's verse id " + randomVerseId);
        return db.getSpecialVerse(randomVerseId);
    }
}
