package projekt.zespolowy.dziura.AppView;

import android.app.ProgressDialog;
import projekt.zespolowy.dziura.DziuraActivity;

public class SendingProgressBar implements Runnable
{
	private DziuraActivity dziuraAct;
	public ProgressDialog progress;

	public SendingProgressBar(DziuraActivity dziuraact)
	{
		this.dziuraAct = dziuraact;
	}
	
	public void run() 
	{
		progress = new ProgressDialog(dziuraAct);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("Trwa wysy³anie zg³oszenia");
		progress.setIndeterminate(true);
		progress.show();
	}
	
	public void stop()
	{
		progress.dismiss();
	}

}
