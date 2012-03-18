package projekt.zespolowy.dziura;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class MyOnZglosClickListener implements OnClickListener
{
	
	private DziuraActivity dziuraAct;
	private CheckBox gps_check;
	private CheckBox email_check;
	private EditText mail_txt; 

	public MyOnZglosClickListener(DziuraActivity dziuraActivity, CheckBox agps_check, CheckBox aemail_check, EditText amail_txt)
	{
		this.dziuraAct = dziuraActivity;
		this.gps_check = agps_check;
		this.email_check = aemail_check;
		this.mail_txt = amail_txt;
	}

	public void onClick(View arg0)
	{
		if(sprawdzPola() == true)
		{
			if (gps_check.isChecked() == true) //jesli jest zaznaczone pole z GPSem to pobieramy pozycje
			{
				LocationManager locationManager = (LocationManager) dziuraAct.getSystemService(Context.LOCATION_SERVICE);
				if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
				{  //jesli GPS nie jest uruchomiony to pokazujemy okienko zeby wlaczyc GPSa
					dziuraAct.wyswietlTekst("W³¹cz GPS");
					try 
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						dziuraAct.wyswietlTekst(e.getMessage());
					}
					Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
					dziuraAct.startActivity(gpsOptionsIntent);
				}
				else
				{ //jesli jest wlaczony to pobieramy lokalizacje
					Criteria crta = new Criteria(); 
			        crta.setAccuracy(Criteria.ACCURACY_FINE); 
			        crta.setAltitudeRequired(false); 
			        crta.setBearingRequired(false); 
			        crta.setCostAllowed(true); 
			        crta.setPowerRequirement(Criteria.POWER_LOW); 
			        String provider = locationManager.getBestProvider(crta, true);
			        double currentLatitude, currentLongitude;
					Location currentLocation = locationManager.getLastKnownLocation(provider);
					if(currentLocation != null)
					{
						currentLatitude = currentLocation.getLatitude();
						currentLongitude = currentLocation.getLongitude();
						dziuraAct.wyswietlTekst("lastKnown");
					}
					else
					{
						LocationListener mlocListener = new MyLocationListener(dziuraAct.getApplicationContext(), this);
						locationManager.requestLocationUpdates(provider, 1000, 0, mlocListener);
						currentLatitude = ((MyLocationListener) mlocListener).getLat();
						currentLongitude = ((MyLocationListener) mlocListener).getLon();
						dziuraAct.wyswietlTekst("current");
						locationManager.removeUpdates(mlocListener);
					}
					dziuraAct.wyswietlTekst("Zg³oszenie wys³ane (aktualna pozycja lat = "+ currentLatitude + ", lon = " + currentLongitude + ")");
					showSuccesDialog();
				}
			}
			else
			{ //jezeli nie chcemy GPSa to tu wysylamy zgloszenie z podanym adresem (albo raczej polozenie z mapy)
				dziuraAct.wyswietlTekst("Zg³oszenie wys³ane (wykorzystujê adres/mapê)");
				showSuccesDialog();
			}
		}
		else
		{
			//wyswietlTekst("Zg³oszenie nie zosta³o poprawnie wype³nione");
		}
	}
	
	private void showSuccesDialog() 
	{
		AlertDialog dialog = new AlertDialog.Builder(dziuraAct).create();
		dialog.setMessage("Zg³oszenie zosta³o pomyœlnie wys³ane");
		dialog.setTitle("Dziêkujemy");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				dziuraAct.finish();
			}
		});
		dialog.show();
		
		//save configuration
		dziuraAct.appConfig.setEMail(dziuraAct.eMail.getText().toString());
		dziuraAct.appConfig.setUseGPS(dziuraAct.cGPS.isChecked());
		dziuraAct.appConfig.setSendMail(dziuraAct.cMail.isChecked());
		dziuraAct.appConfig.setLat(dziuraAct.point.getLatitudeE6()/1E6);
		dziuraAct.appConfig.setLon(dziuraAct.point.getLongitudeE6()/1E6);
		dziuraAct.appConfig.save(dziuraAct.CONFIG_FILENAME, dziuraAct);
	}

	//sprawdzenie pol, np regExy, chociaz jak nie bedzie wpisywania adresu to sie mniej to przyda
    //ale mozna sprawdzac, czy jezeli ktos chce otrzymywac email to czy wpisal ten email poprawnie
    private boolean sprawdzPola()
    {
    	String pattern = null;
        if(email_check.isChecked())
        {
            Editable email = mail_txt.getText();
            String strEmail = email.toString();
            pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}";
            if(!matchRegEx(strEmail, pattern))
            {
            	dziuraAct.vibrate(300);            	
            	dziuraAct.wyswietlTekst("Wpisz adres email poprawnie");
            	mail_txt.requestFocus();
            	return false;
            }
        }
        if(gps_check.isChecked() == false && dziuraAct.isMarkerAdded == false)
        {
        	dziuraAct.vibrate(300);
        	dziuraAct.wyswietlTekst("Zaznacz na mapie miejsce wyst¹pienia szkody");
        	dziuraAct.mapView.requestFocus();
        	return false;
        }
    	return true;
    }
    
    private boolean matchRegEx(String str, String pattern)
    {
    	Pattern patt = Pattern.compile(pattern); 
        Matcher matcher = patt.matcher(str); 
        return matcher.matches();
    }
}
