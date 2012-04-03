package projekt.zespolowy.dziura.AppView;

import java.util.List;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.GestureListener;
import projekt.zespolowy.dziura.MyItemizedOverlay;
import projekt.zespolowy.dziura.MyOnItemSelectedListener;
import projekt.zespolowy.dziura.MyOnZglosClickListener;
import projekt.zespolowy.dziura.R;
import projekt.zespolowy.dziura.GPS.MyOnGPSClickListener;
import projekt.zespolowy.dziura.Mail.MailChecker;
import projekt.zespolowy.dziura.Mail.MyOnEmailClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.pm.ActivityInfo;
import android.gesture.GestureOverlayView;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class OptionView {
	public View mOptionsView;
	public Spinner sDamageType;
	public CheckBox cGPS;
	public CheckBox cMail;
	public EditText eMail;
	public boolean saveMail; //if true - save mail in config file / false - when mail is not matching regex - not saving
	public TextView tDamagePlace;
	public Button bSubmit;
	public MyOnZglosClickListener mZglosListener;
	
	//map stuff
	public MapView mapView;
	public MapController mapCtrl;
	public GeoPoint point;
	public boolean isMarkerAdded;
	public MyItemizedOverlay itemizedOverlay;
	public double latitude;
	public double longitude;
	private LinearLayout linLayMap;
	
	private DziuraActivity app;
	
	public OptionView(DziuraActivity app) {
		this.app = app;
		mOptionsView = app.findViewById(R.id.optionView);
		
		app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		app.isCameraViewSet = false;
		
		initGesture();
		initDamageTypeList();
		initMail();
		initGPS();
		initMap();

		// TODO: valid comment? 
		//sprawdzenie przed wys³aniem, czy pola zosta³y poprawnie wype³nione
		bSubmit = (Button) app.findViewById(R.id.submit);
		mZglosListener = new MyOnZglosClickListener(app, cGPS, cMail, eMail);
		bSubmit.setOnClickListener(mZglosListener);
		
		//set data read from config file
		eMail.setText(app.appConfig.getEMail());
		cGPS.setChecked(app.appConfig.getUseGPS());
		if(!cGPS.isChecked())
		{
			linLayMap.setVisibility(View.VISIBLE);
		}
		cMail.setChecked(app.appConfig.getSendMail());
		if(cMail.isChecked())
		{
			eMail.setVisibility(View.VISIBLE);
			eMail.setEnabled(true);
		}
		//cGPS.performClick();
		//cMail.performClick();
		saveMail = true;
		
	}

	public void initGesture() {
        GestureOverlayView gestures = (GestureOverlayView) app.findViewById(R.id.gesturesOptions);
		gestures.addOnGesturePerformedListener(new GestureListener(app));
		GestureOverlayView gestures2 = (GestureOverlayView) app.findViewById(R.id.gesturesOptions2);
		gestures2.addOnGesturePerformedListener(new GestureListener(app));
	}
	
	public void initGPS() {
		cGPS = (CheckBox) app.findViewById(R.id.checkBoxGetGPS); //checkBox 'czy pobierac polozenie GPS'
		tDamagePlace = (TextView) app.findViewById(R.id.textViewDamagePlace); //napisz 'miejsce wystapienia zdarzenia'

		//pokazanie pola na adres, w zaleznosci od checkBoxa
		cGPS.setOnClickListener(new MyOnGPSClickListener(app));
	}
	
	public void initMail() {
		cMail = (CheckBox) app.findViewById(R.id.checkBoxGetInfo); //checkBox o emailu
		eMail = (EditText) app.findViewById(R.id.editTextMail); //pole tekstowe na email
		eMail.addTextChangedListener(new MailChecker(eMail));
		
		//pokazanie pola na email, w zaleznosci od checkBoxa
		cMail.setOnClickListener(new MyOnEmailClickListener(cMail, eMail));
	}
	
	public void initDamageTypeList() {
		sDamageType = (Spinner) app.findViewById(R.id.spinnerDamageType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				app, R.array.damage_type_items, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sDamageType.setOnItemSelectedListener(new MyOnItemSelectedListener());
		sDamageType.setAdapter(adapter);
	}

	public void initMap() {
		mapView = (MapView) app.findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
	    MapController mapCtrl = mapView.getController();
	    mapCtrl.setZoom(12);
	    latitude = app.appConfig.getLat();
	    longitude = app.appConfig.getLon();
	    point = new GeoPoint((int)(latitude * 1e6), (int)(longitude * 1e6));
	    mapCtrl.setCenter(point);
	    isMarkerAdded = false;
	    linLayMap = (LinearLayout) app.findViewById(R.id.linLayMap);
	    
		//Add marker to map
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = app.getResources().getDrawable(R.drawable.mapmarker);
		itemizedOverlay = new MyItemizedOverlay(drawable, app);
		OverlayItem overlayitem = new OverlayItem(new GeoPoint(0, 0), null, null);
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		mapView.setSatellite(app.appConfig.getMapSatellite()); //load satellite/normal view from configuration file
	}
	
}
