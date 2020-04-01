package com.example.studyhotspot;

import android.content.Context;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewAdapter3.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter3";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mMS1 = new ArrayList<>();
    private Context mContext;
    private ActivityPageMain activityPageMain;


    public RecyclerViewAdapter3(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1,
                                Context mContext, ActivityPageMain activityPageMain) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.activityPageMain = activityPageMain;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter3.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitylayout_3, parent, false);
        RecyclerViewAdapter3.ViewHolder holder = new RecyclerViewAdapter3.ViewHolder(view);

        return holder;
    }

    //Will change based on what the layout is like ************
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter3.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);

        holder.imageName.setText(mImageNames.get(position));
        holder.MS2.setText(mMS1.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityPageMain.showFriendActivityInfo(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView MS2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            MS2 = itemView.findViewById(R.id.MS2);
        }
    }

}