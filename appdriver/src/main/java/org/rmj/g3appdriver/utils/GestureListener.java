package org.rmj.g3appdriver.utils;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.rmj.g3appdriver.etc.OnSwipeListener;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100; // Minimum distance in pixels
    private static final int SWIPE_VELOCITY_THRESHOLD = 100; // Minimum velocity

    private final OnSwipeListener loSwipeListener;

    public GestureListener(Activity foActivity){
        this.loSwipeListener = (OnSwipeListener) foActivity;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true; // Must return true for other events to be detected
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                // Horizontal swipe
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        loSwipeListener.OnSwipeRight();
                    } else {
                        loSwipeListener.OnSwipeLeft();
                    }
                    return true;
                }
            } else {
                // Vertical swipe
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        loSwipeListener.OnSwipeDown();
                    } else {
                        loSwipeListener.OnSwipeUp();
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
