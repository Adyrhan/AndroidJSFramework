package com.adyrsoft.android.js.facebook;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.adyrsoft.android.js.camera.PictureContentProvider;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * This class allows javascript code to post to Facebook using the object 'facebookPosterInterface'.
 * Be sure to call setOnCompleteCallback() from Javascript code, before calling the method post().
 * To make it work with the desired WebView, you only have to pass the instance of the WebView to the constructor of this class.
 * No addJavascriptInterface calls needed.
 * @author Adrián Pérez <aperezhrd@gmail.com>
 *
 */
public class JSFacebookPoster {
	private static final String TAG = "JSFacebookPoster";
	private static final String FACEBOOK_GRAPH_URL = "https://graph.facebook.com/";
	private static final String MULTIPART_BOUNDARY = "----myfuckingawesomeboundaryasdf";
	private static final String JS_OBJECT_NAME = "facebookPosterInterface";
	
	private WebView mWeb;
	private String mRedirectUrl;
	private String mResponseString;
	private Handler mUiHandler;
	
	private String mOnCompletedCallback;
	
	public JSFacebookPoster(WebView wv, String redirectUrl) {
		this.mWeb = wv;
		this.mRedirectUrl = redirectUrl;
		this.mWeb.addJavascriptInterface(this, JS_OBJECT_NAME);
		this.mResponseString = "";
		this.mOnCompletedCallback = "";
		mUiHandler = new Handler();
	}
	
	private boolean postPicture(final String accessToken, final String userId, final String message, final FileInputStream fis) {
		// Create Http client
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("TapMango");
		
		// Build url for request 
		String photosUrl = FACEBOOK_GRAPH_URL+userId+"/photos?access_token="+accessToken;
		HttpPost request = new HttpPost(photosUrl);
		
		// Add html form headers (multipart/form-data)
		request.addHeader("Content-Type", "multipart/form-data; boundary="+MULTIPART_BOUNDARY);
		
		// Build content to send
		// Data header
		StringBuilder dataHeader = new StringBuilder();
		dataHeader.append("Content-Disposition: form-data; name=\"source\"; filename=");
		dataHeader.append("\"");
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.DATE));
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.MONTH)+1);
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR));
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.HOUR));
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.MINUTE));
		dataHeader.append(GregorianCalendar.getInstance().get(GregorianCalendar.SECOND));
		dataHeader.append(".jpg\"\r\n");
		dataHeader.append("Content-Type: image/jpeg\r\n\r\n");
		
		// Buid request body
		ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
		try {
			dataBytes.write("--".getBytes());
			dataBytes.write(MULTIPART_BOUNDARY.getBytes());
			dataBytes.write("\r\n".getBytes());
			dataBytes.write(dataHeader.toString().getBytes());
			
			int readByte = fis.read();
			while(readByte != -1) {
				dataBytes.write(readByte);
				readByte = fis.read();
			}
			
			dataBytes.write("\r\n".getBytes());
			dataBytes.write("--".getBytes());
			dataBytes.write(MULTIPART_BOUNDARY.getBytes());
			//dataBytes.write("--".getBytes());
			dataBytes.write("\r\n".getBytes());
			dataBytes.write("Content-Disposition: form-data; name=\"message\"\r\n\r\n".getBytes());
			dataBytes.write(message.getBytes());
			dataBytes.write("\r\n".getBytes());
			dataBytes.write("--".getBytes());
			dataBytes.write(MULTIPART_BOUNDARY.getBytes());
			dataBytes.write("--".getBytes());
			dataBytes.write("\r\n".getBytes());
			
		} catch (IOException e1) {
			Log.e(TAG, "Error while building request body: " + e1.getMessage());
			httpClient.close();
			return false;
		}
		
		ByteArrayEntity content = new ByteArrayEntity(dataBytes.toByteArray());
		request.setEntity(content);
		
		// Send and get response
		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			Log.e(TAG, "Unable to send picture to Facebook. Check internet connection.");
			httpClient.close();
			return false; 
		}
		
		httpClient.close();
		// Stringify the response body
		
		try {
			mResponseString = inputStreamToString(response.getEntity().getContent());
		} catch (IllegalStateException e) {
			Log.e(TAG, "Unexpected error: "+e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Unexpected error: "+e.getMessage());
			return false;
		}
		
		// If we fail sending the picture, we stop the procedure
		if(!mResponseString.contains("id")){
			httpClient.close();
			Log.e(TAG, "Unable to send picture to Facebook. Facebook servers may be overloaded (or there has been a change in the expected POST request).");
			return false;
		}
		else {
			return true;
		}
	}
	
	private boolean postMessage(final String accessToken, final String userId, final String message) {
		// Create Http client
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("TapMango");
		
		String feedUrl = FACEBOOK_GRAPH_URL+userId+"/feed?access_token="+accessToken;
		HttpPost request = new HttpPost(feedUrl);
		
		// Add html form headers (multipart/form-data)
		request.addHeader("Content-Type", "multipart/form-data; boundary="+MULTIPART_BOUNDARY);
		
		// Build content to send
		// Data header
		StringBuilder dataHeader = new StringBuilder();
		dataHeader.append("Content-Disposition: form-data; name=\"message\"\r\n\r\n");
		dataHeader.append(message+"\r\n");
		
		ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
		try {
			dataBytes.write("--".getBytes());
			dataBytes.write(MULTIPART_BOUNDARY.getBytes());
			dataBytes.write("\r\n".getBytes());
			dataBytes.write(dataHeader.toString().getBytes());
			dataBytes.write("--".getBytes());
			dataBytes.write(MULTIPART_BOUNDARY.getBytes());
			dataBytes.write("--".getBytes());
			dataBytes.write("\r\n".getBytes());
		} catch (IOException e) {
			Log.e(TAG, "Error while building request body: " + e.getMessage());
			return false;
		}
		
		ByteArrayEntity content = new ByteArrayEntity(dataBytes.toByteArray());
		request.setEntity(content);
		
		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			Log.e(TAG, "Unable to send picture to Facebook. Check internet connection.");
			return false; 
		}
		
		httpClient.close();
		
		// Stringify the response body
		
		try {
			mResponseString = inputStreamToString(response.getEntity().getContent());
		} catch (IllegalStateException e) {
			Log.e(TAG, "Unexpected error: "+e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Unexpected error: "+e.getMessage());
			return false;
		}
		
		if(!mResponseString.contains("id")) {
			Log.e(TAG, "Unable to send picture to Facebook. Facebook servers may be overloaded (or there has been a change in the expected POST request).");
			Log.e(TAG, "JSON response: "+mResponseString);
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * <b>Meant to be used from Javascript code</b>
	 * Post a message to facebook to the timeline of the user 'userId' using the accessToken given.
	 * If there is a picture file in @PictureContentProvider.TEMPORAL_FILE_PATH it's also sent.
	 * @param accessToken access token obtained when the user logged in.
	 * @param userId User id number, obtained from a previous GET request to graph.facebook.com/me, once the user is logged in. 
	 * @param message Message to send.
	 */
	@JavascriptInterface
	public void post(final String accessToken, final String userId, final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Open file to send
				boolean sendWithPicture = true;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(PictureContentProvider.TEMPORAL_FILE_PATH);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Log.w(TAG, "File "+PictureContentProvider.TEMPORAL_FILE_PATH+" not found. Sending post without picture.");
					sendWithPicture = false;
				}
				
				boolean result = false;
				if(sendWithPicture){
					result = postPicture(accessToken, userId, message, fis);
					try {
						fis.close();
					} catch (IOException e) {
						Log.e(TAG, "Cannot close the picture file. Has it been deleted? "+e.getMessage());
					}
				}
				else {
					result = postMessage(accessToken, userId, message);
				}
				
				
				
				if(!result) {
					mUiHandler.post(new Runnable() {
						@Override
						public void run() {
							mWeb.loadUrl(mRedirectUrl);
							mWeb.loadUrl("javascript:"+mOnCompletedCallback+"('error')");
						}
					});
				}
				else {
					mUiHandler.post(new Runnable() {
						@Override
						public void run() {
							mWeb.loadUrl(mRedirectUrl);
							mWeb.loadUrl("javascript:"+mOnCompletedCallback+"('message_sent')");
						}
					});
				}
			}
			
		}).start();
		
	}
	
	/**
	 * <b>Meant to be used from Javascript code</b>
	 * Sets the name of the function callback in the javascript side that will be called when the post has been either sent or an error has ocurred.
	 * The callback function has to take a parameter which will have the result status. An example of a function callback could be:
	 * {@code
	 *  callback = function(response) {
	 *  	if(response == 'message_sent') {
	 *  		//You may notify the user here
	 *  	}
	 *  	else {
	 *  		// The other status is 'error'
	 *  	}
	 *  }
	 * }
	 * @param callback
	 */
	@JavascriptInterface
	public void setOnCompleteCallback(String callback) {
		this.mOnCompletedCallback = callback;
	}
	
	private String inputStreamToString(InputStream is) throws IOException {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    
	    // Return full string
	    return total.toString();
	}
}
