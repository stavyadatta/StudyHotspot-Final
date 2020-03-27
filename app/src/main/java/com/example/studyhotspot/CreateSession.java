package com.example.studyhotspot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
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
    private EditText mSessionParticipants;
    private Switch mSwitchPublic;
    private FloatingActionButton addParticipants;

    private FloatingActionButton sessionUpload;
    private BottomAppBar bottomAppBar;


    String startDate;
    String startTime;
    String endDate;
    String endTime;
    int sessionParticipants;
    boolean isPublic;

    boolean isStartTime = true;

    FirebaseFirestore db;

    String name = null;
    String coord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            name = extras.getString("Name");
            coord = extras.getString("Coord");
        }

        setUpBottomAppBar();
        setUpDetailsPage();
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

        
        Timestamp startDateTimeTimestamp = new Timestamp(startDateFormatted);
        Timestamp endDateTimeTimestamp = new Timestamp(endDateFormatted);


        if (!hasValidationErrors(title)) {


            CollectionReference dbSessions = db.collection("sessions");

            Session session = new Session(
                    title, description, startDateTimeTimestamp, endDateTimeTimestamp, sessionParticipants, isPublic
            );

            dbSessions.add(session)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(CreateSession.this, "Session Added", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateSession.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
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
                if (title.contentEquals("Fav")){
                    //
                }
                else if (title.contentEquals("Activities")){
                    Intent intent = new Intent(CreateSession.this, ActivityPageMain.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Settings")){
                    //Intent intent = new Intent(MapsActivity.this, )
                }
                return false;
            }});
    }

    public void setUpDetailsPage(){
        locationName = findViewById(R.id.locationPlaceHolder);
        locationName.setText(name);

        editTitle = findViewById(R.id.title);
        editDescription = findViewById(R.id.description);

        final String TAG = "CreateSession";

        mStartDate = findViewById(R.id.selectstartdateT);
        mEndDate = findViewById(R.id.selectenddateT);
        mStartTime = findViewById(R.id.selectstarttimeT);
        mEndTime = findViewById(R.id.selectendtimeT);
        mSessionParticipants = findViewById(R.id.sessionparticipants);
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

        mSessionParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionParticipants = Integer.valueOf(mSessionParticipants.getText().toString());
                Log.d("NUMBER","Successful number input");
                mSessionParticipants.setText(String.valueOf(sessionParticipants));
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

        sessionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSession();
                Intent intent = new Intent(CreateSession.this, ActivityPageMain.class);
                startActivity(intent);
            }
        });

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add participants
            }
        });
    }

}
