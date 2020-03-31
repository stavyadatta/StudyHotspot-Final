package com.example.studyhotspot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CreateSession extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener{

    EditText editTitle;
    EditText editDescription;
    private TextView locationName;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mStartTime;
    private TextView mEndTime;
    private ImageView back;
    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TextView mSessionParticipants;
    private Switch mSwitchPublic;
    private FloatingActionButton addParticipants;

    private FloatingActionButton sessionUpload;
    private BottomAppBar bottomAppBar;

    private UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    final int LAUNCH_ADD_PARTICIPANTS = 1;


    String location = null;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    boolean isPublic;

    ArrayList<String> creatorNameRaw = new ArrayList<>();
    ArrayList<String> participants = new ArrayList<>();
    static Map<String, Boolean> participantStatus = new HashMap<String, Boolean>();

    boolean isStartTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        getCreatorName();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            location = extras.getString("Name");
        }

        setUpBottomAppBar();
        setUpDetailsPage();
    }

    private void getCreatorName(){
        userDatabaseManager.getCurrentUsername(creatorNameRaw);

        /*firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        creatorID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(creatorID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                creatorEmail = documentSnapshot.getString("email");
            }
        });


        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        if(email.contentEquals(creatorEmail)){
                            creatorName = document.getString("fName");
                            participantStatus.put(creatorName, true);
                        }
                    }
                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });*/
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String h = String.format("%02d", hourOfDay);
        String m = String.format("%02d", minute);
        if(isStartTime==true){
            startTime = h +":"+ m;
            mStartTime.setText(startTime);
        } else {
            endTime = h +":"+ m;
            mEndTime.setText(endTime);
        }
    }

    // Check if error in content
    private boolean hasValidationErrors(String title) {
        if (title.isEmpty()) {
            editTitle.setError("Title required.");
            editTitle.requestFocus();
            return true;
        }
        return false;
    }

    private void saveSession() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        String creatorName = creatorNameRaw.get(0);
        participantStatus.put(creatorName, true);

        System.out.println("DEBUG 1");

        //Date Time
        SimpleDateFormat dateFormatter =new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        String startDateTimeString = startDate + " " + startTime;
        String endDateTimeString = endDate + " " + endTime;
        Log.d("PrintStartDate","startDateTimeString: " + startDateTimeString);
        Log.d("PrintEndDate","endDateTimeString: " + endDateTimeString);
        Log.d("PrintTimezone","timezone: "+  System.getProperty("user.timezone"));
        Log.d("PrintTimezone","timezone: "+ ZonedDateTime.now());

        Date startDateFormatted = new Date();
        Date endDateFormatted = new Date();
        
        try {
            startDateFormatted = dateFormatter.parse(startDateTimeString);
            endDateFormatted = dateFormatter.parse(endDateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG 4");

        
        Timestamp startDateTimeTimestamp = new Timestamp(startDateFormatted);
        Timestamp endDateTimeTimestamp = new Timestamp(endDateFormatted);

        System.out.println(title);
        System.out.println(description);
        System.out.println(creatorName);
        System.out.println(startDateTimeTimestamp);
        System.out.println(endDateTimeTimestamp);
        System.out.println(participantStatus);
        System.out.println(isPublic);


        if (!hasValidationErrors(title)) {
            Session newSession = new Session(
                    title, description, creatorName, location, startDateTimeTimestamp, endDateTimeTimestamp, participantStatus, isPublic
            );

            userDatabaseManager.addSession(newSession);
        }
    }

    @Override
    public void onClick(View v) {
        //
    }

    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        sessionUpload = findViewById(R.id.uploadSession);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")){
                    Intent intent = new Intent(CreateSession.this, FindFriend.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Activities")){
                    Intent intent = new Intent(CreateSession.this, ActivityPageMain.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Settings")){
                    Intent intent = new Intent(CreateSession.this, Logout.class);
                    startActivity(intent);
                }
                return false;
            }});

        sessionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSession();
                Intent intent = new Intent(CreateSession.this, ActivityPageMain.class);
                startActivity(intent);
            }
        });
    }

    public void setUpDetailsPage(){
        locationName = findViewById(R.id.locationPlaceHolder);
        locationName.setText(location);

        editTitle = findViewById(R.id.title);
        editDescription = findViewById(R.id.description);

        final String TAG = "CreateSession";

        mStartDate = findViewById(R.id.selectstartdateT);
        mEndDate = findViewById(R.id.selectenddateT);
        mStartTime = findViewById(R.id.selectstarttimeT);
        mEndTime = findViewById(R.id.selectendtimeT);
        mSessionParticipants = findViewById(R.id.sessionparticipants);
        mSessionParticipants.setText(Html.fromHtml("<u>" + "1" + "</u>" ));
        mSwitchPublic = findViewById(R.id.switchBtn);

        back = findViewById(R.id.back_button);
        addParticipants = findViewById(R.id.addParticipants);


        mSwitchPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchPublic.isChecked()) {
                    isPublic = true;
                }
                else
                    isPublic = false;
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = false;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mStartDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mEndDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                startDate = day + "/" + month + "/" + year;
                mStartDate.setText(startDate);
            }
        };

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                endDate = day + "/" + month + "/" + year;
                mEndDate.setText(endDate);
            }
        };

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateSession.this, SessionAddParticipants.class);
                intent.putExtra("Participants", participants);
                startActivityForResult(intent, LAUNCH_ADD_PARTICIPANTS);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD_PARTICIPANTS) {
            if(resultCode == RESULT_OK){
                participants = data.getStringArrayListExtra("Participants");

                participantStatus.clear();

                for (int i = 0; i<participants.size(); i++){
                    participantStatus.put(participants.get(i), null);
                }

                mSessionParticipants.setText(Html.fromHtml("<u>" +(participantStatus.size()+1) + "</u>" ));
                System.out.println(participantStatus);
            }
        }
    }

}
