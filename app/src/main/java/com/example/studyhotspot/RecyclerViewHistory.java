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

public class RecyclerViewHistory extends RecyclerView.Adapter<RecyclerViewHistory.HistItemView> {

    public final static String TAG = "RecyclerViewHistory";
    private ArrayList<String> namesList = new ArrayList<>();
    private ArrayList<String> questionImg = new ArrayList<>();
    private ArrayList<String> createdPr = new ArrayList<>();
    private ArrayList<String> statusPr = new ArrayList<>();
    private Context currentC;


    public RecyclerViewHistory(ArrayList<String> namesList, ArrayList<String> questionImg, ArrayList<String> createdPr, ArrayList<String> statusPr, Context currentC) {

        this.namesList = namesList;
        this.questionImg = questionImg;
        this.createdPr = createdPr;
        this.statusPr = statusPr;
        this.currentC = currentC;
    }

    @NonNull
    @Override
    public HistItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_historyitem, parent, false);
        HistItemView holder = new HistItemView(view2);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistItemView holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Glide.with(currentC)
                .asBitmap()
                .load(questionImg.get(position))
                .into(holder.image);

        holder.imageName.setText(namesList.get(position));
        holder.Cr.setText(createdPr.get(position));
        holder.Pro.setText(statusPr.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + namesList.get(position));

                Toast.makeText(currentC, namesList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
         return namesList.size();
    }

    public class HistItemView extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView Cr, Pro;

        public HistItemView(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            Cr = itemView.findViewById(R.id.MS1);
            Pro = itemView.findViewById(R.id.MS2);
        }
    }
}
