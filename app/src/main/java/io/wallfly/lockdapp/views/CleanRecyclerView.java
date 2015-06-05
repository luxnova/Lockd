package io.wallfly.lockdapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by JoshuaWilliams on 6/4/15.
 *
 * @version 1.0
 *
 * This recycler view catches an exception thrown when the smooth scroll is still animating and the activity is destroyed.
 * Thats all it does.
 */

public class CleanRecyclerView extends RecyclerView
{
    public CleanRecyclerView(Context context)
    {
        super( context );
    }

    public CleanRecyclerView(Context context, AttributeSet attrs)
    {
        super( context, attrs );
    }

    public CleanRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super( context, attrs, defStyle );
    }

    @Override
    public void stopScroll()
    {
        try
        {
            super.stopScroll();
        }
        catch( NullPointerException exception )
        {
            /**
             *  The mLayout has been disposed of before the
             *  RecyclerView and this stops the application
             *  from crashing.
             */
        }
    }
}