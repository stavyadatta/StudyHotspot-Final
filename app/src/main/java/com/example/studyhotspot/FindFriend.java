package com.example.studyhotspot;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                addedFriendList = (ArrayList<String>) documentSnapshot.get("addedfriends");
                addingFriendList = (ArrayList<String>) documentSnapshot.get("addingfriends");
                //String emailll = documentSnapshot.getString("email");
                //Log.d("ADDED FRIEND LIST", "List: "+((ArrayList<String>) documentSnapshot.get("addedfriends")).get(0));
                //Log.d("ADDED FRIEND LIST", "Size: "+ addedFriendList.size());
                Log.d("ADDED FRIEND LIST", "List: "+ addingFriendList.get(0));
            }
        });

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
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
                    Log.d("tagsuccess", namelist.toString());
                    Log.d("relationship_list", "0: "+ relationshipFriendList.get(0));
                    Log.d("relationship_list", "1: "+ relationshipFriendList.get(1));
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
        RecyclerViewUserAdapter adapter = new RecyclerViewUserAdapter(namelist, emaillist, relationshipFriendList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}