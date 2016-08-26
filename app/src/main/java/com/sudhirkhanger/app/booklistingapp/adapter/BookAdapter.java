package com.sudhirkhanger.app.booklistingapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sudhirkhanger.app.booklistingapp.R;
import com.sudhirkhanger.app.booklistingapp.model.Book;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> bookArrayList) {
        super(context, 0, bookArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Book book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.author);

        titleTextView.setText(book.getTitle());
        authorTextView.setText(TextUtils.join(", ", book.getAuthors()));

        return convertView;
    }
}
