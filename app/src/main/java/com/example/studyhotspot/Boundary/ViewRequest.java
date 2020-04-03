package com.example.studyhotspot.Boundary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.studyhotspot.Control.UserDatabaseManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * ViewRequest retrieves a list of users who have sent the current user friend requests, and their
 * names. ViewRequest then displays these information to the current user using RecyclerViewRequestAdapter.
 */
public class ViewRequest extends AppCompatActivity implements RecyclerViewRequestAdapter.OnItemClickListener{

    ArrayList<String> awaitingFriendList = new ArrayList<String>();
    ArrayList<String> awaitingFriendNameList = new ArrayList<String>();


    String userID;
    public UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;
    private FloatingActionButton refresh;

    ImageView accept;
    ImageView reject;
    ImageView back;

    RecyclerViewRequestAdapter adapter;
    RecyclerView recyclerView;


    private String previousActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        setUpBottomAppBar();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
        }

        back = findViewById(R.id.back_button);
        refresh = findViewById(R.id.refresh);

        userID = userDatabaseManager.getCurrentUserID();
        initRecyclerView();

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setClickable(false);
                initRecyclerView();
            }
        });
    }


    /**
     * initRecyclerView() displays the friend request(s) that the user has received
     */
    public void initRecyclerView(){
        Log.d("initRecyclerView", "initRecyclerView");
        awaitingFriendList = new ArrayList<String>();
        awaitingFriendNameList = new ArrayList<String>();
        userDatabaseManager.getUserAwaitingFriends(awaitingFriendList,awaitingFriendNameList);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                refreshRecyclerView();
            }
        }, 1500);
    }

    private void refreshRecyclerView(){
        recyclerView = findViewById(R.id.recyclerRequests);
        adapter = new RecyclerViewRequestAdapter(awaitingFriendNameList, awaitingFriendList, this, this, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refresh.setClickable(true);
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
                    Intent intent = new Intent(ViewRequest.this, FindFriend.class);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(ViewRequest.this, ActivityPageMain.class);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    Intent intent = new Intent(ViewRequest.this, Logout.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewRequest.this, MapsActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        System.out.println("CLICKED IN HERE");
        awaitingFriendList.remove(position);
        awaitingFriendNameList.remove(position);
        initRecyclerView();
    }
}