package io.wallfly.lockdapp;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        List<ItemData> itemsData = new ArrayList<>();


        itemsData.add(new ItemData("Create Lock", R.drawable.ic_launcher));

        itemsData.add(new ItemData("Set Lock", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Delete Lock", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Help", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Changelog", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Terms of Service", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Support Development", R.drawable.ic_launcher));



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter mAdapter = new MyAdapter(itemsData);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}