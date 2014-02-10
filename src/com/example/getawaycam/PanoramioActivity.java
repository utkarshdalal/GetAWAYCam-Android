package com.example.getawaycam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PanoramioActivity extends Activity {
	
	TextView mTitle, mAuthor, mDate, mAnotherButton, mGalleryButton;
	ImageView mImage;
	double mLatitude, mLongitude;
	JSONObject mJson;
	ArrayList<Picture> pictures;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panoramio);
		mLatitude = intent.getDoubleExtra(MainActivity.LATITUDE_EXTRA, 0.0);
		mLongitude = intent.getDoubleExtra(MainActivity.LONGITUDE_EXTRA, 0.0);
		pictures = intent.getParcelableArrayListExtra(MainActivity.PICTURES_EXTRA);
		//Toast.makeText(this, mLatitude + ", " + mLongitude, Toast.LENGTH_SHORT).show();
		mTitle = (TextView)findViewById(R.id.title);
		mAuthor = (TextView)findViewById(R.id.author);
		mDate = (TextView)findViewById(R.id.date);
		mGalleryButton = (TextView)findViewById(R.id.galleryTextView);
		mImage = (ImageView)findViewById(R.id.imageView1);
		PanoramioImageTask task = new PanoramioImageTask();
		task.execute(new Double[] {mLatitude, mLongitude});
		mGalleryButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent galleryIntent = new Intent(PanoramioActivity.this, GalleryActivity.class);	
				galleryIntent.putParcelableArrayListExtra(MainActivity.PICTURES_EXTRA, pictures);
				startActivity(galleryIntent);
			}			
		});
	}
	
	private class PanoramioImageTask extends AsyncTask<Double, Void, ImageDetails>
	{

		@Override
		protected ImageDetails doInBackground(Double... latlong) {
			// TODO Auto-generated method stub
			double latitude = latlong[0];
			double longitude = latlong[1];
			double minLat = latitude - 5.0;
			double maxLat = latitude + 5.0;
			double minLng = longitude - 5.0;
			double maxLng = longitude + 5.0;
			int randomInt = (int) (Math.random() * 100);
			String results;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			HttpResponse response;
			JSONObject result;
			
			
			try {
				String uri = "http://www.panoramio.com/map/get_panoramas.php?set=public&from=0&to="+randomInt
						+"&minx="+minLat+"&miny="+minLng
						+"&maxx="+maxLat+"&maxy="+maxLng
						+"&size=medium&mapfilter=true";
				request.setURI(new	URI(uri));
				response = client.execute(request);
				StatusLine sl = response.getStatusLine();
				if (sl.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					results = out.toString();
					result = new JSONObject(results);
					JSONArray photos = result.getJSONArray("photos");
					JSONObject object = photos.getJSONObject((int)(Math.random()*photos.length()));
					String name = object.getString("owner_name");
					String title = object.getString("photo_title");
					String date = object.getString("upload_date");
					String photoURL = object.getString("photo_file_url");
					Log.d("Name", name);
					Log.d("Title", title);
					Log.d("Date", date);
					Log.d("URL", photoURL);
					URL url = new URL(photoURL);
					InputStream content = (InputStream) url.getContent();
					Drawable d = Drawable.createFromStream(content, "src");
					ImageDetails imageDetails = new ImageDetails(title, name, date, d);
					//mTitle.setText(title);
					//mAuthor.setText(name);
					//mDate.setText(date);
					return imageDetails;
					//Log.d("Photos", photos.toString());
					//Log.d("Object", object.toString());
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				Log.d("PanoramioAPIProblem", "Problem with Panoramio API");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}

		@Override
		protected void onPostExecute(ImageDetails id) {
			mTitle.setText('"'+id.getTitle()+'"');
			mAuthor.setText("by: " + id.getName());
			mDate.setText(id.getDate());
			mImage.setImageDrawable(id.getDrawable());
			mGalleryButton.setText("View Gallery");
			mGalleryButton.setBackgroundResource(color.holo_blue_dark);
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panoramio, menu);
		return true;
	}

}
