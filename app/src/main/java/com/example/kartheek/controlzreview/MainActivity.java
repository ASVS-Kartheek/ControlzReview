package com.example.kartheek.controlzreview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    ArrayList<String> eventNames = new ArrayList<>();
    ArrayList<String> notifications = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.review_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        callFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void callFirebase(){
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference notification = database.child("notification_review");

        notification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventNames = new ArrayList<>();
                notifications = new ArrayList<>();
                keys = new ArrayList<>();
                i = 0;

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String eventName = ds.child("event").getValue(String.class);
                    String notification = ds.child("text").getValue(String.class);
                    String key = ds.getKey();

                    Log.v("MAIN ACTIVITY FIREBASE",eventName+":"+notification);

                    eventNames.add(eventName);
                    notifications.add(ds.child("text").getValue(String.class));
                    keys.add(key);
                    i++;
                }

                mAdapter = new ReviewAdapter(eventNames,notifications,keys,i,getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
