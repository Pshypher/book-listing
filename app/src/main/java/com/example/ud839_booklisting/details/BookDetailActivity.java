package com.example.ud839_booklisting.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ud839_booklisting.BookListActivity;
import com.example.ud839_booklisting.R;

import java.util.List;

public class BookDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Book> {

    private static final int BOOK_VOLUME_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(BookListActivity.BOOK_ID,
                getIntent().getStringExtra(BookListActivity.BOOK_ID));
        getSupportLoaderManager().initLoader(BOOK_VOLUME_ID, queryBundle, this);
    }

    @NonNull
    @Override
    public Loader<Book> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Book> loader, Book book) {
        ImageView thumbnail = (ImageView) findViewById(R.id.cover_image);
        TextView titleTextView = (TextView) findViewById(R.id.book_title);
        TextView authorsTextView = (TextView) findViewById(R.id.book_authors);
        TextView publisherTextView = (TextView) findViewById(R.id.book_publisher);
        TextView pagesTextView = (TextView) findViewById(R.id.pages);

        if (book.getCoverImage() != null) {
            thumbnail.setImageBitmap(book.getCoverImage());
        }
        titleTextView.setText(book.getTitle());
        authorsTextView.setText(format(book.getAuthors()));
        publisherTextView.setText(book.getPublisher());
        int pages = book.getPages();
        if (pages > 0) {
            pagesTextView.setText(String.format("%d pages", pages));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Book> loader) {
        return;
    }

    private String format(List<String> authors) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < authors.size(); j++) {
            if (j > 0) {
                builder.append(", ");
            }
            builder.append(authors.get(j));
        }
        return builder.toString();
    }
}
