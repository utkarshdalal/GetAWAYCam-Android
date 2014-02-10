package com.example.getawaycam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryActivity extends Activity implements LocationListener {
	
	LocationManager manager;
	String providerName;
	Location lastKnownLocation;
	ArrayList<Picture> pictures;
	GridView gridview;
	ArrayList<Bitmap> bitmaps;
	ArrayList<Boolean> visible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		Intent intent = getIntent();
		pictures = intent.getParcelableArrayListExtra(MainActivity.PICTURES_EXTRA);
		Log.d("Pictures", pictures.toString());
		checkLocation();
		bitmaps = new ArrayList<Bitmap>();
		visible = new ArrayList<Boolean>();
		gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	if (visible.get(position)){
	        		Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
	        		intent.putExtra("Image", pictures.get(position).getFile().getAbsolutePath());
	        		startActivity(intent);
	        	}
	        }
	    });
	}
	
	public void checkLocation() {

        //initialize location manager
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastKnownLocation = location;
		gridview.setAdapter(new ImageAdapter(this));
		//Refresh images
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
	
	public boolean isNear(double latitude, double longitude)
	{
		return ((Math.sqrt(Math.pow((lastKnownLocation.getLatitude() - latitude), 2) + 
				Math.pow(lastKnownLocation.getLongitude() - longitude, 2))) <= 0.1);
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	    	return pictures.size();
	        //return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        Log.d("Picture found", pictures.get(position).toString());
	        Picture picture = pictures.get(position);
	        Log.d("Position", Integer.toString(position));
	        Log.d("Bitmaps size", Integer.toString(bitmaps.size()));
	        if (bitmaps.size() > position && bitmaps.get(position) != null)
	        {
	        	if (isNear(picture.getLatitude(), picture.getLongitude())){
        			imageView.setImageResource(R.drawable.blocked);
        			visible.set(position, false);
        		}
	        	else{
	        		imageView.setImageBitmap(bitmaps.get(position));
	        		visible.set(position, true);
	        	}
	        }
	        else
	        {
	        	File file = picture.getFile();
	        	Log.d("File found", file.toString());
	        	try {
	        		FileInputStream streamIn = new FileInputStream(file);
	        		Bitmap bitmap = BitmapFactory.decodeStream(streamIn);
	        		streamIn.close();
	        		bitmaps.add(bitmap);
	        		if (isNear(picture.getLatitude(), picture.getLongitude())){
	        			imageView.setImageResource(R.drawable.blocked);
	        			visible.add(false);
	        		}
	        		else{
	        			imageView.setImageBitmap(bitmap);
	        			visible.add(true);
	        		}
	        	} catch (FileNotFoundException e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	} catch (IOException e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
	        }
	        //imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	    // references to our images
	    private Integer[] mThumbIds = {R.drawable.camera, R.drawable.gallery, R.drawable.loading
	    };
	}

}
