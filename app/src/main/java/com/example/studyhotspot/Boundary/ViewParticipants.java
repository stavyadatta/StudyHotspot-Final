package com.example.studyhotspot.Boundary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studyhotspot.R;

import java.util.ArrayList;
import java.util.Map;

public class ViewParticipants extends AppCompatActivity {

    private TextView title;
    private ImageView back;

    private String titleN;
    private Map<String, Boolean> participantStatus;
    private ArrayList<String> participants = new ArrayList<String>();

    private RecyclerView recyclerView;
    private RecyclerViewParticipantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participants);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
             titleN = extras.getString("Title");
             participantStatus = (Map<String, Boolean>) extras.getSerializable("ParticipantStatus");
        }

        for(String key: participantStatus.keySet()){
            participants.add(key);
        }

        setUpContent();
        initRecyclerView();
    }

    private void setUpContent(){
        title = findViewById(R.id.sessionTitle);
        title.setText(titleN);

        back = findViewById(R.id.back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerParticipants);
        adapter = new RecyclerViewParticipantsAdapter(participantStatus,this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
