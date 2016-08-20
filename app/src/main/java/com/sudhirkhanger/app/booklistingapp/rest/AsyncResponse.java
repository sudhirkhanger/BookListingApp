package com.sudhirkhanger.app.booklistingapp.rest;

import com.sudhirkhanger.app.booklistingapp.model.Book;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(ArrayList<Book> bookArrayList);
}
