package projekt.zespolowy.dziura.AppView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import projekt.zespolowy.dziura.DziuraActivity;

/**
 * Klasa zawierajaca dialog, ktory zostanie wyswietlony po nacisnieciu na miniaturke wykonanego zdjecia.
 * Zawiera wskazowki na temat korzystania z aplikacji.
 */
public class AfterMiniDialog
{
	private DziuraActivity dziuraAct;
	private AlertDialog dialog;
	
	/**
	 * Konstruktor. Nastepuje w nim inicjalizacja dialogu ktory nastapi wyswietlony przez 
	 * {@link projekt.zespolowy.dziura.AppView.AfterMiniDialog#showDialog()}.
	 * @param context kontekst aplikacji, w ktorej dialog zostanie wyswietlony
	 */
	public AfterMiniDialog(DziuraActivity context)
	{
		this.dziuraAct = context;
		dialog = new AlertDialog.Builder(dziuraAct).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("Wybrane zdjêcie zosta³o w³aœnie wyœwietlone w powiêkszeniu. " +
				"Mo¿esz dodaæ znacznik na zdjêciu poprzez naciœniêcie wybranego " +
				"fragmentu fotografii i opisanie go, a nastêpnie naciœniêcie przycisku z" +
				" zielonym plusem. Aby anulowaæ dodawanie znacznika, naciœnij przycisk X." +
				" Wszystkie dodane przez Ciebie znaczniki zostan¹ wys³ane wraz ze zdjêciem" +
				" w zg³oszeniu szkody.");
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
