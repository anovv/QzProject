package com.me.qzproject;

import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	public String id;
	
	public String name;
	
	public String email;
	
	public String profile_img_url;
	
	public String thumbnail_img_url;
		
	public boolean isFriend;
	
	public User(String id, String name, String email, boolean isFriend){
		this.id = id;
		this.name = name;
		this.email = email;
		this.isFriend = isFriend;

		profile_img_url = "users/profiles/user_" + id + "_profile.jpg";
		thumbnail_img_url = "users/thumbnails/user_" + id + "_thumbnail.jpg";
	}
	
	public User(Map<String, String> user){
		if(user != null){
			this.id = user.get("id");
			this.name = user.get("fullname");
			this.email = user.get("email");
			if(user.containsKey("isFriend")){//TODO fix
				this.isFriend = (user.get("isFriend").equals("1"));
			}
			
			profile_img_url = "users/profiles/user_" + id + "_profile.jpg";
			thumbnail_img_url = "users/thumbnails/user_" + id + "_thumbnail.jpg";
			
		}
	}
	
	public User(Parcel in){
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(profile_img_url);
		dest.writeString(thumbnail_img_url);
		dest.writeByte((byte) (isFriend ? 1 : 0));
	}
	
	private void readFromParcel(Parcel in){
		id = in.readString();
		name = in.readString();
		email = in.readString();
		profile_img_url = in.readString();
		thumbnail_img_url = in.readString();
		isFriend = in.readByte() != 0;
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}
		
	}; 
}
