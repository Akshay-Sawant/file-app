package com.uploadimage.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static com.uploadimage.app.Constants.IMAGE_QUALITY;

class Utility {

    /*
     * This function fetches bitmap and returns the byte[] array.
     * The image gets compressed over here depending on its required quality.
     * */
    byte[] onGetData(Bitmap bitmap) {
        ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, mByteArrayOutputStream);
        return mByteArrayOutputStream.toByteArray();
    }

    /*
    * Custom toast method
    * */
    void onLoadToast(Context mContext, String mMessage) {
        Toast.makeText(
                mContext,
                mMessage,
                Toast.LENGTH_SHORT
        ).show();
    }
}
