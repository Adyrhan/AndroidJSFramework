JSCAMERAINTERFACE INSTRUCTIONS

INCLUDING THE LIBRARY
---------------------
In order to use this library from eclipse, create an Android project from existing source code and select this folder as the source code path. After creation, make sure this project is marked as "is Library" in Project>Properties>Android.

In your application project, go to Project>Properties>Android and add this library.

HOW TO USE IT
-------------

JAVA SIDE:

Having your Activity and your WebView inside it created, make sure your WebView has Javascript enabled with:
    webviewobject.getSettings().setJavascriptEnabled(true);

Then create an instance of JSCameraInterfaceManager, passing to its constructor the WebView object and an instance of a class implementing JSCameraInterface. 

JSCameraInterface has the methods you need to implement to respond to Javascript events. Make sure to implement takePicture() method, which must start the following activity like this:
    startActivityForResult(new Intent(YourActivity.this, com.adyrsoft.android.jsinterface.CameraManagerActivity.class));

Once JSCameraIntefaceManager has been instantiated, you can start loading webpages in your WebView, whose javascript code can access now to this interface through 'cameraInterface' object. The method names are the ones in which description in the Javadoc for JSCameraInterfaceManager contains a note telling "Meant to be used from Javascript code" you don't need to call addJavascriptInterface in your WebView, this is done by JSCameraInterfaceManager internally

The CameraManagerActivity will return result codes depending if the user took a picture or not, and if there was an error. For error codes please check the Javadoc inside doc/ folder.

Make sure to check this result since you need to respond to RESULT_OK, to tell your JSCameraInterfaceManager instance that the picture has been taken by calling its method pictureTaken(). 

JAVASCRIPT SIDE:

Using the cameraInterface object you may access these three methods:

setPictureTakenCallback(callbackstring) Required before calling any other function from this library
takePicture()
pictureNotNeeded()
getPictureUri()

Once you have provided a string with the name of a callback function as argument to setPictureTakenCallback() method, you may use in that callback getPictureUri() method, which returns a string with the uri where the picture taken can be loaded from, and add a <img> tag to the html pointing to that uri.

Call takePicture() when you need to after setPictureTakenCallback() has been called. 

When you're done with the picture, please call pictureNotNeeded(). It will trigger the function provided from Java side that determines what to do with that picture. A good idea is to delete it for privacy purposes.

