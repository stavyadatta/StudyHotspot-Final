package com.example.studyhotspot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FindFriend extends AppCompatActivity {
    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> emaillist = new ArrayList<>();
    ArrayList<String> addedFriendList = new ArrayList<String>();
    ArrayList<String> addingFriendList = new ArrayList<String>();
    ArrayList<Integer> relationshipFriendList = new ArrayList<Integer>();

    String userID;
    String userEmail;
    private Button mViewRequestBtn;
    FirebaseFirestore firebaseFirestore;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;

    private String previousActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView mStatusPlaceholder = findViewById(R.id.status);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        setUpBottomAppBar();
        setUpRequestBtn();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            previousActivity = extras.getString("prevActivity");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                addedFriendList = (ArrayList<String>) documentSnapshot.get("addedfriends");
                addingFriendList = (ArrayList<String>) documentSnapshot.get("addingfriends");
                userEmail = documentSnapshot.getString("email");
                //Log.d("ADDED FRIEND LIST", "List: "+((ArrayList<String>) documentSnapshot.get("addedfriends")).get(0));
                //Log.d("ADDED FRIEND LIST", "Size: "+ addedFriendList.size());
                //Log.d("ADDED FRIEND LIST", "List: "+ addingFriendList.get(0));
            }
        });

        System.out.println(userEmail);

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        if(email.contentEquals(userEmail)){
                            continue;
                        }
                        else{
                            if(addedFriendList.contains(email)){
                                relationshipFriendList.add(2); //2 means added
                            }
                            else if(addingFriendList.contains(email)){
                                relationshipFriendList.add(1); // 1 means adding
                            }
                            else{
                                relationshipFriendList.add(0); // 0 means stranger
                            }
                            namelist.add(document.getString("fName"));
                            emaillist.add(email);
                        }
                    }
                    Log.d("tagsuccess", namelist.toString());
                    //Log.d("relationship_list", "0: "+ relationshipFriendList.get(0));
                    //Log.d("relationship_list", "1: "+ relationshipFriendList.get(1));
                    initRecyclerView();

                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });
        Log.d("Before Recycler", "Before Recycler");

    }

    public void setUpRequestBtn() {
        mViewRequestBtn = findViewById(R.id.viewRequestBtn);
        mViewRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindFriend.this, ViewRequest.class);
                startActivity(intent);

            }
        });
    }

    private void initRecyclerView(){
        Log.d("Recycler Users", "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerUsers);
        RecyclerViewUserAdapter adapter = new RecyclerViewUserAdapter(namelist, emaillist, relationshipFriendList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addFriend(String userID, String targetEmail){
        Log.d("AddingFriends", "Entered");
        Log.d("AddingFriends", "TargetEmail: "+targetEmail);
        boolean status;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").whereEqualTo("email", targetEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AddingFriends", document.getId() + " => " + document.getData());
                                String targetUID = document.getId();

                                DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
                                DocumentReference targetDoc = firebaseFirestore.collection("users").document(targetUID);

                                Task<DocumentSnapshot> t =  userDoc.get();
                                t.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot docsnap) {
                                        userEmail = docsnap.getString("email");
                                        Log.d("get user email","success:" + userEmail);
                                        addFriendUpdateDB(userDoc, targetDoc, targetEmail, userEmail);
                                    }
                                });
                                t.addOnFailureListener(new OnFailureListener() {
                                    public void onFailure(Exception e) {
                                        Log.d("get user email","failed");
                                    }
                                });

                            }
                        } else {
                            Log.d("AddingFriends", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addFriendUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail){

        userDoc.update("addingfriends", FieldValue.arrayUnion(targetEmail))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Update DB", "Userdoc addingfriends successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Update DB", "Error updating document", e);
                                }
                            });
        Log.d("log driectly bove","success:" + userEmail);

        targetDoc.update("awaitingfriends", FieldValue.arrayUnion(userEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "targetDoc awaitingfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });
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
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    //Intent intent = new Intent(MapsActivity.this, )
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