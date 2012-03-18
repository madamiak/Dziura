package projekt.zespolowy.dziura;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyLocationListener implements LocationListener
{
	Context ctx = null;
	double lat = 0.0;
	double lon = 0.0;
	OnClickListener onClLst;

	public MyLocationListener(Context context, OnClickListener onClickListener)
	{
		this.ctx = context;
		this.onClLst = onClickListener;
	}

	public void onLocationChanged(Location location)
	{
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

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
	
	public double getLat()
	{
		return this.lat;
	}
	
	public double getLon()
	{
		return this.lon;
	}

}
