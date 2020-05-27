package com.uploadimage.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class ApiCalls {

    private Utility mUtility = new Utility();

    void onUploadBitmap(final Context mContext, final Bitmap bitmap, final EditText mEditText) {
        VolleyMultipartRequest mVolleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            mUtility.onLoadToast(
                                    mContext,
                                    obj.getString("message")
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mUtility.onLoadToast(
                                mContext,
                                error.getMessage()
                        );
                    }
                }) {

            /*
             * This section holds the parameters for the api call.
             * If the api holds multiple strings values along with image then multiple parameters along with the image
             * can be integrated over here.
             * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(
                        "image_name",
                        mEditText.getText()
                                .toString()
                                .trim()
                );
                return params;
            }

            /*
            * This section handles the mapping of the data fetched from the device and sends it to the api by key-value pair.
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> mParameters = new HashMap<>();
                long mImage = System.currentTimeMillis();
                mParameters.put(
                        "picture",
                        new DataPart(
                                mImage + ".png",
                                mUtility.onGetData(bitmap)
                        )
                );
                return mParameters;
            }
        };

        Volley.newRequestQueue(mContext)
                .add(mVolleyMultipartRequest);
    }
}