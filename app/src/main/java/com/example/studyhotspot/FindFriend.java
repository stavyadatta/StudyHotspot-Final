package com.example.studyhotspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    public void setUpRequestBtn() {
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
                refreshRecyclerView();
            }
        }, 1000);
    }

    private void refreshRecyclerView(){
        if (namelist.isEmpty()){
            Toast.makeText(FindFriend.this, "LOADING...", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    refreshRecyclerView();
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

    public void addFriend(String userID, String targetEmail){
        resetLists();
        userDatabaseManager.getUserAwaitingFriends(awaitingFriendList,awaitingFriendName);
        userDatabaseManager.getUserAddedAddingFriends(addedFriendList, addingFriendList,
                relationshipFriendList, namelist, emaillist);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if (checkIfCanAdd(targetEmail)){
                    Toast.makeText(FindFriend.this, "CAN BE ADDED", Toast.LENGTH_SHORT).show();
                    userDatabaseManager.addFriend(userID, targetEmail);
                }
                else{
                    Toast.makeText(FindFriend.this, "CHECK REQUESTS", Toast.LENGTH_SHORT).show();
                    refreshRecyclerView();
                }
            }
        }, 1500);
    }

    private boolean checkIfCanAdd(String targetEmail){
        if (awaitingFriendList.isEmpty()){
            checkIfCanAdd(targetEmail);
        }
        else {
            if (awaitingFriendList.contains(targetEmail)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}