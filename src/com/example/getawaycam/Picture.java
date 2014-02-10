package com.example.getawaycam;

import java.io.File;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Picture implements Parcelable{
	File mFile;
	double mLatitude;
	double mLongitude;
	Location mLocation;
	public Picture(File file, Location location){
		mFile = file;
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		mLocation = location;
	}
	private Picture(Parcel in) {
		// TODO Auto-generated constructor stub
		mFile = new File(in.readString());
		mLatitude = in.readDouble();
		mLongitude = in.readDouble();
	}
	public File getFile() {
		return mFile;
	}
	public double getLatitude() {
		return mLatitude;
	}
	public double getLongitude() {
		return mLongitude;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mFile.toString());
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
	}
	public static final Parcelable.Creator<Picture> CREATOR = 
			new Parcelable.Creator<Picture>() {
		public Picture createFromParcel(Parcel in) {
			return new Picture(in);
		}
		public Picture[] newArray(int size) {
			return new Picture[size];
		}
	};
}
