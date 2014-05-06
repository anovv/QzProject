package com.me.qzproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public int theme_id = 1;
	
	public int score = 228;
	
	public ArrayList<Map<String, String>> game;
	
	public Map<String, String> answers = new HashMap<String, String>();

	public ProgressDialog dialog;
	
	public TextView question;
	
	public Button ans1;

	public Button ans2;

	public Button ans3;

	public Button ans4;
	
	public int rightAns;
	
	public OnClickListener listener;
	
	public int round = 0;
	
	public long startTime = 0;
	
	public String ansSeq;
	
	Map<String, String> cur;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		new GetGame().execute();
	}	
	
	class GetGame extends AsyncTask<String, String, String>{		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    dialog = new ProgressDialog(MainActivity.this);
		    dialog.setMessage("Pending...");
		    dialog.setCancelable(true);
		    dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			game = APIHandler.getSinglePlayer(theme_id);
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	    
	        if(game == null){
	        	Toast.makeText(getApplicationContext(), "buck buck", 1000).show();
	        }else{
	        	question = (TextView) findViewById(R.id.tv);
				ans1 = (Button) findViewById(R.id.b1);
				ans2 = (Button) findViewById(R.id.b2);
				ans3 = (Button) findViewById(R.id.b3);
				ans4 = (Button) findViewById(R.id.b4);
						
				listener = new OnClickListener(){

					@Override
					public void onClick(View view) {
						if(view.getId() == R.id.b1){
							ansSeq+="0#" + (System.currentTimeMillis() - startTime) + "_";
							if(rightAns == 0){	
								Toast.makeText(getApplicationContext(), "Right", 1000).show();
								getNextQuestion();
							}else{
								Toast.makeText(getApplicationContext(), "Wrong", 1000).show();	
								ans1.setClickable(false);
							}
						}else if(view.getId() == R.id.b2){
							ansSeq+="1#" + (System.currentTimeMillis() - startTime) + "_";
							if(rightAns == 1){	
								Toast.makeText(getApplicationContext(), "Right", 1000).show();
								getNextQuestion();
							}else{
								Toast.makeText(getApplicationContext(), "Wrong", 1000).show();	
								ans2.setClickable(false);									
							}
						}else if(view.getId() == R.id.b3){
							ansSeq+="2#" + (System.currentTimeMillis() - startTime) + "_";
							if(rightAns == 2){	
								Toast.makeText(getApplicationContext(), "Right", 1000).show();
								getNextQuestion();
							}else{
								Toast.makeText(getApplicationContext(), "Wrong", 1000).show();		
								ans3.setClickable(false);								
							}
						}else{
							ansSeq+="3#" + (System.currentTimeMillis() - startTime) + "_";
							if(rightAns == 3){	
								Toast.makeText(getApplicationContext(), "Right", 1000).show();
								getNextQuestion();
							}else{
								Toast.makeText(getApplicationContext(), "Wrong", 1000).show();	
								ans4.setClickable(false);									
							}
						}
					}				
				};
						
				ans1.setOnClickListener(listener);
				ans2.setOnClickListener(listener);
				ans3.setOnClickListener(listener);
				ans4.setOnClickListener(listener);		
				
				getNextQuestion();
	        }
	 	}		
	}
	
	public void getNextQuestion(){
		int num = game.size();
		if(ansSeq != null){
			answers.put(cur.get("id"), ansSeq.substring(0, ansSeq.length() - 1));
		}
		if(round < num){			
			cur = game.get(round);
			
			MainActivity.this.runOnUiThread(new Runnable(){
	
				@Override
				public void run() {
					ans1.setClickable(true);
					ans2.setClickable(true);
					ans3.setClickable(true);
					ans4.setClickable(true);
					question.setText(cur.get("question") + "| answer is " + cur.get("right_ans"));
					ans1.setText(cur.get("ans1"));
					ans2.setText(cur.get("ans2"));
					ans3.setText(cur.get("ans3"));
					ans4.setText(cur.get("ans4"));
					rightAns = Integer.parseInt(cur.get("right_ans"));	
					startTime = System.currentTimeMillis();
				}					
			});		
			round++;	
		}else{			
			TextView r = (TextView) findViewById(R.id.res);
			r.setText(answers + "");
			ans1.setClickable(false);
			ans2.setClickable(false);
			ans3.setClickable(false);
			ans4.setClickable(false);
			new SaveGame().execute();
		}		
		ansSeq = "";
	}
	
	class SaveGame extends AsyncTask<String, String, String>{		

		boolean res = false;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    dialog = new ProgressDialog(MainActivity.this);
		    dialog.setMessage("Saving...");
		    dialog.setCancelable(true);
		    dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.saveGame(answers, theme_id, score);
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	    
	        if(res){
	        	Toast.makeText(getApplicationContext(), "saved", 1000).show();
	        }else{
	        	Toast.makeText(getApplicationContext(), "not saved " + APIHandler.error, 1000).show();	        	
	        }
	 	}		
	}	
}
