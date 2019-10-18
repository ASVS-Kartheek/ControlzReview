package com.example.kartheek.controlzreview;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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

        FirebaseMessaging.getInstance().subscribeToTopic("controlzNotifs");

        mRecyclerView = findViewById(R.id.review_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        subscribeToNotifs();
        callFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private  void subscribeToNotifs()
    {
        final SharedPreferences preferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        boolean flag = preferences.getBoolean("subscribed",false);
        if(!flag) {
            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed";

                            if (!task.isSuccessful()) msg ="Subscription Failed";
                            else preferences.edit().putBoolean("subscribed",true).apply();

                            Log.d("MainActivity", msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Updates",
                    "ATMOS Updates", NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),audioAttributes);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager manager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            assert manager!= null;
            manager.createNotificationChannel(notificationChannel);
        }

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
