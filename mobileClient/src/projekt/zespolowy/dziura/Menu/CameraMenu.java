package projekt.zespolowy.dziura.Menu;

import java.util.ArrayList;
import java.util.List;

import projekt.zespolowy.dziura.R;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;

/**
 * CameraMenu class implements part of menu relating to camera with changing it's (camera) parameters.
 */
public class CameraMenu {

	private static final int MENU_CAMERA_SCENE = 10;
	private static final int MENU_CAMERA_WHITEBALANCE = 11;
	private static final int MENU_CAMERA_QUALITY = 12;
	private static final int MENU_CAMERA_SCENE_AUTO = 1000;
	private static final int MENU_CAMERA_SCENE_ACTION = 1001;
	private static final int MENU_CAMERA_SCENE_PORTRAIT = 1002;
	private static final int MENU_CAMERA_SCENE_LANDSCAPE = 1003;
	private static final int MENU_CAMERA_SCENE_NIGHT = 1004;
	private static final int MENU_CAMERA_SCENE_NIGHPORTRAIT = 1005;
	private static final int MENU_CAMERA_SCENE_THEATRE = 1006;
	private static final int MENU_CAMERA_SCENE_BEACH = 1007;
	private static final int MENU_CAMERA_SCENE_SNOW = 1008;
	private static final int MENU_CAMERA_SCENE_SUNSET = 1009;
	private static final int MENU_CAMERA_SCENE_STEADYPHOTO = 1010;
	private static final int MENU_CAMERA_SCENE_FIREWORKS = 1011;
	private static final int MENU_CAMERA_SCENE_SPORTS = 1012;
	private static final int MENU_CAMERA_SCENE_PARTY = 1013;
	private static final int MENU_CAMERA_SCENE_CANDLELIGHT = 1014;
	private static final int MENU_CAMERA_QUALITY_LOW = 1200;
	private static final int MENU_CAMERA_QUALITY_MEDIUM = 1201;
	private static final int MENU_CAMERA_QUALITY_HIGH = 1202;
	private static final int MENU_CAMERA_QUALITY_VERYHIGH = 1203;
	private static final int MENU_CAMERA_WHITEBALANCE_AUTO = 1100;
	private static final int MENU_CAMERA_WHITEBALANCE_INCANDESCENT = 1101;
	private static final int MENU_CAMERA_WHITEBALANCE_FLUORESCENT = 1102;
	private static final int MENU_CAMERA_WHITEBALANCE_DAYLIGHT = 1103;
	private static final int MENU_CAMERA_WHITEBALANCE_CLOUDYDAYLIGHT = 1104;
	private static final int MENU_CAMERA_WHITEBALANCE_TWILIGHT = 1105;
	private static final int MENU_CAMERA_WHITEBALANCE_SHADE = 1106;
	private static final int MENU_CAMERA_FLASH_AUTO = 1300;
	private static final int MENU_CAMERA_FLASH_OFF = 1301;
	private static final int MENU_CAMERA_FLASH_ON = 1302;
	private static final int MENU_CAMERA_FLASH_REDEYE = 1303;
	private static final int MENU_CAMERA_FLASH_TORCH = 1304;
	private static final int MENU_CAMERA_FLASH = 13;
	private static final int MENU_CAMERA_FOCUS = 14;
	private static final int MENU_CAMERA_FOCUS_AUTO = 1400;
	private static final int MENU_CAMERA_FOCUS_FIXED = 1401;
	private static final int MENU_CAMERA_FOCUS_INFINITY = 1402;
	private static final int MENU_CAMERA_FOCUS_MACRO = 1403;
	
	/**
	 * Attach camera menu elements to given menu.
	 * 
	 * @param menu	Menu which need camera menu items.
	 * @return		Given menu with attached camera menu items. 
	 */
	public static Menu init(Menu menu, Camera camera)
	{
		List<String> tmp = new ArrayList<String>();
		tmp = camera.getParameters().getSupportedFlashModes();
		if(tmp != null)
		{
			SubMenu subFlash = menu.addSubMenu(0, MENU_CAMERA_FLASH, 0, R.string.menu_camera_flash);
			if(tmp.contains(Parameters.FLASH_MODE_AUTO))
				subFlash.add(0, MENU_CAMERA_FLASH_AUTO, 0, R.string.menu_camera_flash_auto);
			if(tmp.contains(Parameters.FLASH_MODE_OFF))
				subFlash.add(0, MENU_CAMERA_FLASH_OFF, 0, R.string.menu_camera_flash_off);
			if(tmp.contains(Parameters.FLASH_MODE_ON))
				subFlash.add(0, MENU_CAMERA_FLASH_ON, 0, R.string.menu_camera_flash_on);
			if(tmp.contains(Parameters.FLASH_MODE_RED_EYE))
				subFlash.add(0, MENU_CAMERA_FLASH_REDEYE, 0, R.string.menu_camera_flash_redeye);
			if(tmp.contains(Parameters.FLASH_MODE_TORCH))
				subFlash.add(0, MENU_CAMERA_FLASH_TORCH, 0, R.string.menu_camera_flash_torch);
		}
		tmp = camera.getParameters().getSupportedFocusModes();
		if(tmp != null)
		{
			SubMenu subFocus = menu.addSubMenu(0, MENU_CAMERA_FOCUS, 0, R.string.menu_camera_focus);
			if(tmp.contains(Parameters.FOCUS_MODE_AUTO))
				subFocus.add(0, MENU_CAMERA_FOCUS_AUTO, 0, R.string.menu_camera_focus_auto);
			if(tmp.contains(Parameters.FOCUS_MODE_FIXED))
				subFocus.add(0, MENU_CAMERA_FOCUS_FIXED, 0, R.string.menu_camera_focus_fixed);
			if(tmp.contains(Parameters.FOCUS_MODE_INFINITY))
				subFocus.add(0, MENU_CAMERA_FOCUS_INFINITY, 0, R.string.menu_camera_focus_infinity);
			if(tmp.contains(Parameters.FOCUS_MODE_MACRO))
				subFocus.add(0, MENU_CAMERA_FOCUS_MACRO, 0, R.string.menu_camera_focus_macro);
		}
		tmp = camera.getParameters().getSupportedSceneModes();
		if(tmp != null)
		{
			SubMenu subScene = menu.addSubMenu(0, MENU_CAMERA_SCENE, 0, R.string.menu_camera_scene);
			if(tmp.contains(Parameters.SCENE_MODE_AUTO))
				subScene.add(0, MENU_CAMERA_SCENE_AUTO, 0, R.string.menu_camera_scene_atuo);
			if(tmp.contains(Parameters.SCENE_MODE_ACTION))
				subScene.add(0, MENU_CAMERA_SCENE_ACTION, 0, R.string.menu_camera_scene_action);
			if(tmp.contains(Parameters.SCENE_MODE_PORTRAIT))
				subScene.add(0, MENU_CAMERA_SCENE_PORTRAIT, 0, R.string.menu_camera_scene_portrait);
			if(tmp.contains(Parameters.SCENE_MODE_LANDSCAPE))
				subScene.add(0, MENU_CAMERA_SCENE_LANDSCAPE, 0, R.string.menu_camera_scene_landscape);
			if(tmp.contains(Parameters.SCENE_MODE_NIGHT))
				subScene.add(0, MENU_CAMERA_SCENE_NIGHT, 0, R.string.menu_camera_scene_night);
			if(tmp.contains(Parameters.SCENE_MODE_NIGHT_PORTRAIT))
				subScene.add(0, MENU_CAMERA_SCENE_NIGHPORTRAIT, 0, R.string.menu_camera_scene_nightportrait);
			if(tmp.contains(Parameters.SCENE_MODE_THEATRE))
				subScene.add(0, MENU_CAMERA_SCENE_THEATRE, 0, R.string.menu_camera_scene_theatre);
			if(tmp.contains(Parameters.SCENE_MODE_BEACH))
				subScene.add(0, MENU_CAMERA_SCENE_BEACH, 0, R.string.menu_camera_scene_beach);
			if(tmp.contains(Parameters.SCENE_MODE_SNOW))
				subScene.add(0, MENU_CAMERA_SCENE_SNOW, 0, R.string.menu_camera_scene_snow);
			if(tmp.contains(Parameters.SCENE_MODE_SUNSET))
				subScene.add(0, MENU_CAMERA_SCENE_SUNSET, 0, R.string.menu_camera_scene_sunset);
			if(tmp.contains(Parameters.SCENE_MODE_STEADYPHOTO))
				subScene.add(0, MENU_CAMERA_SCENE_STEADYPHOTO, 0, R.string.menu_camera_scene_steadyphoto);
			if(tmp.contains(Parameters.SCENE_MODE_FIREWORKS))
				subScene.add(0, MENU_CAMERA_SCENE_FIREWORKS, 0, R.string.menu_camera_scene_fireworks);
			if(tmp.contains(Parameters.SCENE_MODE_SPORTS))
				subScene.add(0, MENU_CAMERA_SCENE_SPORTS, 0, R.string.menu_camera_scene_sports);
			if(tmp.contains(Parameters.SCENE_MODE_PARTY))
				subScene.add(0, MENU_CAMERA_SCENE_PARTY, 0, R.string.menu_camera_scene_party);
			if(tmp.contains(Parameters.SCENE_MODE_CANDLELIGHT))
				subScene.add(0, MENU_CAMERA_SCENE_CANDLELIGHT, 0, R.string.menu_camera_scene_candlelight);
		}
		tmp = camera.getParameters().getSupportedWhiteBalance();
		if(tmp != null)
		{
			SubMenu subWhiteBal = menu.addSubMenu(0, MENU_CAMERA_WHITEBALANCE, 0, R.string.menu_camera_whitebalance);
			if(tmp.contains(Parameters.WHITE_BALANCE_AUTO))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_AUTO, 0, R.string.menu_camera_whitebalance_auto);
			if(tmp.contains(Parameters.WHITE_BALANCE_INCANDESCENT))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_INCANDESCENT, 0, R.string.menu_camera_whitebalance_incandescent);
			if(tmp.contains(Parameters.WHITE_BALANCE_FLUORESCENT))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_FLUORESCENT, 0, R.string.menu_camera_whitebalance_fluorescent);
			if(tmp.contains(Parameters.WHITE_BALANCE_DAYLIGHT))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_DAYLIGHT, 0, R.string.menu_camera_whitebalance_daylight);
			if(tmp.contains(Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_CLOUDYDAYLIGHT, 0, R.string.menu_camera_whitebalance_cloudydaylight);
			if(tmp.contains(Parameters.WHITE_BALANCE_TWILIGHT))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_TWILIGHT, 0, R.string.menu_camera_whitebalance_twilight);
			if(tmp.contains(Parameters.WHITE_BALANCE_SHADE))
				subWhiteBal.add(0, MENU_CAMERA_WHITEBALANCE_SHADE, 0, R.string.menu_camera_whitebalance_shade);
		}
		SubMenu subQuality = menu.addSubMenu(0, MENU_CAMERA_QUALITY, 0, R.string.menu_camera_quality);
		subQuality.add(0, MENU_CAMERA_QUALITY_LOW, 0, R.string.menu_camera_quality_low);
		subQuality.add(0, MENU_CAMERA_QUALITY_MEDIUM, 0, R.string.menu_camera_quality_medium);
		subQuality.add(0, MENU_CAMERA_QUALITY_HIGH, 0, R.string.menu_camera_quality_high);
		subQuality.add(0, MENU_CAMERA_QUALITY_VERYHIGH, 0, R.string.menu_camera_quality_veryhigh);

		return menu;
	}
	
	/**
	 * Sets given parameter.
	 * 
	 * @param parameterId			Id of parameter describes which parameter will be changed.
	 * @param camera				Camera object whose parameter will be changed.
	 * @param photoQuality			Actual photo quality.
	 * @param inPreview
	 * @param isCameraViewSet		Describes which view context is now - camera or other.
	 * @param lCameraPreview
	 */
	public static void setParameter(int parameterId, Camera camera, int photoQuality, boolean inPreview, boolean isCameraViewSet, LinearLayout lCameraPreview) {
		switch(parameterId)
		{
			case MENU_CAMERA_SCENE_AUTO:
				setScene(Parameters.SCENE_MODE_AUTO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_ACTION:
				setScene(Parameters.SCENE_MODE_ACTION, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_PORTRAIT:
				setScene(Parameters.SCENE_MODE_PORTRAIT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_LANDSCAPE:
				setScene(Parameters.SCENE_MODE_LANDSCAPE, camera, inPreview, isCameraViewSet, lCameraPreview);	
				break;
			case MENU_CAMERA_SCENE_NIGHT:
				setScene(Parameters.SCENE_MODE_NIGHT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_NIGHPORTRAIT:
				setScene(Parameters.SCENE_MODE_NIGHT_PORTRAIT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_THEATRE:	
				setScene(Parameters.SCENE_MODE_THEATRE, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_BEACH:
				setScene(Parameters.SCENE_MODE_BEACH, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_SNOW:
				setScene(Parameters.SCENE_MODE_SNOW, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_SUNSET:
				setScene(Parameters.SCENE_MODE_SUNSET, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_STEADYPHOTO:
				setScene(Parameters.SCENE_MODE_STEADYPHOTO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_FIREWORKS:
				setScene(Parameters.SCENE_MODE_FIREWORKS, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_SPORTS:
				setScene(Parameters.SCENE_MODE_SPORTS, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_PARTY:
				setScene(Parameters.SCENE_MODE_PARTY, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_SCENE_CANDLELIGHT:
				setScene(Parameters.SCENE_MODE_CANDLELIGHT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_AUTO:
				setWhiteBalance(Parameters.WHITE_BALANCE_AUTO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_INCANDESCENT:
				setWhiteBalance(Parameters.WHITE_BALANCE_INCANDESCENT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_FLUORESCENT:
				setWhiteBalance(Parameters.WHITE_BALANCE_FLUORESCENT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_DAYLIGHT:
				setWhiteBalance(Parameters.WHITE_BALANCE_DAYLIGHT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_CLOUDYDAYLIGHT:
				setWhiteBalance(Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_TWILIGHT:
				setWhiteBalance(Parameters.WHITE_BALANCE_TWILIGHT, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_WHITEBALANCE_SHADE:
				setWhiteBalance(Parameters.WHITE_BALANCE_SHADE, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_QUALITY_LOW:
				setQuality(30, photoQuality);
				break;
			case MENU_CAMERA_QUALITY_MEDIUM:
				setQuality(50, photoQuality);
				break;
			case MENU_CAMERA_QUALITY_HIGH:
				setQuality(70, photoQuality);
				break;
			case MENU_CAMERA_QUALITY_VERYHIGH:
				setQuality(90, photoQuality);
				break;
			case MENU_CAMERA_FLASH_AUTO:
				setFlash(Parameters.FLASH_MODE_AUTO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FLASH_OFF:
				setFlash(Parameters.FLASH_MODE_OFF, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FLASH_ON:
				setFlash(Parameters.FLASH_MODE_ON, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FLASH_REDEYE:
				setFlash(Parameters.FLASH_MODE_RED_EYE, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FLASH_TORCH:
				setFlash(Parameters.FLASH_MODE_TORCH, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FOCUS_AUTO:
				setFocus(Parameters.FOCUS_MODE_AUTO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FOCUS_FIXED:
				setFocus(Parameters.FOCUS_MODE_FIXED, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FOCUS_INFINITY:
				setFocus(Parameters.FOCUS_MODE_INFINITY, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
			case MENU_CAMERA_FOCUS_MACRO:
				setFocus(Parameters.FOCUS_MODE_MACRO, camera, inPreview, isCameraViewSet, lCameraPreview);
				break;
		}
	}
	
	
	//TODO: wszedzie jest stop camera i start camera, moze dac osobne metody do tego?
	
	private static void setScene(String scene, Camera camera, boolean inPreview, boolean isCameraViewSet, LinearLayout lCameraPreview)
	{
		if(inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.stopPreview();
			inPreview = false;
		}
		Parameters par = camera.getParameters();
		par.setSceneMode(scene);
		camera.setParameters(par);
		if(!inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.startPreview();
			inPreview = true;
		}
	}
	
	private static void setQuality(int quality, int photoQuality)
	{
		photoQuality = quality;
	}
	
	private static void setWhiteBalance(String whBalance, Camera camera, boolean inPreview, boolean isCameraViewSet, LinearLayout lCameraPreview)
	{
		if(inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.stopPreview();
			inPreview = false;
		}
		Parameters par = camera.getParameters();
		par.setWhiteBalance(whBalance);
		camera.setParameters(par);
		if(!inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.startPreview();
			inPreview = true;
		}
	}

	private static void setFlash(String flash, Camera camera, boolean inPreview, boolean isCameraViewSet, LinearLayout lCameraPreview)
	{
		if(inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.stopPreview();
			inPreview = false;
		}
		Parameters par = camera.getParameters();
		par.setFlashMode(flash);
		camera.setParameters(par);
		if(!inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.startPreview();
			inPreview = true;
		}
	}
	
	private static void setFocus(String focus, Camera camera, boolean inPreview, boolean isCameraViewSet, LinearLayout lCameraPreview)
	{
		if(inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.stopPreview();
			inPreview = false;
		}
		Parameters par = camera.getParameters();
		par.setFocusMode(focus);
		camera.setParameters(par);
		if(!inPreview && lCameraPreview.getVisibility()==View.VISIBLE && isCameraViewSet)
		{
			camera.startPreview();
			inPreview = true;
		}
	}

}
