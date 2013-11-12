package com.example.hci_vista_low;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


public class ServerTask  extends AsyncTask<String, Integer , Void>
{
	public byte[] dataToServer;
	private final String DEBUG_TAG = "Vista Server Task";
	//Task state
	private final int UPLOADING_PHOTO_STATE  = 0;
	private final int SERVER_PROC_STATE  = 1;
	
	private ProgressDialog dialog;
	private String SERVERURL = "";
	private ResultView mResultView=null;
	
    public ServerTask(String url, Context mContext, ResultView resultview) {
    	this.SERVERURL = url;//
    	this.mResultView = resultview;
        dialog = new ProgressDialog(mContext);
    }		
	
	//upload photo to server
	HttpURLConnection uploadPhoto(FileInputStream fileInputStream)
	{
		
		final String serverFileName = "test"+ (int) Math.round(Math.random()*1000) + ".jpg";		
		final String lineEnd = "\r\n";
		final String twoHyphens = "--";
		final String boundary = "*****";
		
		try
		{
			URL url = new URL(SERVERURL);
			// Open a HTTP connection to the URL
			final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);				
			// Allow Outputs
			conn.setDoOutput(true);				
			// Don't use a cached copy.
			conn.setUseCaches(false);
			
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			
			DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
			
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + serverFileName +"\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
			
			// read file and write it into form...
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			
			while (bytesRead > 0)
			{
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
		}
		catch (MalformedURLException ex){
			Log.e(DEBUG_TAG, "error: " + ex.getMessage(), ex);
			return null;
		}
		catch (IOException ioe){
			Log.e(DEBUG_TAG, "error: " + ioe.getMessage(), ioe);
			return null;
		}
	}
	
    //get image result from server and display it in result view
	void getResultImage(HttpURLConnection conn){		
		// retrieve the response from server
		InputStream is;
		try {
			is = conn.getInputStream();
			//get result image from server
	        mResultView.resultImage = BitmapFactory.decodeStream(is);
	        is.close();		        
	        mResultView.IsShowingResult = true;	        
		} catch (IOException e) {
			Log.e(DEBUG_TAG,e.toString());
			e.printStackTrace();
		}
	}
	//Main code for processing image algorithm on the server
	void processImage(String inputImageFilePath){			
		publishProgress(UPLOADING_PHOTO_STATE);
		File inputFile = new File(inputImageFilePath);
		try {
			
			//create file stream for captured image file
			FileInputStream fileInputStream  = new FileInputStream(inputFile);
	    	
			//upload photo
	    	final HttpURLConnection  conn = uploadPhoto(fileInputStream);
	    	
	    	//get processed photo from server
	    	if (conn != null){
	    	getResultImage(conn);}
			fileInputStream.close();
		}
        catch (FileNotFoundException ex){
        	Log.e(DEBUG_TAG, ex.toString());
        }
        catch (IOException ex){
        	Log.e(DEBUG_TAG, ex.toString());
        }
	}

    protected void onPreExecute() {
        this.dialog.setMessage("Photo captured");
        this.dialog.show();
    }
	@Override
	protected Void doInBackground(String... params) {			//background operation 
		String uploadFilePath = params[0];
		processImage(uploadFilePath);
		return null;
	}		
	//progress update, display dialogs
	@Override
     protected void onProgressUpdate(Integer... progress) {
    	 if(progress[0] == UPLOADING_PHOTO_STATE){
    		 dialog.setMessage("Uploading");
    		 dialog.show();
    	 }
    	 else if (progress[0] == SERVER_PROC_STATE){
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
