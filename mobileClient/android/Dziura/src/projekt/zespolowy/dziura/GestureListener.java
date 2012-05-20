package projekt.zespolowy.dziura;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.widget.Toast;

/**
 * Klasa obslugujaca gesty. Implementuje interfejs {@link android.gesture.GestureOverlayView.OnGesturePerformedListener}.
 *
 */
public class GestureListener implements OnGesturePerformedListener
{

	private GestureLibrary mLibrary;
	private DziuraActivity mContext;
	
	/**
	 * Konstruktor klasy {@link GestureListener}.
	 * @param context instancja aktywnosci, ktorej obluga gestow ma dotyczyc
	 */
	public GestureListener(DziuraActivity context)
	{
		mContext = context;
	}
	
	/**
	 * W funkcji tej zostaja rozpoznane wykonane gesty i w zaleznosci od tego, w jakim widoku aplikacja aktualnie
	 * sie znajduje i jaki gest zostal wykryty, nastepuje odpowiednia reakcja - przelaczenie widoku. Jezeli wykonany gest
	 * nie zostal odpowiednio zinterpretowany, to wyswietlona zostanie na ekranie informacja, ze gest nie zostal rozpoznany.
	 * 
	 * @param arg0 widok, na ktorym wykonany zostal gest
	 * @param gesture jaki gest zostal wykonany
	 */
	/* (non-Javadoc)
	 * @see android.gesture.GestureOverlayView.OnGesturePerformedListener#onGesturePerformed(android.gesture.GestureOverlayView, android.gesture.Gesture)
	 */
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
			if (prediction.score > 15.0) {

				// Show the spell
				if (prediction.name.contentEquals("leftToRight"))
				{
					if(mContext.tabHost.getCurrentTab() == 1)
					{
						mContext.tabHost.setCurrentTab(0);
					}
					else
					{
						mContext.wyswietlTekst("Aby przejœc do widoku formularza, wykonaj gest w drug¹ stronê.");
					}
				} else if (prediction.name.contentEquals("rightToLeft")) {
					if(mContext.tabHost.getCurrentTab() == 0)
					{
						mContext.tabHost.setCurrentTab(1);
					}
					else
					{
						mContext.wyswietlTekst("Aby przejœc do widoku zdjêæ, wykonaj gest w drug¹ stronê.");
					}
				} else {
					mContext.wyswietlTekst("Nie rozpoznano gestu, spróbuj jeszcze raz.");
				}
			}
		}
	}
}
