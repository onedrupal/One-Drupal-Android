package com.prominentdev.blog.helpers;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Narender Kumar on 4/11/2017.
 * For Prominent Developers, Faridabad (India)
 */

public class PDRestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        if (null == context)
            return;

        if (null == params)
            params = new RequestParams();

        PDUtils.log(url + "?" + params.toString());

        client.setEnableRedirects(false);
        client.setMaxRetriesAndTimeout(5, 10000);
        client.setConnectTimeout(200 * 1000);
        client.setTimeout(600 * 1000);
        client.post(context, url, params, responseHandler);
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        if (null == context)
            return;

        if (null == params)
            params = new RequestParams();

        PDUtils.log(url + "?" + params.toString());

        client.setEnableRedirects(false);
        client.setEnableRedirects(true);
        client.setMaxRetriesAndTimeout(4, 10000);
        client.setConnectTimeout(20 * 1000);
        client.setTimeout(30 * 1000);
        client.get(context, url, params, responseHandler);
    }

    public static void cancel(Context context) {
        client.cancelRequests(context, true);
    }

    public static String getHTTPErrorMessage(int httpStatusCode) {
        String errorMessage = "";
        if (httpStatusCode == -1) {
            errorMessage = "Unable to process request, please retry later!";
        } else if (httpStatusCode < 200) {
            errorMessage = "No connectivity, please retry later.";
        } else if (httpStatusCode == 200) {
            errorMessage = "Oops unknown server halt...";
        } else if (httpStatusCode >= 300 && httpStatusCode < 400) {
            errorMessage = "We're experiencing technical issues...";
        } else if (httpStatusCode >= 400 && httpStatusCode < 500) {
            switch (httpStatusCode) {
                case 400:
                    errorMessage = "Bad request.";
                    break;
                case 401:
                    errorMessage = "Authentication failure.";
                    break;
                case 403:
                    errorMessage = "The resource is forbidden.";
                    break;
                case 404:
                    errorMessage = "The resource was not found.";
                    break;
                case 405:
                    errorMessage = "Method not allowed.";
                    break;
                case 409:
                    errorMessage = "Conflict at server code.";
                    break;
                case 412:
                    errorMessage = "Precondition Failed.";
                    break;
                case 413:
                    errorMessage = "Request Entity Too Large.";
                    break;
            }
        } else if (httpStatusCode >= 500) {
            switch (httpStatusCode) {
                case 500:
                    errorMessage = "Internal Server Error.";
                    break;
                case 501:
                    errorMessage = "Not Implemented.";
                    break;
                case 503:
                    errorMessage = "Service Temporarily Unavailable.";
                    break;
            }
        }
        return errorMessage;
    }

    public static String getHTTPErrorMessage() {
        return "We'll make Our app even better now, please visit back a bit later";
    }
}