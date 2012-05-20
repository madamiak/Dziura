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
		dialog.setMessage("Wykonane zosta³o w³aœnie pierwsze zdjêcie. Mo¿esz teraz wykonaæ kolejn¹ fotografiê," +
				" nacisn¹æ na miniaturkê wykonanego zdjêcia aby zobaczyæ je w powiêkszeniu lub" +
				" przejœæ do formularza aby wys³aæ zg³oszenie szkody. Ka¿de z wykonanych zdjêæ zostanie wys³ane" +
				" wraz ze zg³oszeniem, u³atwi to nam pracê. Pamiêtaj, zdjêcia mo¿esz wykonaæ zarówno przyciskiem" +
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
