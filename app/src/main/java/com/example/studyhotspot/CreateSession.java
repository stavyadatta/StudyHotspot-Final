package com.example.studyhotspot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateSession extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener{

    EditText editTitle;
    EditText editDescription;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mStartTime;
    private TextView mEndTime;
    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private EditText mSessionParticipants;

    boolean isStartTime = true;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        db = FirebaseFirestore.getInstance();

        editTitle = findViewById(R.id.title);
        editDescription = findViewById(R.id.description);

        final String TAG = "CreateSession";

        mStartDate = (TextView) findViewById(R.id.selectstartdateT);
        mEndDate = (TextView) findViewById(R.id.selectenddateT);
        mStartTime = (TextView) findViewById(R.id.selectstarttimeT);
        mEndTime = (TextView) findViewById(R.id.selectendtimeT);
        mSessionParticipants = (EditText) findViewById(R.id.sessionparticipants);

        findViewById(R.id.submitBtn).setOnClickListener(this);

        mSessionParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sessionParticipants = Integer.valueOf(mSessionParticipants.getText().toString());
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

                String date = day + "/" + month + "/" + year;
                mStartDate.setText(date);
            }
        };

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mEndDate.setText(date);
            }
        };
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String h = String.format("%02d", hourOfDay);
        String m = String.format("%02d", minute);
        if(isStartTime==true){
            mStartTime.setText(h +":"+ m);
        } else {
            mEndTime.setText(h +":"+ m);
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


        if (!hasValidationErrors(title)) {


            CollectionReference dbSessions = db.collection("sessions");

            Session session = new Session(
                    title, description
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
        switch(v.getId()){
            case R.id.submitBtn:
                saveSession();
                break;
        }
    }

}
