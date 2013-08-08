package com.adyrsoft.android.jsinterface;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class CameraManagerActivity extends Activity {
	public static final int RESULT_NO_CAMERA_APP_INSTALLED = 1;
	public static final int RESULT_UNEXPECTED_ERROR = 3;
	public static final int RESULT_FILESYSTEM_ERROR = 2;
	private static final int CAMERA_REQUEST_CODE = 0;
	
	private static final String TAG = "CameraManagerActivity";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		File output = new File(PictureContentProvider.TEMPORAL_FILE_PATH);
		if(!output.exists()){
			output.getParentFile().setWritable(true, false);
			output.getParentFile().setReadable(true, false);
			output.getParentFile().mkdirs();
			
			try {
				output.createNewFile();
				output.setWritable(true, false);
				output.setReadable(true, false);
			} catch (IOException e) {
				setResult(RESULT_FILESYSTEM_ERROR);
				finish();
			}
		}
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
		
		try {
			startActivityForResult(intent, CAMERA_REQUEST_CODE);
		}
		catch(ActivityNotFoundException e) {
			Log.e(TAG, "Camera app can't be found in device." + e.getMessage());
			setResult(RESULT_NO_CAMERA_APP_INSTALLED);
			finish();
		}
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode == CAMERA_REQUEST_CODE) {
			if(resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
				setResult(resultCode);
			}
		}
		else {
			Log.e(TAG, "Picture can't be taken or saved. Result Code: "+Integer.toString(resultCode));
			setResult(RESULT_UNEXPECTED_ERROR);
		}
		finish();
	}
}
