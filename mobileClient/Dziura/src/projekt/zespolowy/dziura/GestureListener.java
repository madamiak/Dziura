package projekt.zespolowy.dziura;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GestureListener implements OnGestureListener, OnGesturePerformedListener
{

	private GestureLibrary mLibrary;
	private DziuraActivity mContext;
	
	public GestureListener(DziuraActivity c) {
		mContext = c;
	}
	
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {

    	
		mLibrary = GestureLibraries.fromRawResource(mContext, R.raw.gestures);
        if (!mLibrary.load()) {
        	Toast.makeText(mContext, "Error while loading gesture library.", Toast.LENGTH_SHORT).show();
        }
				
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		// We want at least one prediction
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			// We want at least some confidence in the result
			if (prediction.score > 1.0) {

				// Show the spell
				if (prediction.name.contentEquals("rightToLeft") && mContext.isCameraViewSet == false) {
					//mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					mContext.isCameraViewSet = true;
					mContext.vCamera.resume();
					mContext.vOption.mOptionsView.setVisibility(View.GONE);
					mContext.vCamera.mCameraView.setVisibility(View.VISIBLE);
					if(mContext.appMenu != null)
					{
						mContext.appMenu.getMenu().setGroupVisible(1, false);
						mContext.appMenu.getMenu().setGroupVisible(0, true);
					}
				} else if (prediction.name.contentEquals("leftToRight") && mContext.isCameraViewSet) {
					//mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					mContext.isCameraViewSet = false;
					mContext.vCamera.mCameraView.setVisibility(View.GONE);
					mContext.vOption.mOptionsView.setVisibility(View.VISIBLE);
					if(mContext.appMenu != null)
					{
						mContext.appMenu.getMenu().setGroupVisible(0, false);
						if(mContext.vOption.cGPS.isChecked()!=true)
						{
							mContext.appMenu.getMenu().setGroupVisible(1, true);
						}
						else
						{
							mContext.appMenu.getMenu().setGroupVisible(1, false);
						}
					}
				}
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
}
