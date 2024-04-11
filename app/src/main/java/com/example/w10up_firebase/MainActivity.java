package com.example.w10up_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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



    private int totalPictures = 50; // Total number of pictures to load
    private int picturesLoaded = 0; // Counter to keep track of pictures loaded

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
        serviceIntent.putExtra("lastKnownKey", lastKnownKey);
        startService(serviceIntent);









        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });

        IntentFilter filter = new IntentFilter(ACTION_DATA_LOADED);
        LocalBroadcastManager.getInstance(this).registerReceiver(dataLoadedReceiver, filter);

        // Method to set up the next query

    }

    private BroadcastReceiver dataLoadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ACTION_DATA_LOADED)) {
//                ArrayList<DataClass> receivedDataList = intent.getParcelableArrayListExtra("dataList");
//                lastKnownKey = intent.getStringExtra("lastKnownKey");
//                if (receivedDataList != null) {
//                    dataList.clear();
//                    dataList.addAll(receivedDataList);
//                    adapter.notifyDataSetChanged();
//                }
//            }
            if (intent.getAction().equals(ACTION_DATA_LOADED)) {
                ArrayList<DataClass> receivedDataList = intent.getParcelableArrayListExtra("dataList");
                String receivedLastKnownKey = intent.getStringExtra("lastKnownKey");
                lastKnownKey = receivedLastKnownKey;
                if (receivedDataList != null) {
                    dataList.addAll(receivedDataList);
                    adapter.notifyDataSetChanged();
                }
                picturesLoaded += 2; // Increment the counter by 2 since each service loads 2 pictures

                // Check if all pictures are loaded
                if (picturesLoaded < totalPictures) {
                    // If not, start loading the next set of pictures
                    loadPictures();
                }
            }
        }
    };


    // Unregister the BroadcastReceiver in onDestroy method to avoid memory leaks
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataLoadedReceiver);
    }

    private void loadPictures() {
        // Start the service to load pictures
        Intent serviceIntent = new Intent(this, FirebaseLoadService.class);
        serviceIntent.putExtra("lastKnownKey", lastKnownKey);
        startService(serviceIntent);
    }






}