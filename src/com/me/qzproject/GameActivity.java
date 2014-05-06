package com.me.qzproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.me.qzproject.fragments.GameFragment;

public class GameActivity extends FragmentActivity {
	
	public static GameFragment gameFragment;

	private static FragmentManager fManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		fManager = getSupportFragmentManager();

        FragmentTransaction tx = fManager.beginTransaction();
        
        gameFragment = new GameFragment();
        
        tx.replace(R.id.game, gameFragment);
        
        tx.commit();
		
	}
	
	/*@Override
	public void onStop(){
		gameFragment.finalizze();
		//TODO close cur activity
		super.onStop();
	}*/
	
	@Override
	public void onUserLeaveHint(){
		gameFragment.finalizze();
		super.onUserLeaveHint();
	}
	
	@Override
	public void onBackPressed() {
		if(gameFragment.CURRENT_FRAGMENT_STATE == 1){//LOADING AND WAITING FOR USER
			gameFragment.finalizze();
			super.onBackPressed();
		}else if(gameFragment.CURRENT_FRAGMENT_STATE == 2){//GAME IS RUNNING
			gameFragment.showSurrenderConfirmationDialog();
		}else{//FINAL VIEW
			super.onBackPressed();
		}
	}

}
