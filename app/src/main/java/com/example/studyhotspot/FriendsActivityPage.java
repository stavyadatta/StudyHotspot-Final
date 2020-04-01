package com.example.studyhotspot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class FriendsActivityPage extends AppCompatActivity {

    TextView titleView;
    TextView description;
    TextView startTime;
    TextView startDate;

    TextView endDatetime;

    TextView endDate;
    TextView endTime;

    TextView session_participants;

    TextView location;


    public String title;
    private String des;
    private String documentName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STARTTIME = "startDateTime";
    private static final String KEY_ENDTIME = "endDateTime";
    private static final String KEY_SESSION_NUMBERS = "numOfParticipants";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_LOCATION_NAME = "locationName";


    private static final String TAG = "FriendsActivityPage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        titleView = findViewById(R.id.title);
        description = findViewById(R.id.description);
        startTime = findViewById(R.id.start_time);
        startDate = findViewById(R.id.start_date);

        endDatetime = findViewById(R.id.end_time);

        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);


        session_participants = findViewById((R.id.session_participants));

        // location
        location = findViewById(R.id.location_name);

        if (getIntent().hasExtra("docname")) {
            documentName = getIntent().getStringExtra("docname");
        } else {
            documentName = "X8lsLhFVKURYht6enWnr";
        }

        final DocumentReference docRef = db.collection("hashsessions")
                .document("XNv0L7iQCIxBZzwcy3Ww");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                title = documentSnapshot.getString(KEY_TITLE);
                des = documentSnapshot.getString(KEY_DESCRIPTION);
                description.setText(des);
                description.setTypeface(null, Typeface.BOLD);
                description.setTextColor(Color.BLACK);


                Date date_starting = documentSnapshot.getDate(KEY_STARTTIME);
                startTime.setText(date_starting.toString().substring(10, 20));

                startDate.setText(date_starting.toString().substring(0, 10));

                // ending time

                Date date_ending = documentSnapshot.getDate(KEY_ENDTIME);
                endDate.setText(date_ending.toString().substring(0, 10));
                endTime.setText(date_ending.toString().substring(10, 20));

                // session participants
                Long session_numbers = documentSnapshot.getLong(KEY_SESSION_NUMBERS);
                assert session_numbers != null;
                session_participants.setText(session_numbers.toString());
                session_participants.setTypeface(null, Typeface.BOLD_ITALIC);

                // location putting
                String locationName = documentSnapshot.getString(KEY_LOCATION_NAME);

                location.setText(Html.fromHtml("<u>" + locationName + "</u>"));
                location.setTextColor(getColor(R.color.hyperlinkBlue));
                location.setTypeface(null, Typeface.BOLD);

                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=");
                        sb.append(locationName);
                        sb.append("&inputtype=textquery&fields=place_id&key=AIzaSyCo7BtlsuOVcER0l-THnPurg5v1RjBXXtU&locationbias=ipbias");
                        String url = sb.toString();

                        System.out.println(url);
                        JSONObject jsonObject = null;

                        try {
                            String jsonstring = URLReader.readUrl(sb.toString());
                            jsonObject = new JSONObject(jsonstring);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(FriendsActivityPage.this, LocationInformationActivity.class);
                        intent.putExtra("Name", locationName);
                        try {
                            intent.putExtra("PlaceID", jsonObject.get("candidates").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);

                    }
                });


            }
        });
    }
}


