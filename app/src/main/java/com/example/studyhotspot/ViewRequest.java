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
                emaillist = new ArrayList<>();
                namelist = new ArrayList<>();
                for (String email :awaitingFriendList) {
                    Log.d("forloop","email: " +email);
                    firebaseFirestore.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                System.out.println("HERE");
                                Log.d("onComplete","entered on complete for email: "+email);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d("AddingFriends", document.getId() + " => " + document.getData());

                                    String targetUID = document.getId();

                                    DocumentReference targetDoc = firebaseFirestore.collection("users").document(targetUID);
                                    namelist.add(document.getString("fName"));
                                    emaillist.add(document.getString("email"));
                                    initRecyclerView();

                                    /*Task<DocumentSnapshot> t =  targetDoc.get();
                                    t.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot docsnap) {
                                            String name = docsnap.getString("fName");
                                            namelist.add(name);
                                            String email = docsnap.getString("email");
                                            emaillist.add(email);
                                            Log.d("added to namelist, name:", name);

                                            Log.d("before init", "to begin recyclerview");

                                        }
                                    });
                                    t.addOnFailureListener(new OnFailureListener() {
                                        public void onFailure(Exception e) {
                                        }
                                    });*/

                                }

                            } else {
                                Log.d("AddingFriends", "Error getting documents: ", task.getException());
                            }

                        }
                    });
                }

            }
        });

        //Log.d("useremail","useremail: " +awaitingFriendList.size());

    }


    private void initRecyclerView(){
        Log.d("init Recycler View", "enter initRecyclerView functions.");
        RecyclerView recyclerView = findViewById(R.id.recyclerRequests);
        RecyclerViewRequestAdapter adapter = new RecyclerViewRequestAdapter(namelist, emaillist, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void acceptRequest(String userID, String targetEmail){
        Log.d("AcceptFriends", "Entered");
        Log.d("AcceptFriends", "TargetEmail: "+targetEmail);
        boolean status;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").whereEqualTo("email", targetEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //Log.d("AddingFriends", document.getId() + " => " + document.getData());
                        String targetUID = document.getId();

                        DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
                        DocumentReference targetDoc = firebaseFirestore.collection("users").document(targetUID);

                        Task<DocumentSnapshot> t =  userDoc.get();
                        t.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot docsnap) {
                                userEmail = docsnap.getString("email");
                                Log.d("AcceptFriends","UserEmail:" + userEmail);
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
        Log.d("RejectFriends", "Entered");
        Log.d("RejectFriends", "TargetEmail: "+targetEmail);
        boolean status;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").whereEqualTo("email", targetEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //Log.d("AddingFriends", document.getId() + " => " + document.getData());
                        String targetUID = document.getId();

                        DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
                        DocumentReference targetDoc = firebaseFirestore.collection("users").document(targetUID);

                        Task<DocumentSnapshot> t =  userDoc.get();
                        t.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot docsnap) {
                                userEmail = docsnap.getString("email");
                                Log.d("RejectFriends","UserEmail:" + userEmail);
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
    /*
    public void refreshing() {
        Intent intent = new Intent(ViewRequest.this, ViewRequest.class);
        //finish();
        startActivity(intent);
    };
    */
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