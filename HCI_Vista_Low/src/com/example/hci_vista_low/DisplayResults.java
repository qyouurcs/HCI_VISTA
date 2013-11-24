package com.example.hci_vista_low;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;


import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hci_vista_low.Selector;
import com.example.hci_vista_low.ListOfSelectors;



public class DisplayResults extends Activity {
	Mat mat;              // OpenCV matrix for image
	Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
	ImageView result;
	private static final String    TAG                 = "OCVSample::Activity";
	  
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		  @Override
		  public void onManagerConnected(int status) {
		      switch (status) {
		          case LoaderCallbackInterface.SUCCESS:
		          {
		              Log.i(TAG, "OpenCV loaded successfully");
		              OpenCVInit();
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
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback)){
        	Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
        else{ 
        	Log.i(TAG, "opencv successfull"); 
        }
	}
	
    public void OpenCVInit()
    {
        setContentView(R.layout.activity_display_results);
		mat = new Mat();
    }

    public void display()
    {
		Intent intent = getIntent();
		String result_fn = intent.getStringExtra(MainActivity.EXTRA_INTENT_MESSAGE);
		//Bitmap image = BitmapFactory.decodeFile(result_fn); 
		// result = (ImageView) findViewById(R.id.result_pic);
		// result.setImageBitmap(image);
		setContentView(new SingleTouchEventView(this, null,result_fn ));
    }	
}
