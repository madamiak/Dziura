package projekt.zespolowy.dziura.GPS;

import projekt.zespolowy.dziura.DziuraActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Listener obslugujacy nacisniecie checkboxa dotyczacego pobierania lokalizacji przez odbiornik GPS.
 * Implementuje interfejs {@link android.view.View.OnClickListener}.
 *
 */
public class MyOnGPSClickListener implements OnClickListener 
{
	private DziuraActivity dziuraAct;

	/**
	 * Konstruktor klasy {@link MyOnGPSClickListener}.
	 * @param dziuraAct instancja klasy aktywnosci, dla ktorej ma nastapic nasluchiwanie nacisniecia checkboxa
	 */
	public MyOnGPSClickListener(DziuraActivity dziuraAct)
	{
		this.dziuraAct = dziuraAct;
	}

	/**
	 * Funkcja wywolywana po nacisnieciu checkboxa. Wykonuje akcje, w zaleznosci od tego, czy zostal zaznaczony, czy odznaczony:
	 * <ul>
	 * <li><i>jezeli zostal zaznaczony</i> - chowa mape oraz opcje menu, dotyczaca trybu mapy
	 * <li><i>jezeli zostal odznaczony</i> - sprawdza, czy aplikacja ma lacznosc z Internetem. Jezeli tak, to wyswietla mape, jezeli nie
	 * to umozliwia nawiazanie polaczenia, poprzez wysweitlenie ekranu ustawien polaczen bezprzewodowych.
	 * </ul>
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) 
	{
		if(dziuraAct.vOption.cGPS.isChecked()==true)
		{
			dziuraAct.vOption.tDamagePlace.setVisibility(View.GONE);
			dziuraAct.vOption.linLayMap.setVisibility(View.GONE);
			if(dziuraAct.appMenu != null)
			{
				dziuraAct.appMenu.getMenu().setGroupVisible(1, false);
			}
		}
		else
		{
			boolean isInternetEnabled = dziuraAct.isInternetEnabled();
			if (!(isInternetEnabled)) 
			{  //jesli internet nie jest uruchomiony to pokazujemy okienko zeby wlaczyc internet
				dziuraAct.vOption.wifiMap = true;
				dziuraAct.showWirelessOptions("Aby korzystaæ z mapy, nale¿y po³¹czyæ siê z Internetem. Czy chcesz siê po³¹czyæ?", "Uwaga");
				dziuraAct.vOption.cGPS.setChecked(true);
			}
			else
			{
				dziuraAct.vOption.tDamagePlace.setVisibility(View.VISIBLE);
				dziuraAct.vOption.linLayMap.setVisibility(View.VISIBLE);
				if(dziuraAct.appMenu != null)
				{
					dziuraAct.appMenu.getMenu().setGroupVisible(1, true);
				}
			}
		}
	}

}
