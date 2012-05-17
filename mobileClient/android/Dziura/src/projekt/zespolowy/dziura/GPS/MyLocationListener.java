package projekt.zespolowy.dziura.GPS;

import projekt.zespolowy.dziura.DziuraActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Listener lokalizacji GPS. Implementuje interfejs {@link android.location.LocationListener}.
 *
 */
/**
 * @author Daniel
 *
 */
public class MyLocationListener implements LocationListener
{
	private DziuraActivity ctx = null;

	/**
	 * Konstruktor klasy {@link MyLocationListener}.
	 * @param context instancja aktywnosci aplikacji
	 */
	public MyLocationListener(DziuraActivity context)
	{
		this.ctx = context;
	}

	/**
	 * Funkcja wywolywana po zmianie aktualnej lokalizacji urzadzenia. Zapisuje odczytane wspolrzedne w zmiennych <i>currentLatitude</i> i 
	 * <i>currentLongitude</> klasy {@link projekt.zespolowy.dziura.MyOnZglosClickListener}.
	 */
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	public void onLocationChanged(Location location)
	{
			ctx.vOption.mZglosListener.currentLatitude = location.getLatitude();
			ctx.vOption.mZglosListener.currentLongitude = location.getLongitude();
	}

	/**
	 * Nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	public void onProviderDisabled(String provider)
	{
		//
	}

	/**
	 * Nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	public void onProviderEnabled(String provider)
	{
		//
	}

	/**
	 * Nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		//onClLst.notify();
	}

}
