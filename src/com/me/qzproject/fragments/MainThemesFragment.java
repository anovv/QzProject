package com.me.qzproject.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.me.qzproject.APIHandler;
import com.me.qzproject.FragmentTestActivity;
import com.me.qzproject.R;
import com.me.qzproject.Theme;
import com.me.qzproject.User;
import com.me.qzproject.adapters.ThemesAdapter;

public class MainThemesFragment extends ListFragment implements OnLoadDataListener{


	private ThemesAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
	private int mShortAnimationDuration;
	private View mLoadingView;
	private View mContentView;
	private static Map<String, ArrayList<Theme>> themes;
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    	
        Theme u = (Theme) getListAdapter().getItem(position - 1);//TODO lol        
    	Fragment f = SecondaryThemeFragment.newInstance(u.id, themes.get(u.id));
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
        View view = inflater.inflate(R.layout.main_themes_fragment, container, false);
        //ListFragmentLayout.setupIds(view);
        ListView lv = (ListView) view.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();
        int lvIndex = parent.indexOfChild(lv);
		
        mPullToRefreshListView = new PullToRefreshListView(getActivity());
        mPullToRefreshListView.setLayoutParams(lv.getLayoutParams());

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>(){
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	
            	FriendsFragment.showMessage("", (TextView) getActivity().findViewById(R.id.main_themes_message), mShortAnimationDuration);
            	
            	GetThemes gThemes = new GetThemes();
    	        gThemes.setListener(MainThemesFragment.this);
    	        gThemes.execute();
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
        mLoadingView = getActivity().findViewById(R.id.main_themes_spinner);
		mContentView = getActivity().findViewById(R.id.main_themes_ll);
        //return mPullToRefreshListView;
        mLoadingView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

        mContentView.setVisibility(View.GONE);

    	GetThemes gThemes = new GetThemes();
        gThemes.setListener(MainThemesFragment.this);
        gThemes.execute();
        //this.onLoadComplete(new ArrayList<User>());

    }
           
	@Override
	public void onLoadComplete(Map<String, ArrayList<Theme>> themeList) {	
		
		if(getActivity() != null){
			//TODO crossfade
			if(mPullToRefreshListView != null){
				mPullToRefreshListView.onRefreshComplete();
			}
			
			FriendsFragment.crossfade(mContentView, mLoadingView, mShortAnimationDuration);

			if(themeList != null){			
				ArrayList<Theme> mainThemes = new ArrayList<Theme>();
				for(Entry<String, ArrayList<Theme>> entry : themeList.entrySet()){
					String id = entry.getKey();
					ArrayList<Theme> ar = entry.getValue();
					for(Theme t : ar){
						if(t.id.equals(id)){
							mainThemes.add(t);
						}
					}
				}
				FriendsFragment.showMessage("", (TextView) getActivity().findViewById(R.id.main_themes_message), mShortAnimationDuration);
	            mAdapter = new ThemesAdapter(getActivity(), /*android.R.id.list*/ android.R.id.list, mainThemes);
            	if(mAdapter != null){
	            	setListAdapter(mAdapter);
				}
            	
            }else{             	
            	FriendsFragment.showMessage("Connection Error", (TextView) getActivity().findViewById(R.id.main_themes_message), mShortAnimationDuration);
            	mAdapter = new ThemesAdapter(getActivity(), android.R.id.list, new ArrayList<Theme>());
            	if(mAdapter != null){
	            	setListAdapter(mAdapter);
				}
            }
		}
	}

	static class GetThemes extends AsyncTask<String, String, String>{
		
		public Map<String, ArrayList<Map<String, String>>> res;
		
		private OnLoadDataListener mListener;

		public void setListener(OnLoadDataListener listener){
		    mListener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.getThemes();
			if(res != null){
				themes = new HashMap<String, ArrayList<Theme>>();
				for(Entry<String, ArrayList<Map<String, String>>> m : res.entrySet()){
					String key = m.getKey();
					ArrayList<Map<String, String>> ar = m.getValue();
					ArrayList<Theme> a = new ArrayList<Theme>();
					for(Map<String, String> t : ar){
						a.add(new Theme(t));
					}
					themes.put(key, a);
				}
			}
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(mListener != null){
	        	mListener.onLoadComplete(themes);
	        }		        	
		}
	}		

}

interface OnLoadDataListener{
	public void onLoadComplete(Map<String, ArrayList<Theme>> themes);
}











