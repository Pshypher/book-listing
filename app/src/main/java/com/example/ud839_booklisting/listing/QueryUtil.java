package com.example.ud839_booklisting.listing;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.example.ud839_booklisting.NetworkUtil.*;


public class QueryUtil {

    private static final String LOG_TAG = QueryUtil.class.getSimpleName();

    private QueryUtil() {
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
            urlString = BookListActivity.GOOGLE_BOOKS_BASE_URL + "?q=" +
                    URLEncoder.encode(urlString, "UTF-8");
            url = new URL(urlString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem parsing url string.", e);
        }

        return url;
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
