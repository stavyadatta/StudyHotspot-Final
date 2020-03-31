package com.example.studyhotspot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SessionAddParticipants extends AppCompatActivity implements RecyclerViewAddParticipantAdapter.OnItemClickListener{

    private static final String TAG = "SESSIONADDParticipants";

    ArrayList<String> addedFriendList = new ArrayList<String>();
    ArrayList<String> friendNameList = new ArrayList<>();
    private ArrayList<String> participants = new ArrayList<String>();

    //FirebaseFirestore firebaseFirestore;
    private UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    private String userID;
    private String userName;
    private String userEmail;

    private TextView participantCount;
    private FloatingActionButton addParticipants;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;
    private FloatingActionButton refresh;

    private RecyclerView recyclerView;
    private RecyclerViewAddParticipantAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_add_participants);

        setUpFriends();
        setUpBottomAppBar();
        setUpContent();
    }


    private void setUpFriends(){
        userID = userDatabaseManager.getCurrentUserID();
        userEmail = userDatabaseManager.getCurrentUserEmail();
        userDatabaseManager.getUserAddedFriends(friendNameList);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initRecyclerView();
            }
        }, 1500);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerParticipants);
        adapter = new RecyclerViewAddParticipantAdapter(friendNameList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        homeButton = findViewById(R.id.homeButton);
        refresh = findViewById(R.id.refresh);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")) {
                    Intent intent = new Intent(SessionAddParticipants.this, ActivityPageMain.class);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(SessionAddParticipants.this, ActivityPageMain.class);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    //Intent intent = new Intent(MapsActivity.this, )
                }

                return false;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionAddParticipants.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRecyclerView();
            }
        });
    }

    private void setUpContent(){
        participantCount = findViewById(R.id.participantCount);
        addParticipants = findViewById(R.id.addParticipants);

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Participants",participants);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: "+position);
        if (participants.contains(friendNameList.get(position))){
            participants.remove(friendNameList.get(position));
        }
        else {
            participants.add(friendNameList.get(position));
        }
        System.out.println(participants);
        participantCount.setText(""+participants.size());
    }
}