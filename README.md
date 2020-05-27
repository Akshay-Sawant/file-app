Follow the steps to upload the file to ther server:

Step 1 - Go to the build.gradle file and upload this dependency into the dependencies section

    //adding volley library
    implementation 'com.android.volley:volley:1.1.0'
    
Step 2 - Go to the Manifest file and add this permissions
    
    <!-- adding permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
