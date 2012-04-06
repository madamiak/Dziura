package projekt.zespolowy.dziura.AppView;

import com.google.android.maps.GeoPoint;

import android.location.Criteria;
import android.os.AsyncTask;
import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.GPS.MyLocationListener;

public class GetLocationTask extends AsyncTask < DziuraActivity, String, Boolean >
{
	private DziuraActivity dziuraAct;
	private MyLocationListener mlocListener;
	
	public GetLocationTask(DziuraActivity dziuraAct)
	{
		this.dziuraAct = dziuraAct;
	}
	
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
        dziuraAct.vOption.mZglosListener.locationManager.requestLocationUpdates(provider, 500, 0, mlocListener);
	}

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
				//dziuraAct.vOption.mZglosListener.locationManager.removeUpdates(mlocListener);
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
			dziuraAct.vOption.point =  new GeoPoint((int)(dziuraAct.vOption.mZglosListener.currentLatitude * 1e6), (int)(dziuraAct.vOption.mZglosListener.currentLongitude * 1e6));
		}
		return true;
	}
	
	protected void onPostExecute(Boolean result)
	{
		if(result == true)
		{
			dziuraAct.vOption.mZglosListener.showSuccesDialog();
		}
	}
}
