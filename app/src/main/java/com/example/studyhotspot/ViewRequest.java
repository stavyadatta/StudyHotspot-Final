package com.example.studyhotspot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class ViewRequest extends AppCompatActivity {

    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> emaillist = new ArrayList<>();
    ArrayList<String> addedFriendList = new ArrayList<String>();
    ArrayList<String> awaitingFriendList = new ArrayList<String>();


    String userID;
    String userEmail;
    FirebaseFirestore firebaseFirestore;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton homeButton;

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

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                addedFriendList = (ArrayList<String>) documentSnapshot.get("addedfriends");
                awaitingFriendList = (ArrayList<String>) documentSnapshot.get("awaitingfriends");
                userEmail = documentSnapshot.getString("email");
                //Log.d("Oncreate","awaitingFriendList: " +awaitingFriendList.get(0));

                for (String email :awaitingFriendList) {
                    Log.d("forloop","email: " +email);
                    firebaseFirestore.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("onComplete","success");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("AddingFriends", document.getId() + " => " + document.getData());

                                    String targetUID = document.getId();

                                    DocumentReference targetDoc = firebaseFirestore.collection("users").document(targetUID);

                                    Task<DocumentSnapshot> t =  targetDoc.get();
                                    t.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot docsnap) {
                                            String name = docsnap.getString("fName");
                                            namelist.add(name);
                                            Log.d("name", name);

                                        }
                                    });
                                    t.addOnFailureListener(new OnFailureListener() {
                                        public void onFailure(Exception e) {
                                        }
                                    });

                                }

                            } else {
                                Log.d("AddingFriends", "Error getting documents: ", task.getException());
                            }
                            Log.d("init", "begin recyclerview");

                        }
                    });
                }

            }
        });

        //Log.d("useremail","useremail: " +awaitingFriendList.size());

        initRecyclerView();

    }


    private void initRecyclerView(){
        Log.d("Recycler Users", "initRecyclerView: init recyclerUsers.");
        RecyclerView recyclerView = findViewById(R.id.recyclerRequests);
        RecyclerViewRequestAdapter adapter = new RecyclerViewRequestAdapter(namelist, awaitingFriendList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void acceptRequest(String userID, String targetEmail){
        Log.d("acceptFriends", "Entered");
        Log.d("AcceptFriend", "TargetEmail: "+targetEmail);
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
                                acceptRequestUpdateDB(userDoc, targetDoc, targetEmail, userEmail);
                            }
                        });
                        t.addOnFailureListener(new OnFailureListener() {
                            public void onFailure(Exception e) {
                                Log.d("get user email","failed");
                            }
                        });

                    }
                } else {
                    Log.d("Accepting Request", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void rejectRequest(String userID, String targetEmail){
        Log.d("acceptFriends", "Entered");
        Log.d("AcceptFriend", "TargetEmail: "+targetEmail);
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
                                rejectRequestUpdateDB(userDoc, targetDoc, targetEmail, userEmail);
                            }
                        });
                        t.addOnFailureListener(new OnFailureListener() {
                            public void onFailure(Exception e) {
                                Log.d("get user email","failed");
                            }
                        });

                    }
                } else {
                    Log.d("Rejecting Request", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void refreshing() {
        Intent intent = new Intent(ViewRequest.this, ViewRequest.class);
        //finish();
        startActivity(intent);
    };

    private void acceptRequestUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail){

        userDoc.update("addedfriends", FieldValue.arrayUnion(targetEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "Userdoc addedfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });
        Log.d("log driectly above","success:" + userEmail);

        userDoc.update("awaitingfriends", FieldValue.arrayRemove(targetEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "Userdoc awaitingfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });
        Log.d("log driectly above","success:" + userEmail);

        targetDoc.update("addedfriends", FieldValue.arrayUnion(userEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "targetDoc addedfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });

        targetDoc.update("addingfriends", FieldValue.arrayRemove(userEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "targetDoc addingfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });
    }


    private void rejectRequestUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail){

        userDoc.update("awaitingfriends", FieldValue.arrayRemove(targetEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "Userdoc awaitingfriends successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update DB", "Error updating document", e);
                    }
                });
        Log.d("log driectly above","success:" + userEmail);


        targetDoc.update("addingfriends", FieldValue.arrayRemove(userEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update DB", "targetDoc addingfriends successfully updated!");
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
                    Toast.makeText(ViewRequest.this, "Social Page", Toast.LENGTH_LONG).show();
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(ViewRequest.this, ActivityPageMain.class);
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
                    Intent intent = new Intent(ViewRequest.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }
}