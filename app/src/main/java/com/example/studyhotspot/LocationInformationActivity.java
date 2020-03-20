package com.example.studyhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class LocationInformationActivity extends AppCompatActivity {


    private TextView mLocationName;
    private FloatingActionButton homeButton;
    private FloatingActionButton directions;
    private ImageView back;
    private TextView address;
    private TextView d2;
    private TextView d3;
    private TextView d4;

    private OkHttp obj = new OkHttp();
    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);

        mLocationName = findViewById(R.id.location_name);
        homeButton = findViewById(R.id.homeButton);
        directions = findViewById(R.id.directions);
        back = findViewById(R.id.back_button);

        address = findViewById(R.id.details_address);
        d2 = findViewById(R.id.details_2);
        d3 = findViewById(R.id.details_3);
        d4 = findViewById(R.id.details_4);

        String name = null;
        String coord = null;
        String rawPlaceID = null;

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            name = extras.getString("Name");
            mLocationName.setText(name);
            coord = extras.getString("Coord");
            rawPlaceID = extras.getString("PlaceID");
        }

        String placeID = null;
        int begin = rawPlaceID.indexOf(":");
        int end = rawPlaceID.indexOf("}",begin);
        placeID = rawPlaceID.substring(begin+2,end-1);



        try {
            //jsonObject = obj.accessData(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*address.setText(placeID);
        d2.setText(name);
        d3.setText(name);
        d4.setText(name);*/

        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.google.com/maps?daddr=");
        sb.append(coord);
        String url = sb.toString();


        directions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }
}
