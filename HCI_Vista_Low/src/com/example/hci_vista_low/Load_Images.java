package com.example.hci_vista_low;

import android.os.Environment;
import java.io.*;
import java.util.*;

import android.content.Context;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.database.Cursor;
import android.util.Log;
public class Load_Images {
	
	/**
	 * This function return the default picture gallery folder in the system.
	 * @return String of the folder.
	 */
	public static String getExternalPublicPictureDir()
	{
		File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DCIM + "/Camera");
		Log.e("ImageGallery",path.getAbsolutePath());
		return path.getAbsolutePath();
	}
	/**
	 * 
	 * @param context
	 * @return a List<String> which contains all the filenames.
	 */
	public static ArrayList<String> getCameraImages(Context context) {
		String[] image_media = {Images.Media.DATA, Images.Media._ID};
		String bucketString = MediaStore.Images.Media.BUCKET_ID + "=?";
		String bucketId = getExternalPublicPictureDir();
		bucketId = String.valueOf(bucketId.toLowerCase().hashCode());
		
	    final Cursor cursor = context.getContentResolver().query(
	    		Images.Media.EXTERNAL_CONTENT_URI,
	    		image_media, 
	    		bucketString, 
	    		new String[]{bucketId}, 
	    		Images.Media._ID);
	    int image_count = cursor.getCount();
	    ArrayList<String> result = new ArrayList<String>(image_count);
	    for(int i = 0; i < image_count; i++){
	    	cursor.moveToPosition(i);
	    	int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
	    	result.add(cursor.getString(idx));
	    }
	    cursor.close();
	    return result;
	}
}
