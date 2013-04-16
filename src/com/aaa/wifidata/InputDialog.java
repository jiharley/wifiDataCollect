package com.aaa.wifidata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InputDialog extends Activity {
	private EditText ssidInput;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputdialog);

		ssidInput = (EditText) findViewById(R.id.inputSSID);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String[] ssidList = bundle.getStringArray("wifiSSID");
		String editString = "";
		for (int i = 0; i < ssidList.length; i++) {
			editString += ssidList[i]+",";
		}
		ssidInput.setText(editString);
		((Button) findViewById(R.id.confirmbtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle backBundle = new Bundle();
				String[] backList = ssidInput.getText().toString().split(",");
				backBundle.putStringArray("testWifiList", backList);
				Intent backIntent = InputDialog.this.getIntent().putExtras(backBundle);
				InputDialog.this.setResult(0, backIntent);
				InputDialog.this.finish();
			}
		});
		((Button) findViewById(R.id.canclebtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputDialog.this.finish();
			}
		});
	}
	
}
