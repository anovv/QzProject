package com.me.qzproject.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.qzproject.R;
import com.me.qzproject.User;

public class FinalFragment extends Fragment{
	
	Bitmap imgUser1;
	Bitmap imgUser2;
	User user1;
	User user2;
	int score1;
	int score2;
	
	//UI
	
	private ImageView vImgUser1;
	private ImageView vImgUser2;
	private TextView vNameUser1;
	private TextView vNameUser2;
	private TextView vResult;
	private View vFinal;
	
	public static Fragment newInstance(User user1, User user2, Bitmap imgUser1, Bitmap imgUser2, int score1, int score2){
		Fragment f = new FinalFragment();
		Bundle b = new Bundle();
		b.putParcelable("user1", user1);
		b.putParcelable("user2", user2);
		b.putParcelable("imgUser1", imgUser1);
		b.putParcelable("imgUser2", imgUser2);
		b.putInt("score1", score1);
		b.putInt("score2", score2);
		f.setArguments(b);
		
		return f;
	}
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user1 = getArguments().getParcelable("user1");
        user2 = getArguments().getParcelable("user2");
        imgUser1 = getArguments().getParcelable("imgUser1");
        imgUser2 = getArguments().getParcelable("imgUser2");
        score1 = getArguments().getInt("score1");
        score2 = getArguments().getInt("score2");
        View v = inflater.inflate(R.layout.final_fragment, null);      
        
        vImgUser1 = (ImageView) v.findViewById(R.id.final_user_1_img);
        vImgUser2 = (ImageView) v.findViewById(R.id.final_user_2_img);
        
        vNameUser1 = (TextView) v.findViewById(R.id.final_user_1_name);
        vNameUser2 = (TextView) v.findViewById(R.id.final_user_2_name);
        
        vResult = (TextView) v.findViewById(R.id.final_result);
        vFinal = v.findViewById(R.id.final_view);
        //TODO send info to the server in background
        
        vImgUser1.setImageBitmap(imgUser1);
        vImgUser2.setImageBitmap(imgUser2);
        
        vNameUser1.setText(user1.name);
        vNameUser2.setText(user2.name);
        
        vResult.setText((score1 >= score2) ? "You win" : "You lose");
        
        return v;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
				FriendsFragment.crossfade(vFinal, null, getResources().getInteger(android.R.integer.config_shortAnimTime));
			}
			
		});
	}
	
}
