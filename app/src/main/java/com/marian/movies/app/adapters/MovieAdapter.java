package com.marian.movies.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.marian.movies.app.model.MovieModel;
import com.marian.movies.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marian on 1/5/2016.
 */
public class MovieAdapter extends ArrayAdapter<ArrayList<MovieModel.Result>> {

    private Context mContext;
    ArrayList<MovieModel.Result> movieModel;
    Holder holder;

    public MovieAdapter(Context context, int resource,ArrayList<MovieModel.Result> results ) {
        super(context, resource);
        mContext = context;
        this.movieModel = results;
    }


    public int getCount() {
        return movieModel.size();
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view=convertView;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.grid_row, parent, false);
            holder = new Holder();
            holder.imageView = (ImageView) view.findViewById(R.id.image_item);
            view.setTag(holder);
        } else {
            view.getTag(position);
        }
        //imageView.setImageResource(mThumbIds[position]);
        if (movieModel.size()>0) {
          Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+movieModel.get(position).poster_path).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

        }
        return view;
    }

    public class Holder{
        ImageView imageView;
    }

}
