package com.example.ud839_booklisting.details;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Book {

    private Bitmap mThumbnail;
    private String mTitle;
    private ArrayList<String> mAuthors;
    private String mPublisher;
    private int mPages;

    public Book(Bitmap thumbnail, String title, ArrayList<String> authors, String publisher,
                int pageCount) {
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthors = authors;
        mPublisher = publisher;
        mPages = pageCount;
    }

    public Bitmap getCoverImage() {
        return mThumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public int getPages() {
        return mPages;
    }
}
