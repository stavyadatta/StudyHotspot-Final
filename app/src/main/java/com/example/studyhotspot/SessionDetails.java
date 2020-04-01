package com.example.studyhotspot;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SessionDetails extends AppCompatActivity {

    CollapsingToolbarLayout sessionTitle;
    TextView creatorName;
    TextView description;

    TextView startTime;
    TextView startDate;
    TextView endDate;
    TextView endTime;

    TextView session_participants;
    TextView location;
    Button viewParticipants;
    Button leave_session;
    ImageView back;

    String userID;
    String currentUser;
    String userEmail;

    public String title;
    public String creatorN;
    private String des;
    private String locationName;
    private String documentName;
    private String status;

    private Date date_starting;
    private Date date_ending;
    private Long session_numbers;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Boolean> participantStatus = new HashMap<String, Boolean>();

    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATOR = "creatorName";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STARTTIME = "startDateTime";
    private static final String KEY_ENDTIME = "endDateTime";
    private static final String KEY_SESSION_NUMBERS = "numOfParticipants";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_PARTICIPANTS = "participantStatus";


    private static final String TAG = "ScrollingActivity" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_page);

        if (getIntent().hasExtra("docname")) {
            documentName = getIntent().getStringExtra("docname");
            currentUser = getIntent().getStringExtra("currentUser");
            userID = getIntent().getStringExtra("currentUID");
            userEmail = getIntent().getStringExtra("userEmail");
            status = getIntent().getStringExtra("Status");
        } else {
            Toast.makeText(SessionDetails.this, "ERROR", Toast.LENGTH_LONG).show();
            finish();
        }

        getInformation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setUpContent();
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
                locationName = documentSnapshot.getString(KEY_LOCATION);
            }
        });
    }

    private void setUpContent(){
        sessionTitle = findViewById(R.id.toolbar_layout);
        creatorName = findViewById(R.id.creator_name);
        location = findViewById(R.id.location_name);
        description = findViewById(R.id.description);

        startTime = findViewById(R.id.start_time);
        startDate = findViewById(R.id.start_date);

        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);

        session_participants = findViewById(R.id.session_participants);
        viewParticipants = findViewById(R.id.view_all_participants);
        back = findViewById(R.id.back_button);
        leave_session = findViewById(R.id.leave_session);

        sessionTitle.setTitle(title);
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
            e.printStackTrace();
            Toast.makeText(SessionDetails.this, "LOADING", Toast.LENGTH_LONG).show();
            setUpContent();
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
                    String jsonstring = URLReader.readUrl(sb.toString());
                    jsonObject = new JSONObject(jsonstring);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(SessionDetails.this, LocationInformationActivity.class);
                intent.putExtra("Name", locationName);
                try {
                    intent.putExtra("PlaceID", jsonObject.get("candidates").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });

        viewParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionDetails.this, ViewParticipants.class);
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

        if (status != null && status.contentEquals("past")){
            leave_session.setBackgroundColor(0xFFF0F0F0);
            leave_session.setClickable(false);
            leave_session.setText("PAST");
        }
        else {
            leave_session.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SessionDetails.this);

                    // title for ur dialog box
                    builder.setTitle("Leaving session");
                    builder.setMessage("Are you sure you want to leave this session");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SessionDetails.this, "Yes has been clicked",
                                    Toast.LENGTH_SHORT).show();
                            // uncomment to delete the document really
                            //docRef.delete();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SessionDetails.this, "No has been clicked",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

    }
}
