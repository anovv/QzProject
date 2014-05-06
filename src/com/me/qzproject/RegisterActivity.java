package com.me.qzproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	EditText email_v;
	EditText name_v;
	EditText password_v;
	Button confirm;
	ProgressDialog dialog;
	
	String email;
	String password;
	String name;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		email_v = (EditText) findViewById(R.id.email);
		name_v = (EditText) findViewById(R.id.fullname);
		password_v = (EditText) findViewById(R.id.password);
		confirm = (Button) findViewById(R.id.confirm_reg);
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				register();
			}			
		});
	}
	
	public void register(){

		email = email_v.getText().toString();
		password = password_v.getText().toString();
		name = name_v.getText().toString();
		
		if(isValidCredentials()){
			APIHandler.email = email;
			APIHandler.password = password;
			APIHandler.name = name;
			new Register().execute();
		}else{
			//TODO
		}
	}
	
	public boolean isValidCredentials(){
		//TODO
		return true;
	}
	
	class Register extends AsyncTask<String, String, String>{		
		
		boolean res;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		    dialog = new ProgressDialog(RegisterActivity.this);
		    dialog.setMessage("Registering...");
		    dialog.setCancelable(true);
		    dialog.show();
		}


		@Override
		protected String doInBackground(String... arg0) {
			res = APIHandler.register();
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	    
	        if(res){
	        	Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
	        }else{
	        	Toast.makeText(getApplicationContext(), APIHandler.error, Toast.LENGTH_LONG).show();	        	
	        }
		}
	}
}
