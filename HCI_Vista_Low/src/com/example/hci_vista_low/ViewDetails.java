package com.example.hci_vista_low;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewDetails extends Activity {

	private String price = null;
	private String title = null;
	private String details = null;
	private String[] imgs = null;
	private String url = null;
	TextView txt_price = null;
	TextView txt_title = null;
	TextView txt_details = null;
	ImageView img = null;
	Button btn_check = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_details);
		Intent intent = getIntent();
		price = intent.getStringExtra(ImageRetrievalActivity.EXTRA_INTENT_PRICE);
		title = intent.getStringExtra(ImageRetrievalActivity.EXTRA_INTENT_TITLE);
		details = intent.getStringExtra(ImageRetrievalActivity.EXTRA_INTENT_DETAILS);
		imgs = intent.getStringArrayExtra(ImageRetrievalActivity.EXTRA_INTENT_IMG);
		url = intent.getStringExtra(ImageRetrievalActivity.EXTRA_INTENT_URL);
		url = url.replace("\\", "");
		txt_price = (TextView) findViewById(R.id.txt_price);
		txt_price.setText(price);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText(title);
		txt_details = (TextView) findViewById(R.id.txt_details);
		txt_details.setText(details);
		img = (ImageView) findViewById(R.id.img_result);
		btn_check = (Button) findViewById(R.id.check);
		btn_check.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.e("ViewDetails", url);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		Bitmap bt = BitmapFactory.decodeFile(imgs[0]);
		//double scale = bt.getHeight() / 50.0;
		//scale = scale > bt.getWidth() / 50.0 ? bt.getWidth() / 50.0 : scale;
		img.setImageBitmap(bt);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_details, menu);
		return true;
	}

}
