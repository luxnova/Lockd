package io.wallfly.lockdapp.adaptersandlisteners;

import android.animation.Animator;
import android.view.View;

/**
 * Created by JoshuaWilliams on 5/25/15.
 */
public class ViewGoneAnimationListener implements Animator.AnimatorListener {
    View view;
    public ViewGoneAnimationListener(){
        setView(null);
    }
    public ViewGoneAnimationListener(View view){
        setView(view);
    }


    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
         getView().setVisibility(View.INVISIBLE);
        if(getView().getTag() != null) ((View)getView().getTag()).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }



    public View getView(){return  view;}

    public void setView(View view) {
        this.view = view;
    }

    public ViewGoneAnimationListener getAnimation(){return this;}
}
