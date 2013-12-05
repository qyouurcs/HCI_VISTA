package com.example.gallery;


import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.example.gallery.*;
import com.example.hci_vista_low.R;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class LoadIcon extends Thread {

	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	
	private ImageSimpleAdapter adapter;
	private ProgressDialog progressDialog;

	public LoadIcon(ImageSimpleAdapter adapter, ProgressDialog progressDialog) {
		this.adapter = adapter;
		this.progressDialog = progressDialog;
	}

	public Runnable ImageLoding(final Map<String, Object> listItem,
			final String path) {
		Thread thread = new Thread() {
			public void run() {
				try {
					Bitmap bitmap;
					if (imageCache.containsKey(path)) {
						SoftReference<Bitmap> temp = imageCache.get(path);
						bitmap = temp.get();
						if (bitmap != null) {
							listItem.remove(R.drawable.ic_launcher);
							listItem.put("icon", bitmap);
							hander.sendEmptyMessage(0);
							return;
						}
					}

					bitmap = BitmapTool.getBitmap(path, 100, 100);
					imageCache.put(path, new SoftReference<Bitmap>(bitmap));
					listItem.remove(R.drawable.ic_launcher);
					listItem.put("icon", bitmap);

					hander.sendEmptyMessage(0);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		return thread;
	}

	// 处理消息
	private Handler hander = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				adapter.notifyDataSetChanged();
				if (progressDialog.isShowing())
					progressDialog.dismiss();
				break;
			}
		}
	};

}
