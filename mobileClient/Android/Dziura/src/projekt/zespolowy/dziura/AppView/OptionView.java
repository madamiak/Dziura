package projekt.zespolowy.dziura.AppView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.GestureListener;
import projekt.zespolowy.dziura.MyOnItemSelectedListener;
import projekt.zespolowy.dziura.MyOnZglosClickListener;
import projekt.zespolowy.dziura.R;
import projekt.zespolowy.dziura.GPS.MyOnGPSClickListener;
import projekt.zespolowy.dziura.Mail.MailChecker;
import projekt.zespolowy.dziura.Mail.MyOnEmailClickListener;
import projekt.zespolowy.dziura.Map.MyItemizedOverlay;
import projekt.zespolowy.dziura.Map.MyMapView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.pm.ActivityInfo;
import android.gesture.GestureOverlayView;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Klasa odpowiedzialna za widok formularza. Znajduja sie tu zmienne wszystkich kontrolek/przyciskow itd
 * z tego widoku oraz funkcje na nich operujace.
 * 
 * @see projekt.zespolowy.dziura.AppView.CameraView
 */
public class OptionView
{
	/**
	 * Widok formularza na ktorym umieszczone sa wszystkie elementy skladowe - pola tekstowe, mape, itd.
	 */
	public View mOptionsView;
	
	/**
	 * Lista wyboru rodzaju szkody.
	 */
	public Spinner sDamageType;
	
	/**
	 * Checkbox decydujacy o pobieraniu lokalizacji z GPS badz o uzywaniu mapy.
	 */
	public CheckBox cGPS;
	
	/**
	 * Checkbox decydujacy o tym, czy wysylane beda powiadomienia o zmianie stanu zgloszenia na email uzytkownika, czy nie.
	 */
	public CheckBox cMail;
	
	/**
	 * Pole tekstowe zawierajace adres email wpisany przez uzytkownika.
	 */
	public EditText eMail;
	
	/**
	 * Czy adres email ma zostac zapisany w pliku konfiguracyjnym (nie zostanie zapisany jesli wpisany adres
	 * nie spelnia wyrazenia regularnego okreslajacego poprawny adres).
	 */
	public boolean saveMail; //if true - save mail in config file / false - when mail is not matching regex - not saving
	
	/**
	 * Tekst statyczny wyswietlany nad mapa - 'Wskaz miejsce wystapienia szkody'.
	 */
	public TextView tDamagePlace;
	
	/**
	 * Przycisk sluzacy do rozpoczecia wysylania zgloszenia.
	 */
	public ImageButton bSubmit;
	
	/**
	 * Listener przypisany do przycisku wysylania {@link #bSubmit}.
	 */
	public MyOnZglosClickListener mZglosListener;
	
	/**
	 * Listener przypisany do listy wyboru rodzaju szkody {@link #sDamageType}.
	 */
	public MyOnItemSelectedListener damageTypeListener;
	
	/**
	 * Pole tekstowe sluzace do wpisania opisu zgloszenia.
	 */
	public EditText descriptionTxt;
	
	
	//map stuff	
	/**
	 * Widok mapy.
	 */
	public MyMapView mapView;
	
	/**
	 * Kontroler mapy.
	 * @see <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/MapController?hl=pl-PL">MapController</a>
	 */
	public MapController mapCtrl;
	
	/**
	 * Punkt okreslajacy polozenie geograficzne.
	 * @see <a href="https://developers.google.com/maps/documentation/android/reference/com/google/android/maps/GeoPoint?hl=pl-PL">GeoPoint</a>
	 */
	public GeoPoint point;
	
	/**
	 * Czy na mapie zostalo zaznaczone polozenie.
	 */
	public boolean isMarkerAdded;
	
	/**
	 * Warstwa nakladana na mape, na ktorej rysowany jest znacznik zaznaczonej lokalizacji.
	 */
	public MyItemizedOverlay itemizedOverlay;
	
	/**
	 * Szerokosc geograficzna.
	 */
	public double latitude;
	
	/**
	 * Dlugosc geograficzna.
	 */
	public double longitude;
	
	/**
	 * Layout zawierajacy mape {@link #mapView} oraz tekst nad nia umieszczony {@link #tDamagePlace}.
	 */
	public LinearLayout linLayMap;
	
	/**
	 * Obiekt klasy aktywnosci aplikacji, do ktorej {@link OptionView} jest podpiety.
	 */
	private DziuraActivity app;
	
	/**
	 * Zmienna zwiazana z pytaniem uzytkownika o chec polaczenia z Internetem w celu korzystania z mapy.
	 * Jezeli ma wartosc <i>true</i> to mapa zostanie schowana po tym, jak uzytkownik wybierze klawisz <i>Nie</i>
	 * w dialogu pytajacym o polaczenie z Internetem.
	 */
	public boolean wifiMap = false; //jezeli true, to podczas wyswietlania dialogu 'czy chcesz sie polaczyc z internetem aby korzystac z mapy'
									//po nacisnieciu buttona 'nie' mapa zostanie schowana, a wifiMap ustawiona z powrotem na false
	
	/**
	 * Konstruktor klasy {@link OptionView}. Wykonuje nastepujace czynnosci:
	 * <ul>
	 * <li>przypisuje do zmiennej lokalnej referencje do instancji klasy {@link DziuraActivity} podana jako argument
	 * <li>przypisuje do zmiennych lokalnych odpowiadajace im widoki, layouty, przyciski
	 * <li>inicjalizuje dzia³anie gestów w tym widoku
	 * <li>inicjalizuje liste typow szkod
	 * <li>inicjalizuje mape
	 * <li>przypisuje 'sluchaczy' przyciskom
	 * <li>ustawia orientacje ekranu na <i>PORTRAIT</i>
	 * </ul>
	 * @param app instancja aktywnosci aplikacji
	 */
	public OptionView(DziuraActivity app)
	{
		this.app = app;
		mOptionsView = app.findViewById(R.id.optionView);
		
		app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		initGesture();
		initDamageTypeList();
		initMail();
		initGPS();
		initMap();

		bSubmit = (ImageButton) app.findViewById(R.id.submit);
		mZglosListener = new MyOnZglosClickListener(app);
		bSubmit.setOnClickListener(mZglosListener);
		
		descriptionTxt = (EditText) app.findViewById(R.id.editTextDescription);	
		
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

	private void initGesture() {
        GestureOverlayView gestures = (GestureOverlayView) app.findViewById(R.id.gesturesOptions);
		gestures.addOnGesturePerformedListener(new GestureListener(app));
		GestureOverlayView gestures2 = (GestureOverlayView) app.findViewById(R.id.gesturesOptions2);
		gestures2.addOnGesturePerformedListener(new GestureListener(app));
	}
	
	private void initGPS() {
		cGPS = (CheckBox) app.findViewById(R.id.checkBoxGetGPS); //checkBox 'czy pobierac polozenie GPS'
		tDamagePlace = (TextView) app.findViewById(R.id.textViewDamagePlace); //napisz 'miejsce wystapienia zdarzenia'

		//pokazanie pola na adres, w zaleznosci od checkBoxa
		cGPS.setOnClickListener(new MyOnGPSClickListener(app));
	}
	
	private void initMail() {
		cMail = (CheckBox) app.findViewById(R.id.checkBoxGetInfo); //checkBox o emailu
		eMail = (EditText) app.findViewById(R.id.editTextMail); //pole tekstowe na email
		eMail.addTextChangedListener(new MailChecker(eMail));
		
		//pokazanie pola na email, w zaleznosci od checkBoxa
		cMail.setOnClickListener(new MyOnEmailClickListener(cMail, eMail));
	}
	
	private void initDamageTypeList() 
	{
		if(app.isInternetEnabled() == true)
		{
			getDamageTypesFromServer();
		}
		String[] typesArray = app.appConfig.getDamageTypes();
		sDamageType = (Spinner) app.findViewById(R.id.spinnerDamageType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(app, android.R.layout.simple_spinner_item, typesArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		damageTypeListener = new MyOnItemSelectedListener();
		sDamageType.setOnItemSelectedListener(damageTypeListener);
		sDamageType.setAdapter(adapter);
	}

	private void getDamageTypesFromServer()
	{
		ArrayList<String> lista = new ArrayList<String>();
		//preparing post
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000); 
		HttpClient httpclient = new DefaultHttpClient(params); 
	    HttpGet request = new HttpGet("http://dziura.zapto.org:3000//res//categories.json");
	    request.setHeader("Accept", "application/json"); 
	    request.setHeader("Content-type", "application/json"); 
	    try 
	    {
	    	HttpResponse response;
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity!=null)
			{
				//read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
				{
					instream = new GZIPInputStream(instream);
				}
				// convert content stream to a String
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				try 
				{
					while ((line = reader.readLine()) != null) 
					{
						sb.append(line + "\n");
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				finally 
				{
					try 
					{
						instream.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				String resultString = sb.toString();
			    instream.close();
			    resultString = resultString.substring(0,resultString.length()-1);
			    JSONArray responseJSONArray = new JSONArray(resultString);
			    for(int i=0; i < responseJSONArray.length(); i++)
			    {
			    	JSONObject tmp = responseJSONArray.getJSONObject(i);
			    	lista.add(tmp.getString("name"));
			    }
			}
			if(response.getStatusLine().getStatusCode() == 200)
			{
				String[] typesList = new String[lista.size()];
				lista.toArray(typesList);
			    app.appConfig.setDamageTypes(typesList);
				return;
			}
		}
	    catch (SocketTimeoutException e)
	    {
	    	e.printStackTrace();
			return;
	    }
	    catch (ConnectTimeoutException e)
	    {
	    	e.printStackTrace();
			return;
	    }
	    catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			return;
		} 
	    catch (IOException e) 
		{
			e.printStackTrace();
			return;
		} 
	    catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return;
	}

	private void initMap() {
		mapView = (MyMapView) app.findViewById(R.id.mapView);
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
