package com.example.hci_vista_low;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CustomerImageViewAdapter extends ArrayAdapter<String>{

	private Context context;
	private int layoutResourceId;
	private ArrayList<String> data = new ArrayList<String>();

	public CustomerImageViewAdapter(Context context, int layoutResourceId,
			ArrayList<String> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}
		// Here we use all the
		String image_fn = data.get(position);
		Log.e("" + position + "", image_fn);

		Bitmap cur_Bitmap = BitmapFactory.decodeFile(image_fn);
		int width = cur_Bitmap.getWidth();
		int height = cur_Bitmap.getHeight();
		float scaleHeight = (float) height / (float) 100;
		float scale;
		float scaleWidth = (float) width / (float) 100;
		if (scaleWidth < scaleHeight)
			scale = scaleHeight;
		else
			scale = scaleWidth;
		cur_Bitmap = Bitmap.createScaledBitmap(cur_Bitmap,
				(int) (width / scale), (int) (height / scale), true);
		holder.image.setImageBitmap(cur_Bitmap);
		return row;
	}

	static class ViewHolder {
		ImageView image;
	}
}

