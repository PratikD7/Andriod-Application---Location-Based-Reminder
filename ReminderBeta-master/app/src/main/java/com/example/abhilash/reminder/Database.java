package com.example.abhilash.reminder;

 import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;


/**
 * Created by Abhilash on 8/27/2015.
 */
public class Database extends Activity {
     ArrayList<String> details = new ArrayList<String>();
     ArrayAdapter<String> reminderAdapter;
    String data="";
    Button cancel_btn, delete_btn;
    SQLiteDatabase  myDB = null;

    SharedPreferences someData;
    SharedPreferences.Editor editor;
    HashMap<String, String> map = new HashMap<String, String>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String TableName = "myTable";


        someData = getSharedPreferences("SubLatLng",0);
        editor = someData.edit();

        map = (HashMap<String, String>) someData.getAll();


  /* Create a Database. */
        try {

            myDB = this.openOrCreateDatabase("ReminderDatabase", MODE_PRIVATE, null);
   /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " ( Subject VARCHAR , Description VARCHAR , Location VARCHAR , SDate VARCHAR , EDate VARCHAR , Lat NUMBER , Lon NUMBER , STime VARCHAR , ETime VARCHAR );");


            ContentValues values = new ContentValues();
            values.put("Subject", MainActivity.sub);
            values.put("Description", MainActivity.des);
            values.put("Location", AddLocation.address);
            Log.e("TagRugby", AddLocation.address + "address");
            values.put("Lon", AddLocation.lng);
            values.put("Lat", AddLocation.lat);
            values.put("SDate", MainActivity.sdate);
            values.put("EDate", MainActivity.edate);
            values.put("STime", MainActivity.time);
            values.put("ETime", MainActivity.time2);

            myDB.insert("myTable", null, values);

            editor.putString(AddLocation.lat + " " + AddLocation.lng+" "+MainActivity.sdate+" "+MainActivity.edate
                    +" "+MainActivity.time+" "+MainActivity.time2,
                    MainActivity.sub);
            editor.commit();
            startService(new Intent(this,MyService.class));


   /*retrieve data from database */
            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);

            int Column1 = c.getColumnIndex("Subject");
            int Column2 = c.getColumnIndex("Description");
            int Column3 = c.getColumnIndex("Location");
            int Column4 = c.getColumnIndex("Lon");
            int Column5 = c.getColumnIndex("Lat");
            int Column6 = c.getColumnIndex("SDate");
            int Column8 = c.getColumnIndex("EDate");
            int Column7 = c.getColumnIndex("STime");
            int Column9 = c.getColumnIndex("ETime");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String subject = c.getString(Column1);
                    data = "Subject:" + subject + "\n";
                    String desc = c.getString(Column2);
                    data = data + "Description:" + desc+ "\n";
                    String location = c.getString(Column3);
                    data = data + "Location:" + location+ "\n";
                    double Lon = c.getDouble(Column4);
                    data = data + "Longitude:" + Lon + "\n";
                    double Lat = c.getDouble(Column5);
                    data = data + "Latitude:" + Lat + "\n";
                    String sdate = c.getString(Column6);
                    data = data + "Start Date:" + sdate + "\n";

                    String edate = c.getString(Column8);
                    data = data + "End Date:" + edate + "\n";

                    String Stime = c.getString(Column7);
                    data = data + "Start Time:" + Stime + "\n";

                    String Etime = c.getString(Column9);
                    data = data + "Start Time:" + Etime + "\n";
                    details.add(data);

                    editor.putString(Lat+" "+Lon+" "+sdate+" "+edate+" "+Stime+" "+Etime,subject);
                    //Des.add(desc);
                    //Loc.add(location);

                } while (c.moveToNext());

                editor.commit();

                List<String> weekReminder = new ArrayList<String>(details);


                reminderAdapter = new ArrayAdapter<String>(
                        this, // The current context (this activity)
                        R.layout.text_view_reminder, // The name of the layout ID.
                        R.id.list_txt, // The ID of the textview to populate.
                        weekReminder);

                final ListView lv = new ListView(this);
                lv.setPadding(20, 20, 20, 20);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String forecast = reminderAdapter.getItem(position);
                        Toast.makeText(Database.this, forecast, Toast.LENGTH_SHORT).show();
                    }
                });



                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   final int pos, long id) {

                        //creation of dialog... that is it will pop up and promt user to delete reminder
                        final Dialog dialog = new Dialog(Database.this);
                        dialog.setTitle("Delete");
                        dialog.setContentView(R.layout.delete);
                        dialog.show();

                        //cancel deletion of reminder
                        cancel_btn = (Button) dialog.findViewById(R.id.cancel);
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }


                        });

                        //delete reminder from database
                        delete_btn = (Button) dialog.findViewById(R.id.del);
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String forecast = reminderAdapter.getItem(pos);
                                String[] tokens = forecast.split("[:\n]");
                                data = tokens[1];
                                String[] whereArgs = new String[]{String.valueOf(data)};
                                myDB.delete(TableName, "Subject=?", whereArgs);
                                Toast.makeText(Database.this, "Reminder is deleted successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

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
                                Log.e("delete ",delKey+"||||||||||"+map.get(delKey));
                                editor.commit();

                                reminderAdapter.remove(forecast);
                                lv.setAdapter(reminderAdapter);
                                setContentView(lv);
                                data = "";
                            }


                        });
                        return true;
                    }
                });

                // auto delete reminders after 10 days
                //String[] date = new String[15] ;
                //Cursor d = myDB.rawQuery("SELECT * FROM " + TableName, null);
                //int autoDel = c.getColumnIndex("Date");








                lv.setAdapter(reminderAdapter);
                setContentView(lv);
            }
        }catch (Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (myDB != null) {
                //myDB.close();
                details.clear();
                data = "";
                //Loc.clear();
                //Des.clear();
            }
        }

    }


    public void onBackPressed()
    {
    //     code here to show dialog
        super.onBackPressed();


        Intent i = new Intent(Database.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        startActivity(i);
       // startActivity(new Intent(this,MainActivity.class));// optional depending on your needs
    }

}




