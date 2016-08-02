package com.example.kishan.popularmovies;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title, releaseDate, poster, vote, synopsis;

        final String POSTER = "poster_path";
        final String RELEASE = "release_date";
        final String TITLE = "title";
        final String VOTE = "vote_average";
        final String SYNOPSIS = "overview";

        Bundle bundle = getIntent().getExtras();

        title = bundle.getString(TITLE);
        releaseDate = bundle.getString(RELEASE);
        poster = bundle.getString(POSTER);
        vote = bundle.getString(VOTE);
        synopsis = bundle.getString(SYNOPSIS);

        ImageView img_poster = (ImageView)findViewById(R.id.mvi_poster);
        Picasso.with(getBaseContext()).load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + poster).into(img_poster);

        TextView textView_rating = (TextView) findViewById(R.id.mvi_rating);
        textView_rating.setText(getString(R.string.rating) + ": " + vote + "/10");

        TextView textView_releaseDate = (TextView) findViewById(R.id.mvi_releaseDate);
        textView_releaseDate.setText(getString(R.string.release_date) + ": " + releaseDate);

        TextView textView_description = (TextView) findViewById(R.id.mvi_description);
        textView_description.setText(synopsis);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);



    }

}
