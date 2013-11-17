package com.example.hci_vista_low;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
public class DisplayResults extends Activity {
	Mat mat;
	private static final String    TAG                 = "OCVSample::Activity";
	  
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		  @Override
		  public void onManagerConnected(int status) {
		      switch (status) {
		          case LoaderCallbackInterface.SUCCESS:
		          {
		              Log.i(TAG, "OpenCV loaded successfully");
		              display();
		          } break;
		          default:
		          {
		              super.onManagerConnected(status);
		          } break;
		      }
		  }  
	  };
		  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback))
        {
          Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
        else{ Log.i(TAG, "opencv successfull"); 
        
        }
	}
	

    public void display()
    {
        setContentView(R.layout.activity_display_results);
		Intent intent = getIntent();
		String result_fn = intent.getStringExtra(MainActivity.EXTRA_INTENT_MESSAGE);
		Bitmap image = BitmapFactory.decodeFile(result_fn); 
		ImageView result = (ImageView) findViewById(R.id.result_pic);
		
		mat = new Mat();
		Utils.bitmapToMat(image, mat);
		
		Core.rectangle(mat, new Point(20,20), new Point(1000,1000), new Scalar( 0, 0, 255 ));
		
		
		Utils.matToBitmap(mat, image);
		
		result.setImageBitmap(image);
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_results, menu);
		return true;
	}

}
