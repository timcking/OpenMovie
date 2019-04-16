package com.timothyking.openmovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
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

public class MovieDetail extends AppCompatActivity {
    public  static final String TAG  = "OpenWeather";
    TextView textTitle;
    ImageView imagePoster;
    TextView textYear;
    TextView textRuntime;
    TextView textCast;
    TextView textPlot;
    TextView textDirector;
    String movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        textTitle = findViewById( R.id.textTitle);
        imagePoster = findViewById(R.id.imagePoster);
        textYear = findViewById(R.id.textYear);
        textDirector = findViewById(R.id.textDirector);
        textRuntime = findViewById(R.id.textRuntime);
        textPlot = findViewById(R.id.textPlot);
        textCast = findViewById(R.id.textCast);

        Intent intent = getIntent();
        ArrayList<String> message = (ArrayList<String>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        textTitle.setText(message.get(0));
        movieID = (message.get(1));
        Picasso.get().load(message.get(2)).into(imagePoster);
        textYear.setText(message.get(3));

        String myURL = getString(R.string.search_url) + "i=" + movieID;

        DownloadTask task = new DownloadTask();
        task.execute(myURL);
        Log.i(TAG, movieID);
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                String director = jsonObject.getString("Director");
                String runtime = jsonObject.getString("Runtime");
                String plot = jsonObject.getString("Plot");
                String actors = jsonObject.getString("Actors");

                actors = actors.replaceAll("\\, ", "\n");

                textDirector.setText(director);
                textRuntime.setText(runtime);
                textPlot.setText(plot);
                textCast.setText(actors);

            } catch(JSONException e) {
                Toast.makeText(MovieDetail.this, "Not found, try another movie title", Toast.LENGTH_LONG).show();
                Log.e(TAG, "error while fetching movie info", e);
            }
        }
    }
}
