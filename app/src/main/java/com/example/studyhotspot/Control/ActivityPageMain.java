package com.example.studyhotspot.Control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.studyhotspot.Boundary.InvitationPage;
import com.example.studyhotspot.R;
import com.example.studyhotspot.Boundary.History;
import com.example.studyhotspot.Boundary.SessionDetails;
import com.example.studyhotspot.Boundary.Logout;
import com.example.studyhotspot.Boundary.RecyclerViewAdapter;
import com.example.studyhotspot.Boundary.RecyclerViewAdapter2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * ActivityPageMain retrieves all the sessions in the database and applies the relevant filter logic to obtain the following 3 lists:
 * <p>1. List of sessions that user is currently participating / going to participate in, which are further divided into</p>
 * <ul>
 * <li>1a. List of upcoming sessions</li>
 * <li>1b. List of ongoing sessions</li>
 * </ul>
 * <p>Note that for sessions which have already passed, they will not be shown here, as they will be displayed in session history.</p>
 * <p>2. List of sessions that the user is invited to.</p>
 * <p>3. List of upcoming / ongoing sessions that the user's friends is participating in. This allows
 * an user to 'follow' his friends and see what they are doing.</p>
 * <p> This is also where most of the Glide is used, as the images are sourced online using the given
 * url
 * </p>
 * <p> RecyclerViews work by initialising first the required arrays of information using initBitmaps. Afterwards, it
 * calls initRecyclerviews to create the adapters for the views
 * </p>
 */
public class ActivityPageMain extends AppCompatActivity {

    private UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    String userID;
    String currentUser;
    String userEmail;

    String startDate;
    String startTime;


    private FloatingActionButton homeButton;
    private BottomAppBar bottomAppBar;
    private Button historyButton;
    private FloatingActionButton refresh;

    //RecyclerView stuffs
    private static final String TAG = "ActivityPageMain";
    private static final int VIEW_INVITE = 1;
    private static final int VIEW_INFO = 2;

    //for 1st box

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> fMS1 = new ArrayList<>();
    private ArrayList<String> fMS2 = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> id1 = new ArrayList<>();

    //History
    private ArrayList<String> historyNames = new ArrayList<>();
    private ArrayList<String> historyCreators = new ArrayList<>();
    private ArrayList<String> historyIDs = new ArrayList<String>();
    private ArrayList<String> historyStatus = new ArrayList<String>();

    //for 2nd box
    private ArrayList<String> mNames2 = new ArrayList<>();
    private ArrayList<String> mImageUrls2 = new ArrayList<>();
    private ArrayList<String> fI1 = new ArrayList<>();
    private ArrayList<String> mImageUrls22 = new ArrayList<>();
    private ArrayList<String> mImageUrls23 = new ArrayList<>();
    private ArrayList<String> id2 = new ArrayList<>();

    //for 3rd box
    private ArrayList<String> friendNames = new ArrayList<>();
    private ArrayList<String> friendImageUrls2 = new ArrayList<>();
    private ArrayList<String> friendStatus = new ArrayList<>();
    private ArrayList<String> friendImages = new ArrayList<>();
    private ArrayList<String> friendIDs = new ArrayList<>();
    private ArrayList<String> friendCreators = new ArrayList<>();

    private String previousActivity = null;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            previousActivity = extras.getString("prevActivity");
            currentUser = extras.getString("currentUser");
            userID = extras.getString("currentUID");
            userEmail = extras.getString("userEmail");
        }

        setUpBottomAppBar();
        setUpContent();
    }

    private void setUpContent() {
        historyButton = findViewById(R.id.history);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistory();
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityPageMain.this, "REFRESHING", Toast.LENGTH_LONG).show();
                System.out.println("REFRESHING");
                initImageBitmaps();
            }
        });

        initImageBitmaps();
    }

    private void initImageBitmaps() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        id1.clear();
        mNames.clear();
        mImageUrls.clear();
        fMS1.clear();
        fMS2.clear();

        historyNames.clear();
        historyIDs.clear();
        historyCreators.clear();
        historyStatus.clear();

        id2.clear();
        mNames2.clear();
        mImageUrls2.clear();
        fI1.clear();
        mImageUrls22.clear();
        mImageUrls23.clear();

        friendNames.clear();
        friendStatus.clear();
        friendImages.clear();
        friendIDs.clear();
        friendCreators.clear();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        String startDateTimeString = startDate + " " + startTime;
        Date current = new Date();
/*        try {
            current = dateFormatter.parse(startDateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        Timestamp currentTS = new Timestamp(current);
        System.out.println(currentTS);


        db.collection("hashsessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    String status;
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        HashMap<String, Boolean> doch = (HashMap<String, Boolean>) document.getData().get("participantStatus");
                        Boolean isPublic = (Boolean) document.getData().get("privateORpublic");

                        Timestamp actTS = new Timestamp(document.getDate("startDateTime"));
                        Timestamp endTS = new Timestamp(document.getDate("endDateTime"));

                        if (doch.containsKey(currentUser)) {

                            if (doch.get(currentUser) != null && doch.get(currentUser) == true) {
                                if (currentTS.compareTo(actTS) < 0) {
                                    status = "Upcoming";
                                    id1.add(document.getId());
                                    mNames.add(document.getString("title"));
                                    mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                                    fMS1.add("Status: " + status);
                                    fMS2.add("Created by: " + document.getString("creatorName"));
                                }
                                else if ((currentTS.compareTo(endTS) < 0 && currentTS.compareTo(actTS) > 0)) {
                                    status = "Ongoing";
                                    id1.add(document.getId());
                                    mNames.add(document.getString("title"));
                                    mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                                    fMS1.add("Status: " + status);
                                    fMS2.add("Created by: " + document.getString("creatorName"));
                                }
                                else if (currentTS.compareTo(endTS) > 0){
                                    status = "Passed";
                                    historyIDs.add(document.getId());
                                    historyNames.add(document.getString("title"));
                                    historyCreators.add("Created by: " + document.getString("creatorName"));
                                    historyStatus.add("Status: Past");
                                }
                            }

                            else if (doch.get(currentUser) == null) {
                                if (currentTS.compareTo(endTS) < 0) {
                                    id2.add(document.getId());
                                    mNames2.add(document.getString("title"));
                                    mImageUrls2.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                                    fI1.add("Created by: " + document.getString("creatorName"));
                                    mImageUrls22.add("https://png2.cleanpng.com/sh/cb26fdf957d05d2f15daec63603718fb/L0KzQYm3UsE1N5D6iZH0aYP2gLBuTfNpbZRwRd9qcnuwc73wkL1ieqUyfARuZX6whLrqi71uaaNwRadqOETkSIftUMk6bpc9RqI5OUC3SIa8UcUyQGc5S6U6MUC2SYW1kP5o/kisspng-check-mark-clip-art-green-tick-mark-5a84a86f099ff8.0090485515186433110394.png");
                                    mImageUrls23.add("https://png2.cleanpng.com/sh/a003283ac6c66b520295b049d5fa5daf/L0KzQYm3VMA0N5puiZH0aYP2gLBuTfNpbZRwRd9qcnuwc7F0kQV1baMygdV4boOwg8r0gv9tNahmitDybnewRbLqU8NnbZRqSqI9YUCxQYq8UMMzQWU2TaQ7N0S4Q4O7WcI2QF91htk=/kisspng-check-mark-computer-icons-symbol-warning-5ac33fece204a0.1950329415227453249258.png");
                                }
                            }

                            else if (doch.get(currentUser) == false) {
                                historyIDs.add(document.getId());
                                historyNames.add(document.getString("title"));
                                historyCreators.add("Created by: " + document.getString("creatorName"));
                                historyStatus.add("Status: Rejected");
                            }
                        }

                        else{
                            if (isPublic){
                                if (currentTS.compareTo(actTS) < 0) {
                                    status = "Upcoming";
                                    friendIDs.add(document.getId());
                                    friendNames.add(document.getString("title"));
                                    friendImages.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                                    friendStatus.add("Status: " + status);
                                    friendCreators.add("Created by: " + document.getString("creatorName"));
                                }
                                else if (currentTS.compareTo(endTS) < 0) {
                                    status = "Ongoing";
                                    friendIDs.add(document.getId());
                                    friendNames.add(document.getString("title"));
                                    friendImages.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                                    friendStatus.add("Status: " + status);
                                    friendCreators.add("Created by: " + document.getString("creatorName"));
                                }
                            }
                        }
                    }
                }
            }
        });
        refreshRecyclerViews(0);
    }

    private void refreshRecyclerViews(int time){
        if (time > 2){
            return;
        }
        if (id1.isEmpty() || historyIDs.isEmpty() || id2.isEmpty() || friendIDs.isEmpty()) {
            Toast.makeText(ActivityPageMain.this, "LOADING...", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    refreshRecyclerViews(time + 1);
                }
            }, 1000);
        }
        initRecyclerView();
        initRecyclerView2();
        initRecyclerView3();
    }

    private void initRecyclerView() {
/*        Log.d(TAG, "initRecyclerView: the last of titles are:" + mNames.get(mNames.size() - 1));
        if (mNames.size() != id1.size())
            return;*/
        Log.d(TAG, "1initRecyclerView: init recyclerview.");

        RecyclerView recyclerView = findViewById(R.id.recyclerview1);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mImageUrls, fMS1, fMS2, this, this, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initRecyclerView2() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView recyclerView2 = findViewById(R.id.recyclerview2);
        RecyclerViewAdapter2 adapter2 = new RecyclerViewAdapter2(mNames2, mImageUrls2, fI1, mImageUrls22, mImageUrls23, this, this);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initRecyclerView3() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView recyclerView3 = findViewById(R.id.recyclerview3);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(friendNames, friendImages, friendStatus, friendCreators, this, this, true);
        recyclerView3.setAdapter(adapter);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
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
                if (title.contentEquals("Friends")) {
                    Intent intent = new Intent(ActivityPageMain.this, FindFriend.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Toast.makeText(ActivityPageMain.this, "Activity Page", Toast.LENGTH_LONG).show();
                } else if (title.contentEquals("Settings")) {
                    Intent intent = new Intent(ActivityPageMain.this, Logout.class);
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
                if (previousActivity != null && previousActivity.contentEquals("HOME")) {
                    finish();
                } else {
                    Intent intent = new Intent(ActivityPageMain.this, MapsActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void openHistory() {
        Intent intent = new Intent(this, History.class);
        intent.putExtra("IDs", historyIDs);
        intent.putExtra("Names", historyNames);
        intent.putExtra("Creators", historyCreators);
        intent.putExtra("Status", historyStatus);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("currentUID", userID);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }
    /**
     * ActivityPageMain's showSessionInfo() controls the logic when the user wants to show the info of the session
     * Friend information is needed depending on whether the user wants to see "My Sessions" or "Friends Activities"
     * By using intent, it allows the page to change from the ActivityPageMain to the SessionDetails.
     * Relevant information such as document name is passed to the next page.
     * @param position refers to the ViewHolder being clicked on in the RecyclerView
     * @param friend refers to whether the user is clicking on "My Sessions" or "Friends Activities"
     */
    //From actvity page to activity info page, to pass in document id

    public void showSessionInfo(int position, Boolean friend) {
        Intent intent = new Intent(this, SessionDetails.class);
        if (!friend) {
            intent.putExtra("docname", id1.get(position));
            intent.putExtra("position", position);
            startActivityForResult(intent, VIEW_INFO);
        }
        else{
            intent.putExtra("docname", friendIDs.get(position));
            intent.putExtra("Status", "friend");
            startActivity(intent);
        }
    }
    /**
     * ActivityPageMain's showInviteInfo controls the logic when the user wants to show the info of an invite session
     * By using intent, it allows the page to change from the ActivityPageMain to the InvitationPage.
     * Relevant information such as document name is passed to the next page.
     * @param position refers to the ViewHolder being clicked on in the RecyclerView
     */

    public void showInviteInfo(int position) {
        Intent intent = new Intent(this, InvitationPage.class);
        intent.putExtra("docname", id2.get(position));
        intent.putExtra("position", position);
        System.out.println(id2.get(position));
        startActivityForResult(intent, VIEW_INVITE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_INVITE) {
            if(resultCode == RESULT_OK){
                Boolean decision = data.getBooleanExtra("Decision", true);
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    processInvitation(position, decision);
                }
                else{
                    Toast.makeText(ActivityPageMain.this, "REQUEST FAILED", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (requestCode == VIEW_INFO){
            if(resultCode == RESULT_OK){
                int position = data.getIntExtra("position", -1);
                System.out.println("INSIDE: "+position);
                if (position != -1) {
                    leaveSession(position);
                }
            }
        }
    }
    /**
     * ActivityPageMain's processInvitation() controls the logic when the user chooses to accept or decline the invitation
     * It reads information from the database using addOnCompleteListener.
     * True if user accepts the invitation. False if the user rejects the invitation.
     * @param position refers to the ViewHolder being clicked on in the RecyclerView
     * @param decision refers to whether the user is clicking on the tick or the cross
     */

    public void processInvitation(int position, Boolean decision){
        String targetID = id2.get(position);

        db.collection("hashsessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (targetID.contentEquals(document.getId())) {
                            HashMap<String, Boolean> doch = (HashMap<String, Boolean>) document.getData().get("participantStatus");
                            if (decision) {
                                doch.replace(currentUser, null, true);
                            }
                            else{
                                doch.replace(currentUser, null, false);
                            }
                            DocumentReference documentRef=document.getReference();
                            documentRef.update("participantStatus", doch).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ActivityPageMain.this, "DONE", Toast.LENGTH_LONG).show();
                                    initImageBitmaps();
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ActivityPageMain.this, "REQUEST FAILED", Toast.LENGTH_LONG).show();
                                    initImageBitmaps();
                                    return;
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * ActivityPageMain's leaveSession() controls the logic when the user chooses to leave the session
     * It reads information from the database using addOnCompleteListener, and then updates the database from
     * true to false
     * @param position refers to the ViewHolder being clicked on in the RecyclerView
     */

    public void leaveSession(int position){
        String targetID = id1.get(position);
        System.out.println(targetID);

        db.collection("hashsessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (targetID.contentEquals(document.getId())) {
                            HashMap<String, Boolean> doch = (HashMap<String, Boolean>) document.getData().get("participantStatus");
                            doch.replace(currentUser, true, false);

                            DocumentReference documentRef=document.getReference();
                            documentRef.update("participantStatus", doch).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ActivityPageMain.this, "LEFT", Toast.LENGTH_LONG).show();
                                    initImageBitmaps();
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ActivityPageMain.this, "REQUEST FAILED", Toast.LENGTH_LONG).show();
                                    initImageBitmaps();
                                    return;
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}

