package projekt.zespolowy.dziura.Menu;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.R;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MyManue class implements all menu options from application. 
 */
public class MyMenu {

	private static final int MENU_QUIT = 0;
	private Menu mMenu;
	private DziuraActivity app;
	
	public MyMenu(DziuraActivity d, Menu m) {
		mMenu = m;
		app = d;
		mMenu = CameraMenu.init(mMenu, app.vCamera.camera);
		mMenu = MapMenu.init(mMenu);
		mMenu.add(2, MENU_QUIT, 0, R.string.menu_quit);
		if(app.isCameraViewSet)
		{
			mMenu.setGroupVisible(0, true);
			mMenu.setGroupVisible(1, false);
		}
		else
		{
			mMenu.setGroupVisible(0, false);
			mMenu.setGroupVisible(1, true);	
		}
	}
	
	/**
	 * Provides execution of selected functionality.
	 * 
	 * @param featureId		Element id which was pushed.
	 * @param item			Menu item which was pushed.
	 * @return				True if execution ends succeed.
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		CameraMenu.setParameter(item.getItemId(), app.vCamera.camera, app.vCamera.photoQuality, app.vCamera.inPreview, app.isCameraViewSet, app.vCamera.lCameraPreview);
		MapMenu.setParameter(item.getItemId(), app.vOption.mapView);
		if (item.getItemId() == MENU_QUIT) {
			app.appExit();
		}
		return true;
	}
	
	/**
	 * Menu getter.
	 * 
	 * @return 	Menu object.
	 */
	public Menu getMenu() {
		return mMenu;
	}
}
