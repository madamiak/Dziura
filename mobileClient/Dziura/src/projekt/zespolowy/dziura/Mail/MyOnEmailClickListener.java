package projekt.zespolowy.dziura.Mail;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class MyOnEmailClickListener implements OnClickListener
{
	private CheckBox cMail;
	private EditText eMail;

	public MyOnEmailClickListener(CheckBox emailCheckbox, EditText emailEditText) {
		this.cMail = emailCheckbox;
		this.eMail = emailEditText;
	}

	/**
	 * Changes visibility of edit text field to type email.
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
