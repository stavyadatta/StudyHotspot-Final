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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerViewUserAdapter extends RecyclerView.Adapter<RecyclerViewUserAdapter.ViewUserHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames = new ArrayList<>();
    private ArrayList<String> meMails = new ArrayList<>();
    private  ArrayList<Integer> relationshipList = new ArrayList<>();
    private Context mContext;
    int status;

    public RecyclerViewUserAdapter(ArrayList<String> mfNames, ArrayList<String> meMails, ArrayList<Integer> rsList, Context mContext) {
        this.mfNames = mfNames;
        this.meMails = meMails;
        this.relationshipList = rsList;
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

        holder.fName.setText(mfNames.get(position));
        holder.eMail.setText(meMails.get(position));

        status = relationshipList.get(position);
        if(status==0){
            holder.statusButton.setImageResource(R.drawable.plus_0);
        }
        else if(status==1){
            holder.statusButton.setImageResource(R.drawable.pending_1);
        }
        else{
            holder.statusButton.setImageResource(R.drawable.check_2);
        }

        holder.userlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, mfNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        holder.statusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                status = relationshipList.get(position);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: "+status);
                if(status==0){
                    Toast.makeText(mContext, "CAN BE ADDED", Toast.LENGTH_SHORT).show();
                    FindFriend friendFinder = new FindFriend();
                    friendFinder.addFriend(userID, meMails.get(position));
                }
                else if(status==1){
                    Toast.makeText(mContext, "ALREADY ADDED", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "ALREADY FRIENDS", Toast.LENGTH_SHORT).show();
                }
                //meMails.get(position)
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfNames.size();
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