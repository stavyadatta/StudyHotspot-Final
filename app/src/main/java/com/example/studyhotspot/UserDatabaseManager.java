package com.example.studyhotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class UserDatabaseManager {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth fAuth;
    String currentUserID;
    String currentUserEmail;
    DocumentReference documentReference;

    Activity thisActivity;

    ArrayList<String> addedFriendList;

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

    public void getCurrentUsername(ArrayList<String> userName){
        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        if(currentUserEmail.contentEquals(email)){
                            userName.add(document.getString("fName"));
                        }
                    }
                } else {
                    Log.d("tagfail", "Error getting documents: ", task.getException());
                }
            }
        });
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
}
