package com.example.w10up_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");

    final private int maxLoad = 3;

    String lastKnownKey = null;

    public static final String ACTION_DATA_LOADED = "com.example.w10up_firebase.DATA_LOADED";


//    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ACTION_DATA_LOADED)) {
//
//                List<DataClass> temp = intent.getParcelableArrayListExtra("dataList");
//                // Do whatever you want with the dataList received
//                if(temp!=null) {
//                    dataList.addAll(temp);
//                }
//            }
//            adapter.notifyDataSetChanged();
//
//        }
//    };

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_DATA_LOADED)) {
                // Retrieve the message from the intent
                String message = intent.getStringExtra("message");

                // Log the message to verify
                Log.d("BroadcastReceiver", "Received message: " + message);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new MyAdapter(this, dataList);
        gridView.setAdapter(adapter);

        Intent serviceIntent = new Intent(this, FirebaseLoadService.class);
        startService(serviceIntent);


       // Query firstQuery = databaseReference.orderByKey().limitToFirst(3);


//        firstQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
//                    dataList.add(dataClass);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


     //   loadInitialItems();



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Method to set up the next query

    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        IntentFilter filter = new IntentFilter(ACTION_DATA_LOADED);
//        registerReceiver(dataReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(dataReceiver);
//    }


    private void loadInitialItems() {
        Query firstQuery = databaseReference.orderByKey().limitToFirst(maxLoad+1);

        firstQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        dataList.add(dataClass);
                        lastKnownKey = dataSnapshot.getKey();
                    }
                }
                if (!dataList.isEmpty()) {
                    dataList.remove(dataList.size() - 1);
                }

                adapter.notifyDataSetChanged();

                new Handler().postDelayed(() -> {
                    // Load next items based on the last known key
                    loadNextItems();
                }, 2000); // Delay in milliseconds (2000 ms = 2 seconds)

                // Load next items based on the last known key
               // loadNextItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadNextItems() {
        if (lastKnownKey != null) {
            Query nextQuery = databaseReference.orderByKey().startAt(lastKnownKey).limitToFirst(maxLoad+1);
            nextQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                        if (dataClass != null) {
                            dataList.add(dataClass);
                            lastKnownKey = dataSnapshot.getKey();
                        }
                    }
                    if (!dataList.isEmpty()) {
                        dataList.remove(dataList.size() - 1);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }
}