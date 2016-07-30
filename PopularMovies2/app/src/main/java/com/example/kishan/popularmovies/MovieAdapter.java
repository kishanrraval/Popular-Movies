package com.example.kishan.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class MovieAdapter extends ArrayAdapter<Movie>{

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context,0, movies);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);

        ImageView img = (ImageView) rootView.findViewById(R.id.poster_item);
        Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + movie.getPoster()).into(img);

        TextView text = (TextView) rootView.findViewById(R.id.name_item);
        text.setText(movie.getName());

        /*Random rand = new Random();

        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        int randomColor = Color.rgb(r,g,b);

        rootView.setBackgroundColor(randomColor);*/

        return rootView;
    }
}
