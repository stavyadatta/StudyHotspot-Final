package com.example.studyhotspot;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationInformationActivity extends AppCompatActivity {


    private TextView locationName;
    private FloatingActionButton createSession;
    private FloatingActionButton directions;
    private ImageView back;
    private TextView address;
    private TextView phoneNumber;
    private TextView openStatus;
    private TextView hours;
    private TextView price;
    private TextView website;
    private TextView rating;


    String name = null;
    String coord = null;
    String rawPlaceID = null;

    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);

        locationName = findViewById(R.id.location_name);
        createSession = findViewById(R.id.createSession);
        directions = findViewById(R.id.directions);
        back = findViewById(R.id.back_button);

        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone);
        openStatus = findViewById(R.id.openStatus);
        hours = findViewById(R.id.hours);
        price = findViewById(R.id.price);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            name = extras.getString("Name");
            locationName.setText(name);
            coord = extras.getString("Coord");
            rawPlaceID = extras.getString("PlaceID");
        }

        String placeID = null;
        int begin = rawPlaceID.indexOf(":");
        int end = rawPlaceID.indexOf("}",begin);
        placeID = rawPlaceID.substring(begin+2,end-1);

        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/place/details/json?place_id=");
        sb.append(placeID);
        sb.append("&fields=formatted_address,rating,formatted_phone_number,opening_hours," +
                "website,price_level&key=AIzaSyCo7BtlsuOVcER0l-THnPurg5v1RjBXXtU");

        System.out.println(sb.toString());

        //ADDRESS

        JSONObject result = null;
        try {
            String jsonstring = URLReader.readUrl(sb.toString());
            jsonObject = new JSONObject(jsonstring);
            result = jsonObject.getJSONObject("result");
            address.setText(result.getString("formatted_address"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //PHONE

        try {
            phoneNumber.setText(result.getString("formatted_phone_number"));
        } catch (JSONException e) {
            phoneNumber.setText("Unavailable");
        }

        //PRICE

        try {
            String pLevel = "";
            for (int i = 0; i < result.getInt("price_level"); i++) {
                pLevel = pLevel + "$";
            }
            price.setText(pLevel);
        } catch (JSONException e){
            price.setText("Unavailable");
        }

        //OPENING HOURS

        try {
            JSONObject open = result.getJSONObject("opening_hours");
            System.out.println(open.get("open_now"));
            if ((boolean)open.get("open_now")){
                openStatus.setText("OPEN");
                openStatus.setTextColor(Color.parseColor("#006400"));
            }
            else{
                openStatus.setText("CLOSED");
                openStatus.setTextColor(Color.parseColor("#800000"));
            }

            JSONArray weekdays = (JSONArray) open.get("weekday_text");
            sb = new StringBuilder();

            for (int i = 0; i < weekdays.length(); i++){
                sb.append(weekdays.get(i));
                sb.append("\n");
            }

            hours.setText(sb.toString());

        } catch (JSONException e) {
            openStatus.setText("Opening Hours Unavailable");
            e.printStackTrace();
        }


        sb = new StringBuilder();
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

        createSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationInformationActivity.this, CreateSession.class);
                intent.putExtra("Name", name);
                intent.putExtra("Coord", coord);
                Log.d("name:", name);
                Log.d("Coord", coord);
                startActivity(intent);
            }
        });

    }
}
