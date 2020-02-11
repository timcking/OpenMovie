package com.timothyking.openmovie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.timothyking.openmovie.MESSAGE";
    public  static final String TAG  = "OpenWeather";
    private EditText editSearch;
    Button buttonSearch;
    ListView mainListView;
    HashMap<Integer, String> hmapTitle = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapMovieID = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapPoster = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapYear = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = findViewById( R.id.mainListView );
        buttonSearch = findViewById(R.id.buttonSearch);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getMovieItem(position);
            }
        });
    }

    public void getMovieItem(int position) {
        String title = hmapTitle.get(position);
        String movieid = hmapMovieID.get(position);
        String poster = hmapPoster.get(position);
        String year = hmapYear.get(position);

        ArrayList<String> listStrings = new ArrayList<>();
        listStrings.add(title);
        listStrings.add(movieid);
        listStrings.add(poster);
        listStrings.add(year);

        // Call new activity
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(EXTRA_MESSAGE, listStrings);
        startActivity(intent);
    }

    public void searchMovie (View view) {
        editSearch = findViewById(R.id.editSearch);
        String strSearch =  (editSearch.getText().toString());

        // Replace spaces with + for search
        strSearch = strSearch.replaceAll(" ", "+");

        // Using string resource
        String myURL = getString(R.string.search_url) + "s=" + strSearch;

        DownloadTask task = new DownloadTask();
        task.execute(myURL);
        Log.i(TAG, strSearch);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "Error while fetching movie info (this should not happen really!)", e);
            } catch (IOException e) {
                Log.e(TAG, "Error while fetching movie info", e);
            } catch (Exception e) {
                Log.e(TAG, "Unknown exception");
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null) {
                Toast.makeText(MainActivity.this, "Not found, try another movie title", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> labelList = new ArrayList<String>();
            // Create ArrayAdapter using the label list
            ListAdapter listAdapter = new ArrayAdapter<String>(MainActivity.this,
                    R.layout.simplerow, labelList) {

                // Alternate row colors
                @Override
                public View getView(int position, View listAdapter, ViewGroup parent) {
                    // Get the current item from ListView
                    View view = super.getView(position, listAdapter, parent);
                    if (position % 2 == 1) {
                        // Set a background color for ListView regular row/item
                        view.setBackgroundColor(Color.parseColor("#B3D7FF"));
                    } else {
                        // Set the background color for alternate row/item
                        view.setBackgroundColor(Color.parseColor("#CCE4FF"));
                    }
                    return view;
                }
            };

            try {
                JSONObject jsonObject = new JSONObject(result);
                String movieHits = jsonObject.getString("Search");
                JSONArray arr = new JSONArray(movieHits);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String title = jsonPart.getString("Title");
                    String imdbID = jsonPart.getString("imdbID");
                    String poster = jsonPart.getString("Poster");
                    String year = jsonPart.getString("Year");

                    hmapTitle.put(i, title);
                    hmapMovieID.put(i, imdbID);
                    hmapPoster.put(i, poster);
                    hmapYear.put(i, year);

                    labelList.add(i, title + " (" + year + ")");
                }
            } catch(JSONException e) {
                Toast.makeText(MainActivity.this, "Not found, try another movie title", Toast.LENGTH_LONG).show();
                Log.e(TAG, "error while fetching weather info", e);
            }

            // Set the ArrayAdapter as the ListView's adapter
            mainListView.setAdapter( listAdapter );

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
        }
    }
}
