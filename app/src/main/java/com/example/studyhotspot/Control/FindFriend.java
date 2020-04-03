package com.example.studyhotspot.Control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyhotspot.R;
import com.example.studyhotspot.Boundary.Logout;
import com.example.studyhotspot.Boundary.RecyclerViewUserAdapter;
import com.example.studyhotspot.Boundary.ViewRequest;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * FindFriend will retrieve the user's social network information, and uses RecyclerViewUserAdapter to display
 * the users belonging to this network accordingly.
 *
 * <p>FindFriend will also interact with UserDatabaseManager to run checks whenever user attempts to add a friend.</p>
 * <p>If the user attempts to send a request to another user, who has already sent him a request between the
 * time he loaded the FindFriend page and clicked on the add button, FindFriend will prompt the user to refresh the page.</p>
 */
public class FindFriend extends AppCompatActivity {
    ArrayList<String> namelist;
    ArrayList<String> emaillist;
    ArrayList<String> addedFriendList;
    ArrayList<String> addingFriendList;
    ArrayList<Integer> relationshipFriendList;

    ArrayList<String> awaitingFriendList = new ArrayList<String>();
    ArrayList<String> awaitingFriendName = new ArrayList<String>();

    String userID;
    String currentUser;
    String userEmail;
    private Button mViewRequestBtn;

    private UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;
    private FloatingActionButton refresh;
    private RecyclerView recyclerView;
    private TextView requestCount;

    private String previousActivity = null;
    final int VIEW_REQUEST = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        context = this;

        setUpBottomAppBar();
        setUpRequestBtn();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
            currentUser = extras.getString("currentUser");
            userID = extras.getString("currentUID");
            userEmail = extras.getString("userEmail");
        }

        userID = userDatabaseManager.getCurrentUserID();
        userEmail = userDatabaseManager.getCurrentUserEmail();
        initRecyclerView();
    }

    private void setUpRequestBtn() {
        mViewRequestBtn = findViewById(R.id.viewRequestBtn);
        mViewRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindFriend.this, ViewRequest.class);
                startActivityForResult(intent, VIEW_REQUEST);
            }
        });

        requestCount = findViewById(R.id.requestCount);

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setClickable(false);
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView(){
        resetLists();

        userDatabaseManager.getUserAddedAddingFriends(addedFriendList, addingFriendList,
                relationshipFriendList, namelist, emaillist);
        userDatabaseManager.getUserAwaitingFriends(awaitingFriendList,awaitingFriendName);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                refreshRecyclerView(0);
            }
        }, 1000);
    }

    private void refreshRecyclerView(int times){
        if (times < 2 && (namelist.isEmpty() || awaitingFriendList.isEmpty())){
            Toast.makeText(FindFriend.this, "LOADING...", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    refreshRecyclerView(times+1);
                }
            }, 1000);
        }
        Log.d("Recycler Users", "initRecyclerView: init recyclerview.");
        recyclerView = findViewById(R.id.recyclerUsers);
        RecyclerViewUserAdapter adapter = new RecyclerViewUserAdapter(namelist, emaillist, relationshipFriendList, userDatabaseManager,
                this, FindFriend.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FindFriend.this));
        refreshRequestCount();
        refresh.setClickable(true);
    }

    private void refreshRequestCount(){
        if (awaitingFriendList.size()>0){
            requestCount.setText(""+awaitingFriendList.size());
            requestCount.setBackground(getResources().getDrawable(R.drawable.circle_textview));
        }
        else{
            requestCount.setText("");
            requestCount.setBackground(getResources().getDrawable(R.drawable.transparent));
        }
    }

    private void resetLists(){
        addedFriendList = new ArrayList<String>();
        addingFriendList = new ArrayList<String>();
        relationshipFriendList = new ArrayList<Integer>();
        namelist = new ArrayList<String>();
        emaillist = new ArrayList<String>();
        awaitingFriendList = new ArrayList<String>();
        awaitingFriendName = new ArrayList<String>();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                initRecyclerView();
            }
        }
    }

    /**
     * FindFriend's addFriend() occurs whenever an user attempts to add another user as friend.
     * This guards against scenarios where one user sent request to another user that has just sent him a request earlier.
     * By doing this, it helps enforce the rule that the user can only send friend request if no request has been sent from either party.
     * @param userID UID of current user
     * @param targetEmail email address of the user that the current user wishes to add
     */
    public void addFriend(String userID, String targetEmail){
        resetLists();
        userDatabaseManager.getUserAwaitingFriends(awaitingFriendList,awaitingFriendName);
        userDatabaseManager.getUserAddedAddingFriends(addedFriendList, addingFriendList,
                relationshipFriendList, namelist, emaillist);

        Toast.makeText(FindFriend.this, "PROCESSING...", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Boolean result = checkIfCanAdd(targetEmail);
                if (result == null){
                    Toast.makeText(FindFriend.this, "ERROR: TRY AGAIN", Toast.LENGTH_SHORT).show();
                    refreshRecyclerView(0);
                }
                else if (result){
                    Toast.makeText(FindFriend.this, "CAN BE ADDED", Toast.LENGTH_SHORT).show();
                    userDatabaseManager.addFriend(userID, targetEmail);
                }
                else if (!result){
                    Toast.makeText(FindFriend.this, "CHECK REQUESTS", Toast.LENGTH_SHORT).show();
                    refreshRecyclerView(0);
                }
            }
        }, 2000);
    }

    private Boolean checkIfCanAdd(String targetEmail){
        if (awaitingFriendList.isEmpty() && addedFriendList.isEmpty() && addingFriendList.isEmpty()){
            return null;
        }
        else{
                if (awaitingFriendList.contains(targetEmail)) {
                    return false;
                } else {
                    return true;
                }
            }
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
                    Toast.makeText(FindFriend.this, "Social Page", Toast.LENGTH_LONG).show();
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(FindFriend.this, ActivityPageMain.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    Intent intent = new Intent(FindFriend.this, Logout.class);
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
                if (previousActivity != null && previousActivity.contentEquals("HOME")){
                    finish();
                }
                else {
                    Intent intent = new Intent(FindFriend.this, MapsActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}