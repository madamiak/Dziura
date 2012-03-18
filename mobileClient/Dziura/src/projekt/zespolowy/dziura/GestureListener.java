package projekt.zespolowy.dziura;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.view.View;
import android.widget.Toast;

public class GestureListener implements OnGesturePerformedListener{

	private GestureLibrary mLibrary;
	private DziuraActivity mContext;
	
	GestureListener(DziuraActivity c) {
		mContext = c;
	}
	
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {

		mLibrary = GestureLibraries.fromRawResource(mContext, R.raw.spells);
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
				if (prediction.name.contentEquals("Thunder Spell") && mContext.isCameraViewSet == false) {
					mContext.isCameraViewSet = true;
					mContext.mOptionsView.setVisibility(View.GONE);
					mContext.mCameraView.setVisibility(View.VISIBLE);
					Toast.makeText(mContext, "To camera preview!", Toast.LENGTH_SHORT).show();
				} else if (prediction.name.contentEquals("Thunder Spell")) {
					mContext.isCameraViewSet = false;
					mContext.mCameraView.setVisibility(View.GONE);
					mContext.mOptionsView.setVisibility(View.VISIBLE);
					
					Toast.makeText(mContext, "To options view!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
