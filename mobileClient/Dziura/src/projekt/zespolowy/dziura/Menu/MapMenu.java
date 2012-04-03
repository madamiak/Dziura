package projekt.zespolowy.dziura.Menu;

import com.google.android.maps.MapView;

import projekt.zespolowy.dziura.R;
import android.view.Menu;
import android.view.SubMenu;

/**
 * MapMenu class implements part of menu relating to map with changing it's (map) parameters.
 */
public class MapMenu {

	private static final int MENU_MAP_MODE = 21;
	private static final int MENU_MAP_MODE_MAP = 2100;
	private static final int MENU_MAP_MODE_SATELLITE = 2101;
	
	/**
	 * Attach map menu elements to given menu.
	 * 
	 * @param menu	Menu which need map menu items.
	 * @return		Given menu with attached map menu items. 
	 */
	public static Menu init(Menu menu) {
		SubMenu subMap = menu.addSubMenu(1, MENU_MAP_MODE, 0, R.string.menu_map_mode);
		subMap.add(1, MENU_MAP_MODE_MAP, 0, R.string.menu_map_mode_map);
		subMap.add(1, MENU_MAP_MODE_SATELLITE, 0, R.string.menu_map_mode_satellite);
		return menu;
	}
	
	/**
	 * Sets given parameter.
	 * 
	 * @param parameterId	Id of parameter describes which parameter will be changed.
	 * @param mapView		Map object whose parameter will be changed.
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
