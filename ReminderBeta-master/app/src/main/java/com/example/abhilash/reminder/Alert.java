package com.example.abhilash.reminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by CHINMAY on 27-10-2015.
 */
public class Alert extends Activity
{

    SharedPreferences someData;
    SharedPreferences.Editor editor;


    HashMap<String, String> map = new HashMap<String, String>();



    TextView tvSubject,tvDistance;
    Button bDone,bSnooze,bShow;
    SQLiteDatabase myDB2;
    String data;
    String subject="error";


    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.alert);


        someData = getSharedPreferences("SubLatLng", 0);
        editor = someData.edit();
        map = (HashMap<String, String>) someData.getAll();


        Bundle basket = getIntent().getExtras();
         subject = basket.getString("subject", "no such subject");
        double distance = basket.getDouble("distance",500.00);
        tvSubject = (TextView)findViewById(R.id.tvSubject);
        tvDistance = (TextView)findViewById(R.id.tvDistance);
        tvSubject.setText("Subject :"+subject);
        tvDistance.setText("Distance : "+distance);

        bDone = (Button)findViewById(R.id.btDone);
        bSnooze = (Button)findViewById(R.id.btSnooze);
        bShow = (Button)findViewById(R.id.btExisting);

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB2 = Alert.this.openOrCreateDatabase("ReminderDatabase", MODE_PRIVATE, null);
                data = subject;
                String[] whereArgs = new String[]{String.valueOf(data)};
                myDB2.delete("myTable", "Subject=?", whereArgs);
                Toast.makeText(Alert.this, "Reminder is deleted successfully", Toast.LENGTH_SHORT).show();

                Set<Map.Entry<String, String>> se = map.entrySet();

                String delKey="";

                for (Map.Entry<String, String> me : se) {
                    if(me.getValue().equals(data))
                    {
                        delKey=me.getKey();
                        break;
                    }

                }
                editor.remove(delKey);
                editor.commit();
                map.remove(delKey);
                if(map.size()>0)
                setNextAlarm(30*1000);
                finish();

            }
        });

        bSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextAlarm(30 * 1000);// ideally it should be around 10 mins.
                Toast.makeText(getApplicationContext(), "Snoozed " ,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Alert.this,Existing.class);
                setNextAlarm(30*1000);
                startActivity(in);
                finish();

            }
        });

    }


    public void setNextAlarm(long next_alarm)
    {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(Alert.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                Alert.this, 0, mNotificationReceiverIntent, 0);


        // Set single alarm


        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + next_alarm,
                mNotificationReceiverPendingIntent);//UPGRADEABLE
        // Show Toast message
        Toast.makeText(getApplicationContext(), " Next alarm set .. " ,
                Toast.LENGTH_SHORT).show();

        //-------------------------------------

    }
}
