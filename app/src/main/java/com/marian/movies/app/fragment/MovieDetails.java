package com.marian.movies.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marian.movies.app.adapters.TailorsAdapter;
import com.marian.movies.app.model.ReviewsModel;
import com.marian.movies.app.model.TrailerModel;
import com.marian.movies.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetails#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MovieDetails extends Fragment {
    TrailerModel TrailerModelObj;
    ReviewsModel reviewModelObj;
    TailorsAdapter tailorsAdapter;
    ListView TailorListView;
    ListView reviewsListView;
    ArrayList<TrailerModel.Result> TArrayList;
    ArrayList<String> rArrayList;
    ArrayAdapter<String> Tadapter;
    ArrayAdapter<String> reviews_adapter;
    static int favorited_count=0;
    static boolean favorited=false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String MyPREFERENCES = "MyPrefs" ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String Movie_Title;
    String Movie_Poster_Path;
    String Movie_Release_Date;
    String Movie_Overview;
    double Movie_Avg_Rate;
    int Moive_Id;
    ProgressBar progressBar;
    RelativeLayout container;
    ImageButton fav_btn;

    private OnFragmentInteractionListener mListener;

    public MovieDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetails newInstance(String param1, String param2) {
        MovieDetails fragment = new MovieDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            Movie_Title = getArguments().getString("movie_title");
            Movie_Poster_Path= getArguments().getString("movie_poster_path");
            Movie_Release_Date=getArguments().getString("movie_release_date");
            Movie_Avg_Rate=getArguments().getDouble("movie_vote_avg");
            Moive_Id=getArguments().getInt("movie_id");
            Movie_Overview=getArguments().getString("movie_overview");
        }
    }

    //check if movie already favorited
     boolean isFavorited()
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        //if stored in shared prefrences
        if(settings.contains(""+Moive_Id))
        {
            favorited=true;
            return true;
        }
        favorited=false;
        return  false;
    }

     void storeMovie(Context context)
    {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        int favorited_count = settings.getInt("count", -1);
        if(favorited_count==-1)
        {
            favorited_count=0;
        }
        favorited_count++;
        editor.putInt("count",favorited_count);
        editor.putInt(""+Moive_Id,favorited_count);//movie index in shared preferences
        editor.putInt("movieId_" + favorited_count, Moive_Id); //3
        editor.putString("movieTitle_"+favorited_count,Movie_Title);
        editor.putString("moviePosterPath_"+favorited_count,Movie_Poster_Path);
        editor.putString("movieReleaseDate_"+favorited_count,Movie_Release_Date);
        editor.putString("movieOverview_"+favorited_count,Movie_Overview);
        editor.putString("movieAvgRate_" + favorited_count, "" + Movie_Avg_Rate);
       /*

        editor.putLong(MyPREFERENCES,Double.doubleToLongBits(Movie_Avg_Rate));*/
        editor.commit(); //4
        favorited=true;
        changeBtnDisplay();
    }
    void unFavorite()
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); //1
        editor = settings.edit();

        //get movie index in shared preferences
        int index = settings.getInt(""+Moive_Id, -1);
        if(index!=-1)
        {
            editor.remove(""+Moive_Id);
            editor.remove("movieId_"+index);
            editor.remove("movieTitle_"+index);
            editor.remove("moviePosterPath_"+index);
            editor.remove("movieReleaseDate_"+index);
            editor.remove("movieOverview_"+index);
            editor.remove("movieOverview_"+index);
        }
        editor.apply();
        favorited=false;
        changeBtnDisplay();
    }
    void changeBtnDisplay()
    {
        if(favorited)
        {
            fav_btn.setImageResource(R.drawable.ic_star_yellow_24dp);
        }
        else
        {
            fav_btn.setImageResource(R.drawable.ic_star_white_24dp);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_movie__details, container, false);
        //create tailor model object
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        container = (RelativeLayout) view.findViewById(R.id.container_abz);
        TextView title=(TextView)(view.findViewById(R.id.txt_title));

        title.setText(Movie_Title);

        TextView overview=(TextView)(view.findViewById(R.id.txt_overview));

        overview.setText(Movie_Overview);

        //button favorite
         fav_btn=(ImageButton)view.findViewById(R.id.btn_fav);
         isFavorited();
        changeBtnDisplay();
        fav_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!isFavorited())
                {
                   //store movie id in shared preferences
                    storeMovie(getActivity());
                }
                else
                {
                    //unfavorite
                    unFavorite();
                }

            }
        });
        //title.setText("http://image.tmdb.org/t/p/w185"+Movie_Poster_Path);
        ImageView imgView_poster=(ImageView)(view.findViewById(R.id.img_poster));
        TextView release_date=(TextView)(view.findViewById(R.id.txt_release_date));
        release_date.setText(Movie_Release_Date);

        TextView avg_vote=(TextView)(view.findViewById(R.id.txt_avg_vote));
        avg_vote.setText("" + Movie_Avg_Rate);

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185"+Movie_Poster_Path).into(imgView_poster);
        TrailerModelObj = new TrailerModel();
        TrailerModelObj.results = new ArrayList<>();

        reviewModelObj = new ReviewsModel();
        reviewModelObj.results = new ArrayList<>();
        //Tailor listview
        TailorListView=(ListView)(view.findViewById(R.id.TList));


        //review listview
        reviewsListView=(ListView)(view.findViewById(R.id.RList));
        rArrayList=new ArrayList<String>();
        TArrayList=new ArrayList<TrailerModel.Result>();

        tailorsAdapter = new TailorsAdapter(getActivity(), TArrayList);


        //String[]arr={"a","d"};
        //Tailor adapter
      //  Tadapter= new ArrayAdapter<String>(getActivity(),
        //        android.R.layout.simple_list_item_1, TArrayList);
        TailorListView.setAdapter(tailorsAdapter);

        //Review adapter
        reviews_adapter= new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, rArrayList);
        reviewsListView.setAdapter(reviews_adapter);

        TailorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String videoId = TrailerModelObj.results.get(position).T_key;
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.putExtra("VIDEO_ID", videoId);*/
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                getActivity().startActivity(intent);
            }
        });
        new FetchTrailerTask().execute("" + Moive_Id);
        new FetchReviewsTask().execute("" + Moive_Id);
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    class FetchTrailerTask extends AsyncTask<String, Void, TrailerModel>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            //container.setVisibility(View.GONE);
        }

        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        /*protected void onPostExecute(TrailerModel result) {
            //Log.d("tag", result.page+"");
            TArrayList.addAll(result.results);
              tailorsAdapter.notifyDataSetChanged();
        }*/



        private TrailerModel getTrailersFromJson(String TJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.

            JSONObject MovieModelObject = new JSONObject(TJsonStr);


            JSONArray resultsArray = MovieModelObject.getJSONArray("results");
            //movieModel.results = new ArrayList<>();

            for(int i = 0; i < resultsArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                TrailerModel.Result result= new TrailerModel().new Result();
                JSONObject jsonObject= (JSONObject) resultsArray.get(i);
                result.T_id = jsonObject.getString("id");
                result.T_iso=jsonObject.getString("iso_639_1");
                result.T_key=jsonObject.getString("key");
                result.T_name=jsonObject.getString("name");
                result.T_site=jsonObject.getString("site");
                result.T_size=jsonObject.getInt("size");
                result.T_type=jsonObject.getString("type");

                TrailerModelObj.results.add(result);
            }
            return TrailerModelObj;
        }

        @Override
        protected void onPostExecute(TrailerModel trailerModel) {
           // super.onPostExecute(trailerModel);
            progressBar.setVisibility(View.GONE);
            //container.setVisibility(View.VISIBLE);
            /*for(int i=1;i<=trailerModel.results.size(); i++)
            {
                TArrayList.add(i+"");
            }*/
            TArrayList.addAll(trailerModel.results);
            tailorsAdapter.notifyDataSetChanged();

            //Tadapter.notifyDataSetChanged();
        }

        @Override
        protected TrailerModel doInBackground(String... params)
        {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String forcastJsonStr=null;
            try
            {
                //popularity.desc
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by="+params[0]+"&api_key=8d491d13915b93cbf8c008675924660f&page="+params[1]);
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/videos?";

                final String FORMAT_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(FORMAT_PARAM, "9bc86fa1c1a938bb26471279fd25babb")
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
                if(forcastJsonStr !=null)
                    return getTrailersFromJson(forcastJsonStr);
            }catch (JSONException e){
                Log.e("tag",e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }


    }


    //Fetch Reviews Task
    class FetchReviewsTask extends AsyncTask<String, Void, ReviewsModel>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            //container.setVisibility(View.GONE);
        }


        private ReviewsModel getReviewsFromJson(String TJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.


            JSONObject ReviewModelObject = new JSONObject(TJsonStr);

            reviewModelObj.id = ReviewModelObject.getInt("id");
            reviewModelObj.page = ReviewModelObject.getInt("page");
            JSONArray resultsArray = ReviewModelObject.getJSONArray("results");
            //movieModel.results = new ArrayList<>();

            for(int i = 0; i < resultsArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                ReviewsModel.Result result= new ReviewsModel().new Result();
                JSONObject jsonObject= (JSONObject) resultsArray.get(i);
                result._id = jsonObject.getString("id");
                result._author=jsonObject.getString("author");
                result._content=jsonObject.getString("content");
                result._url=jsonObject.getString("url");


                reviewModelObj.results.add(result);
            }
            return reviewModelObj;
        }

        @Override
        protected void onPostExecute(ReviewsModel reviewModel) {
            // super.onPostExecute(trailerModel);
            progressBar.setVisibility(View.GONE);

            for(int i=0;i<reviewModel.results.size(); i++)
            {
                ReviewsModel.Result obj= reviewModel.results.get(i);
                rArrayList.add(obj._author);
                rArrayList.add(obj._content);
                rArrayList.add(obj._url);
            }

            reviews_adapter.notifyDataSetChanged();
        }

        @Override
        protected ReviewsModel doInBackground(String... params)
        {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String forcastJsonStr=null;
            try
            {
                //popularity.desc
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by="+params[0]+"&api_key=8d491d13915b93cbf8c008675924660f&page="+params[1]);
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/reviews?";

                final String FORMAT_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(FORMAT_PARAM, "9bc86fa1c1a938bb26471279fd25babb")
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
                if(forcastJsonStr !=null)
                    return getReviewsFromJson(forcastJsonStr);
            }catch (JSONException e){
                Log.e("tag",e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }


    }

}