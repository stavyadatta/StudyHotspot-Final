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

import com.example.studyhotspot.Control.UserDatabaseManager;
import com.example.studyhotspot.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * RecyclerViewUserAdapter displays the Friend Requests that the user has received from other users.
 * For each friend request, RecyclerViewUserAdapter displays the originator's name and email.
 * Additionally, RecyclerViewUserAdapter uses UserDatabaseManager's logic in accepting / rejecting a request to
 * update firestore accordingly.
 */
public class RecyclerViewRequestAdapter extends RecyclerView.Adapter<RecyclerViewRequestAdapter.ViewRequestHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames = new ArrayList<>();
    private ArrayList<String> meMails = new ArrayList<>();
    private Context mContext;
    int status;
    private OnItemClickListener mOnItemClickListener;
    private ViewRequest viewRequest;

    UserDatabaseManager userDatabaseManager;

    public RecyclerViewRequestAdapter(ArrayList<String> mfNames, ArrayList<String> meMails, ViewRequest viewRequest,
                                      Context mContext, OnItemClickListener onItemClickListener) {
        this.mfNames = mfNames;
        this.meMails = meMails;
        this.viewRequest = viewRequest;
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
        this.userDatabaseManager = viewRequest.userDatabaseManager;
    }

    @NonNull
    @Override
    public ViewRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestlayout, parent, false);
        ViewRequestHolder holder = new ViewRequestHolder(view, mOnItemClickListener);

        return holder;
    }


    //Will change based on what the layout is like ****
    @Override
    public void onBindViewHolder(@NonNull ViewRequestHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Log.d("size", "" + meMails.size());
        Log.e("size", "" + mfNames.size());

        holder.eMail.setText(meMails.get(position));
        holder.fName.setText(mfNames.get(position));

        holder.accept.setImageResource(R.drawable.check_2);
        holder.reject.setImageResource(R.drawable.cross_4);

        holder.accept.setEnabled(true);
        holder.reject.setEnabled(true);

        holder.requestLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, mfNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });


        holder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                holder.accept.setEnabled(false);
                holder.reject.setEnabled(false);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: " + status);
                Toast.makeText(mContext, "CAN BE Accepted", Toast.LENGTH_SHORT).show();


                userDatabaseManager.acceptRequest(userID, meMails.get(position), mfNames.get(position));

                meMails.remove(position);
                mfNames.remove(position);
                viewRequest.initRecyclerView();
            }});

        holder.reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                holder.reject.setEnabled(false);
                holder.accept.setEnabled(false);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                Log.d("statusNumber", "status: "+status);
                Toast.makeText(mContext, "CAN BE rejected", Toast.LENGTH_SHORT).show();

                userDatabaseManager.rejectRequest(userID, meMails.get(position), mfNames.get(position));

                meMails.remove(position);
                mfNames.remove(position);
                viewRequest.initRecyclerView();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount", "meMails: "+meMails.size());
        Log.d("getItemCount", "mfNames: "+mfNames.size());

        return Math.min(meMails.size(), mfNames.size());
    }

    public class ViewRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout requestLayout;
        TextView fName;
        TextView eMail;
        ImageView accept;
        ImageView reject;
        OnItemClickListener VOnItemClickListener;


        public ViewRequestHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            requestLayout = itemView.findViewById(R.id.request_layout);
            fName = itemView.findViewById(R.id.user_name);
            eMail = itemView.findViewById(R.id.user_email);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            VOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            VOnItemClickListener.onItemClick(getAdapterPosition());

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}