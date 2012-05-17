package projekt.zespolowy.dziura.Mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Klasa sprawdzajaca poprawnosc wpisanego emaila. Implementuje interfejs {@link android.text.TextWatcher}.
 *
 */
public class MailChecker implements TextWatcher
{
	private EditText eMail;
	
	/**
	 * Konstruktor klasy {@link MailChecker}.
	 * @param mail pole tekstowe, w ktorym mail bedzie wpisywany
	 */
	public MailChecker(EditText mail) {
		eMail = mail;
	}
	
	/**
	 * Funkcja wywolywana po zmianie tekstu w polu tekstowym. Sprawdza jego zgodnosc z wyrazeniem regularnym okreslajacym prawidlowy
	 * adres email. Jezeli tekst pasuje do wyrazenia regularnego, to zostaje pokolorowany na zielono. Jezeli email jest
	 * niepoprawny, to kolor tekstu zmienia sie na czerwony.
	 * @param arg0 obiekt zawierajacy tekst wprowadzony do pola tekstowego
	 */
	public void afterTextChanged(Editable arg0) {
		Editable edEmail = eMail.getText();
		String strEmail = edEmail.toString();
        String pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}";
        if(!matchRegEx(strEmail, pattern)) 
        {
        	eMail.setTextColor(Color.RED);
        }
        else 
        {
        	eMail.setTextColor(Color.GREEN);
        }	
	}

	/**
	 * Nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {		
	}

	/**
	 * Nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
    
    private boolean matchRegEx(String str, String pattern)
    {
    	Pattern patt = Pattern.compile(pattern); 
        Matcher matcher = patt.matcher(str); 
        return matcher.matches();
    }

}