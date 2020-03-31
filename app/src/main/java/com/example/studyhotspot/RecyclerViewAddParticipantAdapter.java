package com.example.studyhotspot;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerViewAddParticipantAdapter extends RecyclerView.Adapter<RecyclerViewAddParticipantAdapter.ViewUserHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mfNames;

    private OnItemClickListener mOnItemClickListener;

    public RecyclerViewAddParticipantAdapter(ArrayList<String> mfNames, OnItemClickListener onItemClickListener) {
        this.mfNames = mfNames;

        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addparticipantlayout, parent, false);
        return (new ViewUserHolder(view, mOnItemClickListener));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewUserHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.name.setText(mfNames.get(position));

        holder.chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    System.out.println(position);
                }
                else{
                    System.out.println(position+" Unclicked");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfNames.size();
    }

    public class ViewUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout add_participant_layout;
        TextView name;
        Chip chip;
        OnItemClickListener VOnItemClickListener;


        public ViewUserHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            chip = itemView.findViewById(R.id.addUser);
            add_participant_layout = itemView.findViewById(R.id.add_participant_layout);
            this.VOnItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            VOnItemClickListener.onItemClick(getAdapterPosition());
            if (chip.isChecked()){
                chip.setChecked(false);
            }
            else{
                chip.setChecked(true);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}