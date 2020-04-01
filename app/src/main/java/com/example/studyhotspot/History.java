package com.example.studyhotspot;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private ImageView backButton;

    private ArrayList<String> historyNames = new ArrayList<>();
    private ArrayList<String> historyCreators = new ArrayList<>();
    private ArrayList<String> historyIDs = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<String> historyStatus = new ArrayList<String>();

    private static final String TAG = "HistoryPage";

    private String previousActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
            historyIDs = extras.getStringArrayList("IDs");
            historyNames = extras.getStringArrayList("Names");
            historyCreators = extras.getStringArrayList("Creators");
            historyStatus = extras.getStringArrayList("Status");
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

    public void showPastSessionInfo(int position) {
        Intent intent = new Intent(this, SessionDetails.class);
        intent.putExtra("docname", historyIDs.get(position));
        intent.putExtra("Status", historyStatus.get(position));
        startActivity(intent);
    }
}
