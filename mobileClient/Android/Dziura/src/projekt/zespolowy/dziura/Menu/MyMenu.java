package projekt.zespolowy.dziura.Menu;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.R;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Klasa {@link MyMenu} implementuje cale menu, wlaczakac w to czesc aparatu ({@link CameraMenu})  i mapy ({@link MapMenu}).
 */
public class MyMenu
{
	private static final int MENU_QUIT = 0;
	private Menu mMenu;
	private DziuraActivity app;
	
	/**
	 * Konstruktor klasy {@link MyMenu}. Tworzy menu wspolne dla obu widokow oraz inicjalizuje {@link CameraMenu}  i {@link MapMenu}.
	 * @param d aktywnosc, dla ktorej menu jest tworzone
	 * @param m instnacja {@link android.view.Menu} ktora zostanie zmodyfikowana
	 */
	public MyMenu(DziuraActivity d, Menu m)
	{
		mMenu = m;
		app = d;
		mMenu = CameraMenu.init(mMenu, app.vCamera.camera);
		mMenu = MapMenu.init(mMenu);
		mMenu.add(2, MENU_QUIT, 0, R.string.menu_quit);
		if(app.tabHost.getCurrentTab() == 0)
		{
			mMenu.setGroupVisible(0, true);
			mMenu.setGroupVisible(1, false);
		}
		else
		{
			mMenu.setGroupVisible(0, false);
			if(app.vOption.cGPS.isChecked()!=true)
			{
				mMenu.setGroupVisible(1, true);
			}
			else
			{
				mMenu.setGroupVisible(1, false);
			}	
		}
	}
	
	/**
	 * Zapewnia wykonanie odpowiedniej akcji, w zaleznosci od wybranej z menu opcji.
	 * 
	 * @param featureId		Identyfikator elementu wybranego z menu.
	 * @param item			Obiekt wybranej z menu opcji.
	 * @return				<li>true - zawsze
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		CameraMenu.setParameter(item.getItemId(), app.vCamera.camera, app.vCamera.photoQuality, app.vCamera.inPreview, app.tabHost.getCurrentTab(), app.vCamera.lCameraPreview);
		MapMenu.setParameter(item.getItemId(), app.vOption.mapView);
		if (item.getItemId() == MENU_QUIT) {
			app.appExit();
		}
		return true;
	}
	
	/**
	 * Getter {@link android.view.Menu}.
	 * 
	 * @return 	<li>obiekt {@link MyMenu}
	 */
	public Menu getMenu() {
		return mMenu;
	}
}
