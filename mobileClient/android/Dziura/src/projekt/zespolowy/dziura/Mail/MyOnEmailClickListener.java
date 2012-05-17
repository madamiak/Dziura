package projekt.zespolowy.dziura.Mail;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Listener sprawdzajacy, czy nacisnieto checkboxa zwiazanego z wysylaniem powiadomien o zgloszeniu na adres email.
 * Implementuje interfejs {@link android.view.View.OnClickListener}.
 *
 */
public class MyOnEmailClickListener implements OnClickListener
{
	private CheckBox cMail;
	private EditText eMail;

	/**
	 * Konstruktor klasy {@link MyOnEmailClickListener}.
	 * @param emailCheckbox checkbox, dla ktorego Listener nasluchuje zmian zaznaczenia
	 * @param emailEditText pole tekstowe sluzace do wprowadzenia adresu email
	 */
	public MyOnEmailClickListener(CheckBox emailCheckbox, EditText emailEditText) {
		this.cMail = emailCheckbox;
		this.eMail = emailEditText;
	}

	/**
	 * Funkcja wywolywana po nacisnieciu na checkboxa zwiazanego z adresem email. W zaleznosci od zaznaczenia/odznaczenia
	 * zmienia widocznosc pola tekstowego sluzaczego do wpisania emaila, na ktory maja byc wysylane powiadomienia o zmianie stanu zgloszenia.
	 */
	public void onClick(View v)
	{
		if(cMail.isChecked() == true) {
			eMail.setVisibility(View.VISIBLE);
			eMail.setEnabled(true);
			eMail.requestFocus();
		} else {
			eMail.setVisibility(View.GONE);
			eMail.setEnabled(false);
		}
	}
}
