package com.sudhirkhanger.app.booklistingapp.rest;

import android.os.AsyncTask;
import android.util.Log;

import com.sudhirkhanger.app.booklistingapp.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GoogleBookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

    private final static String LOG_TAG = GoogleBookAsyncTask.class.getSimpleName();

    private static final String ITEMS = "items";
    private static final String VOLUME_INFO = "volumeInfo";
    private static final String TITLE = "title";
    private static final String AUTHORS = "authors";

    public AsyncResponse delegate = null;

    public GoogleBookAsyncTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... strings) {

        // https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String bookJsonStr = null;

        try {
            // Construct the URL for the Google Books API query
            URL url = new URL(strings[0]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                bookJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                bookJsonStr = null;
            }
            bookJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            bookJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonConvertor(bookJsonStr);
    }

    @Override
    protected void onPostExecute(ArrayList<Book> bookArrayList) {
        delegate.processFinish(bookArrayList);
    }

    private ArrayList<Book> jsonConvertor(String bookJsonStr) {

        ArrayList<Book> bookArrayList = new ArrayList<>();

        try {
            JSONObject bookJsonObject = new JSONObject(bookJsonStr);
            if (!bookJsonObject.isNull(ITEMS)) {

                JSONArray bookJsonArray = bookJsonObject.getJSONArray(ITEMS);

                for (int i = 0; i < bookJsonArray.length(); i++) {

                    JSONObject itemJsonObject = bookJsonArray.getJSONObject(i);
                    JSONObject volumeInfoObject = itemJsonObject.getJSONObject(VOLUME_INFO);

                    String title = volumeInfoObject.getString(TITLE);

                    String[] authors = new String[]{"No Authors"};

                    if (!volumeInfoObject.isNull(AUTHORS)) {
                        JSONArray authorsArray = volumeInfoObject.getJSONArray(AUTHORS);
                        Log.d(LOG_TAG, "authors #" + authorsArray.length());
                        authors = new String[authorsArray.length()];
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors[j] = authorsArray.getString(j);
                        }
                    }
                    bookArrayList.add(new Book(title, authors));
                }
            } else {
                bookArrayList = null;
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
        }
        return bookArrayList;
    }
}
