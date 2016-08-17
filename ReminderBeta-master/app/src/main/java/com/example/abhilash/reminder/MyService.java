
//this is working : the best


// Usally :time required for service to complete work: 1 SEC

package com.example.abhilash.reminder;

/**
 * Created by CHINMAY on 12-10-2015.
 */
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener//, com.google.android.gms.location.LocationListener
{
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }



    Intent in;
    int test;
    ArrayList<String> notify_subjects=new ArrayList<String>();
    ArrayList<Double> notify_distances=new ArrayList<Double>();



    //-------------
    PowerManager.WakeLock mWakeLock;

    NotificationManager nm;
    static final int notification_id = 136432;
    PendingIntent pi[];
    private final long[] mVibratePattern = { 0, 500 ,500 ,500,500,500 };
    //------------------------
    String toast = "not connected yet";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location myCurrentLoc;
    Location destLoc;
    double distance = 0;
    double min_distance = Double.MAX_VALUE;
    long next_alarm = 500;
    long next_alarm2= 0;
    String notify_subject;
    LocationManager locationManager;
    //------------repeat--------------------

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;
    private static final long INITIAL_ALARM_DELAY = 1 * 10 * 1000L;
    protected static final long JITTER = 5000L;

    //LocationListener ll;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;


    private final BroadcastReceiver mybroadcast = new AlarmNotificationReceiver();


    SharedPreferences someData;
    SharedPreferences.Editor editor;
    HashMap<String, String> map = new HashMap<String, String>();


    //--------------------------------------
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        //version 1.0----------------
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLockTag");
        mWakeLock.acquire();


        someData = getSharedPreferences("SubLatLng", 0);
        editor = someData.edit();
        map = (HashMap<String, String>) someData.getAll();

        if(map.size() == 0)
            stopSelf();

       // Set<Map.Entry<String, String>> se = map.entrySet();

        //min_distance=0;
        //  Log.e("In MyService", "Map size:" + map.size());
       /* for (Map.Entry<String, String> me : se) {

            if (me.getValue().contains("-")) {
                editor.remove(me.getKey());
                editor.apply();
                editor.commit();
            }

            Log.e("In MyService :", me.getKey() + " || " + me.getValue());

        }
        */


        //====================================================
        // setup google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //createLocationRequest();
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //tvCurrentPos.setText("changedLoc :" + location.getLatitude() + " " + location.getLongitude());
                locationManager.removeUpdates(this);
                myCurrentLoc = location;

                toast = myCurrentLoc.getLatitude() + " " + myCurrentLoc.getLongitude();
                Toast.makeText(MyService.this, toast, Toast.LENGTH_SHORT).show();

                Time now = new Time();
                now.setToNow();
                int cMonth = now.month+1;
                int cDay = now.monthDay;
                int cHour = now.hour;
                int cMin = now.minute;

                int minStartHr=23,maxEndHr=0;
                int toBeProccessed=0;
                Log.e("Time check",cMonth+" "+cDay+" "+cHour+" "+cMin );
                //compare with every entry in Map
                Set<Map.Entry<String, String>> se = map.entrySet();

                for (Map.Entry<String, String> me : se) {
                    String[] str = me.getKey().split(" ");

                    Log.e("SharedP :",me.getValue());

                    double latdest = Double.parseDouble(str[0]);
                    double longdest = Double.parseDouble(str[1]);
                    String sdate=str[2];
                    String edate=str[3];
                    String stime=str[4];
                    String etime=str[5];

                    String s[]=sdate.split("-");
                    int stMonth=Integer.parseInt(s[1]);
                    int stDay=Integer.parseInt(s[2]);

                    s=edate.split("-");
                    int etMonth=Integer.parseInt(s[1]);
                    int etDay=Integer.parseInt(s[2]);


                    String st[]=  stime.split(":");
                    int sthr=Integer.parseInt(st[0]);
                    int stmin =Integer.parseInt(st[1]);


                    st=  etime.split(":");
                    int ethr=Integer.parseInt(st[0]);
                    int etmin =Integer.parseInt(st[1]);
                    Log.e("temp",stDay+" "+etDay+" "+stime +"  "+etime);
                    //------
                    if(cMonth == stMonth){
                        if(cDay == stDay){
                            if(cHour == sthr){
                                if(cMin < stmin){
                                    continue;
                                }

                            }else if(cHour < sthr || cHour > ethr) continue;
                        }else if(cDay < stDay || cDay > etDay) continue;
                    }else if(cMonth < stMonth || cMonth > etMonth) continue;
                    //-------
                    toBeProccessed++;
                    Log.e("to be processed",":"+toBeProccessed);
                    Log.e("sdfsdfsd",minStartHr+" \\ "+maxEndHr);
                    if(minStartHr > sthr)
                        minStartHr = sthr;
                    if(maxEndHr < ethr )
                        maxEndHr = ethr;



                    destLoc = new Location("Destination");
                    destLoc.setLatitude(latdest);
                    destLoc.setLongitude(longdest);
                    distance = myCurrentLoc.distanceTo(destLoc);
                    Log.e("Required ",me.getKey()+" || "+me.getValue());

                    if(distance < 500)
                    {
                        notify_subjects.add(me.getValue());
                        notify_distances.add(distance);
                    }


                    if (min_distance > distance) {
                        notify_subject =me.getValue();
                        min_distance = distance;

                    }

                    //System.out.println(me.getKey()+"  --  "+me.getValue());
                }


                if(cHour > maxEndHr)// relax in night hours
                {
                    setNextAlarm( (minStartHr + 23 - maxEndHr )*60*60*1000  );
                    stopSelf();
                }

                Log.e("tag", "Map size :" + map.size() + " || min_dist:" + min_distance);
                if (min_distance < 500) {
                    Toast.makeText(MyService.this, "You are within 500 m of " + notify_subject, Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) MyService.this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds 5 times
                    v.vibrate(mVibratePattern, 5 );

                    //----------------------------------------------------------------
                  //  pi = new PendingIntent;
                 //   Intent not_intent[] = new Intent[10];

                    nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    //Intent not_intent = new Intent(MyService.this,Existing.class);

                    int i=notify_subjects.size();
                    do {
                        nm.cancel(i-1);
                      Intent   not_intent = new Intent(MyService.this,Alert.class);
                        String body = notify_subjects.get(i-1);
                        double distance = notify_distances.get(i-1);

                        not_intent.putExtra("subject", body);
                        not_intent.putExtra("distance", distance);

                        PendingIntent pi= PendingIntent.getActivity(MyService.this, i-1, not_intent, PendingIntent.FLAG_ONE_SHOT );


                        String title = "You are within 500 mts to this";

                        Notification n = new Notification(R.raw.android_app_icon, body
                                , System.currentTimeMillis());
                        n.setLatestEventInfo(MyService.this, body, title, pi);
                        n.flags |= Notification.FLAG_AUTO_CANCEL;
                        n.defaults = Notification.DEFAULT_ALL;
                        nm.notify(i-1, n);
                        i--;
                    }while(i>0);





                    //------------------------------------------------------------------

                    setNextAlarm(30*1000);
                    stopSelf();


                } else {

                    if (toBeProccessed == 0) // start date end date with in range but mornings first alarm
                    {
                        setNextAlarm((minStartHr - cHour) * 60 * 100);  // only considering hour difference
                    }else {
                        next_alarm = distaceToTime(min_distance);


                        setNextAlarm(next_alarm);
                    }
                }

                //     PowerManager powerManager  = (PowerManager) getSystemService(Context.POWER_SERVICE);
                //    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                //           "MyWakelockTag");
                //  wakeLock.release();

                stopSelf();//calls destroy method
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        //waits for onConnected CallBack
        //----------------------------

        // Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        // Toast.makeText(this, "after connectn: "+toast, Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();


        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Alarm.SERVICE_CREATED");
        //  registerReceiver(mybroadcast, filter);
        Log.e("tag", "BRciver registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mWakeLock.release();
        Toast.makeText(this, "Service Destroyed: " + toast, Toast.LENGTH_SHORT).show();


        //Vibrator v = (Vibrator) MyService.this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        // v.vibrate(500);


    }

    @Override
    public void onConnected(Bundle bundle) {
        //  myCurrentLoc= LocationServices.FusedLocationApi.getLastLocation(
        //         mGoogleApiClient);

        Log.d("tag-->", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        //startLocationUpdates();

        //  PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
        //        mGoogleApiClient, mLocationRequest,this);
        Log.e("tag--", "Location update started ..............: ");


        /*

        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();

        //------------------------------
        mLocationRequest =  LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        myCurrentLoc= LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
       // mLocationManager =(LocationManager)this.getSystemService(LOCATION_SERVICE);
        // mLocationManager.requestLocationUpdates();
        //mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        toast = myCurrentLoc.getLatitude() + " " + myCurrentLoc.getLongitude();
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

        //compare with every entry in Map
        Set<Map.Entry<String,String>> se=map.entrySet();

        for(Map.Entry<String,String> me : se)
        {
            String[] str = me.getValue().split(" ");
            double latdest = Double.parseDouble(str[0]);
            double longdest = Double.parseDouble(str[1]);
            destLoc = new Location("Destination");
            destLoc.setLatitude(latdest);
            destLoc.setLongitude(longdest);
            distance=myCurrentLoc.distanceTo(destLoc);
            //Log.e("tagrugby","Subject:"+me.getKey()+" || lat: "+str[0]+"   lod: "+str[1]+"  || distance:"+distance);

            if(min_distance > distance)
            {
                notify_subject=me.getKey();
                min_distance=distance;
            }

            //System.out.println(me.getKey()+"  --  "+me.getValue());
        }
        Log.e("tag","Map size :"+map.size()+" || min_dist:"+min_distance);
        if(min_distance < 500 )
        {
            Toast.makeText(MyService.this,"You are within 1 km of "+notify_subject,Toast.LENGTH_LONG).show();
            stopSelf();
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

        }
        else {
            //--------------------- Repeat-------------


            //--------------------- Repeat-------------

            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // Create an Intent to broadcast to the AlarmNotificationReceiver
            mNotificationReceiverIntent = new Intent(MyService.this,
                    AlarmNotificationReceiver.class);

            // Create an PendingIntent that holds the NotificationReceiverIntent
            mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                    MyService.this, 0, mNotificationReceiverIntent, 0);


            // Set single alarm

            next_alarm=distaceToTime(min_distance);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + next_alarm,
                    mNotificationReceiverPendingIntent);//UPGRADEABLE
            // Show Toast message
            Toast.makeText(getApplicationContext(), " Alarm Set nearest :"+notify_subject+" || "+min_distance,
                    Toast.LENGTH_SHORT).show();

            //-------------------------------------

        }
        stopSelf();//calls destroy method

        */
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /* @Override
    public void onLocationChanged(Location location) {
        Log.e("tag  ", "Firing onLocationChanged..............................................");
        // first of all stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        myCurrentLoc = location;



        toast = myCurrentLoc.getLatitude() + " " + myCurrentLoc.getLongitude();
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

        //compare with every entry in Map
        Set<Map.Entry<String,String>> se=map.entrySet();

        for(Map.Entry<String,String> me : se)
        {
            String[] str = me.getValue().split(" ");
            double latdest = Double.parseDouble(str[0]);
            double longdest = Double.parseDouble(str[1]);
            destLoc = new Location("Destination");
            destLoc.setLatitude(latdest);
            destLoc.setLongitude(longdest);
            distance=myCurrentLoc.distanceTo(destLoc);
            //Log.e("tagrugby","Subject:"+me.getKey()+" || lat: "+str[0]+"   lod: "+str[1]+"  || distance:"+distance);

            if(min_distance > distance)
            {
                notify_subject=me.getKey();
                min_distance=distance;
            }

            //System.out.println(me.getKey()+"  --  "+me.getValue());
        }
        Log.e("tag", "Map size :" + map.size() + " || min_dist:" + min_distance);
        if(min_distance < 500 )
        {
            Toast.makeText(MyService.this,"You are within 1 km of "+notify_subject,Toast.LENGTH_LONG).show();
            stopSelf();
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

        }
        else {
            //--------------------- Repeat-------------


            //--------------------- Repeat-------------

            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // Create an Intent to broadcast to the AlarmNotificationReceiver
            mNotificationReceiverIntent = new Intent(MyService.this,
                    AlarmNotificationReceiver.class);

            // Create an PendingIntent that holds the NotificationReceiverIntent
            mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                    MyService.this, 0, mNotificationReceiverIntent, 0);


            // Set single alarm

            next_alarm=distaceToTime(min_distance);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + next_alarm,
                    mNotificationReceiverPendingIntent);//UPGRADEABLE
            // Show Toast message
            Toast.makeText(getApplicationContext(), " Alarm Set nearest :"+notify_subject+" || "+min_distance,
                    Toast.LENGTH_SHORT).show();

            //-------------------------------------

        }
        stopSelf();//calls destroy method
    }
    */


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public long distaceToTime(double distance) {
        if(distance == Double.MAX_VALUE)
            return 30*1000;


        if(distance > 20*1000 )// 20KM
            return 20*60*1000; // 20 min'

        if(distance > 10*1000)    // change to 2  debug within 10 km
            return ( ((int)distance/1)/1000 )*60*1000;

        else
            return 30*1000;     // 30 sec atleast for dis == 500   that is 30 sec is fastest update time


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*public void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.e("tag--", "Location update started ..............: ");
    }*/


    public Location getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                //return lastKnownLocationGPS;
                return lastKnownLocationGPS;
            } else {
                Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                System.out.println("1::" + loc);//----getting null over here
                System.out.println("2::" + loc.getLatitude());
                return loc;
            }
        } else {
            return null;
        }

    }

    public void setNextAlarm(long next_alarm)
    {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(MyService.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MyService.this, 0, mNotificationReceiverIntent, 0);


        // Set single alarm


        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + next_alarm,
                mNotificationReceiverPendingIntent);//UPGRADEABLE
        // Show Toast message
        Toast.makeText(getApplicationContext(), " Alarm Set nearest :" + notify_subject + " || " + min_distance,
                Toast.LENGTH_SHORT).show();

        //-------------------------------------

    }
}