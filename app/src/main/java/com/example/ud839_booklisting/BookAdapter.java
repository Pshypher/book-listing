package com.example.ud839_booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        Book book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder holder; // view lookup cache stored in tag
        if (itemView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
            holder = new ViewHolder(itemView);
            // Cache the viewHolder object inside the fresh view
            itemView.setTag(holder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            holder = (ViewHolder) itemView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        holder.bind(book);
        // Return the completed view to render on screen
        return itemView;
    }


    private static class ViewHolder {
        private TextView mBookTitle;
        private TextView mAuthors;
        private RatingBar mRating;
        private ImageView mThumbnail;

        public ViewHolder(View itemView) {
            mBookTitle = (TextView) itemView.findViewById(R.id.book_title);
            mAuthors = (TextView) itemView.findViewById(R.id.book_authors);
            mRating = (RatingBar) itemView.findViewById(R.id.book_rating);
            mThumbnail = (ImageView) itemView.findViewById(R.id.cover_image);
        }

        public void bind(Book book) {
            mBookTitle.setText(book.getTitle());
            mAuthors.setText(format(book.getAuthors()));
            mRating.setRating(book.getRating());
            if (book.getCoverImage() != null) {
                Bitmap bitmap = book.getCoverImage();
                mThumbnail.setMinimumWidth(bitmap.getWidth());
                mThumbnail.setMinimumHeight(bitmap.getHeight());
                mThumbnail.setImageBitmap(bitmap);
            }

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
}
