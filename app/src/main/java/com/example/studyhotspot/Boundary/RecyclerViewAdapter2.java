package com.example.studyhotspot.Boundary;

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
import com.example.studyhotspot.Control.ActivityPageMain;
import com.example.studyhotspot.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerViewAdapter2 is being used in the activity main page at the invitation menu where it presents
 * the user with -
 * <p> Information bitImage - leads to information of the session page</p>
 * <p> Accept bitImage- accepts the invite ane updates it on the database </p>
 * <p> Reject bitImage - rejects the invite and updates the option to the database </p>
 * The bitImage are coming through glide.
 * The recycler view also outputs the creator's name as well
 */

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter2";

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

    public RecyclerViewAdapter2(ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mMS1,
                                ArrayList<String> mImages2, ArrayList<String> mImages3, Context mContext, ActivityPageMain activityPageMain) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mMS1 = mMS1;
        this.mImages2 = mImages2;
        this.mImages3 = mImages3;
        this.activityPageMain = activityPageMain;
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
                activityPageMain.processInvitation(position, true);
                Toast.makeText(mContext, "ACCEPTING...", Toast.LENGTH_LONG).show();
            }
        });

        holder.image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "REJECTING...", Toast.LENGTH_LONG).show();
                activityPageMain.processInvitation(position, false);
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
