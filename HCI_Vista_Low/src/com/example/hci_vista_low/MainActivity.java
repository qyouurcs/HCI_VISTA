package com.example.hci_vista_low;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	public final static String EXTRA_INTENT_MESSAGE = "com.urcs.hci_vista";
	private Bitmap resultimage;
	private Context mContext = this;
	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	private Uri mVideoUri;
	private String TAG = "DEBUG";
	private String SERVERURL = "";
	private String mCurrentPhotoPath;
	private Button takePictureBtn;
	private ImageButton browseBtn;
	private ImageButton searchBtn;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private TextView url_tv = null;
	private String mAlbumStorageDirFactory = "";
	private boolean mCameraReadyFlag = true;

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = new File(mAlbumStorageDirFactory);

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + ".jpg";
		File albumF = getAlbumDir();
		File imageF = new File(albumF + "/" + imageFileName);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;

		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, actionCode);
	}

	private void handleBigCameraPhoto(Intent data) {

		if (mCurrentPhotoPath != null) {
			Log.v("MainActivity", "CurrentPhotoPath " + mCurrentPhotoPath);
			/* Decode the JPEG file into a Bitmap */
			File file = new File(mCurrentPhotoPath);
			Uri output = Uri.fromFile(file);
			// Intent cameraIntent = new
			// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
			//Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, output);
			//sendBroadcast(intent);
			//Log.v("MainActivity", "Saved Image to " + mCurrentPhotoPath);

			// ask quanzeng about camera code.
			Intent intent1 = new Intent(MainActivity.this, DisplayResults.class);
			intent1.putExtra(EXTRA_INTENT_MESSAGE, mCurrentPhotoPath);
			startActivity(intent1);

		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		SERVERURL = getResources().getString(R.string.server_url);
		takePictureBtn = (Button) findViewById(R.id.takePicture);

		setBtnListenerOrDisable(takePictureBtn, new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
			}
		}, MediaStore.ACTION_IMAGE_CAPTURE);
		url_tv = (TextView) findViewById(R.id.pic_url);
		searchBtn = (ImageButton) findViewById(R.id.searchBtn);

		searchBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				dl_image t = new dl_image(mContext);
				t.execute(url_tv.getText().toString());
			}

		});
		searchBtn.setEnabled(true);
		browseBtn = (ImageButton) findViewById(R.id.browse_gallery);
		browseBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ListViewActivity.class);
				startActivity(intent);
			}
		});
		mVideoUri = null;
		mAlbumStorageDirFactory = Environment.getExternalStorageDirectory()
				+ "/DCIM/" + getString(R.string.album_name);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("ActivityResult", "Activity");
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				handleBigCameraPhoto(data);
				Log.d("ActivityResult", "Activity");
			}
			break;
		} // ACTION_TAKE_PHOTO_B
		} // switch
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		// outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
		// (mImageBitmap != null));
		outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY,
				(mVideoUri != null));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable(Button btn,
			Button.OnClickListener onClickListener, String intentName) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);
		} else {
			btn.setText(getText(R.string.cannot).toString() + " "
					+ btn.getText());
			btn.setClickable(false);
		}
	}

	public class ServerTask extends AsyncTask<String, Integer, Void> {
		public byte[] dataToServer;
		private String fn = "";
		// Task state
		private final int UPLOADING_PHOTO_STATE = 0;
		private final int SERVER_PROC_STATE = 1;
		private ProgressDialog dialog;

		// upload photo to server
		HttpURLConnection uploadPhoto(FileInputStream fileInputStream) {
			final String serverFileName = "test"
					+ (int) Math.round(Math.random() * 1000) + ".jpg";
			final String lineEnd = "\r\n";
			final String twoHyphens = "--";
			final String boundary = "*****";
			try {
				URL url = new URL(SERVERURL);
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
				int bytesAvailable = fileInputStream.available();
				int maxBufferSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				byte[] buffer = new byte[bufferSize];
				// read file and write it into form...
				int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				// send multipart form data after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				publishProgress(SERVER_PROC_STATE);
				// close streams
				fileInputStream.close();
				dos.flush();
				return conn;
			} catch (MalformedURLException ex) {
				Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
			} catch (IOException ioe) {
				Log.e(TAG, "error: " + ioe.getMessage(), ioe);
				return null;
			}
		}

		// get image result from server and display it in result view
		void getResultImage(HttpURLConnection conn) {
			// retrieve the response from server
			InputStream is;
			try {
				is = conn.getInputStream();
				resultimage = BitmapFactory.decodeStream(is);
				String result_jpg_fn = fn + "_1.jpg";
				FileOutputStream fos = new FileOutputStream(result_jpg_fn);
				resultimage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.close();
				Intent intent = new Intent(MainActivity.this,
						DisplayResults.class);
				intent.putExtra(EXTRA_INTENT_MESSAGE, result_jpg_fn);
				is.close();

				startActivity(intent);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}

		// Main code for processing image algorithm on the server
		void processImage(String inputImageFilePath) {
			publishProgress(UPLOADING_PHOTO_STATE);
			Log.v("InputImage", inputImageFilePath);
			File inputFile = new File(inputImageFilePath);
			try {
				FileInputStream fileInputStream = new FileInputStream(inputFile);
				final HttpURLConnection conn = uploadPhoto(fileInputStream);
				if (conn != null) {
					getResultImage(conn);
				}
				fileInputStream.close();
			} catch (FileNotFoundException ex) {
				Log.e(TAG, ex.toString());
			} catch (IOException ex) {
				Log.e(TAG, ex.toString());
			}
		}

		public ServerTask() {
			dialog = new ProgressDialog(mContext);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("Photo captured");
			this.dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) { // background
															// operation
			String uploadFilePath = params[0];
			fn = uploadFilePath;
			processImage(uploadFilePath);
			// release camera when previous image is processed
			mCameraReadyFlag = true;
			return null;
		}

		// progress update, display dialogs
		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (progress[0] == UPLOADING_PHOTO_STATE) {
				dialog.setMessage("Uploading");
				dialog.show();
			} else if (progress[0] == SERVER_PROC_STATE) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				dialog.setMessage("Processing");
				dialog.show();
			}
		}

		@Override
		protected void onPostExecute(Void param) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
}