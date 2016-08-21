package com.sudhirkhanger.app.booklistingapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sudhirkhanger.app.booklistingapp.R;
import com.sudhirkhanger.app.booklistingapp.adapter.BookAdapter;
import com.sudhirkhanger.app.booklistingapp.model.Book;
import com.sudhirkhanger.app.booklistingapp.rest.AsyncResponse;
import com.sudhirkhanger.app.booklistingapp.rest.GoogleBookAsyncTask;
import com.sudhirkhanger.app.booklistingapp.utils.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookListingActivityFragment extends Fragment implements AsyncResponse {

    public static final String GOOGLE_BOOKS_API_SEARCH_QUERY = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=5";

    private ArrayList<Book> mBookArrayList;
    private ArrayAdapter<Book> mBookArrayAdapter;
    private Utility mUtility;
    private final static String LOG_TAG = BookListingActivityFragment.class.getSimpleName();
    private GoogleBookAsyncTask mGoogleBookAsyncTask;

    public BookListingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_listing, container, false);
        mUtility = new Utility();

        mBookArrayList = new ArrayList<>();

        if (mUtility.isNetworkAvailable(rootView.getContext())) {
            mGoogleBookAsyncTask = new GoogleBookAsyncTask(this);
            mGoogleBookAsyncTask.execute(GOOGLE_BOOKS_API_SEARCH_QUERY);
        } else {
            Log.d(LOG_TAG, "network not found");
        }

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        mBookArrayAdapter = new BookAdapter(rootView.getContext(), mBookArrayList);

        if (mBookArrayList == null) {
            listView.setEmptyView(rootView.findViewById(R.id.emptyElement));
        } else {
            listView.setAdapter(mBookArrayAdapter);
        }

        return rootView;
    }

    @Override
    public void processFinish(ArrayList<Book> bookArrayList) {
        mBookArrayAdapter.clear();
        mBookArrayAdapter.addAll(bookArrayList);
        mBookArrayAdapter.notifyDataSetChanged();
    }
}

