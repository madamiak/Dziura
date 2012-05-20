package projekt.zespolowy.dziura.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

/**
 * Klasa reprezentujaca widok mapy. Dziedziczy po klasie 
 * <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/MapView?hl=pl-PL">
 * MapView</a>.
 */
public class MyMapView extends MapView
{
	/**
	 * Konstruktor klasy {@link MyMapView}. Wywoluje konstruktor klasy nadrzednej, czyli
	 * <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/MapView?hl=pl-PL">
	 * MapView</a>, z podanymi parametrami.
	 * Sam konstruktor wywolywany jest samoczynnie przez aktywnosc podczas tworzenia widoku. Nie jest wywolywany w kodzie aplikacji.
	 * @param context kontekst aktywnosci aplikacji
	 * @param attrs zbior atrybutow
	 */
	public MyMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Funkcja wywolywana po dotknieciu widoku mapy. Zostala przeciazona, aby podczas przesuwania mapy
	 * nie przesuwal sie caly widok aplikacji oparty na {@link android.widget.ScrollView}.\
	 * @param ev zdarzenie dotkniecia mapy
	 * @return <li>true - zawsze
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapView#onTouchEvent(android.view.MotionEvent)
	 */
	public boolean onTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
	    switch (action) {
	    case MotionEvent.ACTION_DOWN:
	        // Disallow ScrollView to intercept touch events.
	        this.getParent().requestDisallowInterceptTouchEvent(true);
	        break;

	    case MotionEvent.ACTION_UP:
	        // Allow ScrollView to intercept touch events.
	        this.getParent().requestDisallowInterceptTouchEvent(false);
	        break;
	    }

	    // Handle MapView's touch events.
	    super.onTouchEvent(ev);
		return true;
	}
	
}
