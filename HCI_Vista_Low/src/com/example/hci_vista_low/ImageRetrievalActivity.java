package com.example.hci_vista_low;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImageRetrievalActivity extends Activity {
	public static final String EXTRA_INTENT_PRICE = "PRICE";
	public static final String EXTRA_INTENT_TITLE = "TITLE";
	public static final String EXTRA_INTENT_DETAILS = "DETAILS";
	public static final String EXTRA_INTENT_IMG = "IMG_PATH";
	public static final String EXTRA_INTENT_URL = "URL";
	String mAlbumStorageDirFactory = null;
	ListView list;
	ArrayList<String[]> img_path = null;
	private ArrayList<String> price_list = null;
	private ArrayList<String> details_list = null;
	private ArrayList<String> title_list = null;
	private ArrayList<String> url_list = null;
	private String getAlbumDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			String storageDir =mAlbumStorageDirFactory;
			return storageDir;
		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return null;
	}

	private String createImageFile() {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_" + ".jpg";
		String albumF = getAlbumDir();
		String imageF = albumF + "/" + imageFileName;
		return imageF;
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	     protected Bitmap doInBackground(String... urls) {
	         return loadImageFromNetwork(urls[0]);
	     }

	     protected void onPostExecute(Bitmap result) {
	         //Do something with bitmap eg:
	     }
	 }

	 private Bitmap loadImageFromNetwork(String url){
	      try {
	          Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
	          return bitmap;
	      } catch (Exception e) {
	    	  Log.e("IR ERROR", e.getMessage());
	          e.printStackTrace();
	          return null;
	    }
	 }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_retrieval);
		Intent intent = getIntent();
		String json_result = intent.getStringExtra(DisplayResults.EXTRA_INTENT_MESSAGE_RESULT);
		// I need to extract all the titles to initialize CustomListAdapter class.
		String[] titles = null;
		mAlbumStorageDirFactory = Environment.getExternalStorageDirectory() + "/DCIM/" + getString(R.string.album_name);
		img_path = new ArrayList<String[]>();
		price_list = new ArrayList<String>();
		details_list = new ArrayList<String>();
		title_list = new ArrayList<String>();
		url_list = new ArrayList<String>();
		try{
			JSONArray multiResults = new JSONArray(json_result);
			titles = new String[multiResults.length()];
			for (int j = 0; j < multiResults.length(); j++) {
				JSONObject jsonObject = multiResults.getJSONObject(j);
				String title = jsonObject.getString("title");
				titles[j] = title;
				title_list.add(title);
				
				String price = jsonObject.getString("price");
				price_list.add(price);
				
				String details = jsonObject.getString("details");
				details_list.add(details);
				
				String url = jsonObject.getString("url");
				url_list.add(url);
				
				String paths = jsonObject.getString("paths");
				paths = paths.replace("[",  "");
				paths = paths.replace("]", "");
				String[] imgs = paths.split(",");
				String[] file_i = new String[imgs.length];
				for(int i = 0; i < imgs.length && i < 3; i++)
				{
					dl_image t = new dl_image(this);
					String str = imgs[i].replace("\\", "");
					str = str.replace("\"", "");
					Bitmap b = new DownloadImageTask().execute(str).get();
					String file_path = createImageFile();
					File tmp_file = new File(file_path);
					FileOutputStream fos = new FileOutputStream(tmp_file);
					b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
					fos.close();
					b.recycle();
					file_i[i] = file_path;
				}
				img_path.add(file_i);
				//Log.e("onCreate","");
			}
		}
		catch(Exception e){
			Log.e("IR_onCreate", e.getMessage());
			e.printStackTrace();
		}
		CustomListAdapter adapter = new CustomListAdapter(ImageRetrievalActivity.this, img_path, titles);
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		    	//We should start a new activity.
		        //Toast.makeText(ImageRetrievalActivity.this, 
		        //		"You Clicked at " + position, Toast.LENGTH_SHORT).show();
		    	Intent intent = new Intent(ImageRetrievalActivity.this, ViewDetails.class);
				intent.putExtra(EXTRA_INTENT_PRICE, price_list.get(position));
				intent.putExtra(EXTRA_INTENT_TITLE, title_list.get(position));
				intent.putExtra(EXTRA_INTENT_DETAILS, details_list.get(position));
				intent.putExtra(EXTRA_INTENT_IMG, img_path.get(position));
				intent.putExtra(EXTRA_INTENT_URL, url_list.get(position));
				startActivity(intent);
		    }
		});				
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_retrieval, menu);
		return true;
	}
}
