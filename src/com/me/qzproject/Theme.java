package com.me.qzproject;

import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class Theme implements Parcelable{
	public String id;
	public String name;
	public String description;
	
	public Theme(String id, String name, String description){
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	
	public Theme(Map<String, String> m){
		this.id = m.get("id");
		this.name = m.get("name");
		this.description = m.get("description");
	}
	
	public Theme(Parcel in){
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
		dest.writeString(description);
	}
	
	private void readFromParcel(Parcel in){
		id = in.readString();
		name = in.readString();
		description = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public Theme createFromParcel(Parcel source) {
			return new Theme(source);
		}

		@Override
		public Theme[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Theme[size];
		}
		
	}; 
}
