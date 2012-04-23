package projekt.zespolowy.dziura;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projekt.zespolowy.dziura.AppView.SendingProgressBar;
import projekt.zespolowy.dziura.AppView.SendingTask;
import projekt.zespolowy.dziura.GPS.GetLocationTask;
import projekt.zespolowy.dziura.GPS.LocationProgressBar;

import android.content.Context;
import android.location.LocationManager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Listener oblugujacy nacisniecie przycisku wysylania (widocznego na dole aplikacji, w widoku formularza).
 * Implementuje interfejs {@link android.view.View.OnClickListener}. Po nacisnieciu przycisku wywolana zostaje funkcja
 * {@link projekt.zespolowy.dziura.MyOnZglosClickListener#onClick(View)}.
 *
 */
public class MyOnZglosClickListener implements OnClickListener
{
	
	private DziuraActivity dziuraAct;
	public double currentLatitude = 0.0, currentLongitude = 0.0;
	public LocationManager locationManager;
	public LocationProgressBar locProg;
	public SendingProgressBar sendProg;

	/**
	 * Konstruktor klasy {@link MyOnZglosClickListener}.
	 * @param dziuraActivity instancja aktywnosci, dla ktorej ma nastapic nasluchiwanie nacisniecia przycisku
	 */
	public MyOnZglosClickListener(DziuraActivity dziuraActivity)
	{
		this.dziuraAct = dziuraActivity;
	}

	/**Funkcja wywolana po nacisnieciu przycisky wysylania. Wykonane zostaja kolejno nastepujace operacje:
	 * <ul>
	 * <li>sprawdzenie, czy formularz zostal poprawnie wypelniony
	 * <ul>
	 * <li>czy w przypadku wybrania opcji korzystania z mapy zostala na niej zaznaczona pozycja wystapienia szkody
	 * <li>czy w przypadku wybrania opcji wysylania powiadomien na adres email zostal on w ogole wpisany i czy jest poprawny
	 * </ul>
	 * <li>sprawdzenie, czy polaczenie z Internetem jest aktywne - jest ono konieczne do wyslania zgloszenia - w 
	 * razie potrzeby zostanie wyswietlone okno ustawien umozliwiajace wlaczenie Internetu
	 * <li>sprawdzenie, czy aktywny jest odbiornik GPS - jezeli uzytkownik nie korzysta z mapy - w 
	 * razie potrzeby zostanie wyswietlone okno ustawien umozliwiajace wlaczenie GPSa
	 * <li>w przypadku korzystania z GPSa wyswietlona zostaje informacja o pobieraniu lokalizacji i uruchomione
	 * zostaje zadanie w tle ktore czeka na pobranie lokalizacji przez {@link projekt.zespolowy.dziura.GPS.MyLocationListener}
	 * <li>po pobraniu lokalizacji lub odczytaniu lokalizacji punktu zaznaczonego na mapie wywolana zostaje funkcja
	 * {@link projekt.zespolowy.dziura.MyOnZglosClickListener#showSuccesDialog()}
	 * </ul>
	 * 
	 * @param arg0 widok, ktory zostal nacisniety
	 * @see projekt.zespolowy.dziura.GPS.GetLocationTask
	 * @see projekt.zespolowy.dziura.GPS.LocationProgressBar
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View arg0)
	{
		if(sprawdzPola() == true)
		{
			if (dziuraAct.isInternetEnabled() == true)
			{
				if (dziuraAct.vOption.cGPS.isChecked() == true) //jesli jest zaznaczone pole z GPSem to pobieramy pozycje
				{
					locationManager = (LocationManager) dziuraAct.getSystemService(Context.LOCATION_SERVICE);
					if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
					{ //jesli GPS nie jest uruchomiony to pokazujemy okienko zeby wlaczyc GPSa
						dziuraAct.vOption.mOptionsView.postInvalidate();
						dziuraAct.showGpsOptions("Aby pobraæ lokalizacjê, nale¿y uruchomiæ odbiornik GPS."+
								" Czy chcesz go teraz w³¹czyæ?", "Uwaga");
						//showGpsEnableDialog();
					} 
					else
					{ //jesli jest wlaczony to pobieramy lokalizacje
						locProg = new LocationProgressBar(dziuraAct);
						locProg.run();
						GetLocationTask gpsLoc = new GetLocationTask(dziuraAct);
						gpsLoc.execute();
					}
				}
				else 
				{ //jezeli nie chcemy GPSa to tu wysylamy zgloszenie z polozeniem z mapy)
					showSuccesDialog();
				}
			}
			else
			{
				dziuraAct.showWirelessOptions("Aby wys³aæ zg³oszenie, nale¿y po³¹czyæ siê z Internetem. Czy chcesz siê po³¹czyæ?", "Uwaga");
			}
		}
	}
	

	/**
	 * Funkcja wywolywana przez {@link projekt.zespolowy.dziura.MyOnZglosClickListener} lub {@link projekt.zespolowy.dziura.GPS.GetLocationTask}
	 * w celu kontynuacji procesu wysylania. Zostaja tu wykonane zastepujace kroki:
	 * <ul>
	 * <li>wyswietlona zostaje informacja, ze zgloszenie jest
	 * wysylane i uruchomione zostaje zadanie w tle wysylajace zgloszenie
	 * <li>w przypadku niepowodzenia okreslenia lokalizacji, wyswietlona zostaje na ekranie wiadomosc informujaca,
	 * ze lokalizacji nie udalo sie pobrac i zeby sprobowac ponownie za chwile
	 * </ul>
	 * @see projekt.zespolowy.dziura.AppView.SendingTask
	 * @see projekt.zespolowy.dziura.AppView.SendingProgressBar
	 */
	public void showSuccesDialog() 
	{
		if(currentLatitude == 0.0 && dziuraAct.vOption.cGPS.isChecked() == true)
		{
			dziuraAct.wyswietlTekst("Nie uda³o siê pobraæ Twojej lokalizacji GPS, spróbuj jeszcze raz...");
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
        if(dziuraAct.vOption.cMail.isChecked())
        {
            Editable email = dziuraAct.vOption.eMail.getText();
            String strEmail = email.toString();
            pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}";
            if(!matchRegEx(strEmail, pattern))
            {
            	dziuraAct.vibrate(300);            	
            	dziuraAct.wyswietlTekst("Wpisz adres email poprawnie");
            	dziuraAct.vOption.eMail.requestFocus();
            	return false;
            }
        }
        else
        {
        	Editable email = dziuraAct.vOption.eMail.getText();
            String strEmail = email.toString();
            pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}";
            if(!matchRegEx(strEmail, pattern))
            {
            	dziuraAct.vOption.saveMail = false;
            }
        }
        if(dziuraAct.vOption.cGPS.isChecked() == false && dziuraAct.vOption.isMarkerAdded == false)
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
