package com.example.studyhotspot;

import androidx.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ActivityPageMain extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener {
    Activity activitySS = new Activity("Study @ Starbucks", "001", "Ongoing", "1100","1200", "Chris Johnson");
    Activity activityCU = new Activity("Catch Up", "002", "Upcoming", "1100", "1200", "Lia Palosanu");
    Activity activityEP = new Activity("Exam Prep", "003", "Upcoming", "1100", "1200", "Mike Lee");
    Activity activityOS = new Activity("Open Study!", "004", "Upcoming", "1100","1200","Alvin Choo");
    Activity activityCP = new Activity("Chemistry Practice!", "005", "Upcoming", "1100", "1200", "McCoy Tan");
    ArrayList<Activity> listMSActivity = new ArrayList<Activity>();
    ArrayList<Activity> listIActivity = new ArrayList<>();
    ArrayList<Activity> listFAActivity = new ArrayList<>();


    private FloatingActionButton homeButton;
    private BottomAppBar bottomAppBar;
    private Button historyButton;

    //RecyclerView stuffs
    private static final String TAG = "ActivityPageMain";

    //for 1st box

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> fMS1 = new ArrayList<>();
    private ArrayList<String> fMS2 = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> id1 = new ArrayList<>();

    //for 2nd box
    private ArrayList<String> mNames2 = new ArrayList<>();
    private ArrayList<String> mImageUrls2 = new ArrayList<>();
    private ArrayList<String> fI1 = new ArrayList<>();
    private ArrayList<String> mImageUrls22 = new ArrayList<>();
    private ArrayList<String> mImageUrls23 = new ArrayList<>();

    //for 3rd box
    private ArrayList<String> mNames3 = new ArrayList<>();
    private ArrayList<String> mImageUrls3 = new ArrayList<>();
    private ArrayList<String> fFA1 = new ArrayList<>();

    private String previousActivity = null;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        listMSActivity.add(activitySS);
        listMSActivity.add(activityCU);
        listIActivity.add(activityEP);
        listFAActivity.add(activityOS);
        listFAActivity.add(activityCP);
        db = FirebaseFirestore.getInstance();





        setUpBottomAppBar();
        initImageBitmaps();
        initImageBitmaps2();
        initImageBitmaps3();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
        }

        historyButton = (Button) findViewById(R.id.history);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistory();
            }
        });


    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");
        db.collection("sessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int i = 0;
                    for (QueryDocumentSnapshot document:task.getResult()){
                        id1.add(document.getId());
                        mNames.add(document.getString("title"));
                        mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
                        fMS1.add("Status: Testing");
                        fMS2.add("Created by: Testing");
                        Log.v(TAG, "this is adding id of document" + id1.get(i++));
                        Log.d(TAG, "onComplete: " + id1.size());
                        initRecyclerView();
                    }
                }
            }
        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: the last of titles are:" + mNames.get(mNames.size()-1));
        if (mNames.size() != id1.size())
            return;
        Log.d(TAG, "1initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerview1);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mImageUrls, fMS1, fMS2, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initImageBitmaps2(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for (int i =0;i<listIActivity.size();i++){
            mImageUrls2.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
            mNames2.add(listIActivity.get(i).getname());
            fI1.add("Created by: " + listIActivity.get(i).getCreator());
            mImageUrls22.add("https://png2.cleanpng.com/sh/cb26fdf957d05d2f15daec63603718fb/L0KzQYm3UsE1N5D6iZH0aYP2gLBuTfNpbZRwRd9qcnuwc73wkL1ieqUyfARuZX6whLrqi71uaaNwRadqOETkSIftUMk6bpc9RqI5OUC3SIa8UcUyQGc5S6U6MUC2SYW1kP5o/kisspng-check-mark-clip-art-green-tick-mark-5a84a86f099ff8.0090485515186433110394.png");
            mImageUrls23.add("https://png2.cleanpng.com/sh/a003283ac6c66b520295b049d5fa5daf/L0KzQYm3VMA0N5puiZH0aYP2gLBuTfNpbZRwRd9qcnuwc7F0kQV1baMygdV4boOwg8r0gv9tNahmitDybnewRbLqU8NnbZRqSqI9YUCxQYq8UMMzQWU2TaQ7N0S4Q4O7WcI2QF91htk=/kisspng-check-mark-computer-icons-symbol-warning-5ac33fece204a0.1950329415227453249258.png");

        }
        initRecyclerView2();

    }

    private void initRecyclerView2(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView2 = findViewById(R.id.recyclerview2);
        RecyclerViewAdapter2 adapter2 = new RecyclerViewAdapter2(mNames2, mImageUrls2, fI1, mImageUrls22, mImageUrls23, this);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initImageBitmaps3(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for (int i =0;i<listFAActivity.size();i++){
            mImageUrls3.add("https://upload.wikimedia.org/wikipedia/commons/2/25/Icon-round-Question_mark.jpg");
            mNames3.add(listFAActivity.get(i).getname());
            fFA1.add("Created by: " + listFAActivity.get(i).getCreator());



        }
        initRecyclerView3();

    }

    private void initRecyclerView3(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView3 = findViewById(R.id.recyclerview3);
        RecyclerViewAdapter3 adapter3 = new RecyclerViewAdapter3(mNames3, mImageUrls3, fFA1, this);
        recyclerView3.setAdapter(adapter3);
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
                if (title.contentEquals("Friends")){
                    Intent intent = new Intent(ActivityPageMain.this, FindFriend.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Activities")){
                    Toast.makeText(ActivityPageMain.this, "Activity Page", Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(ActivityPageMain.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void openHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    //From actvity page to activity info page, to pass in document id
    @Override
    public void onNoteClick(int position) {
        Log.v(TAG, "OnNoteClicked:" + id1.get(1)); //id is still retrievable
        Intent intent = new Intent(this, SessionDetails.class);
        intent.putExtra("docname", id1.get(position));
        startActivity(intent);
    }

}
