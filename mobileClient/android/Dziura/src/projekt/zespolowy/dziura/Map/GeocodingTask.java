package projekt.zespolowy.dziura.Map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import projekt.zespolowy.dziura.DziuraActivity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

/**
 * Asynchroniczne zadanie (rozszerza klase abstrakcyjna {@link android.os.AsyncTask}) sluazace do geocodowania, czyli uzyskaniu adresu
 * na podstawie wspolrzednych geograficznych.
 *  Uruchamiane przez wywolanie funkcji {@link android.os.AsyncTask#execute(Object...)}
 * na instancji klasy.
 */
public class GeocodingTask extends AsyncTask< String, String, String >
{
	
	private DziuraActivity dziuraAct;
	private Geocoder geoCoder;
	private boolean showText;

	/**
	 * Konstruktor klasy {@link GeocodingTask}.
	 *
	 * @param dziuraact referencja do instancji klasy {@link projekt.zespolowy.dziura.DziuraActivity}
	 * @param showText czy podczas wyswietlania adresu ma zostac wyswietlony dodatkowy tekst i adres (<i>true</i>) czy tylko adres (<i>false</i>)
	 */
	public GeocodingTask(DziuraActivity dziuraact, boolean showText)
	{
		this.dziuraAct = dziuraact;
		this.showText = showText;
	}
	
	/**
	 * Funkcja wykonywana w watku interfejsu graficznego przed wykonaniem {@link #doInBackground(String...)}.
	 * Inicjalizuje obiekt klasy {@link android.location.Geocoder}.
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	protected void onPreExecute()
	{
		geoCoder = new Geocoder(dziuraAct.getBaseContext(), Locale.getDefault());
	}

	/**
	 * Funkcja ta wykonywana jest przez watek w tle. Pobiera adres na podstawie wspolrzednych geograficznych.
	 * 
	 * @param params argument nie wykorzystywany przez nas w funkcji
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params)
	{
		try 
		{
			List<Address> addresses = geoCoder.getFromLocation(dziuraAct.vOption.point.getLatitudeE6()  / 1E6, 
					dziuraAct.vOption.point.getLongitudeE6() / 1E6, 1);
	
	     	String add = "";
	     	if (addresses.size() > 0) 
	     	{
		      	for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
		      	{
		      		add += addresses.get(0).getAddressLine(i) + "\n";
		      	}
	     	}
	     	add = add.substring(0, add.length()-1);
	     	return add;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Wyswietla adres przy pomocy funkcji <i>wyswietlTekst</i> z klasy {@link DziuraActivity}.
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected void onPostExecute(String result)
	{
		if(!(result == null || result.contains("server")))
		{
			String location = "";
			if(showText == true)
	     	{
	     		location = "Twoja przybli¿ona lokalizacja to:\n";
	     	}
			location += result;
			dziuraAct.wyswietlTekst(location);
		}
	}
}
