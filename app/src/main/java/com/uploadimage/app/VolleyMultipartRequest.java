package com.uploadimage.app;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private Response.Listener<NetworkResponse> mResponseListener;
    private Response.ErrorListener mResponseErrorListener;
    private Map<String, String> mMap;

    ByteArrayOutputStream mByteArrayOutputStream;
    DataOutputStream mDataOutputStream;

    private final String mTwoHyphens = "--";
    private final String mLineEnd = "\r\n";
    private final String mBoundary = "apiclient-" + System.currentTimeMillis();


    VolleyMultipartRequest(int mMethodType, String mApiUrl,
                           Response.Listener<NetworkResponse> mResponseListener,
                           Response.ErrorListener mResponseErrorListener) {
        super(mMethodType, mApiUrl, mResponseErrorListener);
        this.mResponseListener = mResponseListener;
        this.mResponseErrorListener = mResponseErrorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mMap != null) ? mMap : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + mBoundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        mDataOutputStream = new DataOutputStream(mByteArrayOutputStream);

        try {
            Map<String, String> mMapParameters = getParams();
            if (mMapParameters != null && mMapParameters.size() > 0) {
                onParseStringMapToDataOutputStream(mDataOutputStream, mMapParameters, getParamsEncoding());
            }

            Map<String, DataPart> mMapData = getByteData();
            if (mMapData != null && mMapData.size() > 0) {
                onParseDataToDataOutputStream(mDataOutputStream, mMapData);
            }

            mDataOutputStream.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens + mLineEnd);

            return mByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mResponseListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mResponseErrorListener.onErrorResponse(error);
    }

    /**
     * Handle data payload
     */
    protected Map<String, DataPart> getByteData() {
        return null;
    }

    /**
     * Parse string map into data output stream by key and value.
     */
    private void onParseStringMapToDataOutputStream(
            DataOutputStream mDataOutputStream,
            Map<String, String> mMapParameters,
            String mEncodedString
    ) throws IOException {
        try {
            for (Map.Entry<String, String> mMapEntry : mMapParameters.entrySet()) {
                onWriteStringDataToHeaderDataOutputStream(
                        mDataOutputStream,
                        mMapEntry.getKey(),
                        mMapEntry.getValue()
                );
            }
        } catch (UnsupportedEncodingException mUnsupportedEncodingException) {
            throw new RuntimeException(
                    "Encoded string not supported: " + mEncodedString,
                    mUnsupportedEncodingException
            );
        }
    }

    /**
     * Parse data into data output stream.
     */
    private void onParseDataToDataOutputStream(
            DataOutputStream mDataOutputStream,
            Map<String, DataPart> mMapData
    ) throws IOException {
        for (Map.Entry<String, DataPart> mMapEntry : mMapData.entrySet()) {
            onWriteDataFileToHeaderDataOutputStream(
                    mDataOutputStream,
                    mMapEntry.getValue(),
                    mMapEntry.getKey()
            );
        }
    }

    /**
     * Writes string data into header and data output stream.
     */
    private void onWriteStringDataToHeaderDataOutputStream(
            DataOutputStream mDataOutputStream,
            String mParameterName,
            String mParameterValue
    ) throws IOException {
        mDataOutputStream.writeBytes(mTwoHyphens + mBoundary + mLineEnd);
        mDataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + mParameterName + "\"" + mLineEnd);
        mDataOutputStream.writeBytes(mLineEnd);
        mDataOutputStream.writeBytes(mParameterValue + mLineEnd);
    }

    /**
     * Write data file into header and data output stream.
     */
    private void onWriteDataFileToHeaderDataOutputStream(
            DataOutputStream mDataOutputStream,
            DataPart mDataPart,
            String mInputName
    ) throws IOException {
        mDataOutputStream.writeBytes(mTwoHyphens + mBoundary + mLineEnd);
        mDataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                mInputName + "\"; filename=\"" + mDataPart.getFileName() + "\"" + mLineEnd);

        if (mDataPart.getType() != null && !mDataPart.getType().trim().isEmpty()) {
            mDataOutputStream.writeBytes("Content-Type: " + mDataPart.getType() + mLineEnd);
        }

        mDataOutputStream.writeBytes(mLineEnd);

        ByteArrayInputStream mByteArrayInputStream = new ByteArrayInputStream(mDataPart.getContent());
        int mByteArrayInputStreamAvailable = mByteArrayInputStream.available();

        int mMaxBufferSize = 1024 * 1024;
        int mMinBufferSize = Math.min(mByteArrayInputStreamAvailable, mMaxBufferSize);
        byte[] mByteBuuffer = new byte[mMinBufferSize];

        int mByteRead = mByteArrayInputStream.read(mByteBuuffer, 0, mMinBufferSize);

        while (mByteRead > 0) {
            mDataOutputStream.write(mByteBuuffer, 0, mMinBufferSize);
            mByteArrayInputStreamAvailable = mByteArrayInputStream.available();
            mMinBufferSize = Math.min(mByteArrayInputStreamAvailable, mMaxBufferSize);
            mByteRead = mByteArrayInputStream.read(mByteBuuffer, 0, mMinBufferSize);
        }

        mDataOutputStream.writeBytes(mLineEnd);
    }

    static class DataPart {
        private String mFileName;
        private byte[] mByteContent;
        private String mType;

        DataPart(String mFileName, byte[] mByteContent) {
            this.mFileName = mFileName;
            this.mByteContent = mByteContent;
        }

        String getFileName() {
            return mFileName;
        }

        byte[] getContent() {
            return mByteContent;
        }

        String getType() {
            return mType;
        }
    }
}