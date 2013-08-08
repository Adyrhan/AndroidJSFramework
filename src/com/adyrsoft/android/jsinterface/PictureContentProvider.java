package com.adyrsoft.android.jsinterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class PictureContentProvider extends ContentProvider {
	public static final String TEMPORAL_FILE_NAME = "tempfile.jpg";
	public static final String TEMPORAL_FILE_URI = "content://com.adyrsoft/picture";
	public static final String TEMPORAL_FILE_PATH = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMPORAL_FILE_NAME).getAbsolutePath();
	private static final String TAG = "PictureContentProvider";
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		//URI internalUri = URI.create(Environment.getExternalStorageDirectory().getPath()+TEMPORAL_FILE_NAME);
		File file = new File(TEMPORAL_FILE_PATH);
		ParcelFileDescriptor parcel;
		try{
			parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		}
		catch(FileNotFoundException e){
			Log.e(TAG, "Can't find the file "+TEMPORAL_FILE_PATH+" : "+e.getMessage());
			return null;
		}
		return parcel;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
