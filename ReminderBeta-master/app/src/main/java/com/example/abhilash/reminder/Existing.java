package com.example.abhilash.reminder;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
 * Created by Abhilash on 9/22/2015.
 */
public class Existing extends Activity {
    String data;
    ArrayList<String> details = new ArrayList<String>();
    ArrayAdapter<String> viewAdapter;
    SQLiteDatabase myDB2;
    Button cancel_btn, delete_btn;

    SharedPreferences someData;
    SharedPreferences.Editor editor;


    HashMap<String, String> map = new HashMap<String, String>();


    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB2 = this.openOrCreateDatabase("ReminderDatabase", MODE_PRIVATE, null);


        someData = getSharedPreferences("SubLatLng", 0);
        editor = someData.edit();
        map = (HashMap<String, String>) someData.getAll();



   /* Create a Table in the Database. */
        myDB2.execSQL("CREATE TABLE IF NOT EXISTS "
                + "myTable"
                + " ( Subject VARCHAR , Description VARCHAR , Location VARCHAR , SDate VARCHAR , EDate VARCHAR , Lat NUMBER , Lon NUMBER , STime VARCHAR ,ETime VARCHAR );");
        Cursor c = myDB2.rawQuery("SELECT * FROM " + "myTable", null);

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
        try {
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
                    data = data + "Date:" + sdate + "\n";

                    String edate = c.getString(Column8);
                    data = data + "Date:" + edate + "\n";

                    String Stime = c.getString(Column7);
                    data = data + " Start Time:" + Stime + "\n";

                    String Etime = c.getString(Column9);
                    data = data + "End Time:" + Etime + "\n";
                    details.add(data);
                } while (c.moveToNext());

                List<String> weekReminder = new ArrayList<String>(details);


                viewAdapter = new ArrayAdapter<String>(
                        this, // The current context (this activity)
                        R.layout.text_view_reminder, // The name of the layout ID.
                        R.id.list_txt, // The ID of the textview to populate.
                        weekReminder);

                final ListView lv2 = new ListView(this);

                lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String forecast = viewAdapter.getItem(position);
                        Toast.makeText(Existing.this, forecast, Toast.LENGTH_SHORT).show();
                    }
                });

                lv2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   final int pos, long id) {

                        //creation of dialog... that is it will pop up and promt user to delete reminder
                        final Dialog dialog = new Dialog(Existing.this);
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
                                String forecast = viewAdapter.getItem(pos);
                                Log.d("TagRugby","Idhar delete hua.......");
                                String[] tokens = forecast.split("[:\n]");
                                data = tokens[1];
                                String[] whereArgs = new String[]{String.valueOf(data)};
                                myDB2.delete("myTable", "Subject=?", whereArgs);
                                Toast.makeText(Existing.this, "Reminder is deleted successfully", Toast.LENGTH_SHORT).show();
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
                                editor.commit();


                                viewAdapter.remove(forecast);
                                lv2.setAdapter(viewAdapter);
                                setContentView(lv2);
                            }
                        });

                        lv2.setAdapter(viewAdapter);
                        setContentView(lv2);
                        return true;
                    }

                });
                lv2.setAdapter(viewAdapter);
                lv2.setPadding(20, 20, 20, 20);
                setContentView(lv2);
            }
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            setContentView(R.layout.no_reminders);
        }
    }
}