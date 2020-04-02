package com.example.studyhotspot.Boundary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studyhotspot.Control.ActivityPageMain;
import com.example.studyhotspot.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mMS1 = new ArrayList<>();
    private ArrayList<String> mMS2 = new ArrayList<>();
    private Context mContext;
    private ActivityPageMain activityPageMain;
    private History history;
    private Boolean friend;

    public RecyclerViewAdapter(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1, ArrayList<String> mMS2,
                               Context mContext, ActivityPageMain activityPageMain, Boolean friend) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mMS2 = mMS2;
        this.activityPageMain = activityPageMain;
        this.friend = friend;
    }

    public RecyclerViewAdapter(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1, ArrayList<String> mMS2,
                               Context mContext, History history) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mMS2 = mMS2;
        this.history = history;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitylayout_1, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    //Will change based on what the layout is like ************
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);

        holder.sessionName.setText(mImageNames.get(position));
        holder.status.setText(mMS1.get(position));
        holder.creatorName.setText(mMS2.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityPageMain != null) {
                    activityPageMain.showSessionInfo(position, friend);
                }
                else{
                    history.showPastSessionInfo(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView sessionName;
        TextView status, creatorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            sessionName = itemView.findViewById(R.id.sessionName);
            status = itemView.findViewById(R.id.status);
            creatorName = itemView.findViewById(R.id.creatorName);
        }
    }

}
