package com.marian.movies.app.model;

import java.util.ArrayList;

/**
 * Created by aliabozaid on 12/12/15.
 */
public class MovieModel {
    public int page;
    public int total_results;
    public int total_pages;

    public ArrayList<Result> results;
    public class Result {
        public String poster_path;
        public boolean adult;
        public String overview;
        public String release_date;
        public ArrayList<Integer> genre_ids;
        public int id;
        public String original_title;
        public String original_language;
        public String title;
        public String backdrop_path;
        public double popularity;
        public int vote_count;
        public boolean video;
        public double vote_average;
    }
}
