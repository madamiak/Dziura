package projekt.zespolowy.dziura;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DziuraActivity extends MapActivity
{
	public boolean isCameraViewSet = false;
	
	//configuration
	public projekt.zespolowy.dziura.Configuration appConfig;
	public final String CONFIG_FILENAME = "dziuraApp.conf";
	
	// Options view stuff.
	public View mOptionsView;
	
	private Spinner sDamageType;
	public CheckBox cGPS;
	public CheckBox cMail;
	public EditText eMail;
	public TextView tDamagePlace;
	private Button bSubmit;
	
	// Camera view stuff.
	private SurfaceView preview 		= null;
	private SurfaceHolder previewHolder = null;
	public Camera camera 				= null;
	public boolean inPreview 			= false;
	// Maximum number of photos.
	public final int MAX_PHOTOS = 3;
	
	//layouty - zeby wyswietlic/schowac kontrolki to nie pojedynczo tylko caly layout, ktory je zawiera
	public LinearLayout lCameraPreview;
	public LinearLayout lPhotos;
	public LinearLayout lMinPhotos;
	public LinearLayout lTagDesc;
	public LinearLayout lMap;

	// TODO: nakladka na kamere (klasa), ktora bedzie obslugiwac te pierdoly z plikami zdjec?
	// Keeps file names which contain photos.
	public final String[] file_names = new String[MAX_PHOTOS]; 
	public Boolean[] file_names_bool = new Boolean[MAX_PHOTOS]; //tablica przechowujaca boole, czy plik jest uzywany czy nie

	//number of photo on img_big
	public int duze_foto = -1;

	//coordinates of point where tag will be added
	public float img_x = 0;
	public float img_y = 0;

	// list containing tags added to photos
	public List<PhotoTag> tagList;
	
	private ImageButton bTakeFoto;
	private Button bUsunFoto;
	private Button bTakeNextFoto;
	private Button bUsunTagi;
	public ImageView[] imgMinis;
	public MyImageView imgBig;
	private EditText tagTxt;
	private Button bSave;
	private Button bCancel;
	
	public View mCameraView;
	
	//map stuff
	public MapView mapView;
	public MapController mapCtrl;
	public GeoPoint point;
	public boolean isMarkerAdded;
	public MyItemizedOverlay itemizedOverlay;
	public double latitude;
	public double longitude;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		//load configuration from file
		appConfig = new projekt.zespolowy.dziura.Configuration();
		appConfig = appConfig.load(CONFIG_FILENAME, this);
		
		// Creating all views.
		mOptionsView = findViewById(R.id.optionView);
		mCameraView = findViewById(R.id.cameraView);
		
		//creating camera layouts
		lCameraPreview = (LinearLayout) findViewById(R.id.linLayCameraPreview);
		lPhotos = (LinearLayout) findViewById(R.id.linLayPhotos);
		lMinPhotos = (LinearLayout) findViewById(R.id.linLayMinPhotos);
		lTagDesc = (LinearLayout) findViewById(R.id.linLayTagDesc);
		lMap = (LinearLayout) findViewById(R.id.linLayMap);		

		optionsView();
		cameraView();
		
		//set data read from config file
		eMail.setText(appConfig.getEMail());
		cGPS.setChecked(!appConfig.getUseGPS());
		cMail.setChecked(!appConfig.getSendMail());
		cGPS.performClick();
		cMail.performClick();
		
		// Disable visibility for all views besides current.
		mCameraView.setVisibility(View.VISIBLE);
		bTakeFoto.requestFocus();

		mOptionsView.setVisibility(View.GONE);
		
		lPhotos.setVisibility(View.GONE);
		lCameraPreview.setVisibility(View.VISIBLE);
	}

	public void optionsView() {
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		isCameraViewSet = false;

		initGesture();
		initDamageTypeList();
		initMail();
		initGPS();
		initMap();

		// TODO: valid comment? 
		//sprawdzenie przed wys³aniem, czy pola zosta³y poprawnie wype³nione
		bSubmit = (Button) findViewById(R.id.submit);
		bSubmit.setOnClickListener(new MyOnZglosClickListener(this, cGPS, cMail, eMail));

	}
	
	public void cameraView() {	
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		isCameraViewSet = true;
		// gestures
		initGestureCamera();
		

		//ustawienie nazw plikow
		for(int i = 0; i<MAX_PHOTOS; i++) 
		{
			// TODO: check if file exist.
			file_names[i] = "dziura_"+Integer.toString(i)+".jpg";
			file_names_bool[i] = false;
		}

		tagList = new ArrayList<PhotoTag>();

		tagTxt = (EditText) findViewById(R.id.editText7);
		bSave = (Button) findViewById(R.id.buttonSave);
		bCancel = (Button) findViewById(R.id.buttonCancel);
		bSave.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) 
			{
				Editable txt = tagTxt.getText();
				String str_txt = txt.toString();
				PhotoTag tag = new PhotoTag(img_x, img_y, str_txt, duze_foto);
				tagList.add(tag);
				wyswietlTekst("Zapisano");
				tagTxt.setText("");
				lTagDesc.setVisibility(View.GONE);
				imgBig.setClickable(true);
				showDeleteTagsButton();
			}
		});

		bCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) 
			{
				tagTxt.setText("");
				lTagDesc.setVisibility(View.GONE);
				imgBig.setClickable(true);
				showDeleteTagsButton();
			}

		});

		imgBig = (MyImageView) findViewById(R.id.imageViewCamera);
		imgBig.setDziuraActivity(this);

		imgMinis = new ImageView[MAX_PHOTOS];
		imgMinis[0] = (ImageView) findViewById(R.id.imageViewPhoto1);
		imgMinis[1] = (ImageView) findViewById(R.id.imageViewPhoto2);
		imgMinis[2] = (ImageView) findViewById(R.id.imageViewPhoto3);
		for(int i = 0; i<MAX_PHOTOS; i++)
		{
			imgMinis[i].setPadding(5, 0, 5, 0);
			imgMinis[i].setOnClickListener(new MyOnMiniFotoClickListener(this, i));
		}

		//wlaczenie aparatu
		preview = (SurfaceView) findViewById(R.id.surfaceViewCamera);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		if(camera == null)
		{
			camera = Camera.open();
		}

		//zrobienie zdjêcia
		bTakeFoto = (ImageButton) findViewById(R.id.imageButton1);
		bTakeFoto.setOnClickListener(new MyOnFotoClickListener(this));

		//usuniecie zdjecia
		bUsunFoto = (Button) findViewById(R.id.buttonDeletePhoto);
		bUsunFoto.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(duze_foto != -1)
				{
					File photo = new File(Environment.getExternalStorageDirectory(), file_names[duze_foto]);
					if (photo.exists())
					{
						photo.delete();
						pokazPodglad();
						file_names_bool[duze_foto] = false;
						pokazZdjecia();
						for(int i=0; i<tagList.size(); i++)
						{
							if(tagList.get(i).getNumerZdjecia() == duze_foto)
							{
								tagList.remove(i);
								i--;
							}
						}
					}
				}
				showDeleteTagsButton();
			}
		});

		//zrobienie nastepnego zdjecia
		bTakeNextFoto = (Button) findViewById(R.id.buttonNextPhoto);
		bTakeNextFoto.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				pokazPodglad();
				lTagDesc.setVisibility(View.GONE);
				showDeleteTagsButton();
			}
		});
		
		//usuniecie tagow ze zdjecia
		bUsunTagi = (Button) findViewById(R.id.buttonDeleteTags);
		bUsunTagi.setOnClickListener(new OnClickListener()
		{

			public void onClick(View arg0) 
			{
				for(int i=0; i<tagList.size(); i++)
				{
					if(tagList.get(i).getNumerZdjecia() == duze_foto)
					{
						tagList.remove(i);
						i--;
					}
				}
				imgBig.postInvalidate();
				showDeleteTagsButton();
			}
			
		});
	}
	
	private void pokazPodglad()
	{
		if(!inPreview)
		{
			lCameraPreview.setVisibility(View.VISIBLE);
			camera.startPreview();
			inPreview = true;
			lPhotos.setVisibility(View.GONE);
		}
	}

	public void pokazZdjecia()
	{
		int showedPhotos = 0;
		for(int i=0; i<file_names_bool.length; i++)
		{
			if(file_names_bool[i]==true)
			{
				imgMinis[i].setVisibility(View.VISIBLE);
				File photo = new File(Environment.getExternalStorageDirectory(), file_names[i]);
				Bitmap bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
				bm = Bitmap.createScaledBitmap(bm, 80, 60, true);
				imgMinis[i].setImageBitmap(bm);
				showedPhotos++;
			}
			else
			{
				imgMinis[i].setVisibility(View.GONE);
			}
		}
		if(showedPhotos!=0)
		{
			lMinPhotos.setVisibility(View.VISIBLE);
		}
	}

	public void onConfigurationChanged(Configuration configuration)
	{
		super.onConfigurationChanged(configuration);
	}

	public void onResume()
	{
		super.onResume();

		if (camera == null && isCameraViewSet) 
		{
			camera=Camera.open();
			if(preview.getVisibility() == View.VISIBLE)
			{
				inPreview = true;
				camera.startPreview();
			}
		}
	}

	public void onPause() 
	{
		if (isCameraViewSet) {
			if (inPreview) 
			{
				camera.stopPreview();
			}

			camera.release();
			camera = null;
			inPreview = false;
		}
		super.onPause();
	}

	public void onDestroy()
	{
		File photo;
		for(int i=0; i<MAX_PHOTOS; i++)
		{
			photo = new File(Environment.getExternalStorageDirectory(), file_names[i]);

			if (photo.exists())
			{
				photo.delete();
			}
			photo = null;
		}

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
			size.height = 180;
			size.width = 240;
		}
		return size;
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback()
	{
		public void surfaceCreated(SurfaceHolder holder)
		{
			try
			{
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t)
			{
				wyswietlTekst(t.getMessage());
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			//get display size
			DisplayMetrics displaymetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	        int displayHeight = displaymetrics.heightPixels;
	        int displayWidth = displaymetrics.widthPixels;
	        
	        //set camera preview size
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getClosestPreviewSize((int)(displayWidth), (int)(0.75 * displayHeight), parameters);
			parameters.setPreviewSize(size.width, size.height);
			parameters.setRotation(90);
			parameters.set("orientation", "landscape");
			camera.setParameters(parameters);
			preview.setMinimumHeight(size.height);
			preview.setMinimumWidth(size.width);
			if(!inPreview)
			{
				camera.startPreview();
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
			//
		}
	};

	public void initGesture() {
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(new GestureListener(this));
	}
	
	public void initGestureCamera() {
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gesturesCamera);
        gestures.addOnGesturePerformedListener(new GestureListener(this));
	}
	
	public void initGPS() {
		cGPS = (CheckBox) findViewById(R.id.checkBoxGetGPS); //checkBox 'czy pobierac polozenie GPS'
		tDamagePlace = (TextView) findViewById(R.id.textViewDamagePlace); //napisz 'miejsce wystapienia zdarzenia'

		//pokazanie pola na adres, w zaleznosci od checkBoxa
		cGPS.setOnClickListener(new MyOnGPSClickListener(this));
	}
	
	public void initMail() {
		cMail = (CheckBox) findViewById(R.id.checkBoxGetInfo); //checkBox o emailu
		eMail = (EditText) findViewById(R.id.editTextMail); //pole tekstowe na email
		eMail.addTextChangedListener(new MailChecker(eMail));
		
		//pokazanie pola na email, w zaleznosci od checkBoxa
		cMail.setOnClickListener(new MyOnEmailClickListener(cMail, eMail));
	}
	
	public void initDamageTypeList() {
		sDamageType = (Spinner) findViewById(R.id.spinnerDamageType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.damage_type_items, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sDamageType.setOnItemSelectedListener(new MyOnItemSelectedListener());
		sDamageType.setAdapter(adapter);
	}
	
	public void initMap() {
		mapView = (MapView) findViewById(R.id.mapView);
	    mapView.setBuiltInZoomControls(true);
	    MapController mapCtrl = mapView.getController();
	    mapCtrl.setZoom(12);
	    latitude = appConfig.getLat();
	    longitude = appConfig.getLon();
	    point = new GeoPoint((int)(latitude * 1e6), (int)(longitude * 1e6));
	    mapCtrl.setCenter(point);
	    isMarkerAdded = false;
	    
		//Add marker to map
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.mapmarker);
		itemizedOverlay = new MyItemizedOverlay(drawable, this);
		OverlayItem overlayitem = new OverlayItem(new GeoPoint(0, 0), null, null);
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
	}
	
	public void wyswietlTekst(String tekst)
	{
		Toast.makeText(DziuraActivity.this, tekst, Toast.LENGTH_LONG).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
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
	
	public void showDeleteTagsButton()
	{
		int licznik = 0;
		for(int i=0; i < tagList.size(); i++)
		{
			if(tagList.get(i).nr_zdjecia == duze_foto)
			{
				licznik++;
			}
		}
		if(licznik > 0)
		{
			bUsunTagi.setVisibility(View.VISIBLE);
		}
		else
		{
			bUsunTagi.setVisibility(View.GONE);
		}
	}
}