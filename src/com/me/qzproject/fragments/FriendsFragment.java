package com.me.qzproject.fragments;

import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.me.qzproject.APIHandler;
import com.me.qzproject.FragmentTestActivity;
import com.me.qzproject.R;
import com.me.qzproject.User;
import com.me.qzproject.UserActivity;
import com.me.qzproject.adapters.FriendListAdapter;

public class FriendsFragment extends Fragment{
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
			
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	  	  
		View view = inflater.inflate(R.layout.activity_test, container, false);		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setId(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageMargin(1);
	    mViewPager.setPageMarginDrawable(R.color.Gray);
	    
		return view;
	}
		
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			
			if(position == 0){
				fragment = new FriendsListFragment();
			}else if(position == 1){
				fragment = new RequestsListFragment();
			}else{
				fragment = new FindUsersFragment();
			}
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			
			switch (position) {
			case 0:
				return "Friends";
			case 1:
				return "Requests";
			case 2:
				return "Find";
			}
			
			return null;			
		}
	}
	
	public static class FindUsersFragment extends ListFragment implements OnLoadDataListener{

		private FriendListAdapter mAdapter;
        
        private Button findButton;
        private EditText userName;
        private ProgressBar mLoadingView;
        private View mContentView;
        private int mShortAnimationDuration;
		
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
        	
            User u = (User) getListAdapter().getItem(position);//TODO lol
        	Bundle bundle = new Bundle();
        	bundle.putString("id", u.id);
            UserFragment f = new UserFragment();
        	f.setArguments(bundle);
        	FragmentTransaction tx = FragmentTestActivity.fManager.beginTransaction();
            //tx.replace(R.id.main, f);
        	tx.hide(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	FragmentTestActivity.fStack.push(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	tx.add(R.id.main, f);
            //tx.addToBackStack(null);//TODO reversable
        	//tx.replace(R.id.main, Fragment.instantiate(getActivity(), "com.me.qzproject.fragments.FriendsFragment"));
            
            tx.commit();
        	/*Intent intent = new Intent(getActivity(), UserActivity.class);
        	intent.putExtras(bundle); 
        	startActivity(intent);*/            
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	//
            return inflater.inflate(R.layout.find_users_fragment, container, false);
        }
        
        @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        
	        findButton = (Button) getActivity().findViewById(R.id.find_users_button);	        
	        userName = (EditText) getActivity().findViewById(R.id.find_users_et);
	        
	        mLoadingView = (ProgressBar) getActivity().findViewById(R.id.find_users_spinner);
			mContentView = getActivity().findViewById(R.id.find_users_ll);
			
			mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
			
			mLoadingView.setVisibility(View.INVISIBLE);
			//mLoadingView.setVisibility(View.GONE);
			//mContentView.setVisibility(View.GONE);
			
	        findButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					
			        String name = userName.getText().toString();
			        
			        mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

			        mContentView.setVisibility(View.GONE);
			        
			        TextView tv = (TextView) getActivity().findViewById(R.id.find_users_message);
	            	if(tv != null){
	            		tv.setText("");
	            	}
			        
	            	showMessage("", (TextView) getActivity().findViewById(R.id.find_users_message), mShortAnimationDuration);
	            	
	            	mLoadingView.setVisibility(View.VISIBLE);
	            	
			        FindUsers fu = new FindUsers(name);
			        fu.setListener(FindUsersFragment.this);
			        fu.execute();
				}
	        	
	        });        	
	    }
        
		@Override
		public void onLoadComplete(ArrayList<User> usersList) {
			
			if(getActivity() != null){
				//TODO crossfade
				//mLoadingView.setVisibility(View.INVISIBLE);
				crossfade(mContentView, mLoadingView, mShortAnimationDuration);
				
				if(usersList != null){				
		            if(usersList.isEmpty()){
	            		showMessage("No Users Found", (TextView) getActivity().findViewById(R.id.find_users_message), mShortAnimationDuration);
	            	}else{
		            	showMessage("", (TextView) getActivity().findViewById(R.id.find_users_message), mShortAnimationDuration);
	            	}
		            mAdapter = new FriendListAdapter(getActivity(), android.R.id.list, usersList);
	            	if(mAdapter != null){
		            	setListAdapter(mAdapter);
					}
	            	
	            }else{
	            	showMessage("Connection Error", (TextView) getActivity().findViewById(R.id.find_users_message), mShortAnimationDuration);
	            	mAdapter = new FriendListAdapter(getActivity(), android.R.id.list, new ArrayList<User>());
	            	if(mAdapter != null){
		            	setListAdapter(mAdapter);
					}
	            }
			}
		}   
		
	}
	
	public static class FriendsListFragment extends ListFragment implements OnLoadDataListener{
						
        private FriendListAdapter mAdapter;
        private PullToRefreshListView mPullToRefreshListView;
        private int mShortAnimationDuration;
        private View mLoadingView;
        private View mContentView;
        /*private View mContentView;
        private View mLoadingView;
        private int mShortAnimationDuration;*/

        
        /*@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	//super.onCreateView(inflater, container, savedInstanceState);
            return inflater.inflate(R.layout.friend_list_fragment, null);
        }*/
        
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
        	
            User u = (User) getListAdapter().getItem(position - 1);//TODO lol
        	Bundle bundle = new Bundle();
        	bundle.putString("id", u.id);
            UserFragment f = new UserFragment();
        	f.setArguments(bundle);
        	FragmentTransaction tx = FragmentTestActivity.fManager.beginTransaction();
            //tx.replace(R.id.main, f);
        	tx.hide(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	FragmentTestActivity.fStack.push(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	tx.add(R.id.main, f);
            //tx.addToBackStack(null);//TODO reversable
        	//tx.replace(R.id.main, Fragment.instantiate(getActivity(), "com.me.qzproject.fragments.FriendsFragment"));
            
            tx.commit();
        	/*Intent intent = new Intent(getActivity(), UserActivity.class);
        	intent.putExtras(bundle); 
        	startActivity(intent);*/            
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.friend_list_fragment, container, false);
            //ListFragmentLayout.setupIds(view);
            ListView lv = (ListView) view.findViewById(android.R.id.list);
            ViewGroup parent = (ViewGroup) lv.getParent();
            int lvIndex = parent.indexOfChild(lv);
    		
            mPullToRefreshListView = new PullToRefreshListView(getActivity());
            mPullToRefreshListView.setLayoutParams(lv.getLayoutParams());

            mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>(){
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                	
	            	showMessage("", (TextView) getActivity().findViewById(R.id.friend_list_message), mShortAnimationDuration);
                	
	            	GetFriends gFriends = new GetFriends();
        	        gFriends.setListener(FriendsListFragment.this);
        	        gFriends.execute();
                }
            });
            
            parent.addView(mPullToRefreshListView, lvIndex, lv.getLayoutParams());

            //return mPullToRefreshListView;
	        //mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

	        //mContentView.setVisibility(View.GONE);


            return view;
        }
        
                
        @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        //getListView().setEmptyView(noItems("No Friends", getActivity(), getListView()));	
	        /*mContentView = getActivity().findViewById(R.id.ll1);
	        
	        mLoadingView = getActivity().findViewById(R.id.loading_spinner);

	        // Initially hide the content view.
	        mContentView.setVisibility(View.GONE);

	        // Retrieve and cache the system's default "short" animation time.*/
	        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
	        mLoadingView = getActivity().findViewById(R.id.friend_list_spinner);
			mContentView = getActivity().findViewById(R.id.friend_list_ll);
            //return mPullToRefreshListView;
	        mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

	        mContentView.setVisibility(View.GONE);

        	GetFriends gFriends = new GetFriends();
	        gFriends.setListener(this);
	        gFriends.execute();
	        //this.onLoadComplete(new ArrayList<User>());

	    }
	           
		@Override
		public void onLoadComplete(ArrayList<User> friendList) {
			
			
			if(getActivity() != null){
				//TODO crossfade
				if(mPullToRefreshListView != null){
					mPullToRefreshListView.onRefreshComplete();
				}
				
				crossfade(mContentView, mLoadingView, mShortAnimationDuration);

				if(friendList != null){				
		            if(friendList.isEmpty()){
		            	showMessage("No Friends", (TextView) getActivity().findViewById(R.id.friend_list_message), mShortAnimationDuration);
	                }else{
	            		showMessage("", (TextView) getActivity().findViewById(R.id.friend_list_message), mShortAnimationDuration);
	              	}
		            mAdapter = new FriendListAdapter(getActivity(), /*android.R.id.list*/ android.R.id.list, friendList);
	            	if(mAdapter != null){
		            	setListAdapter(mAdapter);
					}
	            	
	            }else{             	
	            	showMessage("Connection Error", (TextView) getActivity().findViewById(R.id.friend_list_message), mShortAnimationDuration);
                	mAdapter = new FriendListAdapter(getActivity(), android.R.id.list, new ArrayList<User>());
	            	if(mAdapter != null){
		            	setListAdapter(mAdapter);
					}
	            }
			}
		}        
    }	
	
	public static class RequestsListFragment extends ListFragment implements OnLoadDataListener{

		private FriendListAdapter mAdapter;
        private PullToRefreshListView mPullToRefreshListView;
		private int mShortAnimationDuration;
		private View mLoadingView;
		private View mContentView;
		
		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.request_list_fragment, container, false);
            //ListFragmentLayout.setupIds(view);
            ListView lv = (ListView) view.findViewById(android.R.id.list);
            ViewGroup parent = (ViewGroup) lv.getParent();
            int lvIndex = parent.indexOfChild(lv);
                        
            mPullToRefreshListView = new PullToRefreshListView(getActivity());
            mPullToRefreshListView.setLayoutParams(lv.getLayoutParams());

            mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>(){
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                	showMessage("", (TextView) getActivity().findViewById(R.id.request_list_message), mShortAnimationDuration);
                			        
                	GetRequests gRequests = new GetRequests();
        	        gRequests.setListener(RequestsListFragment.this);
        	        gRequests.execute();
                }
            });
            
            parent.addView(mPullToRefreshListView, lvIndex, lv.getLayoutParams());

            return view;
        
		}

		
        @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
			mLoadingView = getActivity().findViewById(R.id.request_list_spinner);
			mContentView = getActivity().findViewById(R.id.request_list_ll);
	        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);		
	        
	        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	        	@Override
	            public boolean onItemLongClick(AdapterView<?> a, View view, int position, long id) {
	        		User u = (User) getListAdapter().getItem(position - 1);
	        		showRequestConfirmationDialog(getActivity(), u.id, RequestsListFragment.this, mLoadingView, mContentView, mShortAnimationDuration);
	    	        return true;
	            }
	        });
	        
	        GetRequests gRequests = new GetRequests();
 	        gRequests.setListener(RequestsListFragment.this);
 	        gRequests.execute();         	        	       
	    }        
        
		@Override
		public void onLoadComplete(ArrayList<User> requestList) {
			
			if(getActivity() != null){
				if(mPullToRefreshListView != null){
					mPullToRefreshListView.onRefreshComplete();
				}
				
				crossfade(mContentView, mLoadingView, mShortAnimationDuration);
				
				if(requestList != null){
					if(requestList.isEmpty()){
						showMessage("No requests", (TextView) getActivity().findViewById(R.id.request_list_message), mShortAnimationDuration);
	                	
	            	}else{
	            		showMessage("", (TextView) getActivity().findViewById(R.id.request_list_message), mShortAnimationDuration);
	                	
	            	}
					mAdapter = new FriendListAdapter(getActivity(), android.R.id.list, requestList);
					if(mAdapter != null){
						setListAdapter(mAdapter);						        
					}
	            }else{	
	            	showMessage("Connection Error", (TextView) getActivity().findViewById(R.id.request_list_message), mShortAnimationDuration);
                	mAdapter = new FriendListAdapter(getActivity(), android.R.id.list, new ArrayList<User>());
	            	if(mAdapter != null){
		            	setListAdapter(mAdapter);
					}
	            }
			}
		}        				

        /*@Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
        	
            User u = (User) getListAdapter().getItem(position - 1);//TODO lol
        	Bundle bundle = new Bundle();
        	bundle.putString("id", u.id);            	
        	Intent intent = new Intent(getActivity(), UserActivity.class);
        	intent.putExtras(bundle); 
        	startActivity(intent);            
        }   */
		@Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
        	
            User u = (User) getListAdapter().getItem(position - 1);//TODO lol
        	Bundle bundle = new Bundle();
        	bundle.putString("id", u.id);
            UserFragment f = new UserFragment();
        	f.setArguments(bundle);
        	FragmentTransaction tx = FragmentTestActivity.fManager.beginTransaction();
            //tx.replace(R.id.main, f);
        	tx.hide(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	FragmentTestActivity.fStack.push(FragmentTestActivity.fManager.findFragmentById(R.id.main));
        	tx.add(R.id.main, f);
            //tx.addToBackStack(null);//TODO reversable
        	//tx.replace(R.id.main, Fragment.instantiate(getActivity(), "com.me.qzproject.fragments.FriendsFragment"));
            
            tx.commit();
        	/*Intent intent = new Intent(getActivity(), UserActivity.class);
        	intent.putExtras(bundle); 
        	startActivity(intent);*/            
        }
	}
	
	static class GetFriends extends AsyncTask<String, String, String>{
		
		public ArrayList<User> friendList;
		
		private OnLoadDataListener mListener;

		public void setListener(OnLoadDataListener listener){
		    mListener = listener;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			ArrayList<Map<String, String>> friends = APIHandler.getFriends();	
			if(friends != null){
				friendList = new ArrayList<User>();
				for(Map<String, String> user: friends){
					friendList.add(new User(user));
				}
			}
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(mListener != null){
	        	mListener.onLoadComplete(friendList);
	        }		        	
		}
	}	
	
	static class GetRequests extends AsyncTask<String, String, String>{
		
		public ArrayList<User> requestList;
		
		private OnLoadDataListener mListener;

		public void setListener(OnLoadDataListener listener){
		    mListener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			Map<String, ArrayList<Map<String, String>>> requests = APIHandler.getFriendRequests();
			if(requests != null){
				ArrayList<Map<String, String>> mine = requests.get("mine");
				requestList = new ArrayList<User>();
				for(Map<String, String> u: mine){
					requestList.add(new User(u));
				}
			}
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(mListener != null){
	        	mListener.onLoadComplete(requestList);
	        }		        	
		}
	}
	
	static class FindUsers extends AsyncTask<String, String, String>{
		
		public ArrayList<User> foundList;
		
		private OnLoadDataListener mListener;

		private String name;
		
		public FindUsers(String name){
			this.name = name;
		}
		
		public void setListener(OnLoadDataListener listener){
		    mListener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			if(!name.equals("")){
				ArrayList<Map<String, String>> users = APIHandler.findUser(name);
				if(users != null){
					foundList = new ArrayList<User>();
					for(Map<String, String> u: users){
						foundList.add(new User(u));
					}
				}
			}else{
				foundList = new ArrayList<User>();
			}
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(mListener != null){
	        	mListener.onLoadComplete(foundList);
	        }		        	
		}
	}

	static class ConfirmRequest extends AsyncTask<String, String, String>{
		
		private boolean res;
		
		private String id;
		
		private Context context;
		
		private OnLoadDataListener listener;
		
		public ConfirmRequest(String id, Context context, OnLoadDataListener listener){
			this.id = id;
			this.context = context;
			this.listener = listener;
		}
				
		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.confirmFriendRequest(id);
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(res){
            	GetRequests gRequests = new GetRequests();
            	gRequests.setListener(listener);
            	gRequests.execute();
			}else{
				Toast.makeText(context, "Smth's wrong", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	static class DeclineRequest extends AsyncTask<String, String, String>{
		
		private boolean res;
		
		private String id;
		
		private Context context;
		
		private OnLoadDataListener listener;
		
		public DeclineRequest(String id, Context context, OnLoadDataListener listener){
			this.id = id;
			this.context = context;
			this.listener = listener;
		}
				
		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.declineFriendRequest(id);
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(res){
            	GetRequests gRequests = new GetRequests();
            	gRequests.setListener(listener);
            	gRequests.execute();
			}else{
				Toast.makeText(context, "Smth's wrong", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static void crossfade(View mContentView, final View mLoadingView, int mShortAnimationDuration) {

	    // Set the content view to 0% opacity but visible, so that it is visible
	    // (but fully transparent) during the animation.
		if(mContentView != null){
		    mContentView.setAlpha(0f);
		    mContentView.setVisibility(View.VISIBLE);
	
		    // Animate the content view to 100% opacity, and clear any animation
		    // listener set on the view.
		    mContentView.animate()
		            .alpha(1f)
		            .setDuration(mShortAnimationDuration)
		            .setListener(null);
	
		    // Animate the loading view to 0% opacity. After the animation ends,
		    // set its visibility to GONE as an optimization step (it won't
		    // participate in layout passes, etc.)
		}
	    if(mLoadingView != null){
		    mLoadingView.animate()
		            .alpha(0f)
		            .setDuration(mShortAnimationDuration)
		            .setListener(null);
	    }
		
	}
	
	public static void showRequestConfirmationDialog(final Context context, final String uid, final OnLoadDataListener listener, final View mLoadingView, final View mContentView, final int mShortAnimationDuration){
		
		AlertDialog alertDialog;		
		alertDialog = new AlertDialog.Builder(context).create();
	    alertDialog.setTitle("Confirmation");
	    alertDialog.setMessage("Dou you want to confirm?");
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		
	    	} 
	    }); 

	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Confirm", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		//confirm
	    		mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
		        mContentView.setVisibility(View.GONE);
	    		new ConfirmRequest(uid, context, listener).execute();
         	}
	    }); 

	    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Decline", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		//decline
	    		mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
		        mContentView.setVisibility(View.GONE);
	    		new DeclineRequest(uid, context, listener).execute();
	    	}
	    });
	    
	    alertDialog.show();
	}
	
	public static void showMessage(String message, TextView tv, int duration){
		if(tv != null){
			//tv.setVisibility(View.GONE);
			tv.setAlpha(0.0f);
			tv.setText(message);
			tv.animate().alpha(1f).setDuration(duration);
		}    	
	}
		
	public interface OnLoadDataListener{
		public void onLoadComplete(ArrayList<User> list);
	}

}
