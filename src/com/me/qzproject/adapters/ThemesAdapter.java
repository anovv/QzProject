package com.me.qzproject.adapters;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.me.qzproject.R;
import com.me.qzproject.Theme;

public class ThemesAdapter extends ArrayAdapter<Theme>{

	Context context;
	
	public ThemesAdapter(Context context, int textViewResourceId, ArrayList<Theme> objects) {
        super(context, textViewResourceId, objects);//add objects to constructor
        // TODO Auto-generated constructor stub
        this.context = context;
    }
	
	public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.main_themes_list_row, null);
        }

        Theme rowItem = getItem(position);
    	
    	TextView name = (TextView) row.findViewById(R.id.theme_name_li);
    	TextView description = (TextView) row.findViewById(R.id.theme_description_li);
    	name.setText(rowItem.name);
    	description.setText(rowItem.description);
        return row;
    }
}
