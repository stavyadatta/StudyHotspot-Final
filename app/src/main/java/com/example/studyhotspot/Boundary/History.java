package com.example.studyhotspot.Boundary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyhotspot.R;
import com.example.studyhotspot.Control.ActivityPageMain;
import com.example.studyhotspot.Control.FindFriend;
import com.example.studyhotspot.Control.MapsActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * History retrieves a list of sessions that the user has participated in the past, and the information relevant
 * to these sessions. After that, History uses a RecyclerViewAdapter to display these sessions to the user.
 */
public class History extends AppCompatActivity {

    private ImageView backButton;

    private ArrayList<String> historyNames = new ArrayList<>();
    private ArrayList<String> historyCreators = new ArrayList<>();
    private ArrayList<String> historyIDs = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<String> historyStatus = new ArrayList<String>();
    private String currentUser;
    private String userID;
    private String userEmail;

    private static final String TAG = "HistoryPage";

    private String previousActivity = null;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setUpBottomAppBar();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
            historyIDs = extras.getStringArrayList("IDs");
            historyNames = extras.getStringArrayList("Names");
            historyCreators = extras.getStringArrayList("Creators");
            historyStatus = extras.getStringArrayList("Status");
            currentUser = extras.getString("currentUser");
            userID = extras.getString("currentUID");
            userEmail = extras.getString("userEmail");
        }

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initImageBitmaps();
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for (int i = 0; i < historyIDs.size(); i++){
            images.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerviewhist);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(historyNames, images, historyStatus, historyCreators, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * showPastSessionInfo() allows user to view the details of a selected session that has passed
     * @param position
     */
    public void showPastSessionInfo(int position) {
        Intent intent = new Intent(this, SessionDetails.class);
        intent.putExtra("docname", historyIDs.get(position));
        intent.putExtra("Status", historyStatus.get(position));
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
                    Intent intent = new Intent(History.this, FindFriend.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(History.this, ActivityPageMain.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    Intent intent = new Intent(History.this, Logout.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                }

                return false;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
