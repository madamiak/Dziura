package projekt.zespolowy.dziura;

import java.util.List;

import projekt.zespolowy.dziura.AppView.*;
import projekt.zespolowy.dziura.Menu.*;

import com.google.android.maps.MapActivity;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;


/**
 * Klasa aktywnosci aplikacji. Okresla jej sposob dzialania. Dziedziczy po klasie abstrakcyjnej 
 * <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/MapActivity?hl=pl-PL">
 * MapActivity</a>,
 * aby mozliwe bylo wykorzystanie mapy.
 */
public class DziuraActivity extends MapActivity
{
	private enum serviceDisabling {NOTHING, DIALOGSHOWED, DIALOGCLOSED}
	
	private serviceDisabling wirelessDisabling, gpsDisabling;
	
	//configuration
	/**
	 * Obiekt zawierajacy aktualne ustawienia aplikacji. Obsluguje ich zapisywanie oraz odczytywanie z pliku.
	 */
	public projekt.zespolowy.dziura.Configuration appConfig;
	
	/**
	 * Nazwa pliku konfiguracyjnego, z ktorego ustawienia sa odczytywane i do ktorego sa zapisywane.
	 */
	public final String CONFIG_FILENAME = "dziuraApp.conf";
	
	// Views.
	/**
	 * Instancja obiektu formularza.
	 */
	public OptionView vOption;
	
	/**
	 * Instancja obiektu aparatu.
	 */
	public CameraView vCamera;
	
	/**
	 * Obiekt umozliwiajacy zarzadzanie zakladkami oraz ich zawartoscia.
	 * 
	 * @see android.widget.TabHost
	 */
	public TabHost tabHost;
	
	/**
	 * Obiekt umozliwiajacy zarzadzanie zakladkami (sam pasek umozliwiajacy przelaczanie zakladek).
	 * 
	 * @see android.widget.TabWidget
	 */
	public TabWidget tabWidget;
	
	// Menu.
	/**
	 * Obiekt reprezentujacy menu aplikacji.
	 */
	public MyMenu appMenu;
	
	private boolean exitingApp = false;
	private boolean showInitDialog = true;
	
	/**
	 * Zmienna ustawiana na poczatku dzialania aplikacji. Mowi o tym, czy podczas jej uruchamiania GPS byl aktywny.
	 */
	public boolean isGpsEnabled = false;
	
	/**
	 * Zmienna ustawiana na poczatku dzialania aplikacji. Mowi o tym, czy podczas jej uruchamiania telefon mial polaczenie z Internetem.
	 */
	public boolean isInternetEnabled = false;
	
	private static int createdCount = 0;
	private boolean uruchamianie = true;

	/** Funkcja wywolywana, kiedy aktywnosc jest po raz pierwszy tworzona.
	 * <br><br>Wykonane zostaja tutaj nastepujace czynnosci:
	 * <ul>
	 * <li>Wczytanie ustawien aplikacji z pliku konfiguracyjnego
	 * <li>Inicjalizacja widokow (patrz: {@link OptionView}, {@link CameraView})
	 * <li>Inicjalizacja zakladek
	 * <li>Wyœwietlenie ekranu poczatkowego (chyba ze ustawienia mowia, aby nie wyswietlac)
	 * <li>Sprawdzenie, czy uruchomione sa uslugi dostepu do Internetu i lokalizacji poprzez GPS
	 * </ul>
	 * @param savedInstanceState zapamietany stan instancji aktywnosci
	*/
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
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
		
//		// Disable visibility for all views besides current.
		vCamera.mCameraView.setVisibility(View.VISIBLE);
		vCamera.bTakePhoto.requestFocus();
//
//		vOption.mOptionsView.setVisibility(View.VISIBLE);
//		//cGPS.requestFocus();
//		//isCameraViewSet = false;
		
		initializeTab(savedInstanceState);
		
		showInitDialog = appConfig.getShowInitDialog();
		if(showInitDialog==true)
		{
			showInitDialog();
		}
		
		if(createdCount==0)
		{
			checkServices(); //sprawdzam tylko za pierwszym wywolaniem tej funkcji
		} //tylko czy potrzebne jest to zliczanie? przeciez ta funkcja chyba i tak tylko raz sie wykonuje? :P
		
		createdCount++;
	}
	
	private void initializeTab(Bundle savedInstanceState) 
	{
		LocalActivityManager mLocalActivityManager;
		tabHost = (TabHost) findViewById(R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(this, false);

        tabHost.setup(mLocalActivityManager);
        mLocalActivityManager.dispatchCreate(savedInstanceState); //after the tab's setup is called, you have to call this or it wont work
        
        Drawable fotoIcon = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        Drawable formIcon = getResources().getDrawable(android.R.drawable.ic_menu_agenda);
        
        tabHost.addTab(tabHost.newTabSpec("fotoTab").setIndicator("", fotoIcon).setContent(R.id.gesturesCamera));
        tabHost.addTab(tabHost.newTabSpec("formTab").setIndicator("", formIcon).setContent(R.id.optionView));
        
        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(new MyOnTabChangedListener(this));
        
        tabWidget = tabHost.getTabWidget();
	}

	private void checkServices() 
	{
		isGpsEnabled = isGpsEnabled();
		isInternetEnabled = isInternetEnabled();
		wirelessDisabling = serviceDisabling.NOTHING; 
		gpsDisabling = serviceDisabling.NOTHING;
	}

	private void showInitDialog() //wyswietlenie dialogu na poczatku dzialania aplikacji
	{
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Witaj");
		dialog.setMessage("Wykonaj zdjêcie szkody, któr¹ chcesz zg³osiæ. Nastêpnie wype³nij krótki formularz."+
				"\n\nCzy chcesz, aby ta wiadomoœæ wyœwietla³a siê po uruchomieniu aplikacji?");
		dialog.setButton("Tak", new DialogInterface.OnClickListener()
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
						" Aby z widoku aparatu i zdjêæ przejœæ do formularza, wykonaj na ekranie gest z prawej do lewej."+
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
		dialog.setButton3("Nie", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				showInitDialog = false;
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	/**
	 * Funkcja wywolywana po nacisnieciu klawisza telefonu. Jezeli nacisniety zostal klawisz aparatu (znajdujacy sie z boku telefonu)
	 * to nastepuje wykonanie zdjecia lub przelaczenie do widoku aparatu (jezeli aktywny byl widok formuarza).
	 * 
	 * @param keyCode kod nacisnietego klawisza
	 * @param event zdarzenie zwiazane z klawiszem
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(keyCode == KeyEvent.KEYCODE_CAMERA)
			{
				if(tabHost.getCurrentTab() == 0)
				{
					if(vCamera.inPreview)
					{
						vCamera.bTakePhoto.performClick();
					}
				}
				else
				{
					tabHost.setCurrentTab(0);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	
	/**
	 * Funkcja wywolywana podczas tworzenia menu aplikacji. Menu zostaje utworzone
	 * poprzez konstruktor klasy {@link MyMenu}.
	 * 
	 * @param menu instancja tworzonego menu
	 * @return <li>true - zawsze
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		appMenu = new MyMenu(this, menu);
		return true;
	}
	
	/**
	 * Zapewnia wykonanie odpowiedniej akcji po wybraniu opcji menu (wywolywana jest tu funkcja 
	 * {@link projekt.zespolowy.dziura.Menu.MyMenu#onMenuItemSelected(int, android.view.MenuItem)}).
	 * @param featureId identyfikator panela menu, w ktorym wybrana opcja sie znajduje
	 * @param item instancja klasy {@link android.view.MenuItem} okreslajaca wybrana z menu opcje
	 * @return <li>w zaleznosci od wyniku funkcji {@link projekt.zespolowy.dziura.Menu.MyMenu#onMenuItemSelected(int, MenuItem)}
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		return appMenu.onMenuItemSelected(featureId, item);
	}
	
	public void onConfigurationChanged(Configuration configuration)
	{
		super.onConfigurationChanged(configuration);
	}

	/**
	 * Funkcja wywolywana za kazdym razem, kiedy aktywnosc jest wznawiana. Nastepuje tutaj wznowienie dzialania aparatu.
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	public void onResume() 
	{
		super.onResume();
		vCamera.resume();
		
		if(uruchamianie != true) //zeby te funkcje nie wywolywaly sie przy uruchomieniu aplikacji
		{
			//to potrzebne do poprawnego dzialania aplikacji (np wyswietlanie dialogow, menu) po powrocie z innej aktywnosci
			int currentTab = tabHost.getCurrentTab();
			tabHost.setCurrentTab((currentTab+1)%2);
			tabHost.setCurrentTab(currentTab);
		} //ale zeby pozniej sie wywolywaly to zmieniam uruchamianie na false w onCreate
		uruchamianie = false;
		
		//to potrzebne to poprawnego wyswietlania dialogow przy konczeniu aplikacji
		if(gpsDisabling == serviceDisabling.DIALOGSHOWED)
		{
			gpsDisabling = serviceDisabling.DIALOGCLOSED;
			appExit();
		}
		if(wirelessDisabling == serviceDisabling.DIALOGSHOWED)
		{
			wirelessDisabling = serviceDisabling.DIALOGCLOSED;
			appExit();
		}
	}

	/**
	 * Funkcja wywolywana za kazdym razem, kiedy dzialanie aplikacji zostaje przerwane. Zatrzymane zostaje tutaj dzialanie aparatu.
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	public void onPause() 
	{
		if(!exitingApp)
		{
			if (vCamera.inPreview) 
			{
				vCamera.camera.stopPreview();
			}
			if(vCamera.camera!=null)
			{
				vCamera.camera.release();
			}
		}
		vCamera.camera = null;
		vCamera.inPreview = false;
		super.onPause();
	}

	/**
	 * Funkcja wywolywana podczas zamykania aplikacji. Poza zakonczeniem pracy aparatu i usunieciem zdjec wykonanych przez aplikacje,
	 * nastepuje tu zapisanie aktualnych ustawien do pliku konfiguracyjnego.
	 */
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onDestroy()
	 */
	public void onDestroy()
	{
		saveConfiguration(); //zapisanie ustawien do pliku konfiguracyjnego
		exitingApp = true;
		if (vCamera.camera != null) {
			vCamera.camera.stopPreview();
			vCamera.camera.release();
		}
		
		//delete photos
		vCamera.destroy();

		super.onDestroy();
	}

	public void onUserInteraction()
	{
		super.onUserInteraction();
	}

	//preview size ma byc najwiekszy z mozliwych, aby podglad byl dobrej jakosci, ale tez musza byc zachowane proporcje zdjecia
	private Camera.Size getMaxPreviewSize(Camera.Parameters parameters)
	{
		Camera.Size size = parameters.getPreviewSize();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		//obliczam proporcje zdjecia
		Camera.Size pictSize = getMaxPictureSize(parameters);
		double pictureRatio = (double)pictSize.height / (double)pictSize.width;
		if(previewSizes!=null)
		{
			int maxW = 0;
			int maxIdx = 0;
			for(int i=0; i<previewSizes.size(); i++)
			{
				double ratio = (double)previewSizes.get(i).height / (double)previewSizes.get(i).width;
				if(previewSizes.get(i).width > maxW && ratio == pictureRatio)
				{
					maxW = previewSizes.get(i).width;
					maxIdx = i;
				}
			}
			size.height = previewSizes.get(maxIdx).height;
			size.width = previewSizes.get(maxIdx).width;
		}
		else
		{
			size.height = 240;
			size.width = 320;
		}
		return size;
	}
	
	//preview size ma byc najwiekszy z mozliwych, aby zdjecie bylo dobrej jakosci
	private Camera.Size getMaxPictureSize(Camera.Parameters parameters)
	{
		Camera.Size size = parameters.getPreviewSize();
		List<Camera.Size> pictSizes = parameters.getSupportedPictureSizes();
		if(pictSizes!=null)
		{
			int maxW = pictSizes.get(0).width;
			int maxIdx = 0;
			for(int i=1; i<pictSizes.size(); i++)
			{
				if(pictSizes.get(i).width > maxW)
				{
					maxW = pictSizes.get(i).width;
					maxIdx = i;
				}
			}
			size.height = pictSizes.get(maxIdx).height;
			size.width = pictSizes.get(maxIdx).width;
		}
		else
		{
			size.height = 240;
			size.width = 320;
		}
		return size;
	}
	
	/**
	 * Obiekt obslugujacy tworzenie/zmiane podlgadu z aparatu widocznego w aplikacji.
	 * W ramach tworzenia/zmiany wykonane zostaja takie operacje jak:
	 * <ul>
	 * <li> Ustawienie odpowiedniej wielkosci podgladu, dopasowanej do rozmiaru ekranu telefonu
	 * <li> Przekazanie aparatowi wczytanych wczesniej z pliku konfiguracyjnego ustawien
	 * </ul>
	 */
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
	        //sprawdzam ktory rozmiar ekranu jest wiekszy i zmieniam odpowiednio zmienne, aby width byl dluzszy
			if(displayWidth < displayHeight)
			{
				int tmp = displayWidth;
				displayWidth = displayHeight;
				displayHeight = tmp;
			}
	        
	        if (vCamera.camera != null)
	        {
				//set camera preview size
				Camera.Parameters parameters = vCamera.camera.getParameters();
				Camera.Size size = getMaxPreviewSize(parameters);
				parameters.setPreviewSize(size.width, size.height);
				double previewRatio = (double)size.height / (double)size.width;
				LayoutParams params = vCamera.preview.getLayoutParams();
				params.height = displayHeight - tabWidget.getHeight();//(int) (size.height / x);
				params.width = (int) (params.height / previewRatio);
				vCamera.preview.setLayoutParams(params);
				parameters.setRotation(90);
				parameters.setJpegQuality(70);
				if (appConfig.getCameraSettings() != null)
				{
					parameters.unflatten(appConfig.getCameraSettings());
				}
				vCamera.camera.setParameters(parameters);
				vCamera.previewHolder = vCamera.preview.getHolder();
				vCamera.previewHolder.addCallback(surfaceCallback);
				vCamera.previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

	/**
	 * Wyswietla na ekranie niewielkie pole tekstowe z wiadomoscia, podana jako parametr funkcji. Pole to po chwili znika.
	 * @param tekst wiadomosc, ktora zostanie wyswietlona
	 * @see android.widget.Toast
	 */
	public void wyswietlTekst(String tekst)
	{
		Toast.makeText(DziuraActivity.this, tekst, Toast.LENGTH_LONG).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Wlacza wibracje na podany jako argument czas.
	 * @param milis czas wibracji w milisekundnach
	 */
	public void vibrate(long milis)
	{
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(milis);
	}
	
	/**
	 * Funckja sprawdza, czy aktywne jest polaczenie z Internetem.
	 * @return <li>true - jezeli polaczenie jest aktywne<li>false - w przeciwnym wypadku
	 */
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
	
	/**
	 * Funckja sprawdza, czy aktywna jest usluga lokalizacji GPS.
	 * @return <li>true - jezeli GPS jest aktywny<li>false - w przeciwnym wypadku
	 */
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
	
	/**
	 * Wyswietla dialog zbudowany zgodnie z podanymi parametrami. W przypadku nacisniecia przycisku <i>Tak</i> wyswietlone
	 * zostana ustawienia polaczen bezprzewodowych. W przypadku przycisku <i>Nie</i> dialog zostanie zamkniety.
	 * @param message wiadomosc, jaka zostanie wyswietlona w okienku
	 * @param title tytul dialogu
	 */
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
				if(wirelessDisabling == serviceDisabling.DIALOGSHOWED)
				{
					wirelessDisabling = serviceDisabling.DIALOGCLOSED;
					appExit();
				}
				if(vOption.wifiMap == true)
				{
					if(vOption.cGPS.isChecked() != true)
					{
						vOption.cGPS.performClick();
					}
					vOption.wifiMap = false;
				}
			}
		});
		enableInternetDialog.show();
	}
	
	/**
	 * Wyswietla dialog zbudowany zgodnie z podanymi parametrami. W przypadku nacisniecia przycisku <i>Tak</i> wyswietlone
	 * zostana ustawienia lokalizacji. W przypadku przycisku <i>Nie</i> dialog zostanie zamkniety.
	 * @param message wiadomosc, jaka zostanie wyswietlona w okienku
	 * @param title tytul dialogu
	 */
	public void showGpsOptions(String message, String title)
	{
		AlertDialog enableGpsDialog = new AlertDialog.Builder(this).create();
		enableGpsDialog.setTitle(title);
		enableGpsDialog.setMessage(message);
		enableGpsDialog.setButton("Tak", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(gpsOptionsIntent);
			}
		});
		enableGpsDialog.setButton2("Nie", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				if(gpsDisabling == serviceDisabling.DIALOGSHOWED)
				{
					gpsDisabling = serviceDisabling.DIALOGCLOSED;
					appExit();
				}
			}
		});
		enableGpsDialog.show();
	}
	
	/**
	 * Funkcja wywolywana po wybraniu opcji <i>Zakoncz</i> w menu aplikacji lub po poprawnym wyslaniu zgloszenia.
	 * Sprawdza, czy podczas dzialania aplikacji uruchomiony zostal GPS badz polaczenie z Internetem, i umozliwia ich
	 * wylaczenie, wyswietlajac ekrany odpowiednich ustawien.
	 * @see #showGpsOptions(String, String)
	 * @see #showWirelessOptions(String, String)
	 */
	public void appExit()
	{
		//close internet and gps services
		if(isGpsEnabled == false && isGpsEnabled() == true && gpsDisabling == serviceDisabling.NOTHING)
		{
			gpsDisabling = serviceDisabling.DIALOGSHOWED;
			showGpsOptions("W trakcie dzia³ania aplikacji, uruchomiona zosta³a us³uga korzystania z satelitów GPS."+
					" Czy chcesz j¹ teraz wy³¹czyæ?", "GPS");
		}
		if(gpsDisabling == serviceDisabling.NOTHING || gpsDisabling == serviceDisabling.DIALOGCLOSED)
		{
			if(isInternetEnabled == false && isInternetEnabled() == true && wirelessDisabling == serviceDisabling.NOTHING)
			{
				wirelessDisabling = serviceDisabling.DIALOGSHOWED;
				showWirelessOptions("W trakcie dzia³ania aplikacji, nawi¹zane zosta³o po³¹czenie z Internetem."+
						" Czy chcesz siê teraz roz³¹czyæ?", "Internet");
			}
			if(wirelessDisabling == serviceDisabling.NOTHING || wirelessDisabling == serviceDisabling.DIALOGCLOSED)
			{
				finish();
			}
		}
	}

	private void saveConfiguration()
	{
		if(vOption.saveMail)
		{
			appConfig.setEMail(vOption.eMail.getText().toString());			
		}
		appConfig.setUseGPS(vOption.cGPS.isChecked());
		appConfig.setSendMail(vOption.cMail.isChecked());
		appConfig.setLat(vOption.point.getLatitudeE6()/1E6);
		appConfig.setLon(vOption.point.getLongitudeE6()/1E6);
		if(vCamera.camera != null)
		{
			appConfig.setCameraSettings(vCamera.camera.getParameters().flatten());
		}
		appConfig.setMapSatellite(vOption.mapView.isSatellite());
		appConfig.setShowInitDialog(showInitDialog);
		appConfig.save(CONFIG_FILENAME, this);
	}
}