package com.example.ud839_booklisting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter mAdapter;
    private ProgressBar mProgressIndicator;
    private TextView mEmptyView;
    private String searchQuery;

    public static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final int GOOGLE_BOOKS_ID = 0;
    public static final String EXTRA_URL_QUERY = "QUERY_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist);
        mProgressIndicator = (ProgressBar) findViewById(R.id.loading_spinner);
        ListView listView = (ListView) findViewById(R.id.list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyView);
        mAdapter = new BookAdapter(this, 0, new ArrayList<Book>());
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        searchQuery = savedInstanceState.getString(EXTRA_URL_QUERY);
        if (TextUtils.isEmpty(searchQuery)) {
            return;
        }

        Bundle queryBundle = new Bundle();
        queryBundle.putString(EXTRA_URL_QUERY, searchQuery);
        Loader<List<Book>> loader = getSupportLoaderManager().getLoader(GOOGLE_BOOKS_ID);
        getSupportLoaderManager().initLoader(GOOGLE_BOOKS_ID, queryBundle, this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(EXTRA_URL_QUERY, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final LoaderManager loaderManager = getSupportLoaderManager();
        final MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchQuery = query;
                Bundle queryBundle = new Bundle();
                queryBundle.putString(EXTRA_URL_QUERY, query);
                Loader<List<Book>> loader = loaderManager.getLoader(GOOGLE_BOOKS_ID);
                if (loader == null) {
                    loaderManager.initLoader(GOOGLE_BOOKS_ID, queryBundle, BookListActivity.this);
                } else {
                    loaderManager.restartLoader(GOOGLE_BOOKS_ID, queryBundle, BookListActivity.this);
                }

                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchViewItem.collapseActionView();
                // workaround to avoid issues with some emulators and keyboard devices firing twice
                // if a keyboard enter is used see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        mAdapter.clear();
        mEmptyView.setText(null);
        mProgressIndicator.setVisibility(View.VISIBLE);
        return new BookLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> books) {
        mProgressIndicator.setVisibility(View.GONE);

        if (books == null) {
            mEmptyView.setText(R.string.no_result);
        } else {
            mAdapter.clear();
            mAdapter.addAll(books);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
