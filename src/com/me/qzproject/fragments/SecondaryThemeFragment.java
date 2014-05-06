package com.me.qzproject.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.me.qzproject.R;
import com.me.qzproject.Theme;
import com.me.qzproject.adapters.ExpandableThemesAdapter;

public class SecondaryThemeFragment extends Fragment{
	
	private ArrayList<Theme> themes;
	private String mainId;
	
	public static Fragment newInstance(String mainId, ArrayList<Theme> themes){
		Fragment f = new SecondaryThemeFragment();
		Bundle b = new Bundle();
		b.putParcelableArrayList("themes", themes);
		b.putString("mainId", mainId);
		f.setArguments(b);
		return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        themes = getArguments().getParcelableArrayList("themes");
        mainId = getArguments().getString("mainId");
        
        View v = inflater.inflate(R.layout.secondary_themes_fragment, null);        
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.exp_lv);
        elv.setAdapter(new ExpandableThemesAdapter(mainId, themes, getActivity()));        
        return v;
    }

}
