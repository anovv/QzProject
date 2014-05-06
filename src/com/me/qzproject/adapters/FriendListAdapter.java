package com.me.qzproject.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.me.qzproject.APIHandler;
import com.me.qzproject.R;
import com.me.qzproject.User;
import com.me.qzproject.UserActivity;

public class FriendListAdapter extends ArrayAdapter<User>{
	
	Context context;
	
    public FriendListAdapter(Context context, int textViewResourceId, ArrayList<User> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
    }


	/*private view holder class*/
    /*private class ViewHolder {
        ImageView imageView;
        TextView username;
        TextView email;
    }*/

    public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, null);
        }

        User rowItem = getItem(position);
    	
    	TextView username = (TextView) row.findViewById(R.id.user_name_li);
    	TextView email = (TextView) row.findViewById(R.id.email_li);
    	ImageView imageView = (ImageView) row.findViewById(R.id.list_image);
    	ProgressBar pBar = (ProgressBar) row.findViewById(R.id.list_row_spinner);
    	username.setText(rowItem.name);
        email.setText(rowItem.email);
        String img_url = "http://" + APIHandler.ip + ":80/laravel/public/images/users/thumbnails/user_" + rowItem.id + "_thumbnail.jpg";
        
        new UserActivity.DownloadImage(imageView, img_url, pBar).execute();
        
    	//imageView.setImageResource(R.drawable.ic_launcher);
    	   
        
        /*ViewHolder holder = null;
        User rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_name_li);
            holder.email = (TextView) convertView.findViewById(R.id.email_li);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.username.setText(rowItem.name);        
        //holder.email.setText(rowItem.email);
        holder.username.setText(rowItem.name);
        holder.email.setText(rowItem.email);
        
        //holder.imageView.setImageResource(rowItem.getImageId());
		*/
        return row;
    }
}
