package com.example.studyhotspot.Control;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studyhotspot.Entity.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserDatabaseManager {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth fAuth;
    String currentUserID;
    String currentUserEmail;
    String currentUserName;
    DocumentReference documentReference;

    Activity thisActivity;

    ArrayList<String> addedFriendList;
    ArrayList<String> addingFriendList;
    ArrayList<String> awaitingFriendList;
    ArrayList<String> awaitingFriendName;

    public UserDatabaseManager(Activity activity){
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();
        documentReference = firebaseFirestore.collection("users").document(currentUserID);

        thisActivity = activity;

        documentReference.addSnapshotListener(thisActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                currentUserEmail = documentSnapshot.getString("email");
                currentUserName = documentSnapshot.getString("fName");
                System.out.println(currentUserEmail);
            }
        });
    }

    public String getCurrentUserID(){
        return currentUserID;
    }

    public String getCurrentUserEmail(){
        return currentUserEmail;
    }

    public String getCurrentUsername(){
        return currentUserName;
    }

    public void getUserAddedFriends(ArrayList<String> friendNameList){

        documentReference.addSnapshotListener(thisActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                addedFriendList = (ArrayList<String>) documentSnapshot.get("addedfriends");
            }
        });

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        if(addedFriendList.contains(email)){
                            friendNameList.add(document.getString("fName"));
                        }
                    }
                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getUserAddedAddingFriends(ArrayList<String> addedFriend,
                                          ArrayList<String> addingFriend,
                                          ArrayList<Integer> relationshipFriendList,
                                          ArrayList<String> namelist,
                                          ArrayList<String> emaillist){

        documentReference.addSnapshotListener(thisActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                addedFriendList = (ArrayList<String>) documentSnapshot.get("addedfriends");
                addingFriendList = (ArrayList<String>) documentSnapshot.get("addingfriends");
                awaitingFriendList = (ArrayList<String>) documentSnapshot.get("awaitingfriends");
            }
        });



        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        if (email.contentEquals(currentUserEmail)){
                            continue;
                        }
                        else if(awaitingFriendList.contains(email)){
                            continue;
                        }
                        else if(addedFriendList.contains(email)){
                            addedFriend.add(email);
                            relationshipFriendList.add(2);
                        }
                        else if (addingFriendList.contains(email)){
                            addingFriend.add(email);
                            relationshipFriendList.add(1);
                        }
                        else{
                            relationshipFriendList.add(0);
                        }
                        namelist.add(document.getString("fName"));
                        emaillist.add(email);
                    }
                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getUserAwaitingFriends(ArrayList<String> awaitingFriendL,
                                          ArrayList<String> awaitingFriendN){

        documentReference.addSnapshotListener(thisActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                awaitingFriendList = (ArrayList<String>) documentSnapshot.get("awaitingfriends");
                awaitingFriendName = (ArrayList<String>) documentSnapshot.get("awaitingfriendsname");
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i = 0; i < awaitingFriendList.size(); i++){
                    awaitingFriendL.add(awaitingFriendList.get(i));
                    awaitingFriendN.add(awaitingFriendName.get(i));
                }
            }
        }, 1500);
    }

    public void addSession(Session newSession){
        CollectionReference dbSessions = firebaseFirestore.collection("hashsessions");

        dbSessions.add(newSession)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(thisActivity, "Session Added", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(thisActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
                                //userEmail = docsnap.getString("email");
                                String name = docsnap.getString("fName");
                                Log.d("get user email","success:" + currentUserEmail);
                                addFriendUpdateDB(userDoc, targetDoc, targetEmail, currentUserEmail, name);
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

    private void addFriendUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail, String userName){


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

        targetDoc.update("awaitingfriendsname", FieldValue.arrayUnion(userName))
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
}
