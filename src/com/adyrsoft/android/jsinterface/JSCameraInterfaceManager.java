package com.adyrsoft.android.jsinterface;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * JSCameraInterfaceManager
 * This class manages the events generated from calls from Javascript code. Also communicates with Javascript code
 * @author Adrián Pérez <aperezhrd@gmail.com>
 *
 */
public class JSCameraInterfaceManager {
	
	public interface JSCameraInterface {
		
		/**
		 * This method will be called when Javascript code from the WebView request a picture to be taken from device camera.
		 */
		public void takePicture();
		
		/**
		 * This method will be called when Javascript code from the WebView has ended using the picture taken.
		 */
		public void pictureNotNeeded();
		
	}
	private static final String TAG = "JSCameraInterfaceManager";
	
	private JSCameraInterface mJSInterface;
	private WebView mWebView;
	
	//String references to JS function callbacks
	private String mPictureTakenJSCallback;
	
	/**
	 * Adds the interface object jSInterface to the provided WebView wv and instantiates this object.
	 * @param wv WebView to add the interface to.
	 * @param jSInterface JSCameraInterface to add.
	 */
	public JSCameraInterfaceManager(WebView wv, JSCameraInterface jSInterface) {
		this.mJSInterface = jSInterface;
		this.mWebView = wv;
		this.mPictureTakenJSCallback = "";
		this.mWebView.addJavascriptInterface(this, "cameraInterface");
	}
	
	/**
	 * Returns the interface object in use by this JSCameraInterfaceManager object
	 * @return JSCameraInterface instance used by this object.
	 */
	public JSCameraInterface getJSCameraInterface() {
		return mJSInterface;
	}
	
	/**
	 * This must be called when the picture has been taken, so the JS code gets notified about this event.
	 */
	public void OnPictureTaken() {
		this.mWebView.loadUrl("javascript:"+mPictureTakenJSCallback+"()");
		Log.d(TAG, "OnPictureTaken() called!");
	}
	
	/**
	 * Sets the callback that this object will call to JS code.
	 * Meant to be used from Javascript code.
	 * @param callback Javascript callback function.
	 */
	@JavascriptInterface
	public void setPictureTakenCallback(String callback) {
		this.mPictureTakenJSCallback = callback;
	}
	
	/**
	 * Request the android application to take a picture from the camera.
	 * setPictureTakenCallback must be called before using this method.
	 * Meant to be used from Javascript code.
	 */
	@JavascriptInterface
	public void takePicture() {
		if(mPictureTakenJSCallback.isEmpty()) {
			this.mWebView.loadUrl("javascript:alert('Error: takePicture() called without setting required callback. Call setPictureTakenCallback() first!')");
			return;
		}
		mJSInterface.takePicture();
	}
	
	/**
	 * Notifies the Android application that the picture taken is no longer needed.
	 * Meant to be used from Javascript code.
	 */
	@JavascriptInterface
	public void pictureNotNeeded() {
		mJSInterface.pictureNotNeeded();
	}
	
	/**
	 * Returns the uri where the picture can be loaded from.
	 * @return uri Uri object where the picture can be loaded from.
	 */
	@JavascriptInterface
	public String getPictureUri() {
		return "content://com.adyrsoft/picture";
	}
}
