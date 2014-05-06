package com.me.qzproject;

import java.util.Stack;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.me.qzproject.fragments.FriendsFragment;
import com.me.qzproject.fragments.MainThemesFragment;
import com.me.qzproject.fragments.UserFragment;

public class FragmentTestActivity extends FragmentActivity {

	public static FragmentManager fManager;
	public static Stack<Fragment> fStack;
	
	final String[] data ={"My Profile","Friends", "Themes"};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_test);
		fStack = new Stack<Fragment>();
		/*FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, new FriendsFragment());
        tx.commit();*/
		fManager = getSupportFragmentManager();
		ArrayAdapter adapter = new ArrayAdapter(getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, data);
		 
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.left_drawer);
        
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.navigation_header, navList, false);
        navList.addHeaderView(header, null, false);
        String full_img_url = "users/profiles/user_" + APIHandler.user_id + "_profile.jpg";
        new UserFragment.DownloadImage((ImageView) findViewById(R.id.navig_pic), "http://" + APIHandler.ip + ":80/laravel/public/images/" + full_img_url, null).execute();
        TextView tv = (TextView) findViewById(R.id.navig_tv);
        
        
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
                FragmentTransaction tx = fManager.beginTransaction();
                Fragment f = null;
                if(pos == 1){
                	f = new UserFragment();
                }else if(pos == 2){
                	f = new FriendsFragment();
                }else if(pos == 3){
                	f = new MainThemesFragment();
                }
                while(!fStack.isEmpty()){
                	fStack.pop();
                }
                //tx.replace(R.id.main, f);
                tx.remove(fManager.findFragmentById(R.id.main));
                tx.add(R.id.main, f);
                //tx.addToBackStack(null);*/
                tx.commit();
                navList.setItemChecked(pos, true);                        
                drawer.closeDrawer(navList);           
            }
        });
        //TODO set default id;
        while(!fStack.isEmpty()){
        	fStack.pop();
        }
        FragmentTransaction tx = fManager.beginTransaction();
        //tx.remove(fManager.findFragmentById(R.id.main));   
        tx.add(R.id.main, new UserFragment());
        //tx.addToBackStack(null);
        //tx.replace(R.id.main,Fragment.instantiate(context, fragments[0]));
        tx.commit();
	}
	
	class GetUser extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			
			return null;
		}
		
	}

	@Override
	public void onBackPressed() {
	    if (fStack.size() > 0) {	    	
	        FragmentTransaction ft = fManager.beginTransaction();
	        ft.remove(fManager.findFragmentById(R.id.main));
	        ft.show(fStack.pop());
	        ft.commit();
	    } else {
	        super.onBackPressed();
	    }
	}
}
