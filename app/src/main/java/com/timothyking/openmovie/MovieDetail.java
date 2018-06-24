package com.timothyking.openmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity {
    TextView textTitle;
    ImageView imagePoster;
    TextView textYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        textTitle = (TextView) findViewById( R.id.textTitle);
        imagePoster = (ImageView) findViewById(R.id.imagePoster);
        textYear = (TextView) findViewById(R.id.textYear);

        Intent intent = getIntent();
        ArrayList<String> message = (ArrayList<String>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        textTitle.setText(message.get(0));
        Picasso.get().load(message.get(2)).into(imagePoster);
        textYear.setText(message.get(3));
    }
}
