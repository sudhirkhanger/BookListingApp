package com.sudhirkhanger.app.booklistingapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class BookListingActivityFragment extends Fragment {
//public class BookListingActivityFragment extends Fragment implements AsyncResponse {

    private final static String LOG_TAG = BookListingActivityFragment.class.getSimpleName();

    private ArrayList<Book> mBookArrayList;
    private ArrayAdapter<Book> mBookArrayAdapter;
    private GoogleBookAsyncTask mGoogleBookAsyncTask;

    public static final String GOOGLE_BOOKS_API_BASE_QUERY =
            "https://www.googleapis.com/books/v1/volumes?maxResults=20&q=";

    private Button mButton;
    private EditText mEditText;
    private ListView mListView;
    private TextView mTextView;

    public BookListingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_book_listing, container, false);

        // initialize views and variables
        initView(rootView);
        initValues(rootView);

        if (mBookArrayList.size() == 0) {
            setEmptyView(rootView, rootView.getResources().getString(R.string.enter_search_query));
        } else {
            mListView.setAdapter(mBookArrayAdapter);
        }

        // on Button click
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String searchQuery = mEditText.getText().toString();

                if (searchQuery != null && !searchQuery.equals("")) {

                    String searchString = GOOGLE_BOOKS_API_BASE_QUERY + searchQuery;

                    Log.d(LOG_TAG, searchString);

                    if (Utility.isNetworkAvailable(rootView.getContext())) {
                        mGoogleBookAsyncTask = new GoogleBookAsyncTask(new AsyncResponse() {
                            @Override
                            public void processFinish(ArrayList<Book> bookArrayList) {
                                if (bookArrayList != null) {
                                    mBookArrayAdapter.clear();
                                    mBookArrayAdapter.addAll(bookArrayList);
                                    mBookArrayAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d(LOG_TAG, "bookArrayList is null");
                                    showToast(rootView,
                                            rootView.getResources().getString(R.string.search_item_not_found));
                                }
                            }
                        });
                        mGoogleBookAsyncTask.execute(searchString);
                    } else {
                        Log.d(LOG_TAG, "network not found");
                    }
                } else {
                    showToast(rootView,
                            rootView.getResources().getString(R.string.nothing_entered));
                }
            }
        });
        return rootView;
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.listview);
        mEditText = (EditText) view.findViewById(R.id.search_box);
        mButton = (Button) view.findViewById(R.id.search_button);
        mTextView = (TextView) view.findViewById(R.id.emptyElement);
    }

    private void initValues(View view) {
        mBookArrayList = new ArrayList<>();
        mBookArrayAdapter = new BookAdapter(view.getContext(), mBookArrayList);
        mListView.setAdapter(mBookArrayAdapter);
    }

    private void setEmptyView(View view, String msg) {
        mTextView.setText(msg);
        mListView.setEmptyView(view.findViewById(R.id.emptyElement));
    }

    private void showToast(View view, String msg) {
        Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

