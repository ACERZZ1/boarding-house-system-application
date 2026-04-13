package com.example.boardinghousefinder.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.adapters.FlexibleAdapter;
import com.example.boardinghousefinder.models.BoardingHouse;

import java.util.ArrayList;
import java.util.List;

public class FullListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<BoardingHouse> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("title");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewFull);
        list = new ArrayList<>();

        // TEMP: sample data (later database)
        list.add(new BoardingHouse("Room A", "₱2000", "Nice", R.drawable.test2));
        list.add(new BoardingHouse("Room B", "₱1800", "Cheap", R.drawable.test2));
        list.add(new BoardingHouse("Room A", "₱2000", "Nice", R.drawable.test2));
        list.add(new BoardingHouse("Room B", "₱1800", "Cheap", R.drawable.test2));


        FlexibleAdapter adapter = new FlexibleAdapter(this, list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    // Back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}