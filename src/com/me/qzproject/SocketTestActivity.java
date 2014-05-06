package com.me.qzproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SocketTestActivity extends Activity {

	private Map<String, Map<String, String>> questions;
	private Map<String, Bitmap> images;
			
	private static volatile boolean isConnected;

	private volatile boolean isOppWrong = false;
	private volatile Thread countDownThread;
	private Thread readingThread;
	private volatile PrintWriter writer;
	private volatile BufferedReader reader;
	private static final int port = 1225;
	private String id = "1";
	private String rid = "2";
	private String theme_id = "1";//TODO
	private boolean isRequesting = true;
	private String curQuestion;
	//UI elems
	private TextView tv;
	private Button btn;
	private ImageView img;
	private Button ans1;
	private Button ans2;
	private Button ans3;
	private Button ans4;
	private TextView opScore;
	private TextView yourScore;
	private View opInd;
	private View yourInd;
	private ProgressBar pb;
	private TextView countDown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket_test);
		tv = (TextView) findViewById(R.id.socket_tv);
		btn = (Button) findViewById(R.id.socket_go);
		img = (ImageView) findViewById(R.id.socket_image);
		ans1 = (Button) findViewById(R.id.socket_b1_m);
		ans2 = (Button) findViewById(R.id.socket_b2_m);
		ans3 = (Button) findViewById(R.id.socket_b3_m);
		ans4 = (Button) findViewById(R.id.socket_b4_m);
		opScore = (TextView) findViewById(R.id.socket_op_score);
		yourScore = (TextView) findViewById(R.id.socket_your_score);
		countDown = (TextView) findViewById(R.id.socket_countdown_m);
		opInd = findViewById(R.id.socket_opp_ind);
		yourInd = findViewById(R.id.socket_player_ind);
		pb = (ProgressBar) findViewById(R.id.socket_pb);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				start(SocketTestActivity.this);				
			}			
		});
		AnswerHandler answerHandler = new AnswerHandler();
		ans1.setOnClickListener(answerHandler);
		ans2.setOnClickListener(answerHandler);
		ans3.setOnClickListener(answerHandler);
		ans4.setOnClickListener(answerHandler);
		ans1.setVisibility(View.GONE);
		ans2.setVisibility(View.GONE);
		ans3.setVisibility(View.GONE);
		ans4.setVisibility(View.GONE);
		
	}
	
	class AnswerHandler implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.socket_b1_m){
				writer.println("opmsg#1");
				setUnclickable();
				if(questions.get(curQuestion).get("right_ans").equals("1")){
					yourScore.setText(Integer.parseInt(yourScore.getText().toString()) + Integer.parseInt(countDown.getText().toString()) + "");
					
					writer.println("NEXT");
        			animateIndicator(yourInd, true);
        			setRoundView();
        			stopCountDown();        			
				}else{					
					animateIndicator(yourInd, false);	
					//TODO if op is Wrong stopCountDown and setRoundView
					if(isOppWrong){
						writer.println("NEXT");
	        			setRoundView();
	        			stopCountDown();
					}
				}				
			}else if(v.getId() == R.id.socket_b2_m){
				writer.println("opmsg#2");			
				setUnclickable();	
				if(questions.get(curQuestion).get("right_ans").equals("2")){
					yourScore.setText(Integer.parseInt(yourScore.getText().toString()) + Integer.parseInt(countDown.getText().toString()) + "");
					writer.println("NEXT");
        			animateIndicator(yourInd, true);
        			setRoundView();
        			stopCountDown();
				}else{
        			animateIndicator(yourInd, false);
        			if(isOppWrong){
						writer.println("NEXT");
	        			setRoundView();
	        			stopCountDown();
					}
				}
			}else if(v.getId() == R.id.socket_b3_m){
				writer.println("opmsg#3");		
				setUnclickable();		
				if(questions.get(curQuestion).get("right_ans").equals("3")){
					yourScore.setText(Integer.parseInt(yourScore.getText().toString()) + Integer.parseInt(countDown.getText().toString()) + "");
					writer.println("NEXT");
        			animateIndicator(yourInd, true);
        			setRoundView();
        			stopCountDown();
				}else{
        			animateIndicator(yourInd, false);
        			if(isOppWrong){
						writer.println("NEXT");
	        			setRoundView();
	        			stopCountDown();
					}
				}
			}else{
				writer.println("opmsg#4");
				setUnclickable();
				if(questions.get(curQuestion).get("right_ans").equals("4")){
					yourScore.setText(Integer.parseInt(yourScore.getText().toString()) + Integer.parseInt(countDown.getText().toString()) + "");
					writer.println("NEXT");
        			animateIndicator(yourInd, true);
        			setRoundView();
        			stopCountDown();
				}else{
        			animateIndicator(yourInd, false);
        			if(isOppWrong){
						writer.println("NEXT");
	        			setRoundView();
	        			stopCountDown();
					}
				}				
			}	
			//TODO set next view
			//TODO send ready to server
		}		
	}

	private void setRoundView(){
		new Thread(new Runnable(){		

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						ans1.setVisibility(View.GONE);
						ans2.setVisibility(View.GONE);
						ans3.setVisibility(View.GONE);
						ans4.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						tv.setText("Next Round");						
					}			
				});
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				

				writer.println("READY");
			}	
		}).start();
	}
	
	private void setUnclickable(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				ans1.setClickable(false);
				ans2.setClickable(false);
				ans3.setClickable(false);
				ans4.setClickable(false);
			}			
		});
	}
	
	private void setClickable(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				ans1.setClickable(true);
				ans2.setClickable(true);
				ans3.setClickable(true);
				ans4.setClickable(true);
			}			
		});	
	}
		
	public void start(final Activity activity){
		readingThread = new Thread(new Runnable(){

			@Override
			public void run() {
				try{
					InetAddress serverAddr = InetAddress.getByName(APIHandler.ip);  
	                Socket socket = new Socket(serverAddr, 1225);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));	                
	                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	                sendCredentials(id, isRequesting, rid, writer);
	                
	                String line = null;
	                while ((line = reader.readLine()) != null){
	                	//show line;
	                	isOppWrong = false;
	                	final String ss = line;
	                	activity.runOnUiThread(new Runnable(){
							@Override
							public void run() {
								tv.setText(ss);
							}
	                		
	                	});
	                	String[] str = line.split("#");
	                	String s1 = str[0];
	                	String s2 = str[1];
	                	if(s1.equals("qids")){
	                		//downloading questions and images
	                		String[] ids = s2.split("_");
	                		List<String> qids = new ArrayList<String>();
	                		for(String qid : ids){
	                			qids.add(qid);
	                		}
	                		new GetQuestions(qids, SocketTestActivity.this).execute();
	                		
	                	}else if(s1.equals("servermsg")){
	                		//TODO handle server msg
	                		final String s = s2;		      
	                		curQuestion = s2;
	        				setClickable();
	        				startCountDown();
		                	activity.runOnUiThread(new Runnable(){
								@Override
								public void run() {
									
									tv.setText(questions.get(s).get("question"));
									img.setImageBitmap(images.get(s));
									ans1.setText(questions.get(s).get("ans1"));
									ans2.setText(questions.get(s).get("ans2"));
									ans3.setText(questions.get(s).get("ans3"));
									ans4.setText(questions.get(s).get("ans4"));
									
									img.setVisibility(View.VISIBLE);
									ans1.setVisibility(View.VISIBLE);
									ans2.setVisibility(View.VISIBLE);
									ans3.setVisibility(View.VISIBLE);
									ans4.setVisibility(View.VISIBLE);
								}
		                		
		                	});
	                	}else if(s1.equals("opmsg")){
	                		//TODO handle ops msg
	                		if(questions.get(curQuestion).get("right_ans").equals(s2)){
	                			opScore.setText(Integer.parseInt(opScore.getText().toString()) + Integer.parseInt(countDown.getText().toString()) + "");
	            				
	                			animateIndicator(opInd, true);
	                			setUnclickable();	  
	                			setRoundView();
	                			stopCountDown();
	                		}else{
	                			animateIndicator(opInd, false);
	                			isOppWrong = true;
	                		}
	                	}
                	}
				}catch(final Exception e){
					
					activity.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							tv.setText(e.toString());
						}
	            		
	            	});
				}

			}			
		});
		readingThread.start();
	}
	
	public void sendCredentials(String id, boolean isRequesting, String rid, PrintWriter writer){
		writer.println("id#"+ id);
		String s = (isRequesting) ? "1" : "0";
		writer.println("isRequesting#" + s);
		writer.println("rid#" + rid);		
		writer.println("FIN_1");
	}
	
	class GetQuestions extends AsyncTask<String, String, String>{
		List<String> ids;
		Activity activity;
		GetQuestions(List<String> ids, Activity activity){
			this.ids = ids;
			this.activity = activity;
		}
		
		protected void onPreExecute() {
			//tv.setText("Loading questions");
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					tv.setText("Loading Questions");
				}
        		
        	});
		}
		
		@Override
		protected String doInBackground(String... arg0) {
        	questions = APIHandler.getQuestionsByIds(theme_id, ids);			
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(questions != null){
	        	//TODO download images;
				List<String> img_ids = new ArrayList<String>();
				for(Entry<String, Map<String, String>> entry: questions.entrySet()){
					if(entry.getValue().get("has_img").equals("1")){
						img_ids.add(entry.getKey());
					}
				}				
				new GetImages(img_ids, activity).execute();				
	        }else{
	        	//tv.setText(APIHandler.error);
	        	activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						tv.setText("Loading Questions Error");
					}
	        		
	        	});
	        }		        	
		}		
	}
	
	class GetImages extends AsyncTask<String, String, String>{

		List<String> ids;
		Activity activity;
		GetImages(List<String> ids, Activity activity){
			this.ids = ids;
			this.activity = activity;
		}
		
		protected void onPreExecute() {
			//tv.setText("Loading images");
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					tv.setText("Loading Images");
				}
    		
			});
		}
		
		@Override
		protected String doInBackground(String... params) {

        	images = new HashMap<String, Bitmap>();
			try {
				for(String id : ids){
					String url = "http://" + APIHandler.ip + ":80/laravel/public/images/questions/question_" + theme_id + "_" + id + ".jpg";				
		            InputStream in = new java.net.URL(url).openStream();
		            Bitmap bitmap = BitmapFactory.decodeStream(in);
		            images.put(id, bitmap);			        
				}
				
			} catch (final Exception e) {
	        	images = null;
	        	activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						tv.setText("Images loading error: " + e.toString());
					}
	        		
	        	});
	        }
				
			return null;
		}
		

		protected void onPostExecute(String unused) {
			if(images != null){
	        	//send ready to teh server
				writer.println("READY");
	        }else{
	        	//TODO handle error;
	        	//tv.setText("Images Loading error");	        	
	        }		        	
		}		
	}
	
	public void animateIndicator(final View ind, final boolean isRight){
		new Thread(new Runnable(){
			@Override
			public void run() {
				SocketTestActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if(isRight){
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
				
				SocketTestActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						ind.setBackgroundColor(getResources().getColor(R.color.White));
					}
					
				});
			}
			
		}).start();
	}

	public void startCountDown(){
		
		countDownThread = new Thread(new Runnable(){

			@Override
			public void run() {
				boolean isInterrupted = false;
				long time = 10*1000;//TODO set constant
				while(time>= 0 && !isInterrupted){
					final long temp = time;
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							countDown.setText(temp/1000 + 1 + "");
							pb.setProgress(1000 - (int)temp/10);
						}
						
					});
					time -= 1;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						isInterrupted = true;
					}
				}
				if(!isInterrupted){
					writer.println("NEXT");
					setRoundView();
				}
			}
			
		});
		
		countDownThread.start();
	}
	
	public void stopCountDown(){
		countDownThread.interrupt();
	}
}

