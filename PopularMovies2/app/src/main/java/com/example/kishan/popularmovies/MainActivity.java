package com.example.kishan.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    MovieAdapter myAdapter = null;
    JSONArray JSONArray_mvi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myAdapter = new MovieAdapter(getApplicationContext(), new ArrayList<Movie>());
        GridView myList = (GridView) findViewById(R.id.gridview);
        myList.setAdapter(myAdapter);




        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("My POSITION",""+position);

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("position", position);

                String title, releaseDate, poster, vote, synopsis;

                final String API_POSTER = "poster_path";
                final String API_RELEASE = "release_date";
                final String API_TITLE = "title";
                final String API_VOTE = "vote_average";
                final String API_SYNOPSIS = "overview";


                try {
                    JSONObject mvi_JSON = JSONArray_mvi.getJSONObject(position);
                    title = mvi_JSON.getString(API_TITLE);
                    intent.putExtra(API_TITLE, title);

                    releaseDate = mvi_JSON.getString(API_RELEASE);
                    intent.putExtra(API_RELEASE, releaseDate);

                    poster = mvi_JSON.getString(API_POSTER);
                    intent.putExtra(API_POSTER, poster);

                    vote = mvi_JSON.getString(API_VOTE);
                    intent.putExtra(API_VOTE, vote);

                    synopsis = mvi_JSON.getString(API_SYNOPSIS);
                    intent.putExtra(API_SYNOPSIS, synopsis);

                    startActivity(intent);

                } catch (JSONException e) {
                    Log.e("Error", "Getting an JSON object from JSON", e);
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings_intent = new Intent(this, SettingsActivity.class);
            startActivity(settings_intent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver  , new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver networkStateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            boolean isConnected = ni != null &&
                    ni.isConnectedOrConnecting();


            if (isConnected) {
                updateMovies();
            } else {
                Snackbar.make(findViewById(R.id.parent_View), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Start Wifi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                                wifi.setWifiEnabled(true);
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.holo_blue_light)).show();
            }
        }
    };

    private void updateMovies()
    {
        FetchMovies fetchMovies = new FetchMovies();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String sorting_type = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        Log.d("sorting type", sorting_type);
        fetchMovies.execute(sorting_type);
    }

    class FetchMovies extends AsyncTask<String, Void, Movie[]>
    {

        @Override
        protected void onPostExecute(Movie[] mvis)
        {
            if(mvis != null)
            {
                myAdapter.clear();
                for(Movie movie : mvis)
                {
                    myAdapter.add(movie);
                }
            }
        }

        private String LOG_TAG = FetchMovies.class.getSimpleName();

        private Movie[] getJSONMovieData(String JSONString) throws JSONException{

            final String MVI_RESULT = "results";
            final String PATH = "poster_path";
            final String TITLE = "title";

            JSONObject movieJSON = new JSONObject(JSONString);

            JSONArray mviArray = movieJSON.getJSONArray(MVI_RESULT);

            Movie[] movieList = new Movie[20];

            for(int i = 0; i < mviArray.length(); i++)
            {
                String poster_link;
                String title;

                JSONObject mvi = mviArray.getJSONObject(i);

                poster_link = mvi.getString(PATH);
                title = mvi.getString(TITLE);

                movieList[i] = new Movie(title, poster_link);
            }

            return movieList;
        }

        @Override
        protected Movie[] doInBackground(String... params)
        {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String dataIn = null;

            if(params.length == 0)
            {
                return null;
            }

            try
            {
                final String BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] +"?";
                final String api = "api_key";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(api, getResources().getString(R.string.api_key))
                        .build();


                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line=bufferedReader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                dataIn = buffer.toString(); //Storing Data coming into String

                Log.v("LOG TAG","API Data : "+dataIn);

                JSONObject mvi_JSON = new JSONObject(dataIn);
                JSONArray_mvi = mvi_JSON.getJSONArray("results");

            }
            catch (MalformedURLException e)
            {
                Log.e("URL Connection error", "Error Closing Stream", e);
            }
            catch (IOException e)
            {
                Log.e("IOException", "Error", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("PlaceHolderFragment","Error Closing Stream", e);
                    }
                }

            }

            try {
                return getJSONMovieData(dataIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }



    }



}
