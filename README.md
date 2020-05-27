Follow the steps to upload the file to ther server:

Step 1 - Go to the build.gradle file and upload this dependency into the dependencies section

    //adding volley library
    implementation 'com.android.volley:volley:1.1.0'
    
Step 2 - Go to the Manifest file and add this permissions
    
    <!-- adding permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    
To add the api url go to the EndPoints class
    
    private static final String ROOT_URL = "";      //add the main URL over here
    
    //the below code is an optional part. If api name remains the same but the end point changes then add your end point name over here.
    Or else just remove it from here if it is of no use and then you will get the error at a file, then just remove that part of code from it.
    static final String UPLOAD_URL = ROOT_URL + "";
    public static final String GET_PICS_URL = ROOT_URL + "";
