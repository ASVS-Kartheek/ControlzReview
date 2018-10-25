package com.example.kartheek.controlzreview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<String> eventNames;
    private ArrayList<String> notifications;
    ArrayList<String> keys;
    Context context;
    int i;

    ReviewAdapter(ArrayList<String> eventNames, ArrayList<String> notifications, ArrayList<String> keys, int i, Context context){
        this.eventNames = eventNames;
        this.notifications = notifications;
        this.keys = keys;
        this.context = context;
        this.i = i;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v  = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_item,viewGroup,false);

        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder reviewViewHolder, int i) {

        final int position = reviewViewHolder.getAdapterPosition();

        if(eventNames.get(position)!=null){
            reviewViewHolder.mEventName.setText(eventNames.get(position));
        }else {
            reviewViewHolder.mEventName.setText("");
        }

        if(notifications.get(position)!=null){
            reviewViewHolder.mNotifText.setText(notifications.get(position));
        }else {
            reviewViewHolder.mNotifText.setText("");
        }

        reviewViewHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PUSH NOTIFICATION
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("messages");

                HashMap<String,String> map = new HashMap<>();
                map.put("text",notifications.get(position));
                map.put("event",eventNames.get(position));

                //TODO: CHAnge this to 3
                myRef.child("2").setValue(map);

                Toast.makeText(context, "Notification Pushed", Toast.LENGTH_SHORT).show();

                //DELETE FROM THE DATABASE
                myRef = database.getReference("notification_review").child(keys.get(position));
                myRef.removeValue();


            }
        });

        reviewViewHolder.mRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DELETE FROM THE DATABASE
                DatabaseReference myRef = database.getReference("notification_review").child(keys.get(position));
                myRef.removeValue();

                Toast.makeText(context, "Notification Rejected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return i;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView mEventName;
        TextView mNotifText;
        Button mAcceptBtn;
        Button mRejectBtn;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            mEventName = itemView.findViewById(R.id.event_name);
            mNotifText = itemView.findViewById(R.id.notif_text);
            mAcceptBtn = itemView.findViewById(R.id.accept_btn);
            mRejectBtn = itemView.findViewById(R.id.reject_btn);
        }
    }

}
