package com.me.qzproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class RegisterVKActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_vk);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_vk, menu);
		return true;
	}

}
