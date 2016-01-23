package com.marian.movies.app.model;

import java.util.ArrayList;

/**
 * Created by Marian on 1/10/2016.
 */
public class ReviewsModel {
    public int id;
    public int page;
    public ArrayList<Result> results;
    int total_pages;
    int total_results;
    public class Result {
        public String _id;
        public String _author;
        public String _content;
        public String _url;
    }

}
