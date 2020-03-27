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

import java.util.ArrayList;

public class RecyclerViewUserAdapter extends RecyclerView.Adapter<RecyclerViewUserAdapter.ViewUserHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames = new ArrayList<>();
    private ArrayList<String> meMails = new ArrayList<>();
    private  ArrayList<Integer> relationshipList = new ArrayList<>();
    private Context mContext;

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
        Log.d(TAG, "onBindViewHolder: called. CHANGED!!!!!");

        holder.fName.setText(mfNames.get(position));
        holder.eMail.setText(meMails.get(position));

        int status = relationshipList.get(position);
        if(status==0){
            holder.plus0.setImageResource(R.drawable.plus_0);
            holder.plus0.setVisibility(View.VISIBLE);
        }
        else if(status==1){
            holder.pending1.setImageResource(R.drawable.pending_1);
            holder.pending1.setVisibility(View.VISIBLE);
        }
        else{
            holder.check2.setImageResource(R.drawable.check_2);
            holder.check2.setVisibility(View.VISIBLE);
        }

        holder.userlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, mfNames.get(position), Toast.LENGTH_SHORT).show();
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
        ImageView plus0;
        ImageView pending1;
        ImageView check2;

        public ViewUserHolder(@NonNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.user_name);
            eMail = itemView.findViewById(R.id.user_email);
            plus0 = itemView.findViewById(R.id.plus0);
            pending1 = itemView.findViewById(R.id.pending1);
            check2 = itemView.findViewById(R.id.check2);
            userlayout = itemView.findViewById(R.id.user_layout);
        }
    }
}