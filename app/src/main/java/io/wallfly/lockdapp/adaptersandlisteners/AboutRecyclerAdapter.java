package io.wallfly.lockdapp.adaptersandlisteners;

/**
 * Created by JoshuaWilliams on 6/4/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;
import java.util.List;

import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.Library;

public class AboutRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "AboutAdapter";
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Library> libraries = new ArrayList<>();
    int holdSeconds;
    private View mHeaderView;
    private Activity activity;
    public AboutRecyclerAdapter(Activity activity, List<Library> itemsData) {
        this.libraries = itemsData;
        this.activity = activity;
    }

    // Create new views (invoked by layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER){
            mHeaderView = LayoutInflater.from(activity).inflate(R.layout.about_header, null);
            return new HeaderViewHolder(mHeaderView);
        }
        // create ViewHolder
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, null));
    }


    // Return the size of your libraries (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return libraries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int pos) {

        if(viewHolder instanceof LibraryViewHolder){
            LibraryViewHolder libraryViewHolder = (LibraryViewHolder) viewHolder;
            final Library library = getItem(pos);
            libraryViewHolder.authorTV.setText(library.getAuthor());
            libraryViewHolder.descriptionTV.setText(library.getDescription());
            libraryViewHolder.titleTV.setText(library.getTitle());
            
            libraryViewHolder.subParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i(LOG_TAG, "Long click");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLink()));
                    activity.startActivity(browserIntent);
                    return true;
                }
            });

            }else{
            Utils.getImageLoaderWithConfig().displayImage("drawable://" + R.drawable.lockd_logo_hires,
                    ((HeaderViewHolder) viewHolder).logo,
                    Utils.getDisplayImageOptions());
        }

    }



    public Library getItem(int position){
        return libraries.get(position);
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView logo;
        public HeaderViewHolder(View view) {
            super(view);
            logo = (ImageView) view.findViewById(R.id.lockd_logo);
            text = (TextView) view.findViewById(R.id.textView);
            text.setTypeface(Utils.getRoboto());
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTV, descriptionTV, titleTV;
        public RelativeLayout subParent;
        public CardView layout;

        public LibraryViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            authorTV = (TextView) itemLayoutView.findViewById(R.id.author_text_view);
            descriptionTV = (TextView) itemLayoutView.findViewById(R.id.description_text);
            titleTV = (TextView) itemLayoutView.findViewById(R.id.library_title);
            subParent = (RelativeLayout) itemLayoutView.findViewById(R.id.sub_parent);
            layout = (CardView) itemLayoutView.findViewById(R.id.card_view);

            descriptionTV.setTypeface(Utils.getRoboto());
            authorTV.setTypeface(Utils.getRoboto());
            titleTV.setTypeface(Utils.getRoboto());

        }
    }

}


