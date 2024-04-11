package com.example.w10up_firebase;

import static android.content.Intent.getIntent;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseLoadService extends IntentService {
    private static final String TAG = "FirebaseLoadService";
    private static final int MAX_LOAD = 2; // Set your desired max load

    private DatabaseReference databaseReference;
    private List<DataClass> dataList;
    private String lastKnownKey;

    public FirebaseLoadService() {
        super("FirebaseLoadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        dataList = new ArrayList<>();
      //  lastKnownKey = getIntent().getStringExtra("lastKnownKey");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            lastKnownKey = intent.getStringExtra("lastKnownKey");
            // Use the lastKnownKey here in your query
           // loadItems(lastKnownKey);
        }
        loadItems();
    }

    private void loadItems() {
        Query query;
        if (lastKnownKey == null) {
            query = databaseReference.orderByKey().limitToFirst(MAX_LOAD + 1);
        } else {
            query = databaseReference.orderByKey().startAt(lastKnownKey).limitToFirst(MAX_LOAD + 1);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {


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

                Intent intent = new Intent(MainActivity.ACTION_DATA_LOADED);
                intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
                intent.putExtra("lastKnownKey", lastKnownKey);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            }

            @Override
            public void onCancelled( @NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
    }
}
