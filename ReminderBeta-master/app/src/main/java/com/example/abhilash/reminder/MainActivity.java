package com.example.abhilash.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;





    public static String sub;
    public static String des;
    public static String loc;
    public static String sdate;
    public static String edate;
    public static String time;

    public static String time2;

    public static double Lat = 12.09;
    public static double Lon= 99.76;

    SharedPreferences someData;
    SharedPreferences.Editor editor;
    HashMap<String, String> map = new HashMap<String, String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        someData = getSharedPreferences("SubLatLng",0);
        editor = someData.edit();
        map = (HashMap<String, String>) someData.getAll();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addReminder(View view)
    {
        //TextView str= (TextView) findViewById(R.id.hello_text_view);
        setContentView(R.layout.reminder_ui);
    }

    public void setLocation(View view){
        EditText text1 = (EditText) findViewById(R.id.subject);
        sub = text1.getText().toString();
        EditText text2 = (EditText) findViewById(R.id.description);
        des = text2.getText().toString();


        DatePicker dp1 =(DatePicker) findViewById(R.id.datePicker1);
        sdate = String.valueOf(dp1.getYear())+"-"+String.valueOf(dp1.getMonth()+1)+"-"+String.valueOf(dp1.getDayOfMonth());

        DatePicker dp2 =(DatePicker) findViewById(R.id.datePicker2);
        edate = String.valueOf(dp2.getYear())+"-"+String.valueOf(dp2.getMonth() + 1)+"-"+String.valueOf(dp2.getDayOfMonth());

        TimePicker tp =(TimePicker) findViewById(R.id.timePicker);
        time = tp.getCurrentHour()+":"+tp.getCurrentMinute();

        TimePicker tp2 =(TimePicker) findViewById(R.id.timePicker2);
        time2 = tp2.getCurrentHour()+":"+tp2.getCurrentMinute();

        if (sub == null)
            Toast.makeText(MainActivity.this,"Please enter subject!!",Toast.LENGTH_LONG).show();
        else
        startActivity(new Intent(MainActivity.this, AddLocation.class));
    }

    public void viewReminders(View view)
    {

        startActivity(new Intent(MainActivity.this, Existing.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("App killed", "App destroyed start service :" + map.size());
        setNextAlarm(30*1000);
       // startService(new Intent(this, MyService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void setNextAlarm(long next_alarm)
    {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);


        // Set single alarm


        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + next_alarm,
                mNotificationReceiverPendingIntent);//UPGRADEABLE
        // Show Toast message
        Toast.makeText(getApplicationContext(), " Next alarm set .. ",
                Toast.LENGTH_SHORT).show();

        //-------------------------------------

    }

}
