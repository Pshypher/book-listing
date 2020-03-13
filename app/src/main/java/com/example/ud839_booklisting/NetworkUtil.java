package com.example.ud839_booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class NetworkUtil {

    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();

    private NetworkUtil() {
        // Cannot construct objects of an utility class.
    }

    private static URL createURL(String param) {

        param = param.replace("http://", "https://");
        URL url = null;
        try {
            url = new URL(param);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem parsing url string.", e);
        }

        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        String response = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                response = readStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with http request", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    private static String readStream(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem reading response stream");
        }

        return builder.toString();
    }

    public static Bitmap downloadImage(String urlString) throws IOException {
        if (TextUtils.isEmpty(urlString)) {
            return null;
        }

        URL url = createURL(urlString);
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem downloading image.");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return bitmap;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
