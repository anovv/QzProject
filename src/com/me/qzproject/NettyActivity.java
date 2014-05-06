package com.me.qzproject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.me.qzproject.SocketTestActivity.AnswerHandler;
import com.me.qzproject.SocketTestActivity.GetImages;
import com.me.qzproject.SocketTestActivity.GetQuestions;
import com.me.qzproject.fragments.FriendsFragment.OnLoadDataListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NettyActivity extends Activity {
	
	private String host = APIHandler.ip;
	private int port = 1225;
	private String id = "1";
	private String rid = "2";
	private boolean isRequesting = true;
	private String themeId = "1";
	private GameState gameState;
	private EventLoopGroup group;
	private Channel channel;
	
	private volatile Thread countDownThread;
	private volatile boolean isCountDownRunning = false;
	
	//UI
	
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
		
		gameState = new GameState(id, rid);
		
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
				run();
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
			gameState.hasPlayerAnswered = true;
			String ans = null;
			
			if(v.getId() == R.id.socket_b1_m){
				ans = "1";
			}else if(v.getId() == R.id.socket_b2_m){
				ans = "2";
			}else if(v.getId() == R.id.socket_b3_m){
				ans = "3";
			}else{
				ans = "4";
			}

			channel.writeAndFlush("SEND#" + id + "_" + rid + "_" + ans + "\n");
			
			setUnclickable();
			Map<String, String> question = gameState.questions.get(gameState.qIds.get(gameState.round));
			if(gameState.hasPlayerAnswered && gameState.hasOpAnswered){
				if(question.get("right_ans").equals(ans)){
					gameState.playerScore += Integer.parseInt(countDown.getText().toString());
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							yourScore.setText(gameState.playerScore);
						}
						
					});
					
					animateIndicator(yourInd, true);
	    				
				}else{					
					animateIndicator(yourInd, false);	
				}	
				setRoundView();
    			stopCountDown();        		
			}else{
				if(question.get("right_ans").equals(ans)){
					gameState.playerScore += Integer.parseInt(countDown.getText().toString());
					/*runOnUiThread(new Runnable(){

						@Override
						public void run() {
							yourScore.setText(gameState.playerScore);
						}
						
					});*/
					animateIndicator(yourInd, true);
	    				
				}else{					
					animateIndicator(yourInd, false);	
				}	
			}
		}		
	}
	
	private class GameState{
		public int round;
		public String aId;
		public String bId;
		public int opScore;
		public int playerScore;
		public List<String> qIds;
		public boolean hasOpAnswered = false;
		public boolean hasPlayerAnswered = false;

		public Map<String, Map<String, String>> questions;
		public Map<String, Bitmap> images;	
		
		public GameState(String aId, String bId){
			qIds = new ArrayList<String>();
			this.aId = aId;
			this.bId = bId;
			round = -1;
			opScore = 0;
			playerScore = 0;
		}				
	}
	
	public void setNextQuestion(){
		gameState.round++;
		gameState.hasOpAnswered = false;
		gameState.hasPlayerAnswered = false;
		setClickable();
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Map<String, String> question = gameState.questions.get(gameState.qIds.get(gameState.round));
				img.setImageBitmap(gameState.images.get(gameState.qIds.get(gameState.round)));
				tv.setText(question.get("question"));
				ans1.setText(question.get("ans1"));
				ans2.setText(question.get("ans2"));
				ans3.setText(question.get("ans3"));
				ans4.setText(question.get("ans4"));

				img.setVisibility(View.VISIBLE);
				ans1.setVisibility(View.VISIBLE);
				ans2.setVisibility(View.VISIBLE);
				ans3.setVisibility(View.VISIBLE);
				ans4.setVisibility(View.VISIBLE);
			}
			
		});
		startCountDown();
	}
	
	private void setRoundView(){
		new Thread(new Runnable(){		

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
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
					e.printStackTrace();
				}				

				channel.writeAndFlush("NEXT#" + id + "_" + rid + "\n");
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
	
	public void run(){
		group = new NioEventLoopGroup();
		
		try{
			Bootstrap bootstrap = new Bootstrap()
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ClientInitializer());
			
			channel = bootstrap.connect(host, port).sync().channel();
			
			//channel.writeAndFlush(id + "#" + rid + "#SERVER#REQUEST#" + ((isRequesting) ? "1" : "0") + "\n");
			channel.writeAndFlush("REQUEST#" + id + "_" + rid + "_" + ((isRequesting) ? "1" : "0") + "_" + themeId + "\n");
			//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			/*while(true){
				channel.writeAndFlush(in.readLine() + "\r\n");
				//channel.writeAndFlush("Hi" + "\r\n");
				//Thread.sleep(2000);
			}*/			
		}catch(Exception t){
			//TODO handle
			final Exception e = t;
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					tv.setText(e.toString());
				}
				
			});
		}
		
	}

	private class ClientInitializer extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			ChannelPipeline pipeline = arg0.pipeline();
			
			pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
			pipeline.addLast("decoder", new StringDecoder());
			pipeline.addLast("encoder", new StringEncoder());
			pipeline.addLast("handler", new ClientHandler());
		}			
	}
	
	private class ClientHandler extends SimpleChannelInboundHandler<String>{

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
						
			if(arg1.equals("NEXT")){
				stopCountDown();
				setNextQuestion();
				return;
			}			
			
			String[] strs = arg1.split("#");
			String command = strs[0];
			String[] args = strs[1].split("_");
			
			
			if(command.equals("QIDS")){
				
				List<String> qIds = new ArrayList<String>();
        		for(String qid : args){
        			qIds.add(qid);
        		}
        		gameState.qIds = qIds;
        		new GetQuestions(qIds, NettyActivity.this).execute();
        		
			}else if(command.equals("ANS")){
				
				String ans = args[0];
				gameState.hasOpAnswered = true;
				Map<String, String> question = gameState.questions.get(gameState.qIds.get(gameState.round));
				
				if(gameState.hasOpAnswered && gameState.hasPlayerAnswered){
					if(question.get("right_ans").equals(ans)){
						animateIndicator(opInd, true);
						gameState.opScore = gameState.opScore +  Integer.parseInt(countDown.getText().toString());
						opScore.setText(gameState.opScore);        				
					}else{
						animateIndicator(opInd, false);						
					}						
        			stopCountDown();
        			setRoundView();					
				}else{
					if(question.get("right_ans").equals(ans)){
						animateIndicator(opInd, true);
						gameState.opScore = gameState.opScore +  Integer.parseInt(countDown.getText().toString());
						opScore.setText(gameState.opScore);        				
					}else{
						animateIndicator(opInd, false);						
					}	
				}
			}//TODO handle SUR and ERR;
		}
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
					//tv.setText("Loading Questions");
				}
        		
        	});
		}
		
		@Override
		protected String doInBackground(String... arg0) {
        	gameState.questions = APIHandler.getQuestionsByIds(themeId, ids);			
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(gameState.questions != null){
				List<String> img_ids = new ArrayList<String>();
				for(Entry<String, Map<String, String>> entry: gameState.questions.entrySet()){
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
					//tv.setText("Loading Images");
				}
			});
		}
		
		@Override
		protected String doInBackground(String... params) {

        	gameState.images = new HashMap<String, Bitmap>();
			try {
				for(String id : ids){
					String url = "http://" + APIHandler.ip + ":80/laravel/public/images/questions/question_" + themeId + "_" + id + ".jpg";				
		            InputStream in = new java.net.URL(url).openStream();
		            Bitmap bitmap = BitmapFactory.decodeStream(in);
		            gameState.images.put(id, bitmap);			        
				}
				
			} catch (final Exception e) {
				gameState.images = null;
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
			if(gameState.images != null){
	        	//TODO send ready to teh server

				tv.setText("Waiting for response from server");
				channel.writeAndFlush("NEXT#" + id + "\n");
	        }else{
	        	//TODO handle error;
	        	tv.setText("Images Loading error");	        	
	        }		        	
		}		
	}
	
	public void animateIndicator(final View ind, final boolean isRight){
		new Thread(new Runnable(){
			@Override
			public void run() {
				runOnUiThread(new Runnable(){

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
				
				runOnUiThread(new Runnable(){

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
				isCountDownRunning = true;
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
					setRoundView();
				}
				isCountDownRunning = false;
			}
			
		});
		
		countDownThread.start();
	}
	
	public void stopCountDown(){
		if(isCountDownRunning){
			countDownThread.interrupt();
		}
	}
	
	@Override
	public void onBackPressed() {
		showSurrenderConfirmationDialog(this, id, rid, channel);
	}
	
	public void showSurrenderConfirmationDialog(final Activity activity, final String id, final String rid, final Channel channel){
		
		AlertDialog alertDialog;		
		alertDialog = new AlertDialog.Builder(activity).create();
	    alertDialog.setTitle("Surrender?");
	    alertDialog.setMessage("Dou you want to surrender?");
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "No", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		
	    	} 
	    }); 

	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		//confirm
	    		if(channel != null){
	    			channel.writeAndFlush("SURRENDER#" + id + "_" + rid + "\n");
	    		}
	    		activity.finish();
         	}
	    }); 
	    
	    alertDialog.show();
	}
}
