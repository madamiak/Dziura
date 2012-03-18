package projekt.zespolowy.dziura;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class MyOnEmailClickListener implements OnClickListener
{
	private CheckBox email_check;
	private EditText mail_txt;

	public MyOnEmailClickListener(CheckBox _email_check, EditText _mail_txt)
	{
		this.email_check = _email_check;
		this.mail_txt = _mail_txt;
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(email_check.isChecked()==true)
		{
			mail_txt.setVisibility(View.VISIBLE);
			mail_txt.setEnabled(true);
			mail_txt.requestFocus();
		}
		else
		{
			mail_txt.setVisibility(View.GONE);
			mail_txt.setEnabled(false);
		}
		
	}

}
