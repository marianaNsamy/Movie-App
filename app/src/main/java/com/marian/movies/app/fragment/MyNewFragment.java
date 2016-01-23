package com.marian.movies.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.marian.movies.app.adapters.MovieAdapter;
import com.marian.movies.app.model.MovieModel;
import com.marian.movies.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


//import android.app.FragmentTransaction;

/**
 * Created by Marian on 1/5/2016.
 */
public class MyNewFragment extends Fragment {

    public MyNewFragment()
    {
        movieModel = new MovieModel();
        movieModel.results = new ArrayList<>();
    }
    MovieModel movieModel;
    MovieAdapter movieAdapter;
    String sortBy = "popularity.desc";
    static int page = 1;
    String[] str;
    @Nullable


    /*public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        Activity activity = getActivity();

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        movieAdapter = new MovieAdapter(getActivity(),R.layout.grid_row, movieModel.results);
        gridview.setAdapter(movieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //open movie details fragment
                //send movie id
                Bundle args = new Bundle();
                MovieModel.Result obj=movieModel.results.get(position);
                args.putString("movie_title", obj.title);
                args.putString("movie_poster_path",obj.poster_path);
                args.putString("movie_release_date",obj.release_date);
                args.putDouble("movie_vote_avg", obj.vote_average);
                args.putInt("movie_id",obj.id);
                args.putString("movie_overview",obj.overview);
                MovieDetails newFragment = new MovieDetails();
                if(isTablet(getActivity()))
                {
                    Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_tablet);
                    if(f ==null)
                    {
                        newFragment.setArguments(args);
                        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.fragment_tablet, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else {
                        newFragment.setArguments(args);
                        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_tablet, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                else {

                    newFragment.setArguments(args);
                    android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, newFragment);
                    transaction.addToBackStack(null);
                    //}
// Commit the transaction
                    transaction.commit();
                }

            }
        });
        //str = new String[]{sortBy, page + ""};
        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    page = page + 1;
                    if(!sortBy.equals("favorites")){
                    new FetchMovieTask().execute(new String[]{sortBy, page + ""});
                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //get bundle
        if (getArguments() != null) {

            sortBy = getArguments().getString("SortBy");

        }
        if(sortBy!=null&&!sortBy.equals("favorites")) {
            new FetchMovieTask().execute(new String[]{sortBy, page + ""});
        }
        else
        {
            getfavorites();
        }



    }
     void getfavorites()
     {
         SharedPreferences sharedPref = getActivity().getSharedPreferences(MovieDetails.MyPREFERENCES, Context.MODE_PRIVATE);
         movieModel.results.clear();
         int fav_count = sharedPref.getInt("count", -1);
         for(int i=1;i<=fav_count;i++)
         {
             MovieModel.Result result= new MovieModel().new Result();
             result.poster_path = sharedPref.getString("moviePosterPath_" + i, "");
             result.overview = sharedPref.getString("movieOverview_" + i, "");
             result.release_date = sharedPref.getString("movieReleaseDate_" + i, "");
             result.id = sharedPref.getInt("movieId_" + i, 0);
             result.title = sharedPref.getString("movieTitle_" + i, "");
             result.vote_average=Double.parseDouble(sharedPref.getString("movieAvgRate_"+i,"0"));
             if(result.id!=0)
             {
                movieModel.results.add(result);
             }
         }
         //movieAdapter.clear();
         movieAdapter.notifyDataSetChanged();
         return ;
     }
    //asynctask
    class FetchMovieTask extends AsyncTask<String, Void, MovieModel>
    {
        //private final String Log_Tag=FetchWeatherTask.class.getSimpleName();

        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        private MovieModel getMovieFromJson(String forecastJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.

            JSONObject MovieModelObject = new JSONObject(forecastJsonStr);
            movieModel.page = MovieModelObject.getInt("page");

            JSONArray resultsArray = MovieModelObject.getJSONArray("results");
            //movieModel.results = new ArrayList<>();

            for(int i = 0; i < resultsArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                MovieModel.Result result= new MovieModel().new Result();
                JSONObject jsonObject= (JSONObject) resultsArray.get(i);
                result.poster_path = jsonObject.getString("poster_path");
                result.adult = jsonObject.getBoolean("adult");
                result.overview = jsonObject.getString("overview");
                result.release_date = jsonObject.getString("release_date");
                result.id = jsonObject.getInt("id");
                result.original_title = jsonObject.getString("original_title");
                result.original_language = jsonObject.getString("original_language");
                result.title = jsonObject.getString("title");
                result.backdrop_path = jsonObject.getString("backdrop_path");
                result.popularity = jsonObject.getDouble("popularity");
                result.vote_count = jsonObject.getInt("vote_count");
                result.vote_average=jsonObject.getDouble("vote_average");
                movieModel.results.add(result);
            }
            return movieModel;
        }

        protected void onPostExecute(MovieModel result) {
            //Log.d("tag", result.page+"");
            movieAdapter.notifyDataSetChanged();
        }

        @Override
        protected MovieModel doInBackground(String... params)
        {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String forcastJsonStr=null;
            try
            {
                //popularity.desc
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by="+params[0]+"&api_key=8d491d13915b93cbf8c008675924660f&page="+params[1]);
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String FORMAT_PARAM = "api_key";
                final String UNITS_PARAM = "page";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, "9bc86fa1c1a938bb26471279fd25babb")
                        .appendQueryParameter(UNITS_PARAM, params[1])
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read the inputstream into string
                InputStream inputstream=urlConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();

                if(inputstream==null)
                {
                    return  null;
                }

                reader=new BufferedReader(new InputStreamReader(inputstream));

                String line;

                while((line=reader.readLine())!=null)
                {

                    buffer.append(line+"\n");
                }

                if(buffer.length()==0)
                {
                    return null;
                }
                forcastJsonStr = buffer.toString();
               // Log.v(Log_Tag,"Forecast Json String"+forcastJsonStr);
            }
            catch (Exception e)
            {
                Log.e("PlaceholderFragment", "Error ", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final Exception e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try{
                return getMovieFromJson(forcastJsonStr);
            }catch (JSONException e){
                Log.e("tag",e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }


    }
    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
