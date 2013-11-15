package com.example.hci_vista_low;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class dl_image extends AsyncTask<String, Void, Void> {

	private Context context = null;

	public dl_image(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		// String
		// path=context.getFilesDir()+File.pathSeparator+"temp_"+System.currentTimeMillis()+".png";
		HttpURLConnection conn = null;

		android.util.Log.v("TASK", "opening url=" + params[0]);

		try {
			final URL url = new URL(params[0]);

			InputStream is;
			try {
				conn = (HttpURLConnection) url.openConnection();
				is = conn.getInputStream();
				Bitmap resultimage = BitmapFactory.decodeStream(is);
				Intent intent = new Intent(context, DisplayResults.class);
				File tmp_file = File.createTempFile("image", null);
				FileOutputStream fos = new FileOutputStream(tmp_file);
				resultimage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				intent.putExtra(MainActivity.EXTRA_INTENT_MESSAGE,
						tmp_file.getAbsolutePath());
				is.close();
				fos.close();
				context.startActivity(intent);
			} catch (IOException e) {
				Log.e("dl_image", e.toString());
				e.printStackTrace();
			}
		} catch (IOException e) {
			android.util.Log.e("TASK", "error loading image", e);
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			return null;
		}
	}
}
