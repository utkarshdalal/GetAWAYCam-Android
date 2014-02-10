package com.example.getawaycam;

import android.graphics.drawable.Drawable;

public class ImageDetails {
	String mName, mTitle, mDate;
	Drawable mDrawable;
	public ImageDetails(String title, String name, String date, Drawable drawable){
		mTitle = title;
		mName = name;
		mDate = date;
		mDrawable = drawable;
	}
	public String getName() {
		return mName;
	}
	public String getTitle() {
		return mTitle;
	}
	public String getDate() {
		return mDate;
	}
	public Drawable getDrawable() {
		return mDrawable;
	}

}
