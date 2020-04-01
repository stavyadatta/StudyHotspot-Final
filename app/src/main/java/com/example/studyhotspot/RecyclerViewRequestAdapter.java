package com.example.studyhotspot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecyclerViewRequestAdapter extends RecyclerView.Adapter<RecyclerViewRequestAdapter.ViewRequestHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames = new ArrayList<>();
    private ArrayList<String> meMails = new ArrayList<>();
    private Context mContext;
    int status;
    private OnItemClickListener mOnItemClickListener;
    private ViewRequest viewRequest;

    public RecyclerViewRequestAdapter(ArrayList<String> mfNames, ArrayList<String> meMails, ViewRequest viewRequest,
                                      Context mContext, OnItemClickListener onItemClickListener) {
        this.mfNames = mfNames;
        this.meMails = meMails;
        this.viewRequest = viewRequest;
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestlayout, parent, false);
        ViewRequestHolder holder = new ViewRequestHolder(view, mOnItemClickListener);

        return holder;
    }


    //Will change based on what the layout is like ****
    @Override
    public void onBindViewHolder(@NonNull ViewRequestHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Log.d("size", "" + meMails.size());
        Log.e("size", "" + mfNames.size());

        holder.eMail.setText(meMails.get(position));
        holder.fName.setText(mfNames.get(position));

        holder.accept.setImageResource(R.drawable.check_2);
        holder.reject.setImageResource(R.drawable.cross_4);

        holder.requestLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, mfNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });


        holder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: " + status);
                Toast.makeText(mContext, "CAN BE Accepted", Toast.LENGTH_SHORT).show();

                acceptRequest(userID, meMails.get(position), mfNames.get(position));

                meMails.remove(position);
                mfNames.remove(position);
                viewRequest.initRecyclerView();
            }});

        holder.reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: "+status);
                Toast.makeText(mContext, "CAN BE rejected", Toast.LENGTH_SHORT).show();

                rejectRequest(userID, meMails.get(position), mfNames.get(position));

                meMails.remove(position);
                mfNames.remove(position);
                viewRequest.initRecyclerView();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount", "meMails: "+meMails.size());
        Log.d("getItemCount", "mfNames: "+mfNames.size());

        return Math.min(meMails.size(), mfNames.size());
    }

    public class ViewRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout requestLayout;
        TextView fName;
        TextView eMail;
        ImageView accept;
        ImageView reject;
        OnItemClickListener VOnItemClickListener;


        public ViewRequestHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            requestLayout = itemView.findViewById(R.id.request_layout);
            fName = itemView.findViewById(R.id.user_name);
            eMail = itemView.findViewById(R.id.user_email);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            VOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            VOnItemClickListener.onItemClick(getAdapterPosition());

        }
    }


    public void acceptRequest(String userID, String targetEmail, String targetname){
        Log.d("AcceptFriends", "Entered");
        Log.d("AcceptFriends", "TargetEmail: "+targetEmail);
        boolean status;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
                                String userEmail = docsnap.getString("email");
                                String name = docsnap.getString("fName");
                                Log.d("AcceptFriends","UserEmail:" + userEmail);

                                acceptRequestUpdateDB(userDoc, targetDoc, targetEmail, userEmail, targetname);
                                meMails.remove(userEmail);
                                Log.d("sizexxxx", ""+mfNames.size());
                                mfNames.remove(name);
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

    private void acceptRequestUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail, String userName){

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

        userDoc.update("awaitingfriendsname", FieldValue.arrayRemove(userName))
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

    public void rejectRequest(String userID, String targetEmail, String targetname){
        Log.d("RejectFriends", "Entered");
        Log.d("RejectFriends", "TargetEmail: "+targetEmail);
        boolean status;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
                                String userEmail = docsnap.getString("email");
                                Log.d("RejectFriends","UserEmail:" + userEmail);
                                rejectRequestUpdateDB(userDoc, targetDoc, targetEmail, userEmail, targetname);
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

    private void rejectRequestUpdateDB(DocumentReference userDoc, DocumentReference targetDoc, String targetEmail, String userEmail, String userName){

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

        userDoc.update("awaitingfriendsname", FieldValue.arrayRemove(userName))
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
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}