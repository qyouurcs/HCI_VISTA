package com.example.hci_vista_low;

import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DisplayResults extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 9999;
	Mat mat; // OpenCV matrix for image
	Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
	ImageView result;
	private ProgressBar pb;
	private static final String TAG = "OCVSample::Activity";
	private SingleTouchEventView singleTouch = null;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				OpenCVInit();
				display();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.display_results, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {

		case R.id.action_compose:
			speak();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void speak() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					// If first Match contains the 'search' word
					// Then start web search.
					if (textMatchList.get(0).contains("search")) {

						String searchQuery = textMatchList.get(0).replace(
								"search", " ");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);
					} else {
						// populate the Matches
						String msg = textMatchList.get(0);
						pb.setVisibility(View.VISIBLE);
						AsyncItemDec dec = new AsyncItemDec();
						dec.execute(100);
						// showToastMessage(msg.trim());
					}

				}
				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage("Audio Error");
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage("Client Error");
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage("Network Error");
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage("No Match");
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage("Server Error");
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Trying to load OpenCV library");
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback)) {
			Log.e(TAG, "Cannot connect to OpenCV Manager");
		} else {
			Log.i(TAG, "opencv successfull");
		}

	}

	class AsyncItemDec extends AsyncTask<Integer, Integer, String> {
		protected String doInBackground(Integer... params) {
		
			for (int i = 0; i <= 100; i++) {
				pb.setProgress(i);
				publishProgress(i, 5);
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "";
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {
			// setTitle(result);
			pb.setVisibility(View.INVISIBLE);
		}

	}

	public void OpenCVInit() {
		setContentView(R.layout.activity_display_results);
		pb = (ProgressBar) findViewById(R.id.item_dec_progress);
		singleTouch = (SingleTouchEventView) findViewById(R.id.single_touch);
		// result = (ImageView) findViewById(R.id.result_pic);
		mat = new Mat();
	}

	public void display() {
		Intent intent = getIntent();
		String result_fn = intent
				.getStringExtra(MainActivity.EXTRA_INTENT_MESSAGE);
		// Bitmap image = BitmapFactory.decodeFile(result_fn);
		// result = (ImageView) findViewById(R.id.result_pic);
		// result.setImageBitmap(image);
		// result = new SingleTouchEventView(this, null, result_fn);
		// setContentView(R.layout.activity_display_results);
		// setContentView(new SingleTouchEventView(this, null, result_fn));
		singleTouch.setImageFile(result_fn);
		// addContentView(new SingleTouchEventView(this, null, result_fn));
	}
}
