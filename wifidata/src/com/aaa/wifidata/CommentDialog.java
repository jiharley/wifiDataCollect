package com.aaa.wifidata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CommentDialog extends Activity {
	private EditText commentEntry;
	private String dataComment;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentdialog);
		commentEntry = (EditText) findViewById(R.id.dataComment);
		dataComment = this.getIntent().getStringExtra("comment");
		commentEntry.setText(dataComment);
		
		((Button) findViewById(R.id.commentConfirm)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent backIntent = CommentDialog.this.getIntent().putExtra("comment", commentEntry.getText());
				CommentDialog.this.setResult(1, backIntent);
				CommentDialog.this.finish();
			}
		});
		((Button) findViewById(R.id.commentCancle)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommentDialog.this.finish();
			}
		});
	}
}
