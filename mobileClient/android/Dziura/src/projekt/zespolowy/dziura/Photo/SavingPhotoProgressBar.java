package projekt.zespolowy.dziura.Photo;

import projekt.zespolowy.dziura.DziuraActivity;
import android.app.ProgressDialog;

/**
 * Klasa wyswietlajaca dialog z paskiem postepu podczas zapisywania zdjecia po jego wykonaniu. Implementuje interfejs {@link Runnable}.
 *
 */
public class SavingPhotoProgressBar implements Runnable {
	
	private DziuraActivity dziuraAct;
	private ProgressDialog progress;
	
	/**
	 * Konstruktor klasy {@link SavingPhotoProgressBar}. Przypisuje do zmiennej lokalnej referencje do obiektu klasy {@link DziuraActivity}.
	 * @param context instancja aktywnosci aplikacji
	 */
	public SavingPhotoProgressBar(DziuraActivity context)
	{
		dziuraAct = context;
		progress = new ProgressDialog(dziuraAct);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("Trwa zapisywanie zdjêcia");
		progress.setIndeterminate(true);
	}

	/**
	 * Funkcja inicjalizuje dialog i wyswietla go.
	 */
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
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
