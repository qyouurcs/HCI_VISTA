package com.example.hci_vista_low;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.gallery.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewActivity extends Activity {
	/** Called when the activity is first created. */

	private TextView filePathTextView;
	private ListView fileListView;
	private Button backButton;
	private ProgressDialog progressDialog;
	private File currentParent;
	private File[] currentFiles;
	private ArrayList<String> filePath = new ArrayList<String>();
	private ImageSimpleAdapter adapter;
	private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

	private int selectFileIndex;
	private int clickCount = 0;
	private long firstClick = 0, secondClick = 0;

	private ExecutorService loadImageThreadPool;
	private LoadIcon loadImageThread;

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inflateListView(currentFiles);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		fileListView = (ListView) this.findViewById(R.id.fileListView);
		filePathTextView = (TextView) this.findViewById(R.id.filePathTextView);
		backButton = (Button) this.findViewById(R.id.backButton);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Please wait...");
		progressDialog.setMessage("Open the folder for you.");
		progressDialog.setIndeterminate(false);
		adapter = new ImageSimpleAdapter(ListViewActivity.this, listItems,
				R.layout.listview, new String[] { "filename", "icon", "modify",
						"size" }, new int[] { R.id.fileNameTextView,
						R.id.iconImageView, R.id.fileDateTextView,
						R.id.fileLengthTextView });
		fileListView.setAdapter(adapter);

		loadImageThread = new LoadIcon(this.adapter, this.progressDialog);
		File root = new File("/mnt/sdcard/");
		if (root.exists()) {
			currentParent = root;
			currentFiles = root.listFiles();
			Tool.sortFile(currentFiles);
			inflateListView(currentFiles);
		}
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				progressDialog.show();
				File clickFile = new File(filePath.get(position));
				if (clickFile.isFile()) {
					Intent intent = new Intent();
					intent.setClass(ListViewActivity.this, PictureActivity.class);

					Bundle bundle = new Bundle();
					bundle.putInt("index", position);
					bundle.putStringArrayList("filePath", filePath);
					intent.putExtras(bundle);
					startActivity(intent);
					ListViewActivity.this.onPause();

					loadImageThreadPool.shutdownNow();

					selectFileIndex = position;

					return;
				}
				else {
					File[] tem = clickFile.listFiles();
					currentParent = clickFile;
					currentFiles = tem;
					Tool.sortFile(currentFiles);
					inflateListView(currentFiles);
					selectFileIndex = position;
				}
			}
		});

		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				progressDialog.show();
				loadImageThreadPool.shutdownNow();
				try {
					if (!currentParent.getCanonicalPath().equals("/mnt/sdcard")) {
						currentParent = currentParent.getParentFile();
						currentFiles = currentParent.listFiles();
						Tool.sortFile(currentFiles);
						inflateListView(currentFiles);
						fileListView.setSelection(selectFileIndex);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	private void inflateListView(File[] files) {
		boolean isNeedUIClose = true;
		loadImageThreadPool = Executors.newFixedThreadPool(6);
		listItems.clear();
		filePath.clear();

		if (files != null && files.length != 0) {
			for (int i = 0; i < files.length; ++i) {
				Map<String, Object> listItem = new HashMap<String, Object>();
				if (files[i].isDirectory()) {
					listItem.put("icon", R.drawable.folder);
					listItem.put("size", "");
				} else if (files[i].isFile()) {
					String path = files[i].getPath();
					if (path.endsWith(".jpg") || path.endsWith(".jpeg")
							|| path.endsWith(".png")) {
						listItem.put("icon", R.drawable.ic_launcher);
						listItem.put("size",
								Tool.formetFileSize(files[i].length()));
						loadImageThreadPool.execute(loadImageThread
								.ImageLoding(listItem, path));
						isNeedUIClose = false;
					}
					else {
						continue;
					}
				} else {
					continue;
				}
				listItem.put("filename", files[i].getName());
				File myFile = new File(files[i].getPath());
				long modTime = myFile.lastModified();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				listItem.put("modify", dateFormat.format(new Date(modTime)));
				listItems.add(listItem);
				filePath.add(files[i].getPath());
			}
		}
		
		adapter.notifyDataSetChanged();
		this.fileListView.setAdapter(adapter);

		if (isNeedUIClose) {
			this.progressDialog.dismiss();
		}

		try {
			filePathTextView.setText("Current Folder:"
					+ currentParent.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (currentParent.getCanonicalPath().equals("/mnt/sdcard"))
				backButton.setEnabled(false);
			else
				backButton.setEnabled(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clickCount++;
			if (clickCount == 2) {
				secondClick = System.currentTimeMillis();
				if (secondClick - firstClick < 500) {
					return super.onKeyDown(keyCode, event);
				}
			}
			Toast.makeText(ListViewActivity.this, "Double click to quit.", Toast.LENGTH_SHORT)
					.show();
			clickCount = 1;
			firstClick = System.currentTimeMillis();
			secondClick = 0;
		}
		// return true;
		return false;
	}
}