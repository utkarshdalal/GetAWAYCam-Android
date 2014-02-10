package com.example.getawaycam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
	
	public static int MEDIA_TYPE_IMAGE = 1;
	public static int MEDIA_TYPE_VIDEO = 2;
	public static String LATITUDE_EXTRA = "Latitude Extra";
	public static String LONGITUDE_EXTRA = "Longitude Extra";
	public static String PICTURES_EXTRA = "Pictures Extra";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	ImageView mCameraImageView;
	ImageView mGalleryImageView;
	Uri fileUri;
	ArrayList<File> fileNames = new ArrayList<File>();
	ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	ArrayList<Picture> pictures = new ArrayList<Picture>();
	LocationManager manager;
	Location lastKnownLocation;
	String providerName;
	Date lastUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCameraImageView = (ImageView)findViewById(R.id.cameraImage);
		mGalleryImageView = (ImageView)findViewById(R.id.galleryImage);
		mCameraImageView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				checkLocation();
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);			
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);	
			}			
		});
		mGalleryImageView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent galleryIntent = new Intent(MainActivity.this, GalleryActivity.class);	
				galleryIntent.putParcelableArrayListExtra(PICTURES_EXTRA, pictures);
				startActivity(galleryIntent);	
			}			
		});
	}
	
	protected void onActivityResult(int	requestCode, int resultCode, Intent	data){	
		if	(requestCode ==	CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){	
			if	(resultCode	==	RESULT_OK){
				Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show();
				//Image	captured and saved to fileUri specified	in	the	Intent	
				File file =	new	File(fileUri.getPath());
				//Toast.makeText(this, file.getPath(), Toast.LENGTH_SHORT).show();
				fileNames.add(file);
				Picture picture = new Picture(file, lastKnownLocation);
				pictures.add(picture);
				/*Toast.makeText(this, "The file's location is " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude()
						+ " and its path is " + file.getPath(), Toast.LENGTH_SHORT).show();*/
				Intent panoramioIntent = new Intent(this, PanoramioActivity.class);				
				panoramioIntent.putExtra(LATITUDE_EXTRA, lastKnownLocation.getLatitude());
				panoramioIntent.putExtra(LONGITUDE_EXTRA, lastKnownLocation.getLongitude());
				panoramioIntent.putParcelableArrayListExtra(PICTURES_EXTRA, pictures);
				startActivity(panoramioIntent);
				//if EXTRA_OUTPUT not specified, this will work	
				//Bitmap photo = (Bitmap) data.getExtras().get("data");
				//bitmaps.add(photo);
			}	
			else if	(resultCode	==	RESULT_CANCELED){
				Toast.makeText(this, "Image cancelled", Toast.LENGTH_SHORT).show();
				//	User	cancelled	the	image	capture	
			}
			else{
				Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
				//	Image	capture	failed,	advise	user	
			}	
		}	
	}
	
	public void checkLocation() {

        //initialize location manager
        manager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //check if GPS is enabled
        //if not, notify user with a toast
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is disabled.", Toast.LENGTH_SHORT).show();
        } 
        else {
            //get a location provider from location manager
            //empty criteria searches through all providers and returns the best one
            providerName = manager.getBestProvider(new Criteria(), true);
            lastKnownLocation = manager.getLastKnownLocation(providerName);            
            //sign up to be notified of location updates every 15 seconds
            manager.requestLocationUpdates(providerName, 15000, 1, this);
        }
	}
	
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "GetAWAYCam");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("GetAWAYCam", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		lastKnownLocation = location;
		/*if (lastKnownLocation != null) {
        	Toast.makeText(this, lastKnownLocation.getLatitude() + " latitude, " + lastKnownLocation.getLongitude() + " longitude", 
        			Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(this, "Last known location not found. Waiting for updated location...", 
        			Toast.LENGTH_SHORT).show();
        }*/
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
