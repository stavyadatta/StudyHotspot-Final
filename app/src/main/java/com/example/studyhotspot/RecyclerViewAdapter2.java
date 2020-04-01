package com.example.studyhotspot;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter2";

    private ArrayList<String> sessionIDs = new ArrayList<>();

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mMS1 = new ArrayList<>();
    private ArrayList<String> mImages2 = new ArrayList<>();
    private ArrayList<String > mImages3 = new ArrayList<>();
    private Context mContext;
    String name;
    Handler handler = new Handler();
    private ActivityPageMain activityPageMain;
    //String documentRef;

    public RecyclerViewAdapter2(ArrayList<String> sessionIDs, ArrayList<String> mImageNames,
                                ArrayList<String> mImages, ArrayList<String> mMS1,
                                ArrayList<String> mImages2, ArrayList<String> mImages3, Context mContext,
                                ActivityPageMain activityPageMain ) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mImages2 = mImages2;
        this.mImages3 = mImages3;
        this.activityPageMain = activityPageMain;
        this.sessionIDs = sessionIDs;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitylayout_2, parent, false);
        RecyclerViewAdapter2.ViewHolder holder = new RecyclerViewAdapter2.ViewHolder(view);

        return holder;
    }

    //Will change based on what the layout is like ************
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);

        Glide.with(mContext)
                .asBitmap()
                .load(mImages2.get(position))
                .into(holder.image2);

        Glide.with(mContext)
                .asBitmap()
                .load(mImages3.get(position))
                .into(holder.image3);


        holder.imageName.setText(mImageNames.get(position));
        holder.MS2.setText(mMS1.get(position));


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityPageMain.showInviteInfo(position);
            }
        });

        // Change this for button effects
        holder.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Toast.makeText(mContext, "Accepting Session Invite", Toast.LENGTH_SHORT).show();
                acceptSession(userID, sessionIDs.get(position));
                sessionIDs.remove(position);
                mImageNames.remove(position);
                mImages.remove(position);
                mMS1.remove(position);
                mImages2.remove(position);
                mImages3.remove(position);
            }
        });

        // Change this for button effects
        holder.image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Toast.makeText(mContext, "Rejecting Session Invite", Toast.LENGTH_SHORT).show();
                rejectSession(userID, sessionIDs.get(position));
                sessionIDs.remove(position);
                mImageNames.remove(position);
                mImages.remove(position);
                mMS1.remove(position);
                mImages2.remove(position);
                mImages3.remove(position);
            }
        });
    }

    public void acceptSession(String userID, String sessionID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
        Task<DocumentSnapshot> x =  userDoc.get();
        x.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot1) {
                String userName = documentSnapshot1.getString("fName");

                DocumentReference sessionDoc = firebaseFirestore.collection("hashsessions").document(sessionID);
                Task<DocumentSnapshot> y =  sessionDoc.get();
                y.addOnSuccessListener((new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot2) {
                        Map<String, Boolean> tempparticipantStatus = (Map<String, Boolean>) documentSnapshot2.get("participantStatus");
                        tempparticipantStatus.put(userName, true);

                        sessionDoc.update("participantStatus", tempparticipantStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Update success:", "tempparticipantStatus");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Update failure:", "tempparticipantStatus");
                            }
                        });
                    }
                }));
            }
        });
    }


    public void rejectSession(String userID, String sessionID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
        Task<DocumentSnapshot> x =  userDoc.get();
        x.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot1) {
                String userName = documentSnapshot1.getString("fName");

                DocumentReference sessionDoc = firebaseFirestore.collection("hashsessions").document(sessionID);
                Task<DocumentSnapshot> y =  sessionDoc.get();
                y.addOnSuccessListener((new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot2) {
                        Map<String, Boolean> tempparticipantStatus = (Map<String, Boolean>) documentSnapshot2.get("participantStatus");
                        tempparticipantStatus.put(userName, false);

                        sessionDoc.update("participantStatus", tempparticipantStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Update success:", "tempparticipantStatus");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Update failure:", "tempparticipantStatus");
                            }
                        });
                    }
                }));
            }
        });
    }



    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image, image2, image3;
        //image is question mark
        //image2 is tick
        //image3 is cross
        TextView imageName;
        RelativeLayout parentLayout;
        TextView MS2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            MS2 = itemView.findViewById(R.id.MS2);
        }

    }

}
