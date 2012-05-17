package projekt.zespolowy.dziura.Map;

import java.util.ArrayList;
import java.util.List;

import projekt.zespolowy.dziura.DziuraActivity;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Klasa warstwy nakladanej na widok mapy ({@link MyMapView}). Na warstwie tej umieszczony zostaje wskaznik
 * miejsca zglaszanej szkody, w punkcie nacisniecia mapy. Rozszerza klase abstrakcyjna
 * <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/ItemizedOverlay?hl=pl-PL">
 * ItemizedOverlay</a>.
 *
 */
public class MyItemizedOverlay extends com.google.android.maps.ItemizedOverlay<OverlayItem>
{
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private DziuraActivity dziuraAct;

	/**
	 * Konstruktor klasy {@link MyItemizedOverlay}.
	 * @param defaultMarker obiekt reprezentujacy rysowany znacznik
	 * @param dziuraAct referencja do instancji klasy glownej aktywnosci aplikacji
	 */
	public MyItemizedOverlay(Drawable defaultMarker, DziuraActivity dziuraAct)
	{
		super(boundCenterBottom(defaultMarker));
		this.dziuraAct = dziuraAct;
	}
	
	/**
	 * Funkcja dodajaca do listy punkt, ktory oznaczony zostanie na mapie znacznikiem.
	 * Jezeli jakis punkt istnieje, to lista jest czyszczona, poniewaz dopuszczalny jest tylko
	 * jeden punkt zaznaczony na mapie.
	 * @param overlay punkt w ktorym zostanie wyswietlony znacznik
	 */
	public void addOverlay(OverlayItem overlay)
	{
	    if(mOverlays.size()==0) //add only when there are no others
	    {
	    	mOverlays.add(overlay);
	    	populate();
	    }
	    else
	    {
	    	mOverlays.clear();
	    	mOverlays.add(overlay);
	    	populate();
	    }
	}

	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	/**
	 * Funkcja zwraca rozmiar listy. W tym wypadku bedzie to 0 lub 1.
	 * 
	 * @return <li>rozmiar listy
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	protected boolean onTap(int index) //when tap on marker
	{
//		OverlayItem item = mOverlays.get(index);
//		GeoPoint point = item.getPoint();
		return true;
	}
	
	/**
	 * Funkcja wywolywana po dotknieciu widoku mapy. Odczytuje wspolrzedne punktu, ktory zostal nacisniety i dodaje
	 * w tym miejscu znacznik. Uruchamiane jest rowniez zadanie {@link GeocodingTask} ktore wyswietli adres zaznaczonego miejsca.
	 * 
	 * @param p punkt, w ktorym mapa zostala nacisnieta
	 * @param mapView widok mapy, ktorego dotyczy zdarzenie dotkniecia 
	 * @return <li>true - zawsze
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
	 */
	public boolean onTap(GeoPoint p, MapView mapView)
	{
		dziuraAct.vOption.point = p;
		List<Overlay> mapOverlays = dziuraAct.vOption.mapView.getOverlays();
		OverlayItem overlayitem = new OverlayItem(dziuraAct.vOption.point, null, null);
		dziuraAct.vOption.itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.remove(mapOverlays.size()-1);
		mapOverlays.add(dziuraAct.vOption.itemizedOverlay);
		dziuraAct.vOption.isMarkerAdded = true;
		GeocodingTask geocode = new GeocodingTask(dziuraAct, false);
		geocode.execute();	
		return true;
	}
}
