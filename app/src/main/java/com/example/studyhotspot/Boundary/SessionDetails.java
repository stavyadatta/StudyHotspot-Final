package com.example.studyhotspot.Boundary;



import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.example.studyhotspot.R;
import com.example.studyhotspot.Control.URLReader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SessionDetails allows the user to view the relevant information whenever he selects on a session.
 * It displays the following information:
 * <p>1. Session Title</p>
 * <p>2. Creator Name</p>
 * <p>3. Description</p>
 * <p>4. Start Date & Time</p>
 * <p>5. End Date & Time</p>
 * Furthermore, it will allow user to view the participant status.
 * Additionally, it will create a link for the user to click on, if they need help navigating to the displayed location.
 */
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

    int position;

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
            position = getIntent().getIntExtra("position", -1);
        } else {
            Toast.makeText(SessionDetails.this, "ERROR", Toast.LENGTH_LONG).show();
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
                locationName = documentSnapshot.getString(KEY_LOCATION);
            }
        });
    }

    private void setUpContent(int time){
        if (time > 5){
            Toast.makeText(SessionDetails.this, "PLEASE TRY AGAIN", Toast.LENGTH_LONG).show();
            finish();
        }
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

        if (status != null){
            leave_session.setClickable(false);

            if(status.contentEquals("Status: Past")) {
                leave_session.setBackgroundColor(0xFFF0F0F0);
                leave_session.setText("PAST");
            }
            else if(status.contentEquals("Status: Rejected")) {
                leave_session.setBackgroundColor(0xFFFFBBBB);
                leave_session.setText("REJECTED");
            }
            else if (status.contentEquals("friend")){
                leave_session.setBackgroundColor(0xFFDCF8C6);
                leave_session.setText("FRIEND ACTIVITY");
                leave_session.setTextSize(16);
            }
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
                            Toast.makeText(SessionDetails.this, "LEAVING...",
                                    Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("position",position);
                            System.out.println(position);
                            setResult(RESULT_OK,returnIntent);
                            finish();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SessionDetails.this, "REQUEST CANCELLED",
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
