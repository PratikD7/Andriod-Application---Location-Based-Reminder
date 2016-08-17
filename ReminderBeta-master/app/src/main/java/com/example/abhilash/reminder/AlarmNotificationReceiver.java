
package com.example.abhilash.reminder;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;


public class AlarmNotificationReceiver extends BroadcastReceiver {
    // Notification ID to allow for future updates
   /* private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "AlarmNotificationReceiver";

    // Notification Text Elements
    private final CharSequence tickerText = "Are You Playing Angry Birds Again!";
    private final CharSequence contentTitle = "A Kind Reminder";
    private final CharSequence contentText = "Get back to studying!!";
*/
    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;


    SharedPreferences someData;
    SharedPreferences.Editor editor;
    HashMap<String, String> map = new HashMap<String, String>();

    // Notification Sound and Vibration on Arrival
    //   private final Uri soundURI = Uri
    //         .parse("android.resource://course.examples.Alarms.AlarmCreate/"
    //               + R.raw.alarm_rooster);
  //  private final long[] mVibratePattern = { 0, 200, 200, 300 };

    @Override
    public void onReceive(Context context, Intent intent) {

        // The Intent to be used when the user clicks on the Notification View
       mNotificationIntent = new Intent(context, MyService.class);// Check if problem

        // The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_NO_CREATE);
        // my content


        // Intent i = new Intent();
        // i.setClassName("com.location.chinmay.alarmdemo", "com.location.chinmay.alarmdemo.AfterAlarm");
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(i);

        //--------------------------------------
        someData = context.getSharedPreferences("SubLatLng",0);
        editor = someData.edit();
        map = (HashMap<String, String>) someData.getAll();


        //context.startService(background);
        //context.startService(new Intent(context, MyService.class));
        if(map.size()!=0) {
          //  PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
           // PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
           //         "MyWakelockTag");
          //  wakeLock.acquire(5000);
            context.startService(new Intent(context, MyService.class));
        }

        //2nd    context.startService(new Intent(context, LocationService.class));
        // no need to stop .-> stopSelf() used
        //context.stopService(new Intent(context, MyService.class));

        //--------------------------------------

        // Toast.makeText(getApplicationContext(),"alarm is off",Toast.LENGTH_LONG).show();
        // Build the Notification
        /*Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent)
                .setVibrate(mVibratePattern);

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
*/
        // Pass the Notification to the NotificationManager:
        // mNotificationManager.notify(MY_NOTIFICATION_ID,
        //       notificationBuilder.build());

        // Log occurence of notify() call
        //  Log.i(TAG, "Sending notification at:"
        //        + DateFormat.getDateTimeInstance().format(new Date()));

    }
}
