package io.wallfly.lockdapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

// must use onItemClickListener but don't know how to implement
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private List<ItemData> itemsData = new ArrayList<>();
    private MainActivity mActivity;

    public MyAdapter(MainActivity activity, List<ItemData> itemsData) {
        this.mActivity = activity;
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
        viewHolder.imgViewIcon.setTag(position);
        viewHolder.txtViewTitle.setTag(position);
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
        viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());

        if (position == 4) {
            viewHolder.imgViewIcon.setVisibility(View.GONE);


            viewHolder.txtViewTitle.setHeight(150);
        }

        // Conditional used to set checkbox ONLY visible on first row of recyclerview
        // May not be efficient programming, look at later
        if (position != 0) {
            viewHolder.chkBox.setVisibility(View.GONE);
        }

        //need to getWindow to disable system lock
        if (viewHolder.chkBox.isChecked()) {

            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        }



        viewHolder.txtViewTitle.setOnClickListener(this);
        viewHolder.imgViewIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // testing onClick and is a placeholder for actual tasks
        int position = (int) v.getTag();
        switch (position) {
            case 0:
                Toast.makeText(v.getContext(), "Enable Lock", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(v.getContext(), "Notification Settings", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(v.getContext(), "Privacy Settings", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(v.getContext(), "Reset Lock", Toast.LENGTH_SHORT).show();
                break;
//            case 4:
//                // do nothing as 4th position is partition dividing settings
//                break;
            case 5:
                Toast.makeText(v.getContext(), "About", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(v.getContext(), "Privacy Policy", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                Toast.makeText(v.getContext(), "Help", Toast.LENGTH_SHORT).show();
                break;
            default:
                // do nothing
                break;
        }
    }

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


