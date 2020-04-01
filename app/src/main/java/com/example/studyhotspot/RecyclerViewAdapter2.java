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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter2";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mMS1 = new ArrayList<>();
    private ArrayList<String> mImages2 = new ArrayList<>();
    private ArrayList<String > mImages3 = new ArrayList<>();
    private Context mContext;
    private OnNoteListener2 mOnNoteListener2;
    String name;
    Handler handler = new Handler();
    private ActivityPageMain activityPageMain;
    //String documentRef;

    public RecyclerViewAdapter2(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1, ArrayList<String> mImages2, ArrayList<String> mImages3, Context mContext, OnNoteListener2 onNoteListener2, ActivityPageMain activityPageMain) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mImages2 = mImages2;
        this.mImages3 = mImages3;
        this.mOnNoteListener2 = onNoteListener2;
        this.activityPageMain = activityPageMain;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitylayout_2, parent, false);
        RecyclerViewAdapter2.ViewHolder holder = new RecyclerViewAdapter2.ViewHolder(view, mOnNoteListener2);

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


        // Change this for button effects
        holder.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked here starting");
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d(TAG, "onClick: Clicked here before AcceptSession");
                acceptSession(userID,position);
                //mImageNames.remove(position);
                //mImages.remove(position);
                //mMS1.remove(position);
                //mImages2.remove(position);
                //mImages3.remove(position);
            }
        });


    }

    public void acceptSession(String userID, int position){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                if(document.getId().equals(userID)){
                                    name = document.getString("fName");
                                    Log.d(TAG, "onComplete: instance of same userid" + userID + name);
                                }
                            }
                        }
                    }
                });

        handler.postDelayed(new Runnable() {
            public void run() {
        //error here - name is null
        Log.d(TAG, "acceptSession: after getting username " + name + userID);
        firebaseFirestore.collection("hashsessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int i =0;
                    for(QueryDocumentSnapshot document:task.getResult()){
                        HashMap<String, Boolean> doch = (HashMap<String, Boolean>)document.getData().get("participantStatus");
                        if(doch.containsKey(name)){
                            Log.d(TAG, "onComplete: Finding the hashmap with the user's name");
                            if(doch.get(name) == null){
                                Log.d(TAG, "onComplete: Finding the invitation activity");
                                if(position == i){
                                    Log.d(TAG, "onComplete: Found position");
                                    doch.replace(name, null,true);
                                    DocumentReference documentRef=document.getReference();
                                    documentRef.update("participantStatus", doch).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            activityPageMain.initImageBitmaps2();
                                            Log.d(TAG, "onSuccess: Sucess here");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Failure here");

                                        }
                                    });
                                }
                                else{
                                    i++;
                                    continue;
                                }
                            }

                        }
                    }
                }
            }
        });
            }
        }, 5000);
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView image, image2, image3;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView MS2;
        OnNoteListener2 onNoteListener2;

        public ViewHolder(@NonNull View itemView, OnNoteListener2 onNoteListener2) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            MS2 = itemView.findViewById(R.id.MS2);
            this.onNoteListener2 = onNoteListener2;
            image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onNoteListener2.onNoteClick2(getAdapterPosition());

        }

    }

    public interface OnNoteListener2{
        void onNoteClick2(int position);
    }

}
