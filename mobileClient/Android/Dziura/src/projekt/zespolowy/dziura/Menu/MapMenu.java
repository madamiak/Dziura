package projekt.zespolowy.dziura.Menu;

import com.google.android.maps.MapView;

import projekt.zespolowy.dziura.R;
import android.view.Menu;
import android.view.SubMenu;

/**
 * Klasa ta implementuje czesc menu dotyczaca mapy. Umozliwia zmiane jej opcji.
 */
public class MapMenu {

	private static final int MENU_MAP_MODE = 21;
	private static final int MENU_MAP_MODE_MAP = 2100;
	private static final int MENU_MAP_MODE_SATELLITE = 2101;
	
	/**
	 * Inicjalizuje menu.
	 * 
	 * @param menu	obiekt {@link android.view.Menu}, do ktorego zostana przypisane elementu dotyczace mapy
	 * @return		<li>obiekt {@link android.view.Menu} zmodyfikowany o opcje zwiazane z mapa
	 */
	public static Menu init(Menu menu) {
		SubMenu subMap = menu.addSubMenu(1, MENU_MAP_MODE, 0, R.string.menu_map_mode);
		subMap.add(1, MENU_MAP_MODE_MAP, 0, R.string.menu_map_mode_map);
		subMap.add(1, MENU_MAP_MODE_SATELLITE, 0, R.string.menu_map_mode_satellite);
		return menu;
	}
	
	/**
	 * Ustawia dane parametry.
	 * 
	 * @param parameterId	identyfikator parametru, ktory zostanie zmieniony
	 * @param mapView		obiekt mapy, ktora zostanie zmodyfikowana
	 */
	public static void setParameter(int parameterId, MapView mapView) {
		switch(parameterId)
		{
			case MENU_MAP_MODE_MAP:
				mapView.setSatellite(false);
				break;
			case MENU_MAP_MODE_SATELLITE:
				mapView.setSatellite(true);
				break;
		}
	}
}
