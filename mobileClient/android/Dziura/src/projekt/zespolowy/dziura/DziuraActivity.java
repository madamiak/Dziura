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
import android.os.Build;
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
	private enum serviceDisabling {NOTHING, DIALOGSHOWED, DIALOGCLOSED};
	
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
	
	/**
	 * Zmienna ustawiana na poczatku dzialania aplikacji. Mowi o tym, czy podczas jej uruchamiania GPS byl aktywny.
	 */
	public boolean isGpsEnabled = false;
	
	/**
	 * Zmienna ustawiana na poczatku dzialania aplikacji. Mowi o tym, czy podczas jej uruchamiania telefon mial polaczenie z Internetem.
	 */
	public boolean isInternetEnabled = false;
	

	/**
	 * Pole konieczne do poprawnego wyswietlania dialogu o polaczeniu z Internetem w celu korzystania z mapy. Jezeli <i>true</i>
	 * to dialog zostanie wyswietlony.
	 */
	public boolean mapDialog = true;
	
	private boolean uruchamianie = true;
	
	/**
	 * Flaga ustawiana, jezeli po nacisnieciu przycisku wysylania ({@link OptionView#bSubmit}) wyswietli sie
	 * dialog z prosba o aktywowanie GPS lub polaczenie z Internetem. Flaga ta ma na celu automatyczne
	 * kontynuowanie wysylania, po powrocie z ekranu ustawien systemowych, aby nie trzeba bylo ponownie
	 * naciskac przycisku.
	 */
	public boolean sending = false;
	
	/**
	 * Flaga analogiczna do {@link #sending}, ale dziala podczas odznaczania checkboxa {@link OptionView#cGPS},
	 * aby po polaczeniu z Internetem nie trzeba bylo klikac ponownie, aby wyswiwetlic mape.
	 */
	public boolean usingMap = false;
	
	/**
	 * Jezeli wartosc zmiennej <i>sendUsingServer</i> jest rowna: <br>
	 * <li>true, to po nacisnieciu na przycisk wysylania rozpoczete zostanie wysylanie na serwer
	 * <li>false, to po nacisnieciu na przycisk wysylania rozpocznie sie wysylanie przez email
	 */
	public boolean sendUsingServer = true;
	
	/**
	 * Flaga ustawiana po wyswietleniu dialogu po wykonaniu zdjecia, aby po wykonaniu zdjec kolejnych
	 * dialog ten nie byl wyswietlany. Flaga resetowana za kazdym razem, gdy aplikacja jest uruchamiana
	 * na nowo.
	 * Dialog nie zostanie wyswietlony w ogole, jezeli na ekranie poczatkowym {@link projekt.zespolowy.dziura.AppView.InitDialog}
	 * odznaczony zostal checkbox mowiacy o wyswietlaniu dialogow z podpowiedziami.
	 */
	public boolean photoDialogShowed = false;
	
	/**
	 * Flaga ustawiana po wyswietleniu dialogu po nacisnieciu na miniaturke zdjecia, aby po wykonaniu zdjec kolejnych
	 * dialog ten nie byl wyswietlany. Flaga resetowana za kazdym razem, gdy aplikacja jest uruchamiana
	 * na nowo.
	 * Dialog nie zostanie wyswietlony w ogole, jezeli na ekranie poczatkowym {@link projekt.zespolowy.dziura.AppView.InitDialog}
	 * odznaczony zostal checkbox mowiacy o wyswietlaniu dialogow z podpowiedziami.
	 */
	public boolean miniDialogShowed = false;
	
	/**
	 * Flaga ustawiana po wyswietleniu dialogu po przelaczeniu widoku na formularz, aby po wykonaniu zdjec kolejnych
	 * dialog ten nie byl wyswietlany. Flaga resetowana za kazdym razem, gdy aplikacja jest uruchamiana
	 * na nowo.
	 * Dialog nie zostanie wyswietlony w ogole, jezeli na ekranie poczatkowym {@link projekt.zespolowy.dziura.AppView.InitDialog}
	 * odznaczony zostal checkbox mowiacy o wyswietlaniu dialogow z podpowiedziami.
	 */
	public boolean formDialogShowed = false;

	/**
	 * Instancja klasy {@link InitDialog} reprezentujaca ekran powitalny aplikacji.
	 */
	public InitDialog dialog;

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
		
		initializeTab(savedInstanceState);
		
		vOption = new OptionView(this);
		vCamera = new CameraView(this);
		
		vCamera.mCameraView.setVisibility(View.VISIBLE);
		vCamera.bTakePhoto.requestFocus();

		if(appConfig.getShowInitDialog() == true)
		{
			showInitDialog();
		}
		
		checkServices();
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
        
        tabHost.addTab(tabHost.newTabSpec("fotoTab").setIndicator("ZDJÊCIA", fotoIcon).setContent(R.id.gesturesCamera));
        tabHost.addTab(tabHost.newTabSpec("formTab").setIndicator("FORMULARZ", formIcon).setContent(R.id.optionView));
        
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
		dialog = new InitDialog(this);
		dialog.show();
	}
	
	/**
	 * Funkcja wywolywana po nacisnieciu klawisza telefonu. Jezeli nacisniety zostal klawisz aparatu (znajdujacy sie z boku telefonu)
	 * to nastepuje wykonanie zdjecia lub przelaczenie do widoku aparatu (jezeli aktywny byl widok formuarza). Jezeli nacisniety
	 * zostal klawisz 'Wstecz', to uzytkownik zostanie zapytany, czy na pewno chce wylaczyc aplikacje.
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
			if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				showExitDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	
	/**
	 * Wyswietla dialog z pytaniem czy uzytkownik na pewno chce zamknac aplikacje. Po nacisnieciu przycisku 'Tak',
	 * aplikacja jest zamykana. Po nacisnieciu przycisku 'Nie', dialog jest zamykany, a aplikacja dziala nadal.
	 */
	public void showExitDialog()
	{
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
		dialog.setIcon(icon);
		dialog.setTitle("Zg³oœ dziurê");
		dialog.setMessage("Czy na pewno chcesz wy³¹czyæ aplikacjê?");
		dialog.setButton("Tak", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				appExit();
			}
		});
		dialog.setButton2("Nie", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		dialog.show();
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
	
	/**
	 * Funkcja zostala przeciazana, aby uniemozliwic automatyczne obracanie ekranu.
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	public void onConfigurationChanged(Configuration configuration)
	{
		super.onConfigurationChanged(configuration);
	}

	/**
	 * Funkcja wywolywana za kazdym razem, kiedy aktywnosc jest wznawiana. Nastepuje tutaj wznowienie dzialania aparatu oraz
	 * inne konieczne operacje.
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
			mapDialog = false;
			tabHost.setCurrentTab((currentTab+1)%2);
			tabHost.setCurrentTab(currentTab);
			mapDialog = true;
		} //ale zeby pozniej sie wywolywaly to zmieniam uruchamianie na false w onCreate
		uruchamianie = false;
		
		if(sending == true)
		{
			sending = false;
			vOption.bSubmit.performClick();
		}
		
		if(usingMap == true)
		{
			usingMap = false;
			vOption.cGPS.performClick();
		}
		
		if(isInternetEnabled() == false && vOption.cGPS.isChecked() == false && tabHost.getCurrentTab() == 1)
		{
			vOption.wifiMap = true;
			showWirelessOptions("Aby korzystaæ z mapy, nale¿y po³¹czyæ siê z Internetem. Czy chcesz siê po³¹czyæ?", "Uwaga");
		}
		
		//to potrzebne to poprawnego wyswietlania dialogow przy konczeniu aplikacji
		if(gpsDisabling == serviceDisabling.DIALOGSHOWED)
		{
			gpsDisabling = serviceDisabling.DIALOGCLOSED;
			if(isInternetEnabled() == false || isInternetEnabled == true)
			{
				appExit();
			}
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

	/**
	 * Funkcja przeciazana, aby aktywnosc nie byla resetowana np. po wygaszeniu ekranu, lecz aby kontyuowano
	 * aktywnosc juz rozpoczeta (uchroni to m.in. przed strata wpisanych danych).
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onUserInteraction()
	 */
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
				
				//ustawienie max preview frame rate
				List<Integer> fps = parameters.getSupportedPreviewFrameRates();
				int bestFps = getMaxFps(fps);
				parameters.setPreviewFrameRate(bestFps);				

				double previewRatio = (double)size.height / (double)size.width;
				LayoutParams params = vCamera.preview.getLayoutParams();
				if(displayHeight > size.height && displayWidth > size.width)
				{
					params.height = size.height;
					params.width = size.width;
				}
				else
				{
					params.height = displayHeight - tabWidget.getHeight();
					//jezeli wersja androida 3 (czyli tablet) to zawsze wyswietlany bedzie pasek, wiec odejmuje dodatkowo 50
					if(Build.VERSION.SDK_INT < 14 && Build.VERSION.SDK_INT >= 11)
					{
						params.height -= 50;
					}
					params.width = (int) (params.height / previewRatio);
					while(true) //przeliczenie rozmiaru preview, jezeli jest za duzy, aby zostawil miejsce na miniaturki
					{
						if(params.width > (displayWidth - vCamera.bTakePhoto.getWidth() - 80))
						{
							params.height -= 5;
							params.width = (int) (params.height / previewRatio);
						}
						else
						{
							break;
						}
					}
	        	}
				vCamera.preview.setLayoutParams(params);
				parameters.setJpegQuality(50);
				if (appConfig.getCameraSettings() != null)
				{
					parameters.unflatten(appConfig.getCameraSettings());
				}
				vCamera.camera.setParameters(parameters);
				vCamera.previewHolder = vCamera.preview.getHolder();
				vCamera.previewHolder.addCallback(surfaceCallback);
				vCamera.previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				vCamera.previewHolder.setSizeFromLayout();
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

		private int getMaxFps(List<Integer> fps)
		{
			int max = fps.get(0);
			for(int i=1; i<fps.size(); i++)
			{
				if(fps.get(i) > max)
				{
					max = fps.get(i);
				}
			}
			return max;
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
	 * Jezeli telefon posiada zainstalowana wersjê systemu operacyjnego Android 4.0 lub nowsza,
	 * to wyswietlone zostana zamiast <i>Tak</i> 2 przyciski, osobne dla Wifi i sieci komórkowych.
	 * @param message wiadomosc, jaka zostanie wyswietlona w okienku
	 * @param title tytul dialogu
	 */
	public void showWirelessOptions(String message, String title)
	{
		AlertDialog enableInternetDialog = new AlertDialog.Builder(this).create();
		Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
		enableInternetDialog.setIcon(icon);
		enableInternetDialog.setTitle(title);
		enableInternetDialog.setMessage(message);
		String buttonTxt = "Tak";
		if(Build.VERSION.SDK_INT >= 14 ) //14 - android 4.0
		{
			enableInternetDialog.setButton3("WiFi", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
					Intent wifiOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
					startActivity(wifiOptionsIntent);
				}
			});
			buttonTxt = "Sieci komórkowe";
		}
		enableInternetDialog.setButton(buttonTxt, new DialogInterface.OnClickListener()
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
		Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
		enableGpsDialog.setIcon(icon);
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
					if(isInternetEnabled() == false || isInternetEnabled == true)
					{
						appExit();
					}
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
			showGpsOptions("W trakcie dzia³ania aplikacji uruchomiony zosta³ odbiornik sygna³u GPS."+
					" Czy chcesz go teraz wy³¹czyæ?", "GPS");
		}
		if(isGpsEnabled == true || gpsDisabling == serviceDisabling.DIALOGCLOSED ||
							(isGpsEnabled == false && isGpsEnabled() == false))
		{
			if(isInternetEnabled == false && isInternetEnabled() == true && wirelessDisabling == serviceDisabling.NOTHING)
			{
				wirelessDisabling = serviceDisabling.DIALOGSHOWED;
				showWirelessOptions("W trakcie dzia³ania aplikacji nawi¹zane zosta³o po³¹czenie z Internetem."+
						" Czy chcesz siê teraz roz³¹czyæ?", "Internet");						
			}
			if((isInternetEnabled == true || wirelessDisabling == serviceDisabling.DIALOGCLOSED ||
					(isInternetEnabled == false && isInternetEnabled() == false)))
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
		appConfig.setSendUsingServer(sendUsingServer);
		appConfig.save(CONFIG_FILENAME, this);
	}
}