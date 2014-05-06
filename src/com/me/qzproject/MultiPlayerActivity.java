package com.me.qzproject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MultiPlayerActivity extends Activity {
	
	String LOG_TAG = "qz";
		
	String message;
	
	int oppScore = 0;
	
	int playerScore = 0;
	
	public Map<String, Map<String, String>> game;
	
	ProgressDialog dialog;
	
	public int theme_id = 1;
		
	public volatile boolean isRunning = false;
	
	public volatile boolean isCountDownRunning = false;
	
	public volatile int round = 0;
		
	public int score = 228;	
	
	public Map<String, String> answers = new HashMap<String, String>();	
	
	public StartOpponent so;
	
	public volatile Thread countDownThread;
		
	public TextView question;
	
	TextView countDown;
	
	ProgressBar pb;
	
	public Button ans1;

	public Button ans2;

	public Button ans3;

	public Button ans4;
	
	public int rightAns;
	
	public OnClickListener listener;
		
	public long startTime = 0;
	
	public String ansSeq;
	
	Map<String, String> cur;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp);			
		
		new GetGame().execute();
	}
	
	class GetGame extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    dialog = new ProgressDialog(MultiPlayerActivity.this);
		    dialog.setMessage("Pending...");
		    dialog.setCancelable(true);
		    dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			game = APIHandler.getMultiPlayer(theme_id);
			//game = APIHandler.getFakeMP();			
			return null;
		}
		
		protected void onPostExecute(String unused) {
	        dialog.dismiss();	   
	        if(game != null){
	        	startPlayer();
	        	getNextQuestion();
	        	//startOpponent();
	        	//Toast.makeText(getApplicationContext(), "gud", 1000).show();
	        }else{
	        	Toast.makeText(getApplicationContext(), APIHandler.error, 1000).show();
	        }
		}
	}
	
	public void startOpponent(){
		//getNextQuestion();
		so = new StartOpponent();
		isRunning = true;
		so.execute();
		//so.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void startCountDown(){
		
		countDownThread = new Thread(new Runnable(){

			@Override
			public void run() {
				long time = 10*1000;
				while(time>= 0 && !Thread.currentThread().isInterrupted()){
					final long temp = time;
					MultiPlayerActivity.this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							countDown.setText(temp/1000 + "");
							pb.setProgress(1000 - (int)temp/10);
						}
						
					});
					time -= 1;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				if(!Thread.currentThread().isInterrupted()){
					stopOpponent();
					MultiPlayerActivity.this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerActivity.this);
							builder.setTitle("Time is up")
									.setMessage("Ready for the next question?")
									.setCancelable(false)
									.setNegativeButton("Ready",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												round++;
												dialog.cancel();
												getNextQuestion();
												//startOpponent();
												
											}
										});
							AlertDialog alert = builder.create();
							alert.show();		
						}
						
					});
				}
			}
			
		});
		
		countDownThread.start();
	}
	
	public void stopCountDown(){
		countDownThread.interrupt();
		/*try {
			countDownThread.join();
		} catch (InterruptedException e) {
			
		}*/
		//join? - nope
	}
	
	class StartOpponent extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			while(round <= game.size() - 1 && isRunning){
				if(round < game.size() - 1){				
						
					String ansSeq = game.get("question_" + round).get("ans_seq");
					String[] ans = ansSeq.split("_");				
					long prev = 0;
					int i = 0;
					while(i < ans.length && isRunning){
						String s = ans[i];
						String[] temp = s.split("#");
						final int questionNum = Integer.parseInt(temp[0]);
						long time = Long.parseLong(temp[1]);
						try {
							TimeUnit.MILLISECONDS.sleep(time - prev);
						} catch (InterruptedException e) {
							
						}		
						prev = time;	
						boolean isRight = questionNum == rightAns;//?
						i++;
						if(isRunning){
							if(isRight){
								message = "Op won";
								Log.d(LOG_TAG, "Op won " + round);
								oppScore++;
								round++;
								animateIndicator(findViewById(R.id.opp_ind), true);
								makePause();
								stopCountDown();
								stopOpponent();
							}else{
								animateIndicator(findViewById(R.id.opp_ind), false);
							}
						}
					}
				}else{
					handleFinish();
				}
			}
			return null;
		}		
	}
	
	public void animateIndicator(final View ind, final boolean right){
		new Thread(new Runnable(){

			@Override
			public void run() {
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if(right){
							ind.setBackgroundColor(getResources().getColor(R.color.Green));
						}else{
							ind.setBackgroundColor(getResources().getColor(R.color.Red));							
						}
					}
					
				});
				
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					
				}
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						ind.setBackgroundColor(getResources().getColor(R.color.White));
					}
					
				});
			}
			
		}).start();
	}
		
	public void stopOpponent(){
		isRunning = false;
		so.cancel(true);
	}
	
	public void startPlayer(){
		pb = (ProgressBar) findViewById(R.id.pb);
		countDown = (TextView) findViewById(R.id.countdown_m);
		question = (TextView) findViewById(R.id.tv_m);
		ans1 = (Button) findViewById(R.id.b1_m);
		ans2 = (Button) findViewById(R.id.b2_m);
		ans3 = (Button) findViewById(R.id.b3_m);
		ans4 = (Button) findViewById(R.id.b4_m);
						
		listener = new OnClickListener(){

			@Override
			public void onClick(View view) {
				if(view.getId() == R.id.b1_m){
					ansSeq+="0#" + (System.currentTimeMillis() - startTime) + "_";
					if(rightAns == 0){	
						stopOpponent();
						Log.d(LOG_TAG, "You won " + round);
						message = "You won";
						playerScore++;
						round++;
						animateIndicator(findViewById(R.id.player_ind), true);
						stopCountDown();
						makePause();
						//makePause();
						//getNextQuestion();
						//startOpponent();
					}else{
						Toast.makeText(getApplicationContext(), "Wrong", 300).show();	
						ans1.setClickable(false);
						animateIndicator(findViewById(R.id.player_ind), false);
						//ans1.setVisibility(8);
					}
				}else if(view.getId() == R.id.b2_m){
					ansSeq+="1#" + (System.currentTimeMillis() - startTime) + "_";
					if(rightAns == 1){		
						stopOpponent();
						Log.d(LOG_TAG, "You won " + round);
						message = "You won";
						playerScore++;
						round++;
						animateIndicator(findViewById(R.id.player_ind), true);
						stopCountDown();
						makePause();
						//makePause();
						//getNextQuestion();
						//startOpponent();
					}else{
						Toast.makeText(getApplicationContext(), "Wrong", 300).show();	
						ans2.setClickable(false);	
						animateIndicator(findViewById(R.id.player_ind), false);
						//ans2.setVisibility(8);								
					}
				}else if(view.getId() == R.id.b3_m){
					ansSeq+="2#" + (System.currentTimeMillis() - startTime) + "_";
					if(rightAns == 2){		
						stopOpponent();
						Log.d(LOG_TAG, "You won " + round);
						message = "You won";
						playerScore++;
						round++;
						animateIndicator(findViewById(R.id.player_ind), true);
						stopCountDown();
						makePause();
						//makePause();
						//getNextQuestion();
						//startOpponent();
					}else{
						Toast.makeText(getApplicationContext(), "Wrong", 300).show();		
						ans3.setClickable(false);
						animateIndicator(findViewById(R.id.player_ind), false);	
						//ans3.setVisibility(8);							
					}
				}else{
					ansSeq+="3#" + (System.currentTimeMillis() - startTime) + "_";
					if(rightAns == 3){	
						stopOpponent();
						Log.d(LOG_TAG, "You won " + round);
						message = "You won";
						playerScore++;
						round++;
						animateIndicator(findViewById(R.id.player_ind), true);
						stopCountDown();
						makePause();
						//getNextQuestion();
						//startOpponent();
					}else{
						Toast.makeText(getApplicationContext(), "Wrong", 300).show();	
						ans4.setClickable(false);
						animateIndicator(findViewById(R.id.player_ind), false);
						//ans4.setVisibility(8);									
					}
				}
			}				
		};
				
		ans1.setOnClickListener(listener);
		ans2.setOnClickListener(listener);
		ans3.setOnClickListener(listener);
		ans4.setOnClickListener(listener);			
	}
	
	public void getNextQuestion(){
		if(ansSeq != null && ansSeq.length() > 0){
			answers.put(cur.get("id"), ansSeq.substring(0, ansSeq.length() - 1));
		}
		if(round < game.size() - 1){	
			
			//startCountDown();		
			cur = game.get("question_" + round);
			
			MultiPlayerActivity.this.runOnUiThread(new Runnable(){
	
				@Override
				public void run() {
					
					ans1.setClickable(false);
					ans2.setClickable(false);
					ans3.setClickable(false);
					ans4.setClickable(false);
									
					question.setText(cur.get("question") + "| answer is " + cur.get("right_ans"));
					countDown.setText("");
					pb.setProgress(0);
					ans1.setText(cur.get("ans1"));
					ans2.setText(cur.get("ans2"));
					ans3.setText(cur.get("ans3"));
					ans4.setText(cur.get("ans4"));
					
					rightAns = Integer.parseInt(cur.get("right_ans"));
					
					ans1.setVisibility(8);
					ans2.setVisibility(8);
					ans3.setVisibility(8);
					ans4.setVisibility(8);

					/*try {
						//Thread.sleep(3000);
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					ans1.setVisibility(0);
					ans2.setVisibility(0);
					ans3.setVisibility(0);
					ans4.setVisibility(0);
					
					startCountDown();
					startOpponent();
					startTime = System.currentTimeMillis();*/
					getNextQuestionHelper();
				}					
			});				
			
			//curRound++;
		}else{			
			handleFinish();
		}		
		ansSeq = "";
	}
	
	public void getNextQuestionHelper(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					//Thread.sleep(3000);
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){
				
					@Override
					public void run() {
						ans4.setVisibility(0);
					}
				});
				
				try {
					//Thread.sleep(3000);
					TimeUnit.MILLISECONDS.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){
					
					@Override
					public void run() {
						ans3.setVisibility(0);
					}
				});
				
				try {
					//Thread.sleep(3000);
					TimeUnit.MILLISECONDS.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){
					
					@Override
					public void run() {
						ans2.setVisibility(0);
					}
				});
				
				try {
					//Thread.sleep(3000);
					TimeUnit.MILLISECONDS.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){
					
					@Override
					public void run() {
						ans1.setVisibility(0);
					}
				});
				
				try {
					//Thread.sleep(3000);
					TimeUnit.MILLISECONDS.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				startCountDown();
				startOpponent();
				startTime = System.currentTimeMillis();
				
				MultiPlayerActivity.this.runOnUiThread(new Runnable(){
					
					@Override
					public void run() {
						ans1.setClickable(true);
						ans2.setClickable(true);
						ans3.setClickable(true);
						ans4.setClickable(true);						
					}
				});				
			}
			
		}).start();
	}
	
	public void handleFinish(){
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				TextView r = (TextView) findViewById(R.id.res_m);
				r.setText((oppScore > playerScore) ? "Op won " + oppScore + " vs " + playerScore : "You won " + playerScore + " vs " + oppScore);
				ans1.setClickable(false);
				ans2.setClickable(false);
				ans3.setClickable(false);
				ans4.setClickable(false);
			}
			
		});
		//new SaveGame().execute();
	}
	
	public void makePause(){
		
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				//Toast.makeText(getApplicationContext(), "pause", 3000).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerActivity.this);
				builder.setTitle(message)
						.setMessage("Ready?")
						.setCancelable(false)
						.setNegativeButton("Ready",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										getNextQuestion();
										//startOpponent();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}			
		});
		
		/*try {
			TimeUnit.MILLISECONDS.sleep(3000);
		} catch (InterruptedException e) {
		}*/
	}
		
	class SaveGame extends AsyncTask<String, String, String>{		

		boolean res = false;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    dialog = new ProgressDialog(MultiPlayerActivity.this);
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
