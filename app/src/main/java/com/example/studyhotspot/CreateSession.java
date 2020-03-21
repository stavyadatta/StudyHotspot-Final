package com.example.studyhotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateSession extends AppCompatActivity implements View.OnClickListener{

    EditText editTitle;
    EditText editDescription;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        db = FirebaseFirestore.getInstance();

        editTitle = findViewById(R.id.title);
        editDescription = findViewById(R.id.description);

        findViewById(R.id.submitBtn).setOnClickListener(this);
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
