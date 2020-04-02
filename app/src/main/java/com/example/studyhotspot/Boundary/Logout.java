package com.example.studyhotspot.Boundary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studyhotspot.Control.ActivityPageMain;
import com.example.studyhotspot.Control.FindFriend;
import com.example.studyhotspot.Control.MapsActivity;
import com.example.studyhotspot.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Logout extends AppCompatActivity {
    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String currentUser;
    String userEmail;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;
    private String previousActivity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        setUpBottomAppBar();
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
            currentUser = extras.getString("currentUser");
            userID = extras.getString("currentUID");
            userEmail = extras.getString("userEmail");
        }

        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });
    }
    private void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.d("signing out", "ready to start new");
        startActivity(intent);
    }

    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        homeButton = findViewById(R.id.homeButton);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")) {
                    Intent intent = new Intent(Logout.this, FindFriend.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(Logout.this, ActivityPageMain.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    Toast.makeText(Logout.this, "Settings Page", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previousActivity != null && previousActivity.contentEquals("HOME")){
                    finish();
                }
                else{
                    Intent intent = new Intent(Logout.this, MapsActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}
