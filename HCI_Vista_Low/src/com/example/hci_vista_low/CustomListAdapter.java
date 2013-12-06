package com.example.hci_vista_low;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private String[] web;
	private String json_result = null; 
	private JSONArray multiResults = null;
	private ArrayList<String[]> imgs = null; 
	public CustomListAdapter(Activity context, ArrayList<String[]> imgs, String[] titles){
		super(context, R.layout.singleitem, titles);
		this.context = context;
		this.web = titles;
		//Here we should download all the images.
		this.imgs = imgs;
		Log.e("Custom", "Constructor");
		//multiResults = new JSONArray(json_result);
	}
	public CustomListAdapter(Activity context, String[] web, Integer[] imageId) {
		super(context, R.layout.singleitem, web);
		this.context = context;
		this.web = web;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.singleitem, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		txtTitle.setText(web[position]);
		/*if( position == 1)
		{
			Uri img_url = Uri.parse("http://ec2-54-200-138-198.us-west-2.compute.amazonaws.com/vista/BAGSNEW/Large-6-_8275426.jpg");
			imageView.setImageURI(img_url);
		}
		else*/
		//imageView.setImageResource(R.drawable.search);
		String[] files = imgs.get(position);
		Log.e("Custom", files[0]);
		Bitmap bt = BitmapFactory.decodeFile(files[0]);
		Log.e("Custom", files[0]);
		//imageView.setImageURI( Uri.fromFile(new File(files[0])) );
		double scale = bt.getHeight() / 50.0;
		scale = scale > bt.getWidth() / 50.0 ? bt.getWidth() / 50.0 : scale;
		imageView.setImageBitmap(Bitmap.createScaledBitmap(bt, (int)(bt.getWidth()/scale), (int)(bt.getHeight()/scale), false));
		return rowView;
	}
}