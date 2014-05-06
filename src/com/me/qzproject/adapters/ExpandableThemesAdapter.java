package com.me.qzproject.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.me.qzproject.R;
import com.me.qzproject.Theme;


public class ExpandableThemesAdapter extends BaseExpandableListAdapter {
	
	Theme main;
	ArrayList<Theme> themes;//without original
	Context context;
	
	public ExpandableThemesAdapter(String mainId, ArrayList<Theme> themes, Context context){
		this.context = context;
		this.themes = new ArrayList<Theme>();
		for(Theme t : themes){
			if(t.id.equals(mainId)){
				main = t;
			}else{
				this.themes.add(t);
			}
		}		
	}
	
	@Override
	public Theme getChild(int arg0, int arg1) {		
		return themes.get(arg0);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg0;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
		TextView textView = new TextView(context);
        textView.setText(getChild(arg0, arg1).name);
        return textView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return 1;
	}

	@Override
	public Theme getGroup(int arg0) {
		return themes.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return themes.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.main_themes_list_row, null);
        TextView name = (TextView) row.findViewById(R.id.theme_name_li);
    	TextView description = (TextView) row.findViewById(R.id.theme_description_li);
    	Theme groupItem = getGroup(groupPosition);
    	name.setText(groupItem.name);
    	description.setText(groupItem.description);
		return row;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}	
}
