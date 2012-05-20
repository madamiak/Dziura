package projekt.zespolowy.dziura.AppView;

import android.app.ProgressDialog;
import projekt.zespolowy.dziura.DziuraActivity;

/**
 * Klasa wyswietlajaca dialog z paskiem postepu podczas wysylania zgloszenia. Implementuje interfejs {@link Runnable}.
 *
 */
public class SendingProgressBar implements Runnable
{
	private DziuraActivity dziuraAct;
	private ProgressDialog progress;

	/**
	 * Konstruktor klasy {@link SendingProgressBar}. Przypisuje do zmiennej lokalnej referencje do obiektu klasy {@link DziuraActivity}.
	 * @param dziuraact instancja aktywnosci aplikacji
	 */
	public SendingProgressBar(DziuraActivity dziuraact)
	{
		this.dziuraAct = dziuraact;
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
		progress.setMessage("Trwa wysy³anie zg³oszenia");
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
