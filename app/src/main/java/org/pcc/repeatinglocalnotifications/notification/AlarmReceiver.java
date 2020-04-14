package org.pcc.repeatinglocalnotifications.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import org.pcc.repeatinglocalnotifications.MainActivity;
import org.pcc.repeatinglocalnotifications.R;

/**
 * Created by ptyagi on 4/17/17.
 */

/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get notification manager to manage/send notifications
        //Intent intent1 = new Intent(String.valueOf(context));
           Intent intentToRepeat = new Intent(context, MainActivity.class);

            intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Pending intent to handle launch of Activity in intent above
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

            //Send local notification
            NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);



    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.sound);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.wasinghand)
                        .setVibrate(new long[] { 1000 })
                        .setNumber(1)
                        .setStyle( new NotificationCompat.BigTextStyle().bigText( "Please Wash Your hands with soap for at least 20 seconds or use an alcohol based sanitizer." ))
                        .setSound(sound)
                        .setAutoCancel(true);

        return builder;
    }
}
