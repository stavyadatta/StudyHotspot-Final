package com.example.studyhotspot;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerViewRequestAdapter extends RecyclerView.Adapter<RecyclerViewRequestAdapter.ViewRequestHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames = new ArrayList<>();
    private ArrayList<String> meMails = new ArrayList<>();
    private Context mContext;
    int status;

    public RecyclerViewRequestAdapter(ArrayList<String> mfNames, ArrayList<String> meMails, Context mContext) {
        this.mfNames = mfNames;
        this.meMails = meMails;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestlayout, parent, false);
        ViewRequestHolder holder = new ViewRequestHolder(view);

        return holder;
    }


    //Will change based on what the layout is like ****
    @Override
    public void onBindViewHolder(@NonNull ViewRequestHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.fName.setText(mfNames.get(position));
        holder.eMail.setText(meMails.get(position));

        holder.accept.setImageResource(R.drawable.check_2);
        holder.reject.setImageResource(R.drawable.cross_4);

        holder.userlayout.setOnClickListener(new View.OnClickListener(){
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
                ViewRequest requestdecider = new ViewRequest();
                requestdecider.acceptRequest(userID, meMails.get(position));
                //holder.statusButton.setImageResource(R.drawable.pending_1);
                
            }});

        holder.reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: "+status);
                Toast.makeText(mContext, "CAN BE rejected", Toast.LENGTH_SHORT).show();
                ViewRequest requestdecider = new ViewRequest();
                requestdecider.rejectRequest(userID, meMails.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfNames.size();
    }

    public class ViewRequestHolder extends RecyclerView.ViewHolder{

        RelativeLayout userlayout;
        TextView fName;
        TextView eMail;
        ImageView accept;
        ImageView reject;


        public ViewRequestHolder(@NonNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.user_name);
            eMail = itemView.findViewById(R.id.user_email);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
        }
    }
}