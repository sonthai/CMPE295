package com.quangpham.drs.utils;

/**
 * Created by quangpham on 3/31/18.
 */

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpEntity;

public class HttpUtils {

    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void post(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(30*1000);
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return AppConstant.URL_BASE_REST_API + relativeUrl;
    }
}
