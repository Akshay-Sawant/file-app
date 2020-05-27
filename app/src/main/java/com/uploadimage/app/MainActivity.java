package com.uploadimage.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.uploadimage.app.Constants.REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageViewMainDisplayImage;
    private EditText mEditTextMainImageName;
    private Button mButtonMainUpload;

    private Intent mIntent;

    private ApiCalls mApiCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageViewMainDisplayImage = findViewById(R.id.imageViewMainDisplayImage);
        mEditTextMainImageName = findViewById(R.id.editTextMainImageName);
        mButtonMainUpload = findViewById(R.id.buttonMainUpload);
        mButtonMainUpload.setOnClickListener(this);

        onGetStoragePermission();
        mApiCalls = new ApiCalls();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMainUpload:
                onClickPickImage();
        }
    }

    /**
     * This function is used to get the permission at the very start of the app when the user opens
     * or load the app for the first time on his device.
     * If the required permission is not given/allowed the app will get redirected towards the settings
     * panel of the device for enabling the permission and if the permission was not provided then to the app will not launch.
     */
    private void onGetStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            finish();
            startActivity(
                    new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName())
                    )
            );
        }
    }

    private void onClickPickImage() {
        if (mEditTextMainImageName.getText().toString().trim().isEmpty()) {
            mEditTextMainImageName.setError(getString(R.string.text_error));
            mEditTextMainImageName.requestFocus();
            return;
        }

        startActivityForResult(
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ),
                100
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Uri mUri = data.getData();
            try {
                Bitmap mBitmap = MediaStore.Images.
                        Media.getBitmap(
                        this.getContentResolver(),
                        mUri
                );
                mImageViewMainDisplayImage.setImageBitmap(mBitmap);
                mApiCalls.onUploadBitmap(
                        getApplicationContext(),
                        mBitmap,
                        mEditTextMainImageName
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
