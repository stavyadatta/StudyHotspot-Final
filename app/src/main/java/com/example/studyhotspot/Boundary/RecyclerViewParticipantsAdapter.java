package com.example.studyhotspot.Boundary;

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

import com.example.studyhotspot.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * RecyclerViewParticipantsAdapter is called by the ViewParticipants page to list out all the names
 * of the participants of the session, tick mark will show that the participant is taking part for sure
 * and the time icon will show that response is still waiting
 */

public class RecyclerViewParticipantsAdapter extends RecyclerView.Adapter<RecyclerViewParticipantsAdapter.ViewUserHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Map<String, Boolean> participantStatus;
    private ArrayList<String> participants = new ArrayList<String>();

    private Context mContext;
    Boolean status;

    public RecyclerViewParticipantsAdapter(Map<String, Boolean> participantStatus, Context mContext) {
        this.participantStatus = participantStatus;

        for(String key: participantStatus.keySet()){
            participants.add(key);
        }

        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlayout, parent, false);
        ViewUserHolder holder = new ViewUserHolder(view);

        return holder;
    }

    //Will change based on what the layout is like ****
    @Override
    public void onBindViewHolder(@NonNull ViewUserHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        String name = participants.get(position);
        holder.fName.setText(name);


        status = participantStatus.get(name);
        if(status == null){
            holder.statusButton.setImageResource(R.drawable.pending_1);
        }
        else if(status){
            holder.statusButton.setImageResource(R.drawable.ic_yes);
        }
        else if (!status){
            holder.statusButton.setImageResource(R.drawable.ic_no);
        }

        holder.userlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, participants.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class ViewUserHolder extends RecyclerView.ViewHolder{

        RelativeLayout userlayout;
        TextView fName;
        TextView eMail;
        ImageView statusButton;


        public ViewUserHolder(@NonNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.user_name);
            eMail = itemView.findViewById(R.id.user_email);
            statusButton = itemView.findViewById(R.id.status);
            userlayout = itemView.findViewById(R.id.user_layout);
        }
    }
}