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
		dialog.setMessage("Widok zdj�� prze��czony zosta� na widok formularza. Mo�esz tutaj wys�a� zg�oszenie, " +
				"jednak wcze�niej wybierz z listy odpowiedni� kategori�. Je�eli nie chcesz korzysta� z odbiornika GPS " +
				"to odznacz pole z nim zwi�zane i zaznacz na wy�wietlonej mapie miejsce wyst�pienia szkody. " +
				"Dodatkowo mo�esz zamie�ci� s�owny opis zg�oszenia, aby przes�a� nam wi�cej szczeg��w. Spos�b wysy�ania " +
				"zg�oszenia wybierz z menu.");
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
