package com.aaa.wifidata;

import java.util.ArrayList;
import java.util.List;
import javax.security.auth.PrivateCredentialPermission;
import android.os.Bundle;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.text.Selection;
import android.view.Menu;
import android.widget.*;
import android.view.View;
import android.view.View.OnClickListener; 
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class NextActivity extends Activity {
	private SQLiteDatabase mydb=null;
    private final static String DATABASE_NAME = "MyDB.db";
    private final static String TABLE_NAME = "wifi_test";
    private final static String ID = "id";
    private final static String SSID = "SSID";
    private final static String RSSI = "RSSI";
    private final static String DETAIL = "Detail";
    private final static String CLEAN_TABLE = "delete from "+TABLE_NAME;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Button backButton = (Button) findViewById(R.id.button1);
        Button cleanButton = (Button) findViewById(R.id.button2);
        mydb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        showDate();
        mydb.close();
        
        backButton.setOnClickListener(
        		new OnClickListener(){
        			public void onClick(View v)
        			{
        				NextActivity.this.finish();
        			}
        		});
        cleanButton.setOnClickListener(
        		new OnClickListener() {
					public void onClick(View v) {
						mydb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
						mydb.execSQL(CLEAN_TABLE);
						showDate();
						mydb.close();
					}
				});
    }
    public void showDate()
    {
    	ListView lv = (ListView) findViewById(R.id.listView1);
    	lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData()));
    }
    private List<String> getData(){
        
        List<String> data = new ArrayList<String>();
        Cursor cur=mydb.query(TABLE_NAME, new String[]{ID,SSID,RSSI,DETAIL}, null, null, null, null, null);
        int count = cur.getCount();
        if(cur!=null&&count>=0)
        {
        	if(cur.moveToFirst())
        	{
        		do {
					String SSID = cur.getString(1);
					String RSSI = cur.getString(2);
					String DETAIL = cur.getString(3);
					data.add("SSID:"+SSID+"\t\tRSSI:"+RSSI+"\t\tDetail:"+DETAIL+"\n");
				} while (cur.moveToNext());
        	}
        }    
        cur.close();
        return data;
    }

}
