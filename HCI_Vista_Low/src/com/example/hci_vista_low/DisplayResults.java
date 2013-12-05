package com.example.hci_vista_low;

import java.io.ByteArrayOutputStream;
import android.graphics.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayResults extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 9999;
	private Bitmap resultimage;
	Mat mat; // OpenCV matrix for image
	Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
	ImageView result;
	private ProgressDialog main_pd;
	
	private static final String TAG = "OCVSample::Activity";
	private SingleTouchEventView singleTouch = null;
	private LinearLayout action_layout = null;
	private Button btn_cancel = null; // cancel the current selection.
	private Button btn_submit = null; // submit the small patches to the server.
	private Context mContext = this;
	private String SERVERURL = "";
	private String people_detect_url = "";
	private String bag_detect_url = "";
	private TextView json_view = null;
	private String result_fn = "";
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

	
	private void private_init()
	{
		setContentView(R.layout.activity_display_results);
		if(singleTouch == null) // Same with the progress bar.
		{
			singleTouch = (SingleTouchEventView) findViewById(R.id.single_touch);
			action_layout = (LinearLayout) findViewById(R.id.bottomBar);
			singleTouch.setActionLayout(action_layout);
		}
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				singleTouch.clear();
				json_view.setText("");
				json_view.setVisibility(View.INVISIBLE);
			}
		});
		btn_submit = (Button) findViewById(R.id.btn_submit);
		// In this submit action, we are going to send out the patches to the
		// server.
		btn_submit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Bitmap[] selected_areas = singleTouch.getSelectionBitmaps();
				// do the server staff.
				String[] formated_results = new String[selected_areas.length];
				String json_result = "";
				for (int i = 0; i < selected_areas.length; i++) {
					ServerTask task = null;
					if( i==0 )
						task = new ServerTask(SERVERURL, "Initializing task", main_pd, PostProcess.IMG_SEARCH);
					else
						task = new ServerTask(SERVERURL, false, PostProcess.IMG_SEARCH);
					task.setIdx(i + 1);
					try {
						json_result = task.execute(selected_areas[i]).get();
						JSONArray multiResults = new JSONArray(json_result);
						System.out.println(multiResults.length() + "\n");
						for (int j = 0; j < multiResults.length(); j++) {
							JSONObject jsonObject = multiResults
									.getJSONObject(j);
							String title = jsonObject.getString("title");
							String desc = jsonObject.getString("details");
							double price = jsonObject.getDouble("price");
							String path = jsonObject.getString("url");
							System.out.println("title:" + title);
							System.out.println("desc: " + desc);
							System.out.println("price: " + price);
							System.out.println("path: " + path);
							formated_results[i] += title + '\n';
							/*
							 * formated_results[i] += desc + '\n';
							 * formated_results[i] += price + '\n';
							 * formated_results[i] += path + '\n';
							 */
						}
						formated_results[i] += "----------------------------\n";

					} 
					catch (JSONException e) {
						// TODO Auto-generated catch block
						formated_results[i] += json_result;
						e.printStackTrace();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				String display_titles = "";
				for (int i = 0; i < formated_results.length; i++) {
					display_titles += formated_results[i];
				}
				json_view.setText(display_titles);
				json_view.setVisibility(View.VISIBLE);
				singleTouch.clear();
			}
		});
		json_view = (TextView) findViewById(R.id.json_result);
		SERVERURL = getResources().getString(R.string.server_url);
		people_detect_url = getResources().getString(R.string.people_detector);
		bag_detect_url = getResources().getString(R.string.bag_detector);
		main_pd = new ProgressDialog(this);
	
		//pd = ProgressDialog.show(DisplayResults.this, null,this.init_text);
	}
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
		switch (item.getItemId()) 
		{
		case R.id.action_compose:
			speak();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public enum PostProcess{
		SPEECH, IMG_SEARCH
	}
	/**
	 * bounding box returned by speech recognition.
	 * @param bounding_box
	 */
	public void postProcessSpeak(String bounding_box){
		if (bounding_box != null) {
			String[] multiples = bounding_box.split("\n");
			//may detect multiple objects in the same image.
			String[] xys = bounding_box.split(",");
			
			if (xys.length >= 4) {
				int[] c = new int[4];
				// Should restore the xy first to the
				// non-scaled version.
				for (int i = 0; i < 4; i++)
					c[i] = (int) (0.5 + Double.parseDouble(xys[i])) * 4;
				// Now it's time to draw the bounding boxes.
				int red = Color.argb(255, 255, 0, 0);
				singleTouch.addSelector(new Selector(c[0],c[1],c[2],c[3], red));
				singleTouch.invalidate();
			}
		}
	}
	/**
	 * Process the retrieval results from the server.
	 */
	public void postProcessUpload(){
		return;
	}
	public void speak() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
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
						String searchQuery = textMatchList.get(0).replace("search", " ");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);
					} else {
						// populate the Matches
						String msg = textMatchList.get(0);
						msg = msg.toLowerCase();
						
						showToastMessage(msg.trim());
						String server_url = null;
						Log.e("Voice Text",msg);
						if (msg.contains("people") || msg.contains("person")) 
							server_url = this.people_detect_url;
						else
							if(msg.contains("bag") || msg.contains("bags"))
								server_url = this.bag_detect_url;
						if(server_url != null)
						{
							// now, we need to send this picture to the server
							// to detect people.
							Bitmap upload_image = BitmapFactory
									.decodeFile(this.result_fn);
							// Bitmap small_version =
							// Bitmap.createBitmap(upload_image,upload_image.getWidth()/4,
							// upload_image.getHeight()/4,false);
							
							Bitmap small_version = Bitmap.createScaledBitmap(
									upload_image, singleTouch.getWidth()/4,
									singleTouch.getHeight()/4, false);
							ServerTask task = new ServerTask(server_url, "Process your command: " + msg,main_pd, PostProcess.SPEECH);
							String bounding_box = "";
							Log.e("bounding box", bounding_box);
							try {
								task.execute(small_version);
							} 
							catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
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
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e("DisplayOnCreate", "Trying to load OpenCV library");
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
			
		}
	}

	public void OpenCVInit() {
		//Log.e("OpenCVInit", "I should only appear once");
		mat = new Mat();
		private_init();
	}

	public void display() {
		Intent intent = getIntent();
		result_fn = intent.getStringExtra(MainActivity.EXTRA_INTENT_MESSAGE);
		Log.e("DEBUG", result_fn);
		singleTouch.setImageFile(result_fn);
	}

	public class ServerTask extends AsyncTask<Bitmap, Integer, String> {
		public byte[] dataToServer;
		public Bitmap processed_image = null;
		private final int UPLOADING_PHOTO_STATE = 0;
		private final int SERVER_PROC_STATE = 1;
		private String server_url = "";
		private String init_text = "";
		private ProgressDialog pd;
		private int idx = -1;
		private boolean isShowPD = false;
		private PostProcess pp_type;
		/**
		 * 
		 * @param image_patches
		 * @param format
		 *            .jpg or .png which indicate the format of the uploaded
		 *            image.
		 * @return
		 */
		HttpURLConnection uploadPhoto(byte[] image_patches, String format) {
			final String serverFileName = System.currentTimeMillis() + "_"
					+ (int) Math.round(Math.random() * 1000) + format;
			final String lineEnd = "\r\n";
			final String twoHyphens = "--";
			final String boundary = "*****";
			try {
				URL url = new URL(server_url);
				// URL url = new URL(people_detect_url);
				// Open a HTTP connection to the URL
				final HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// Allow Inputs
				conn.setDoInput(true);
				// Allow Outputs
				conn.setDoOutput(true);
				// Don't use a cached copy.
				conn.setUseCaches(false);
				// Use a post method.
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
						+ serverFileName + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
				// create a buffer of maximum size
				// dos.write(buffer, 0, bufferSize);
				dos.write(image_patches);
				// send multipart form data after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				// close streams
				dos.flush();
				return conn;
			} catch (MalformedURLException ex) {
				Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				Log.e("HTTP-upload", "error: " + ioe.getMessage(), ioe);
				return null;
			}
		}

		public ServerTask(String server_url, boolean isShowPD, PostProcess type) {
			this.server_url = server_url;
			this.isShowPD = isShowPD;
			this.pp_type = type;
			// dialog = new ProgressDialog(mContext);
		}

		public ServerTask(String server_url, String init_text, ProgressDialog pd, PostProcess type) {
			this.server_url = server_url;
			this.init_text = init_text;
			this.isShowPD = true;
			this.pd = pd;
			this.pp_type = type;
			// dialog = new ProgressDialog(mContext);
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}

		public String dealResponseResult(InputStream inputStream) {
			String resultData = null; //
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			resultData = new String(byteArrayOutputStream.toByteArray());
			return resultData;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.e("In PreExecute ","In PreExecute");
			if( this.isShowPD )
			{
				Log.e("In PreExecute ","Showing pd");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		}

		@Override
		protected String doInBackground(Bitmap... params) { // background
											// operation
			Bitmap upload_image = params[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			upload_image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
			byte[] bytes_array = stream.toByteArray();
			// processImage(bytes_array);
			// Main code for processing image algorithm on the server
			publishProgress(UPLOADING_PHOTO_STATE);
			final HttpURLConnection conn = uploadPhoto(bytes_array, ".jpg");
			publishProgress(SERVER_PROC_STATE);
			if (conn != null) {
				// now it's time to receive the result for the just uploaded
				// image.
				// get image result from server and display it in result view
				InputStream is;
				try {
					is = conn.getInputStream();
					
					String json_txt_result = dealResponseResult(is);
					// System.out.println("Json: " + json_txt_result);
					is.close();
					conn.disconnect();// No use anymore.
					Log.e("DoInBackground", json_txt_result);
					return json_txt_result;
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			}
			return null;
		}

		// progress update, display dialogs
		@Override
		protected void onProgressUpdate(Integer... progress) {
			if(!this.isShowPD)
				return;
			if (progress[0] == UPLOADING_PHOTO_STATE) {
				Log.e("UPloading Image","Uploading image");
				pd.setMessage("Uploading Image");
				pd.show();
				// dialog.setMessage("Uploading");
				// dialog.show();
			} else if (progress[0] == SERVER_PROC_STATE) {
				if(pd.isShowing())
					pd.dismiss();
				pd.setMessage("Please wait for me to receive results");
				pd.show();
			}
		}

		@Override
		protected void onPostExecute(String param) {
			if(this.isShowPD && pd.isShowing())
				pd.dismiss();
			Log.e("PostExecute", pp_type + " " + param );
			switch(pp_type){
				case SPEECH:
					Log.e("PostExecute", param);
					postProcessSpeak(param);
					break;
				case IMG_SEARCH:
					postProcessUpload();
					break;
				default:
					break;
			}
		}
	}
}
