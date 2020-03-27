package com.example.studyhotspot;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FindFriend extends AppCompatActivity {
    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> emaillist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        namelist.add(document.getString("fName"));
                        emaillist.add(document.getString("email"));
                    }
                    Log.d("tagsuccess", namelist.toString());
                    initRecyclerView();

                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });
        Log.d("Before Recycler", "Before Recycler");

    }

    private void initRecyclerView(){
        Log.d("Recycler Users", "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerUsers);
        RecyclerViewUserAdapter adapter = new RecyclerViewUserAdapter(namelist, emaillist, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}