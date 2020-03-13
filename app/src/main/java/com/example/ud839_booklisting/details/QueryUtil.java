package com.example.ud839_booklisting.details;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.example.ud839_booklisting.listing.BookListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ud839_booklisting.NetworkUtil.*;


class QueryUtil {
    private static final String LOG_TAG = com.example.ud839_booklisting.listing.QueryUtil.class.getSimpleName();

    private QueryUtil() {
        // Cannot construct objects of an utility class.
    }


    public static Book fetchData(String param) throws IOException {

        URL url = createURL(param);

        String response = null;
        if (url != null) {
            response = makeHttpRequest(url);
        }

        if (!TextUtils.isEmpty(response)) {
            return getBookDetail(response);
        }

        return null;
    }

    private static URL createURL(String param) {
        URL url = null;
        try {
            param = BookListActivity.GOOGLE_BOOKS_BASE_URL + "/" + param;
            url = new URL(param);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem parsing url string", e);
        }

        return url;
    }


    private static Book getBookDetail(String JSONResponse) throws IOException {

        Book book = null;
        try {
            JSONObject rootObject = new JSONObject(JSONResponse);
            JSONObject volumeInfo = rootObject.getJSONObject("volumeInfo");

            JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
            Bitmap thumbnail = null;
            if (imageLinks != null) {
                thumbnail = downloadImage(imageLinks.optString("thumbnail"));
            }

            String title = volumeInfo.getString("title");
            JSONArray bookAuthors = volumeInfo.optJSONArray("authors");
            ArrayList<String> authors = new ArrayList<String>();
            if (bookAuthors != null) {
                for (int j = 0; j < bookAuthors.length(); j++) {
                    authors.add(bookAuthors.getString(j));
                }
            }

            String publisher = volumeInfo.optString("publisher", "None");
            int pageCount = volumeInfo.optInt("pageCount");
            book = new Book(thumbnail, title, authors, publisher, pageCount);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON data: " + e.getMessage());
        }

        return book;
    }
}
