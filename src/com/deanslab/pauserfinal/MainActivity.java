package com.deanslab.pauserfinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences prefs = this.getSharedPreferences("PAUSER_DATA", Context.MODE_PRIVATE);
		String prefName = prefs.getString("SAVED_NAME", " ");
		if (!prefName.equals(" ")) {
			((EditText) (findViewById(R.id.nameField))).setText(prefName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void listenClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Initializing ...");
			dialog.show();
			new Handler().postDelayed(new Runnable(){
			    public void run() {
			    	dialog.dismiss();
			    }
			}, 1500);			
			String name = ((EditText) (findViewById(R.id.nameField))).getText().toString();
			name.trim();
			if (name.length() > 0) {
				SharedPreferences prefs = this.getSharedPreferences("PAUSER_DATA", Context.MODE_PRIVATE);
				Editor editor = prefs.edit();
				editor.putString("SAVED_NAME", name);
				editor.commit();
				Intent intent = new Intent(this, ListenerService.class);
				intent.putExtra("NAME", name);
				startService(intent);
			}
		} else {
			Intent intent = new Intent(this, ListenerService.class);
			stopService(intent);
			final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Destroying ...");
			dialog.show();
			new Handler().postDelayed(new Runnable(){
			    public void run() {
			    	dialog.dismiss();
			    }
			}, 1000);	
		}
	}
	
}
