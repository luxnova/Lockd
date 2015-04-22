package io.wallfly.lockdapp;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateActivity();
    }

    private void initiateActivity() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        List<ItemData> itemsData = new ArrayList<>();

        // R.drawable.ic_launcher is used as a blank icon template for now
        // until custom icons are created
        itemsData.add(new ItemData("Enable Lockd", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Notification Settings", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Settings", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Reset Lock", R.drawable.ic_launcher));
        //position of Other ItemData title is 4, this is used for the
        //partition used to separate settings
        itemsData.add(new ItemData("Other", R.drawable.ic_launcher));
        itemsData.add(new ItemData("About", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Policy", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Help", R.drawable.ic_launcher));



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter mAdapter = new MyAdapter(this, itemsData);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        initiateActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        initiateActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanUpActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        cleanUpActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanUpActivity();
    }

    /**
     * Clean up method, set global vars to null
     */
    private void cleanUpActivity() {

    }

}
