package projekt.zespolowy.dziura.AppView;

import projekt.zespolowy.dziura.DziuraActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Klasa zawierajaca dialog wyswietlany po wykonaniu zdjecia w aplikacji.
 * Zawiera podpowiedzi, jak korzystac z aplikacji.
 */
public class AfterPhotoDialog
{
	private DziuraActivity dziuraAct;
	private AlertDialog dialog;
	
	/**
	 * Konstruktor. Nastepuje w nim inicjalizacja dialogu ktory nastapi wyswietlony przez 
	 * {@link projekt.zespolowy.dziura.AppView.AfterPhotoDialog#showDialog()}.
	 * @param context kontekst aplikacji, w ktorej dialog zostanie wyswietlony
	 */
	public AfterPhotoDialog(DziuraActivity context)
	{
		this.dziuraAct = context;
		dialog = new AlertDialog.Builder(dziuraAct).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("Wykonane zosta�o w�a�nie pierwsze zdj�cie. Mo�esz teraz wykona� kolejn� fotografi�," +
				" nacisn�� na miniaturk� wykonanego zdj�cia aby zobaczy� je w powi�kszeniu lub" +
				" przej�� do formularza aby wys�a� zg�oszenie szkody. Ka�de z wykonanych zdj�� zostanie wys�ane" +
				" wraz ze zg�oszeniem, u�atwi to nam prac�. Pami�taj, zdj�cia mo�esz wykona� zar�wno przyciskiem" +
				" na ekranie jak i przyciskiem aparatu z boku telefonu.");
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}
	
	/**
	 * Wyswietla dialog.
	 */
	public void showDialog()
	{
		dialog.show();
	}
}
