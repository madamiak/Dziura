package projekt.zespolowy.dziura;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MailChecker implements TextWatcher
{
	private EditText eMail;
	
	MailChecker(EditText mail) {
		eMail = mail;
	}
	
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

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {		
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
    
    private boolean matchRegEx(String str, String pattern)
    {
    	Pattern patt = Pattern.compile(pattern); 
        Matcher matcher = patt.matcher(str); 
        return matcher.matches();
    }

}