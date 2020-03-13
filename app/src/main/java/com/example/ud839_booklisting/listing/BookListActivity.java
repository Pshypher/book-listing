package com.example.ud839_booklisting.listing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ud839_booklisting.R;
import com.example.ud839_booklisting.details.BookDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.ud839_booklisting.NetworkUtil.isConnected;

public class BookListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter mAdapter;
    private ProgressBar mProgressIndicator;
    private TextView mEmptyView;
    private String searchQuery;

    private ListView.OnItemClickListener mOnItemClickListener =
            new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Book book = (Book) mAdapter.getItem(position);
                    Intent bookDetailIntent = new Intent(BookListActivity.this,
                            BookDetailActivity.class);
                    bookDetailIntent.putExtra(BOOK_ID, book.getBookID());
                    startActivity(bookDetailIntent);
                }
            };

    private static final int GOOGLE_BOOKS_ID = 0;
    private static final String BOOK_LIST_STATE = "BOOK_LIST_STATE";
    public static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String EXTRA_URL_QUERY = "QUERY_EXTRA";
    public static final String BOOK_ID = "ID";


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
        listView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState == null)
            return;

        searchQuery = savedInstanceState.getString(EXTRA_URL_QUERY);
        Parcelable state = savedInstanceState.getParcelable(BOOK_LIST_STATE);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(EXTRA_URL_QUERY, searchQuery);
        if (state != null) {
            ListView listView = (ListView) findViewById(R.id.list_view);
            getSupportLoaderManager().initLoader(GOOGLE_BOOKS_ID, queryBundle, this);
            listView.onRestoreInstanceState(state);
        } else {
            if (!TextUtils.isEmpty(searchQuery)) {
                Loader<List<Book>> loader = getSupportLoaderManager().getLoader(GOOGLE_BOOKS_ID);
                if (isConnected(this))
                    getSupportLoaderManager().restartLoader(GOOGLE_BOOKS_ID, queryBundle,
                            this);
                else
                    postNoConnection();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(EXTRA_URL_QUERY, searchQuery);

        if (!mAdapter.isEmpty()) {
            ListView listView = (ListView) findViewById(R.id.list_view);
            outState.putParcelable(BOOK_LIST_STATE, listView.onSaveInstanceState());
        }
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

                if (!isConnected(BookListActivity.this))
                    postNoConnection();
                else {
                    if (loader == null)
                        loaderManager.initLoader(GOOGLE_BOOKS_ID, queryBundle,
                                BookListActivity.this);
                    else
                        loaderManager.restartLoader(GOOGLE_BOOKS_ID, queryBundle,
                                BookListActivity.this);
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

        if (books == null)
            mEmptyView.setText(R.string.no_result);
        else {
            mAdapter.clear();
            mAdapter.addAll(books);
            mAdapter.notifyDataSetChanged();
        }

        searchQuery = null;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void postNoConnection() {
        mProgressIndicator.setVisibility(View.GONE);
        mEmptyView.setText(R.string.disconnected);
    }
}
