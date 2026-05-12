package com.envanter.android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EnvanterListActivity extends AppCompatActivity {

    private RecyclerView rvEnvanterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envanter_list);

        rvEnvanterList = findViewById(R.id.rvEnvanterList);
        rvEnvanterList.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Envanter listesi adapter'i ve API entegrasyonu yapilacak
    }
}
