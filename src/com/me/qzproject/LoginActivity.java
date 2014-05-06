package com.me.qzproject;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	EditText email_v;
	EditText password_v;
	Button confirm;
	Button register;
	Button register_vk;	
	ProgressDialog dialog;
	
	Map<String, String> loginInfo;
	
	String email;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getPreferences(MODE_PRIVATE).edit().remove("qz_email").remove("qz_password").commit();		
		if(hasCredentials()){
			email = getSharedPreferences("qz_pref", MODE_PRIVATE).getString("qz_email", "");
			password = getSharedPreferences("qz_pref", MODE_PRIVATE).getString("qz_password", "");
			APIHandler.email = email;
			APIHandler.password = password;
			new Login().execute();
		}else{
			setContentView(R.layout.activity_login);	
			
			email_v = (EditText) findViewById(R.id.email_l);
			password_v = (EditText) findViewById(R.id.password_l);
			confirm = (Button) findViewById(R.id.confirm_l);
			register = (Button) findViewById(R.id.reg);
			register_vk = (Button) findViewById(R.id.reg_vk);
			
			confirm.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View view) {
					login();
				}			
			});
			
			register.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					//TODO start reg activity
				}
				
			});
			
			register_vk.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO start reg_vk activity;				
				}
				
			});
		}
	}
	
	public boolean hasCredentials(){
		return getSharedPreferences("qz_pref", MODE_PRIVATE).contains("qz_email") && getSharedPreferences("qz_pref", MODE_PRIVATE).contains("qz_password");
	}
	
	public void login(){
		email = email_v.getText().toString();
		password = password_v.getText().toString();
		APIHandler.email = email;
		APIHandler.password = password;
		new Login().execute();
	}
	
	class Login extends AsyncTask<String, String, String>{
				
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		    dialog = new ProgressDialog(LoginActivity.this);
		    dialog.setMessage("Login...");
		    dialog.setCancelable(true);
		    dialog.show();
		}


		@Override
		protected String doInBackground(String... arg0) {
			loginInfo = APIHandler.login();
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	    
	        //Toast.makeText(getApplicationContext(), email + "|" + password, Toast.LENGTH_LONG).show();
	        if(loginInfo != null){
	        	if(!hasCredentials()){
	        		getSharedPreferences("qz_pref", MODE_PRIVATE).edit().putString("qz_email", email).putString("qz_password", password).commit();	        		
	        	}
	        	//Toast.makeText(getApplicationContext(), loginInfo + "", Toast.LENGTH_LONG).show();
	        	APIHandler.user_id = loginInfo.get("id");
	        	APIHandler.key = loginInfo.get("key");
	        	APIHandler.signature = APIHandler.getHash(APIHandler.email, APIHandler.key);
	        	
	        	//TODO start menu activity;
	        	startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
	        }else{
	        	Toast.makeText(getApplicationContext(), APIHandler.error, Toast.LENGTH_LONG).show();	        	
	        }
		}
	}
}
