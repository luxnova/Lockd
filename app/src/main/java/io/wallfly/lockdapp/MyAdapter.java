package io.wallfly.lockdapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

// must use onItemClickListener but don't know how to implement
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements AdapterView.OnItemClickListener {
    private List<ItemData> itemsData = new ArrayList<>();
    int position;

    public MyAdapter(List<ItemData> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
        viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());

        viewHolder.txtViewTitle.setOnItemClickListener(this);
        viewHolder.imgViewIcon.setOnItemClickListener(this);

        if(position == positionOfPartition)
        {
            viewHolder.imgViewIcon..setVisibility(View.GONE);
            
            //Now just set the font size and look via viewHolder.txtViewTitle....
        }
        this.position = position;

    }

    @Override
    public void onItemClick(AdapterView<?>, View v, int position, long vid) {
        int id = v.getId();

        switch (id)
        {
            case R.id.item_title:
                if(position == 0)
                {
                    //Create Lock activity tasks
                }
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTitle;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);




        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
