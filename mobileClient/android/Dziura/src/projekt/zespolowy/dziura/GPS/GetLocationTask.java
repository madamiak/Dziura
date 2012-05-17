package projekt.zespolowy.dziura.GPS;

import com.google.android.maps.GeoPoint;

import android.location.Criteria;
import android.os.AsyncTask;
import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.Map.GeocodingTask;

/**
 * Asynchroniczne zadanie (rozszerza klase abstrakcyjna {@link android.os.AsyncTask}) sluazace do pobrania lokalizacji w
 * osobnym watku dzialajacym w tle.
 *  Uruchamiane przez wywolanie funkcji {@link android.os.AsyncTask#execute(Object...)}
 * na instancji klasy.
 */
public class GetLocationTask extends AsyncTask < DziuraActivity, String, Boolean >
{
	private DziuraActivity dziuraAct;
	private MyLocationListener mlocListener;
	public double latitude = 0.0, longitude = 0.0;
	
	/**
	 * Konstruktor klasy {@link GetLocationTask}.
	 * @param dziuraAct referencja do instancji klasy {@link projekt.zespolowy.dziura.DziuraActivity}
	 */
	public GetLocationTask(DziuraActivity dziuraAct)
	{
		this.dziuraAct = dziuraAct;
	}
	
	/**
	 * Funkcja wykonywana w watku interfejsu graficznego przed wykonaniem {@link #doInBackground(DziuraActivity...)}.
	 * Inicjalizuje sluchacza lokalizacji ({@link projekt.zespolowy.dziura.GPS.MyLocationListener}) i rozpoczyna
	 * proces szukania sygnalu GPS.
	 * 
	 * @see android.location.Criteria
	 * @see android.location.LocationManager
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	protected void onPreExecute()
	{
		Criteria crta = new Criteria(); 
        crta.setAccuracy(Criteria.ACCURACY_FINE); 
        crta.setAltitudeRequired(false); 
        crta.setBearingRequired(false); 
        crta.setCostAllowed(true); 
        crta.setPowerRequirement(Criteria.POWER_LOW); 
        String provider = dziuraAct.vOption.mZglosListener.locationManager.getBestProvider(crta, true);
        mlocListener = new MyLocationListener(dziuraAct);
        dziuraAct.vOption.mZglosListener.locationManager.requestLocationUpdates(provider, 50, 0, mlocListener);
	}

	/**
	 * Funkcja ta wykonywana jest przez watek w tle. Sprawdza, czy sluchacz lokalizacji
	 * {@link projekt.zespolowy.dziura.GPS.MyLocationListener}
	 * pobral juz wspolrzedne geograficzne. Jezeli tak, to watek jest przerywany i nastepuje
	 * zapisanie tych wspolrzednych oraz zamkniecie dialogu wyswietlonego przez {@link LocationProgressBar}.
	 * 
	 * @param params argument nie wykorzystywany przez nas w funkcji
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(DziuraActivity... params)
	{
		synchronized(this)
		{
			for(int i=0; i<30; i++) //wait 30 seconds for location
			{
				try 
				{
					this.wait(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				if(dziuraAct.vOption.mZglosListener.currentLatitude != 0.0)
				{
					break;
				}
			}
		}
		if(dziuraAct.vOption.mZglosListener.locProg.progress.isShowing())
        {
        	dziuraAct.vOption.mZglosListener.locProg.stop();
        }
		dziuraAct.vOption.mZglosListener.locationManager.removeUpdates(mlocListener);
		if(dziuraAct.vOption.mZglosListener.currentLatitude != 0.0)
		{
			dziuraAct.vOption.point =  new GeoPoint((int)(dziuraAct.vOption.mZglosListener.currentLatitude * 1e6), 
					(int)(dziuraAct.vOption.mZglosListener.currentLongitude * 1e6));
		}
		return true;
	}
	
	/**
	 * Funkcja wywolywana po zakonczeniu pracy przez watek uruchomiony poprzez {@link #execute(DziuraActivity...)}.
	 * Wywoluje funkcje <i>showSuccesDialog()</i> {@link projekt.zespolowy.dziura.MyOnZglosClickListener)}. Poza tym,
	 * jezeli wspolrzedne zostay poprawnie okreslone, to zostana one poddane geocodowaniu (przez wykonanie
	 * zadania klasy {@link projekt.zespolowy.dziura.Map.GeocodingTask}) w celu wyswietlenia na ekranie adresu
	 * na podstawie pobranej lokalizacji.
	 * @param result wartosc zwrocona przez funkcje {@link #doInBackground(DziuraActivity...)}
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected void onPostExecute(Boolean result)
	{
		if(result == true)
		{
			if(dziuraAct.vOption.mZglosListener.currentLatitude != 0.0)
			{
				GeocodingTask geocode = new GeocodingTask(dziuraAct, true);
				geocode.execute();
			}
			dziuraAct.vOption.mZglosListener.showSuccesDialog();
		}
	}
}
