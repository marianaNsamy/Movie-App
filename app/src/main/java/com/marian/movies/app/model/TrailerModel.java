package com.marian.movies.app.model;

import java.util.ArrayList;

/**
 * Created by Marian on 1/9/2016.
 */
public class TrailerModel {
    public ArrayList<Result> results;
    public class Result {
        public String T_id;
        public String T_iso;
        public String T_key;
        public String T_name;
        public String T_site;
        public int T_size;
        public String T_type;

    }
}
