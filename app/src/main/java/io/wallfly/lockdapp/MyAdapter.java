package io.wallfly.lockdapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

// must use onItemClickListener but don't know how to implement
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {
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

        if (position == 4) {
            viewHolder.imgViewIcon.setVisibility(View.GONE);

            //Need to find a way to convert this to 100DP, current solution is:
            // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65,
            // getResources().getDisplayMetrics());
            //But this does NOT work because the activity has no context
            //Need to find a way to use context to use getResources()
            viewHolder.txtViewTitle.setHeight(150);
        }

        // Conditional used to set checkbox ONLY visible on first row of recyclerview
        // May not be efficient programming, look at later
        if (position != 0) {
            viewHolder.chkBox.setVisibility(View.GONE);
        }


//        viewHolder.txtViewTitle.setOnItemClickListener(this);
//        viewHolder.imgViewIcon.setOnItemClickListener(this);

        this.position = position;

    }

//    @Override
//    public void onItemClick(AdapterView<?>, View v, int position, long vid) {
//        int id = v.getId();
//
//        switch (id)
//        {
//            case R.id.item_title:
//                if(position == 0)
//                {
//                    //Create Lock activity tasks
//                }
//        }
//    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        public CheckBox chkBox;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            chkBox = (CheckBox) itemLayoutView.findViewById(R.id.item_checkbox);




        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
