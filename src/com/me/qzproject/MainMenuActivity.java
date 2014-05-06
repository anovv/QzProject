package com.me.qzproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
	
	Button logout;	
	Button friends;
	Button profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				logout();
			}			
		});
		
		friends = (Button) findViewById(R.id.friends);
		friends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenuActivity.this, FriendsActivity.class));
			}
			
		});
		
		profile = (Button) findViewById(R.id.profile);
		profile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
	        	bundle.putString("id", APIHandler.user_id);            	
	        	Intent intent = new Intent(MainMenuActivity.this, UserActivity.class);
	        	intent.putExtras(bundle); 
	        	startActivity(intent);            
			}
			
		});
	}
	
	public void logout(){
		new Logout().execute();
	}
	
	class Logout extends AsyncTask<String, String, String>{
		
		ProgressDialog dialog;
		
		boolean res;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		    dialog = new ProgressDialog(MainMenuActivity.this);
		    dialog.setMessage("Log out...");
		    dialog.setCancelable(true);
		    dialog.show();
		}


		@Override
		protected String doInBackground(String... arg0) {
			res = APIHandler.logout();
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	    
	        if(res){
	    		getSharedPreferences("qz_pref", MODE_PRIVATE).edit().remove("qz_email").remove("qz_password").commit();
	        	//Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
	        	//TODO close current activity
	        	startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
	        }else{
	        	Toast.makeText(getApplicationContext(), APIHandler.error, Toast.LENGTH_LONG).show();	        	
	        }
		}
	}

}
