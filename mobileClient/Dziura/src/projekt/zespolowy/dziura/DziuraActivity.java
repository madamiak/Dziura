package projekt.zespolowy.dziura;

import java.util.ArrayList;
import java.util.List;

import projekt.zespolowy.dziura.AppView.*;
import projekt.zespolowy.dziura.Menu.*;

import com.google.android.maps.MapActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class DziuraActivity extends MapActivity
{
	public boolean isCameraViewSet = false;
	
	//configuration
	public projekt.zespolowy.dziura.Configuration appConfig;
	public final String CONFIG_FILENAME = "dziuraApp.conf";
	
	// Views.
	public OptionView vOption;
	public CameraView vCamera;
	
	// Menu.
	public MyMenu appMenu;
	
	private boolean exitingApp = false;
	private boolean showInitDialog = true;
	
	public boolean isGpsEnabled = false;
	public boolean isInternetEnabled = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//load configuration from file
		appConfig = new projekt.zespolowy.dziura.Configuration();
		appConfig = appConfig.load(CONFIG_FILENAME, this);
		
		vOption = new OptionView(this);
		vCamera = new CameraView(this);
		
		// Disable visibility for all views besides current.
		vCamera.mCameraView.setVisibility(View.VISIBLE);
		vCamera.bTakePhoto.requestFocus();

		vOption.mOptionsView.setVisibility(View.GONE);
		//cGPS.requestFocus();
		//isCameraViewSet = false;
		
		showInitDialog = appConfig.getShowInitDialog();
		if(showInitDialog==true)
		{
			showInitDialog();
		}
		
		checkServices();
	}
	
	private void checkServices() 
	{
		isGpsEnabled = isGpsEnabled();
		isInternetEnabled = isInternetEnabled();
	}

	private void showInitDialog() 
	{
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Witaj");
		dialog.setMessage("Wykonaj zdjêcie szkody, któr¹ chcesz zg³osiæ. Nastêpnie wype³nij krótki formularz."+
				"\n\nJe¿eli nie chcesz, aby ta wiadomoœæ siê wyœwietla³a, naciœnij przycisk \"Rozumiem\".");
		dialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		final DziuraActivity dziuraAct = this;
		dialog.setButton2("Wiêcej...", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				final AlertDialog dialog2 = new AlertDialog.Builder(dziuraAct).create();
				dialog2.setMessage("Aby wykonaæ zdjêcie, naciœnij przycisk z ikon¹ aparatu."+
						" Przegl¹daj¹c zdjêcia mo¿esz oznaczaæ na nich tagi, naciskaj¹c na element zdjêcia, który chcesz oznaczyæ."+
						" Aby z widoku aparatu i zdjêæ przejœæ do formularza, wykonaj na ekranie gest z lewej do prawej."+
						" Aby wróciæ z formularza do zdjêæ, wykonaj gest w drug¹ stronê.");
				dialog2.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog2.cancel();
					}
				});
				dialog2.show();
			}
		});
		dialog.setButton3("Rozumiem", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				showInitDialog = false;
				dialog.cancel();
			}
		});
		dialog.show();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		appMenu = new MyMenu(this, menu);
		return true;
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		return appMenu.onMenuItemSelected(featureId, item);
	}

	public void onConfigurationChanged(Configuration configuration)
	{
		super.onConfigurationChanged(configuration);
	}

	public void onResume() {
		super.onResume();
		vCamera.resume();
	}

	public void onPause() 
	{
		if(!exitingApp)
		{
			if (vCamera.inPreview) 
			{
				vCamera.camera.stopPreview();
			}
			vCamera.camera.release();
		}
		vCamera.camera = null;
		vCamera.inPreview = false;
		super.onPause();
	}

	public void onDestroy()
	{
		//vCamera.destroy();

		super.onDestroy();
	}

	public void onUserInteraction()
	{
		super.onUserInteraction();
	}

	//sprawdzenie zgodnego z urzadzeniem rozmiaru podgladu, aby nie bylo wyjatku podczas ustawiania rozmiaru
	private Camera.Size getClosestPreviewSize(int width, int height, Camera.Parameters parameters)
	{ //podaje jaki rozmiar bym chcial, sprawdzam wszystkei i wybieram ten, ktorego roznica z podanym rozmiarem jest najmniejsza
		Camera.Size size = parameters.getPreviewSize();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		if(previewSizes!=null)
		{
			List<Integer> previewDifference = new ArrayList<Integer>();
			int dx, dy;
			for(int i=0; i<previewSizes.size(); i++)
			{
				dx = 0;
				dy = 0;
				dx = Math.abs(previewSizes.get(i).width - width);
				dy = Math.abs(previewSizes.get(i).height - height);
				previewDifference.add(dx+dy);
			}
			int min = previewDifference.get(0);
			int min_idx = 0;
			for(int i=1; i<previewDifference.size(); i++)
			{
				if(previewDifference.get(i) < min)
				{
					min = previewDifference.get(i);
					min_idx = i;
				}
			}
			size.height = previewSizes.get(min_idx).height;
			size.width = previewSizes.get(min_idx).width;
		}
		else
		{
			size.height = 240;
			size.width = 320;
		}
		return size;
	}

	public SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback()
	{
		public void surfaceCreated(SurfaceHolder holder)
		{
			vCamera.resume();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			//get display size
			DisplayMetrics displaymetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	        int displayHeight = displaymetrics.heightPixels;
	        int displayWidth = displaymetrics.widthPixels;
	        
	        
	        if (vCamera.camera != null)
	        {
				//set camera preview size
				Camera.Parameters parameters = vCamera.camera.getParameters();
				Camera.Size size = getClosestPreviewSize((int) (displayWidth),
						(int) (0.8 * displayHeight), parameters);
				LayoutParams params = vCamera.preview.getLayoutParams();
				params.height = size.height;
				params.width = size.width;
				vCamera.preview.setLayoutParams(params);
				parameters.setPreviewSize(size.width, size.height);
				parameters.setRotation(90);
				parameters.setSceneMode(Parameters.SCENE_MODE_AUTO);
				parameters.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
				parameters.setJpegQuality(100);
				if (appConfig.getCameraSettings() != null) {
					parameters.unflatten(appConfig.getCameraSettings());
				}
				vCamera.camera.setParameters(parameters);
				vCamera.preview.setMinimumHeight(size.height);
				vCamera.preview.setMinimumWidth(size.width);
				vCamera.previewHolder = vCamera.preview.getHolder();
				vCamera.previewHolder.addCallback(surfaceCallback);
				vCamera.previewHolder
						.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				try {
					vCamera.camera.setPreviewDisplay(vCamera.previewHolder);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				if (!vCamera.inPreview) {
					vCamera.camera.startPreview();
					vCamera.inPreview = true;
				}
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
			//
		}
	};

	public void wyswietlTekst(String tekst)
	{
		Toast.makeText(DziuraActivity.this, tekst, Toast.LENGTH_LONG).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void vibrate(long milis)
	{
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(milis);
	}
	
	public boolean isInternetEnabled()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
		return false;
	}
	
	public boolean isGpsEnabled()
	{
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void showWirelessOptions(String message, String title)
	{
		AlertDialog enableInternetDialog = new AlertDialog.Builder(this).create();
		enableInternetDialog.setTitle(title);
		enableInternetDialog.setMessage(message);
		enableInternetDialog.setButton("Tak", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				Intent wifiOptionsIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);  
				startActivity(wifiOptionsIntent);
			}
		});
		enableInternetDialog.setButton2("Nie", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		enableInternetDialog.show();
	}
	
	public void appExit()
	{
		//save configuration
		if(vOption.saveMail)
		{
			appConfig.setEMail(vOption.eMail.getText().toString());			
		}
		appConfig.setUseGPS(vOption.cGPS.isChecked());
		appConfig.setSendMail(vOption.cMail.isChecked());
		appConfig.setLat(vOption.point.getLatitudeE6()/1E6);
		appConfig.setLon(vOption.point.getLongitudeE6()/1E6);
		appConfig.setCameraSettings(vCamera.camera.getParameters().flatten());
		appConfig.setMapSatellite(vOption.mapView.isSatellite());
		appConfig.setShowInitDialog(showInitDialog);
		appConfig.save(CONFIG_FILENAME, this);
		
		exitingApp = true;
		
		if(vCamera.camera!=null)
		{
			vCamera.camera.stopPreview();
			vCamera.camera.release();
		}
		
		//delete photos
		vCamera.destroy();
		
		finish();
	}
}