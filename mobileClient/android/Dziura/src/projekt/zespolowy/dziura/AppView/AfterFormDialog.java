package projekt.zespolowy.dziura.AppView;

import projekt.zespolowy.dziura.DziuraActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Klasa zawierajaca dialog, ktory zostanie wyswietlony po przelaczeniu widoku zdjec na widok formularza.
 * Zawiera wskazowki na temat korzystania z aplikacji.
 */
public class AfterFormDialog
{
	private DziuraActivity dziuraAct;
	private AlertDialog dialog;
	
	/**
	 * Konstruktor. Nastepuje w nim inicjalizacja dialogu ktory nastapi wyswietlony przez 
	 * {@link projekt.zespolowy.dziura.AppView.AfterFormDialog#showDialog()}.
	 * @param context kontekst aplikacji, w ktorej dialog zostanie wyswietlony
	 */
	public AfterFormDialog(DziuraActivity context)
	{
		this.dziuraAct = context;
		dialog = new AlertDialog.Builder(dziuraAct).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("Widok zdjêæ prze³¹czony zosta³ na widok formularza. Mo¿esz tutaj wys³aæ zg³oszenie, " +
				"jednak wczeœniej wybierz z listy odpowiedni¹ kategoriê. Je¿eli nie chcesz korzystaæ z odbiornika GPS " +
				"to odznacz pole z nim zwi¹zane i zaznacz na wyœwietlonej mapie miejsce wyst¹pienia szkody. " +
				"Dodatkowo mo¿esz zamieœciæ s³owny opis zg³oszenia, aby przes³aæ nam wiêcej szczegó³ów. Sposób wysy³ania " +
				"zg³oszenia wybierz z menu.");
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
