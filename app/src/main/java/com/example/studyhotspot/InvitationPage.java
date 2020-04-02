package com.example.studyhotspot;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvitationPage extends AppCompatActivity {

    TextView titleView;
    TextView description;
    TextView creatorName;
    TextView location;

    TextView startTime;
    TextView startDate;
    TextView endDate;
    TextView endTime;

    TextView session_participants;
    Button view_participants;
    ImageView back;

    Button accept;
    Button decline;

    String userID;
    String currentUser;
    String userEmail;
    int position;

    public String title;
    private String des;
    private String creatorN;
    private String locationName;
    private String documentName;

    private Date date_starting;
    private Date date_ending;

    private Long session_numbers;
    private Map<String, Boolean> participantStatus = new HashMap<String, Boolean>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATOR = "creatorName";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STARTTIME = "startDateTime";
    private static final String KEY_ENDTIME = "endDateTime";
    private static final String KEY_SESSION_NUMBERS = "numOfParticipants";
    private static final String KEY_LOCATION_NAME = "location";
    private static final String KEY_PARTICIPANTS = "participantStatus";


    private static final String TAG = "InvitationPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_page);

        if (getIntent().hasExtra("docname")){
            documentName = getIntent().getStringExtra("docname");
            currentUser = getIntent().getStringExtra("currentUser");
            userID = getIntent().getStringExtra("currentUID");
            userEmail = getIntent().getStringExtra("userEmail");
            position = getIntent().getIntExtra("position", -1);
        }
        else {
            Toast.makeText(InvitationPage.this, "ERROR", Toast.LENGTH_LONG).show();
            finish();
        }

        getInformation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setUpContent(0);
            }
        }, 1500);
    }

    private void getInformation(){

        final DocumentReference docRef = db.collection("hashsessions")
                .document(documentName);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                title = documentSnapshot.getString(KEY_TITLE);
                creatorN = documentSnapshot.getString(KEY_CREATOR);
                des = documentSnapshot.getString(KEY_DESCRIPTION);

                participantStatus = (Map<String, Boolean>) documentSnapshot.get(KEY_PARTICIPANTS);

                date_starting = documentSnapshot.getDate(KEY_STARTTIME);

                date_ending = documentSnapshot.getDate(KEY_ENDTIME);

                // session participants
                session_numbers = documentSnapshot.getLong(KEY_SESSION_NUMBERS);
                assert session_numbers != null;

                // location putting
                locationName = documentSnapshot.getString(KEY_LOCATION_NAME);
            }
        });
    }

    private void setUpContent(int time){
        if (time > 5){
            Toast.makeText(InvitationPage.this, "PLEASE TRY AGAIN", Toast.LENGTH_LONG).show();
            finish();
        }

        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);

        titleView = findViewById(R.id.title);
        creatorName = findViewById(R.id.creator_name);
        location = findViewById(R.id.location_name);
        description = findViewById(R.id.description);

        startTime = findViewById(R.id.start_time);
        startDate = findViewById(R.id.start_date);

        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);

        session_participants = findViewById(R.id.session_participants);
        view_participants = findViewById(R.id.view_all_participants);
        back = findViewById(R.id.back_button);

        titleView.setText(title);
        creatorName.setText(creatorN);
        description.setText(des);
        description.setTypeface(null, Typeface.BOLD);

        try{
            startTime.setText(date_starting.toString().substring(10,20));
            startDate.setText(date_starting.toString().substring(0, 10));

            endDate.setText(date_ending.toString().substring(0, 10));
            endTime.setText(date_ending.toString().substring(10, 20));

            session_participants.setText(session_numbers.toString());
            session_participants.setTypeface(null, Typeface.BOLD_ITALIC);

            location.setText(Html.fromHtml("<u>" + locationName + "</u>" ));
            location.setTextColor(getColor(R.color.hyperlinkBlue));
            location.setTypeface(null, Typeface.BOLD);

        }catch(Exception e){
            Toast.makeText(InvitationPage.this, "LOADING", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Toast.makeText(InvitationPage.this, "LOADING", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    setUpContent(time+1);
                }
            }, 500);
        }

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
                    jsonObject = URLReader.URL2JSON(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(InvitationPage.this, LocationInformationActivity.class);
                intent.putExtra("Name", locationName);
                try {
                    intent.putExtra("PlaceID", jsonObject.get("candidates").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });

        view_participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvitationPage.this, ViewParticipants.class);
                intent.putExtra("Title", title);
                intent.putExtra("ParticipantStatus", (Serializable) participantStatus);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InvitationPage.this, "ACCEPTING...", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Decision",true);
                returnIntent.putExtra("position",position);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InvitationPage.this, "REJECTING...", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Decision",false);
                returnIntent.putExtra("position",position);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });


    }
}
