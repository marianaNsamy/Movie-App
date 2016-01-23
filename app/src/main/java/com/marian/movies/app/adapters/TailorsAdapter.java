package com.marian.movies.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.marian.movies.app.model.TrailerModel;
import com.marian.movies.app.R;

import java.util.ArrayList;

/**
 * Created by Marian on 1/22/2016.
 */
public class TailorsAdapter extends BaseAdapter {
    ArrayList<TrailerModel.Result> result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public TailorsAdapter(Context mainActivity, ArrayList<TrailerModel.Result> result) {
        // TODO Auto-generated constructor stub

        context=mainActivity;
        this.result = result;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_row, null);
        holder.img=(ImageView) rowView.findViewById(R.id.image_item2);
        holder.img.setImageResource(R.drawable.ic_av_play_circle_fill);
        /*rowView.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });*/
        return rowView;
    }

}
