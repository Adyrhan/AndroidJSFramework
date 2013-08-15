package com.adyrsoft.android.js.statesaver;

import java.util.HashMap;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
 * Allows to restore the js execution state between page loads by saving and loading variable values.
 * This class only allows string values, so you may have to convert them before saving them and after loading them.
 * To access the methods of this class from Javascript you may use the object 'stateSaverInterface'
 * To make it work with the desired WebView, you only have to pass the instance of the WebView to the constructor of this class.
 * No addJavascriptInterface calls needed.
 * @author Adrián Pérez <aperezhrd@gmail.com>
 *
 */
public class JSStateSaver {
	private static final String JS_OBJECT_NAME = "stateSaverInterface";
	private HashMap<String, String> mVars = new HashMap<String, String>();
	private WebView mWeb;
	public JSStateSaver(WebView wv) {
		this.mWeb = wv;
		this.mWeb.addJavascriptInterface(this,  JS_OBJECT_NAME);
	}
	
	/**
	 * <b>Meant to be used from Javascript code</b>
	 * Stores the variable/value pair for later use.
	 * 
	 * @param variable Name of the variable to store, or key to value.
	 * @param value Value associated with the variable or key.
	 */
	@JavascriptInterface
	public void putVarState(String variable, String value) {
		this.mVars.put(variable, value);
	}
	
	/**
	 * <b>Meant to be used from Javascript code</b>
	 * Retrieves the value associated with a variable name or key.
	 * 
	 * @param variable Variable or key associated with the value to retrieve.
	 * @return Value associated with that variable name or key, or 'undefined' if the key doesn't exists.
	 */
	@JavascriptInterface
	public String getVarState(String variable) {
		if(this.mVars.containsKey(variable)){
			return this.mVars.get(variable);
		}
		else {
			return "undefined";
		}
	}
}
