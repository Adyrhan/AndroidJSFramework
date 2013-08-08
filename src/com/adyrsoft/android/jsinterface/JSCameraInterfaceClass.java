package com.adyrsoft.android.jsinterface;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JSCameraInterfaceClass {
	
	public interface JSCameraInterface {
		// Event request from JS code to take a picture
		public void takePicture();
		
		// Event from JS code that the picture taken is no longer needed
		public void messageSent();
		
	}
	private static final String TAG = "JSCameraInterfaceClass";
	private Context mContext;
	private JSCameraInterface mJSInterface;
	private WebView mWebView;
	
	//String references to JS function callbacks
	private String mPictureTakenJSCallback;
	
	public JSCameraInterfaceClass(Context context, WebView wv, JSCameraInterface jSInterface) {
		this.mContext = context;
		this.mJSInterface = jSInterface;
		this.mWebView = wv;
		this.mPictureTakenJSCallback = "";
		this.mWebView.addJavascriptInterface(this, "cameraInterface");
	}
	
	public JSCameraInterface getJSCameraInterface() {
		return mJSInterface;
	}
	
	// Event that happens when Camera Manager has done the picture
	public void OnPictureTaken() {
		this.mWebView.loadUrl("javascript:"+mPictureTakenJSCallback+"()");
		Log.d(TAG, "OnPictureTaken() called!");
	}
	
	@JavascriptInterface
	public void setPictureTakenCallback(String callback) {
		this.mPictureTakenJSCallback = callback;
	}
	
	@JavascriptInterface
	public void takePicture() {
		if(mPictureTakenJSCallback.isEmpty()) {
			this.mWebView.loadUrl("javascript:alert('Error: takePicture() called without setting required callback. Call setPictureTakenCallback() first!')");
			return;
		}
		mJSInterface.takePicture();
	}
	
	@JavascriptInterface
	public void messageSent() {
		mJSInterface.messageSent();
	}
	
	@JavascriptInterface
	public String getPictureUri() {
		return "content://com.adyrsoft/picture";
	}
}
