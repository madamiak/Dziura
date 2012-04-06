package projekt.zespolowy.dziura;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projekt.zespolowy.dziura.AppView.GetLocationTask;
import projekt.zespolowy.dziura.AppView.LocationProgressBar;
import projekt.zespolowy.dziura.AppView.SendingProgressBar;
import projekt.zespolowy.dziura.AppView.SendingTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	public double currentLatitude = 0.0, currentLongitude = 0.0;
	public LocationManager locationManager;
	public LocationProgressBar locProg;
	public SendingProgressBar sendProg;

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
			if (dziuraAct.isInternetEnabled() == true)
			{
				if (gps_check.isChecked() == true) //jesli jest zaznaczone pole z GPSem to pobieramy pozycje
				{
					locationManager = (LocationManager) dziuraAct.getSystemService(Context.LOCATION_SERVICE);
					if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
					{ //jesli GPS nie jest uruchomiony to pokazujemy okienko zeby wlaczyc GPSa
						showGpsEnableDialog();
					} 
					else
					{ //jesli jest wlaczony to pobieramy lokalizacje
						locProg = new LocationProgressBar(dziuraAct);
						locProg.run();
						GetLocationTask gpsLoc = new GetLocationTask(dziuraAct);
						gpsLoc.execute();
					}
				} else { //jezeli nie chcemy GPSa to tu wysylamy zgloszenie z polozeniem z mapy)
					showSuccesDialog();
				}
			}
			else
			{
				dziuraAct.showWirelessOptions("Aby wys³aæ zg³oszenie, nale¿y po³¹czyæ siê z Internetem. Czy chcesz siê po³¹czyæ?", "Uwaga");
			}
		}
	}
	
	private void showGpsEnableDialog()
	{
		AlertDialog enableGpsDialog = new AlertDialog.Builder(dziuraAct).create();
		enableGpsDialog.setTitle("Uwaga");
		enableGpsDialog.setMessage("Aby pobraæ lokalizacjê, nale¿y uruchomiæ us³ugê u¿ywania satelitów GPS."+
				" Czy chcesz j¹ teraz w³¹czyæ?");
		enableGpsDialog.setButton("Tak", new DialogInterface.OnClickListener()
		{
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
						Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						dziuraAct.startActivity(gpsOptionsIntent);
					}
		});
		enableGpsDialog.setButton2("Nie",new DialogInterface.OnClickListener()
		{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
		});
		enableGpsDialog.show();
	}

	public void showSuccesDialog() 
	{
		if(currentLatitude == 0.0 && gps_check.isChecked() == true)
		{
			dziuraAct.wyswietlTekst("Nie uda³o siê pobraæ Twojej lokalizacji GPS, spróbuj jeszcze raz");
		}
		else
		{
			sendProg = new SendingProgressBar(dziuraAct);
			sendProg.run();
			SendingTask send = new SendingTask(dziuraAct);
			send.execute();
		}
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
        else
        {
        	Editable email = mail_txt.getText();
            String strEmail = email.toString();
            pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}";
            if(!matchRegEx(strEmail, pattern))
            {
            	dziuraAct.vOption.saveMail = false;
            }
        }
        if(gps_check.isChecked() == false && dziuraAct.vOption.isMarkerAdded == false)
        {
        	dziuraAct.vibrate(300);
        	dziuraAct.wyswietlTekst("Zaznacz na mapie miejsce wyst¹pienia szkody");
        	dziuraAct.vOption.mapView.requestFocus();
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
