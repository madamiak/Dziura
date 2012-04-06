package projekt.zespolowy.dziura.AppView;

import projekt.zespolowy.dziura.DziuraActivity;
import android.app.ProgressDialog;

public class LocationProgressBar implements Runnable
{
	private DziuraActivity dziuraAct;
	public ProgressDialog progress;

	public LocationProgressBar(DziuraActivity dziuraAct2)
	{
		this.dziuraAct = dziuraAct2;
	}

	public void run() 
	{
		progress = new ProgressDialog(dziuraAct);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("Trwa pobieranie pozycji GPS");
		progress.setIndeterminate(true);
		progress.show();
	}
	
	public void stop()
	{
		progress.dismiss();
	}

}
