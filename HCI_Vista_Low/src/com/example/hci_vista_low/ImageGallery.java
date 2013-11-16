package com.example.hci_vista_low;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ImageGallery extends Activity {
	private GridView gridView;
	private CustomerImageViewAdapter customGallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_gallery);
		gridView = (GridView) findViewById(R.id.image_gallery);
		final ArrayList<String> img_list = Load_Images.getCameraImages(this);
		Log.e("ImageGallery", img_list.size() + "");
		customGallery = new CustomerImageViewAdapter(this, R.layout.image_view,
				img_list);
		gridView.setAdapter(customGallery);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				//Toast.makeText(ImageGallery.this, position + "#Selected",
				//		Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ImageGallery.this, DisplayResults.class);

				intent.putExtra(MainActivity.EXTRA_INTENT_MESSAGE, img_list.get(position));
				Log.e("Image_Gallery","StartIntent " + img_list.get(position));
				startActivity(intent);
			}

		});

	}
}
