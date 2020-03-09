package com.example.ud839_booklisting;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;

class BookLoader extends AsyncTaskLoader<List<Book>> {

    private Bundle args;
    private List<Book> resultFromHTTP;

    public BookLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (resultFromHTTP != null) {
            deliverResult(resultFromHTTP);
        } else {
            forceLoad();
        }
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {

        String query = args.getString(BookListActivity.EXTRA_URL_QUERY);
        List<Book> books = null;
        try {
            books = NetworkUtil.fetchData(query);
        } catch (IOException e) {
            // TODO: Catch IO exception
        }
        return books;
    }

    @Override
    public void deliverResult(@Nullable List<Book> data) {
        resultFromHTTP = data;
        super.deliverResult(data);
    }
}
