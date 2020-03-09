package com.example.ud839_booklisting;

import android.graphics.Bitmap;

import java.util.List;

public class Book {

    private List<String> mAuthors;
    private String mTitle;
    private int mRating;
    private Bitmap mThumbnail;

    public Book(List<String> authors, String title, int rating, Bitmap bitmap) {
        mAuthors = authors;
        mTitle = title;
        mRating = rating;
        mThumbnail = bitmap;
    }

    public List<String> getAuthors() {
        return mAuthors;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getRating() {
        return mRating;
    }

    public Bitmap getCoverImage() {
        return mThumbnail;
    }

}
