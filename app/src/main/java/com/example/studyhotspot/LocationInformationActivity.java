package com.example.studyhotspot;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
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
    private RatingBar ratingBar;
    private TextView tempView;
    private TextView condDesc;
    private ImageView condIcon;
    private TextView time;
    private SeekBar timeBar;
    private RatingBar favorite;
    private BottomAppBar bottomAppBar;


    String name = null;
    String coord = null;
    String rawPlaceID = null;

    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            name = extras.getString("Name");
            coord = extras.getString("Coord");
            rawPlaceID = extras.getString("PlaceID");
        }

        setUpTopBar();
        setUpBottomAppBar();
        setUpInformationPage();

        /*favorite = findViewById(R.id.favorite);*/

/*        favorite.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating == 1){
                    System.out.println("Add to Favorites");
                }
                else if (rating == 0){
                    System.out.println("Remove from Favorites");
                }
            }
        });*/

    }

    private void setUpTopBar(){
        directions = findViewById(R.id.directions);
        back = findViewById(R.id.back_button);
        locationName = findViewById(R.id.location_name);

        locationName.setText(name);

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


    }
    private void setUpInformationPage(){
        //ids
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone);
        openStatus = findViewById(R.id.openStatus);
        hours = findViewById(R.id.hours);
        price = findViewById(R.id.price);
        ratingBar = findViewById(R.id.ratingBar);
        condDesc = findViewById(R.id.condDescr);
        tempView = findViewById(R.id.temp);
        condIcon = findViewById(R.id.condIcon);
        time = findViewById(R.id.time);
        timeBar = findViewById(R.id.timeBar);

        //obtain + display details

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
                sb.append("- ");
                sb.append(weekdays.get(i));
                sb.append("\n");
            }

            hours.setText(sb.toString());

        } catch (JSONException e) {
            openStatus.setText("Opening Hours Unavailable");
            e.printStackTrace();
        }

        ratingBar.setRating(4);

        //WEATHER STARTS HERE

        String jsonstring = null;
        JSONArray weatherJSON = null;

        try {
            jsonstring = URLReader.readUrl("https://api.openweathermap.org/data/2.5/forecast?id=1880251&APPID=8b209724831a07af211a052c5e87e404");
            weatherJSON = new JSONObject(jsonstring).getJSONArray("list");
            System.out.println(weatherJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray finalWeatherJSON = weatherJSON;

        //Initialize timeBar
        try {
            JSONObject cur = finalWeatherJSON.getJSONObject(0);

            //Find and Set Temperature
            int temperature = (cur.getJSONObject("main").getInt("temp")-273);
            tempView.setText(""+temperature+" °C");

            //Find and Set Condition
            String cond = cur.getJSONArray("weather").getJSONObject(0).getString("description");
            condDesc.setText(cond);

            //Set Icon based on Condition
            if (cond.contains("thunder")){
                condIcon.setImageResource(R.mipmap.thunderstorm);
            }
            else if (cond.contains("cloud")){
                condIcon.setImageResource(R.mipmap.cloud);
            }
            else if (cond.contains("rain")){
                condIcon.setImageResource(R.mipmap.rain);
            }
            else if (cond.contains("clear")){
                condIcon.setImageResource(R.mipmap.clear);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set timeBar listener
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //set Timing
                if (progress == 0){
                    time.setText("Now:");
                }
                else if (progress == 1){
                    time.setText("In 1 hour:");
                }
                else{
                    time.setText("In "+progress+" hours:");
                }

                try {
                    JSONObject cur = finalWeatherJSON.getJSONObject(progress);

                    //Find and Set Temperature
                    int temperature = (cur.getJSONObject("main").getInt("temp")-273);
                    tempView.setText(""+temperature+" °C");

                    //Find and Set Condition
                    String cond = cur.getJSONArray("weather").getJSONObject(0).getString("description");
                    condDesc.setText(cond);

                    //Set Icon based on Condition
                    if (cond.contains("thunder")){
                        condIcon.setImageResource(R.mipmap.thunderstorm);
                    }
                    else if (cond.contains("cloud")){
                        condIcon.setImageResource(R.mipmap.cloud);
                    }
                    else if (cond.contains("rain")){
                        condIcon.setImageResource(R.mipmap.rain);
                    }
                    else if (cond.contains("clear")){
                        condIcon.setImageResource(R.mipmap.clear);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        createSession = findViewById(R.id.createSession);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")) {
                    Intent intent = new Intent(LocationInformationActivity.this, FindFriend.class);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(LocationInformationActivity.this, ActivityPageMain.class);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    //Intent intent = new Intent(MapsActivity.this, )
                }

                return false;
            }
        });

        createSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationInformationActivity.this, CreateSession.class);
                intent.putExtra("Name", name);
                intent.putExtra("Coord", coord);
                startActivity(intent);
            }
        });
    }
}
