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

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter2";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mMS1 = new ArrayList<>();
    private ArrayList<String> mImages2 = new ArrayList<>();
    private ArrayList<String > mImages3 = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter2(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1, ArrayList<String> mImages2, ArrayList<String> mImages3, Context mContext) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mImages2 = mImages2;
        this.mImages3 = mImages3;
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


        // Change this for button effects
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image, image2, image3;
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
