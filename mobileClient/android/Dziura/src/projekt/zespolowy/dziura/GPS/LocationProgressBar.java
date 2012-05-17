package projekt.zespolowy.dziura.GPS;

import projekt.zespolowy.dziura.DziuraActivity;
import android.app.ProgressDialog;

/**
 * Klasa wyswietlajaca dialog z paskiem postepu podczas pobierania lokalizacji poprzez GPS. Implementuje interfejs {@link Runnable}.
 *
 */
public class LocationProgressBar implements Runnable
{
	private DziuraActivity dziuraAct;
	public ProgressDialog progress;

	/**
	 * Konstruktor klasy {@link LocationProgressBar}. Przypisuje do zmiennej lokalnej referencje do obiektu klasy {@link DziuraActivity}.
	 * @param dziuraAct2 instancja aktywnosci aplikacji
	 */
	public LocationProgressBar(DziuraActivity dziuraAct2)
	{
		this.dziuraAct = dziuraAct2;
	}

	/**
	 * Funkcja inicjalizuje dialog i wyswietla go.
	 */
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		progress = new ProgressDialog(dziuraAct);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("Trwa pobieranie pozycji GPS");
		progress.setIndeterminate(true);
		progress.show();
	}
	
	/**
	 * Funkcja przerywajaca wyswietlanie dialogu.
	 */
	public void stop()
	{
		progress.dismiss();
	}

}
