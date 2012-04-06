package projekt.zespolowy.dziura.GPS;

import projekt.zespolowy.dziura.DziuraActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {
	private DziuraActivity ctx = null;

	public MyLocationListener(DziuraActivity context)
	{
		this.ctx = context;
	}

	public void onLocationChanged(Location location)
	{
			ctx.vOption.mZglosListener.currentLatitude = location.getLatitude();
			ctx.vOption.mZglosListener.currentLongitude = location.getLongitude();
	}

	// TODO: napisy do @string 
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(ctx, "GPS zosta³ wy³¹czony", Toast.LENGTH_SHORT).show();
	}

	public void onProviderEnabled(String provider)
	{
		Toast.makeText(ctx, "GPS zosta³ uruchomiony", Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		//onClLst.notify();
	}

}
