package com.example.hci_vista_low;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
public class DisplayResults extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_results);
		Intent intent = getIntent();
		String result_fn = intent.getStringExtra(MainActivity.EXTRA_INTENT_MESSAGE);
		Bitmap image = BitmapFactory.decodeFile(result_fn); 
		ImageView result = (ImageView) findViewById(R.id.result_pic);
		result.setImageBitmap(image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_results, menu);
		return true;
	}

}
