package com.example.popularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.data.FavoriteDbHelper;
import com.example.popularmovies.utilities.Client;
import com.example.popularmovies.utilities.Service;
import com.example.popularmovies.data.FavoriteDbHelper;
import com.example.popularmovies.data.Movie;
import com.example.popularmovies.data.MoviesResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    private static final String API_TOKEN = "your_api_key";
    private SwipeRefreshLayout swipeContainer;
    private FavoriteDbHelper favoriteDbHelper;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MoviesAdapter.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //For testing the recipe collection sorting alphabetically
       // TestAdapter testAdapter = new TestAdapter(LayoutInflater.from(this));
       // recyclerView.setAdapter(testAdapter);
       // testAdapter.setMovie(movieList);


    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }

    private void initViews(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); //Portrait
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); //Landscape
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper = new FavoriteDbHelper(activity);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        checkSortOrder();

    }

    private void initViews2(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper = new FavoriteDbHelper(activity);

        getAllFavorite();
    }

    /**
     * This method loads different JSON based on shared preferences
     * @param popular
     */
    private void loadJSON(Boolean popular) {  //POPULAR
        if (popular == true) {

            try {

                Client client = new Client();
                Service apiService =
                        client.getClient().create(Service.class);
                Call<MoviesResponse> call = apiService.getPopularMovies(API_TOKEN);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        List<Movie> movies = response.body().getResults();
                        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            try{

                Client client = new Client();
                Service apiService =
                        client.getClient().create(Service.class);
                Call<MoviesResponse> call = apiService.getTopRatedMovies(API_TOKEN);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        List<Movie> movies = response.body().getResults();
                        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()){
                            swipeContainer.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    }
                });
            }catch (Exception e){
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){//Preference Settings!!
        Log.d(LOG_TAG, "Preferences updated");
        checkSortOrder();
    }

    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.sort_order),
                this.getString(R.string.pref_sort_order_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_sort_order_popular))) {
            Log.d(LOG_TAG, getString(R.string.sorting_popular_msg));  //POPULAR
            loadJSON(true);
        } else if (sortOrder.equals(this.getString(R.string.pref_sort_order_rating))){
            Log.d(LOG_TAG, getString(R.string.sorting_rating_msg));
            loadJSON(false);
        } else{
            Log.d(LOG_TAG, getString(R.string.sorting_favorite_msg));
            initViews2();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (movieList.isEmpty()){
            checkSortOrder();
        }else{

            checkSortOrder();
        }
    }

    private void getAllFavorite(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                movieList.clear();
                movieList.addAll(favoriteDbHelper.getAllFavorite());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
