package com.aaa.wifidata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.R.integer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class WifiTest extends Activity {
	//Variables for database
	private SQLiteDatabase mydb=null;
	private final static String DATABASE_NAME = "MyDB.db";
	private final static String TABLE_NAME = "wifi_test";
	private final static String ID = "id";
	private final static String SSID = "SSID";
	private final static String RSSI = "RSSI";
	private final static String DETAIL = "Detail";
	private final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+ID+" INTEGER PRIMARY KEY,"+SSID+" TEXT,"+RSSI+" TEXT,"+DETAIL+" TEXT)";
	
	private static Boolean isRecordData = false;
	private int counter = 0;
	private static int recordTimesAtEachPoint = 20;
    private ListView listView;
	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
	private Button saveButton;
	private Button seeButton;
	private Button selectButton;
	private String[] testWifi = {"AP1","AP2","AP3","AP4","AP5","AP6","AP7","AP8"};
	private String comment = "0";
	private List<ScanResult> scanWifiList;
	private List<Map<String, Object>> showWifiList;
	
//	private Timer myTimer = null;
//    private TimerTask myTimerTask = null;
	final Handler scanHandler = new Handler();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mydb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        try {
			mydb.execSQL(CREATE_TABLE);
		} catch (Exception e) {
			// TODO: handle exception
		}
        mydb.close();
//        listView = new ListView(this);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_wifi_test);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveListener);
        seeButton = (Button) findViewById(R.id.seeButton);
        seeButton.setOnClickListener(showListener);
        selectButton = (Button) findViewById(R.id.selectbtn);
        selectButton.setOnClickListener(selectListener);
        
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        startTimer();
//        wifiManager.startScan();
        scanHandler.post(scanRunnable);
    }
    ///****保存当前测试的wifi数据****/
    private OnClickListener saveListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(WifiTest.this, CommentDialog.class);
			int i = Integer.parseInt(comment);
			i++;
			intent.putExtra("comment", String.valueOf(i));
			startActivityForResult(intent, 1);
		}
	}; 
	///****筛选需要测试的wifi****/
	private OnClickListener selectListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(WifiTest.this, InputDialog.class);
			Bundle bundle = new Bundle();
			bundle.putStringArray("wifiSSID", testWifi);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		}
	};
	///****查看保存的数据****/
	private OnClickListener showListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(WifiTest.this, NextActivity.class);
			startActivity(intent);
//			WifiTest.this.finish();
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 0 && data!=null) {
			Bundle bundle = data.getExtras();
			testWifi = bundle.getStringArray("testWifiList");
		}
		if (requestCode == 1 && resultCode == 1) {
			comment = data.getExtras().get("comment").toString();
			isRecordData = true;
		}
	}
	
	///****每隔一段时间刷新wifi列表****/
	Runnable scanRunnable = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
//			Log.i("scanRunnable", "startScan");
			wifiManager.startScan();
			//scanHandler.postDelayed(this, 500);
		}
	};
	
//	public void startTimer() {
//		if (myTimer == null) {
//			myTimerTask = new TimerTask() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					wifiManager.startScan();
//					Log.i("startTimer", "startScan");
//				}
//			};
//			myTimer = new Timer();
//			myTimer.schedule(myTimerTask, 0, 500);
//			
//		}
//	}
//	
//	public void closeTimer() {
//		if (myTimer != null) {
//			myTimer.cancel();
//			myTimer = null;
//		}
//		if (myTimerTask != null) {
//			myTimerTask = null;
//		}
//	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0,"Refresh");
        menu.add(0, 1, 0, "stop");
        return true;
    } 
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case 0:
//			startTimer();
			scanHandler.post(scanRunnable);
			break;
		case 1:
//			closeTimer();
			scanHandler.removeCallbacks(scanRunnable);
		default:
			break;
		}
    	return super.onMenuItemSelected(featureId, item); 
    }
    
    protected void onPause() {
    	unregisterReceiver(wifiReceiver);
    	scanHandler.removeCallbacks(scanRunnable);
//    	closeTimer();
    	super.onPause();
    }
    
    protected void onResume() {
    	registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
    	scanHandler.post(scanRunnable);
//    	startTimer();
    	super.onResume();
    }
    
    class WifiReceiver extends BroadcastReceiver {
    	public void onReceive(Context c, Intent intent){
    		//Once scan result is available, start another scan
    		scanHandler.post(scanRunnable);
    		
    		listView = (ListView) findViewById(R.id.wifiListView);
    		SimpleAdapter adapter = new SimpleAdapter(c, getData(testWifi), R.layout.wlist, new String[]{"SSID","BSSID","wifiFrequency","wifiLevel"}, new int[]{R.id.SSID,R.id.BSSID,R.id.wifiFrequency,R.id.wifiLevel});
    		listView.setAdapter(adapter);
    		if (isRecordData) {
    			mydb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    			for (int i = 0; i < showWifiList.size(); i++) {
    				ContentValues wifiValues = new ContentValues(); //键值对应
    				wifiValues.put(SSID, showWifiList.get(i).get("SSID").toString());
    				wifiValues.put(RSSI, showWifiList.get(i).get("wifiLevel").toString());
    				wifiValues.put(DETAIL,comment);
    				mydb.insert(TABLE_NAME, null, wifiValues);
    				wifiValues.clear();
    			}
    			mydb.close();
    			counter++;
//    			Toast.makeText(getApplicationContext(), "正在保存第"+counter+"个数据", Toast.LENGTH_SHORT).show();
    			if (counter >= recordTimesAtEachPoint) {
					counter = 0;
					isRecordData = false;
//					Toast.makeText(getApplicationContext(), "保存完成", Toast.LENGTH_SHORT).show();
					AlertDialog.Builder builder = new Builder(WifiTest.this);
					builder.setMessage("保存完成");
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
    	}
    	
    	private List<Map<String, Object>> getData(String[] testWifi) {
    		scanWifiList = wifiManager.getScanResults();
    		showWifiList = new ArrayList<Map<String,Object>>();
			Map<String, Object> map;
			if (testWifi.length == 0) {
				for (int i = 0; i < scanWifiList.size(); i++) {
					map = new HashMap<String, Object>();
					map.put("SSID", scanWifiList.get(i).SSID);
					map.put("BSSID", scanWifiList.get(i).BSSID);
					map.put("wifiFrequency", scanWifiList.get(i).frequency);
					map.put("wifiLevel", scanWifiList.get(i).level);
					showWifiList.add(map);
				}
			}
			else {
				for (int i = 0; i < scanWifiList.size(); i++) {
					for (int j = 0; j < testWifi.length; j++) {
						if (testWifi[j].equals(scanWifiList.get(i).SSID)) {
							map = new HashMap<String, Object>();
							map.put("SSID", scanWifiList.get(i).SSID);
							map.put("BSSID", scanWifiList.get(i).BSSID);
							map.put("wifiFrequency", scanWifiList.get(i).frequency);
							map.put("wifiLevel", scanWifiList.get(i).level);
							showWifiList.add(map);
						}
					}
				}
			}
			return showWifiList;
		}
    }
}

