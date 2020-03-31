package com.example.studyhotspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class History extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener {

    private Button backButton;
    Activity activityFG = new Activity("Friday Grind", "006","Completed","1200","1500", "Lisa Ong");
    Activity activityES = new Activity("Eat N Study", "007","Cancelled","1700","1900", "Ben Chong");

    ArrayList<Activity> listCCActivity = new ArrayList<Activity>();

    private ArrayList<String> Name = new ArrayList<>();
    private ArrayList<String> ImageUrls = new ArrayList<>();
    private ArrayList<String> fCC1 = new ArrayList<>();
    private ArrayList<String> fCC2 = new ArrayList<>();

    private static final String TAG = "HistoryPage";
    private FloatingActionButton homeButton;
    private BottomAppBar bottomAppBar;

    private String previousActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listCCActivity.add(activityES);
        listCCActivity.add(activityFG);


        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
        }

        backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToActivities();
            }
        });

        setUpBottomAppBar();
        initImageBitmaps();

    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for (int i =0;i<listCCActivity.size();i++){
            ImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
            Name.add(listCCActivity.get(i).getname());
            fCC1.add("Created by: " + listCCActivity.get(i).getCreator());
            fCC2.add("Status: " + listCCActivity.get(i).getStatus());
            if (listCCActivity.get(i).getStatus() == "Completed") {

            }


        }
        initRecyclerView();


    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerviewhist);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(Name, ImageUrls, fCC1, fCC2, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void backToActivities() {
        Intent intent = new Intent(this, ActivityPageMain.class);
        startActivity(intent);
    }

    private void setUpBottomAppBar() {
        //find id
        homeButton = findViewById(R.id.homeButton);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")){
                    Intent intent = new Intent(History.this, FindFriend.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Activities")){
                    Toast.makeText(History.this, "Activity Page", Toast.LENGTH_LONG).show();
                }
                else if (title.contentEquals("Settings")){
                    //Intent intent = new Intent(MapsActivity.this, )
                }

                return false;
            }});

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousActivity != null && previousActivity.contentEquals("HOME")){
                    finish();
                }
                else {
                    Intent intent = new Intent(History.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, detailsPage.class);
        startActivity(intent);
    }
}
