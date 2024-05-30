package sg.edu.np.mad.quizzzy.Models;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ViewFlipper;


public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final ViewFlipper viewFLipper;

    public SwipeGestureDetector(ViewFlipper viewFLipper){
        this.viewFLipper = viewFLipper;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getX() - e1.getY();
        if (Math.abs(diffX) > Math.abs(diffY)){
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                if (diffX > 0){
                    //Swipe right
                    viewFLipper.showPrevious();
                }
                else{
                    //Swipe left
                    viewFLipper.showNext();
                }
                return true;
            }
        }
        return false;
    }
}