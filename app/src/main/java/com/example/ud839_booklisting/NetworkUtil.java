package com.example.ud839_booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class NetworkUtil {

    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();

    private NetworkUtil() {
        // Cannot construct objects of an utility class.
    }


    public static List<Book> fetchData(String query) throws IOException {

        URL url = createURL(query);

        String response = null;
        if (url != null) {
            response = makeHttpRequest(url);
        }

        if (!TextUtils.isEmpty(response)) {
            return extractBooks(response);
        }

        return null;
    }

    private static URL createURL(String urlString) {
        URL url = null;
        try {
            if (!urlString.startsWith("http://")) {
                urlString = BookListActivity.GOOGLE_BOOKS_BASE_URL + "?q=" +
                        URLEncoder.encode(urlString, "UTF-8");
            } else {
                urlString = urlString.replace("http://", "https://");
            }
            url = new URL(urlString);
        } catch (IOException e) {
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

    private static List<Book> extractBooks(String JSONResponse) throws IOException {

        ArrayList<Book> books = new ArrayList<Book>();
        try {
            JSONObject rootObject = new JSONObject(JSONResponse);
            JSONArray bookItems = rootObject.getJSONArray("items");

            for (int i = 0; i < bookItems.length(); i++) {
                JSONObject item = bookItems.getJSONObject(i);
                String id = item.getString("id");
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                JSONArray bookAuthors = volumeInfo.optJSONArray("authors");
                ArrayList<String> authors = new ArrayList<String>();
                if (bookAuthors != null) {
                    for (int j = 0; j < bookAuthors.length(); j++) {
                        authors.add(bookAuthors.getString(j));
                    }
                }

                int rating = volumeInfo.optInt("averageRating");

                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
                Bitmap thumbnail = null;
                if (imageLinks != null) {
                    thumbnail = downloadImage(imageLinks.optString("smallThumbnail"));
                }
                books.add(new Book(id, authors, title, rating, thumbnail));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON data: " + e.getMessage());
        }

        return books;
    }
}
