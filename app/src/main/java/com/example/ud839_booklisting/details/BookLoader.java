package com.example.ud839_booklisting.details;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.ud839_booklisting.BookListActivity;

import java.io.IOException;

public class BookLoader extends AsyncTaskLoader<Book> {

    private Bundle queryBundle;
    private Book resultFromHttp;

    public BookLoader(Context context, Bundle args) {
        super(context);
        queryBundle = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (resultFromHttp != null) {
            deliverResult(resultFromHttp);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(@Nullable Book data) {
        super.deliverResult(data);
        resultFromHttp = data;
    }

    @Nullable
    @Override
    public Book loadInBackground() {
        Book book = null;
        try {
            book = QueryUtil.fetchData(queryBundle.getString(BookListActivity.BOOK_ID));
            resultFromHttp = book;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }
}
