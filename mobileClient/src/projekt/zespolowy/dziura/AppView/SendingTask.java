package projekt.zespolowy.dziura.AppView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import projekt.zespolowy.dziura.DamageNotification;
import projekt.zespolowy.dziura.DziuraActivity;

public class SendingTask extends AsyncTask < DziuraActivity, String, Integer >
{

	private DziuraActivity dziuraAct;
	private DamageNotification zgloszenie;
	private AlertDialog dialog;
	
	public SendingTask(DziuraActivity dziuraAct)
	{
		this.dziuraAct = dziuraAct;
	}
	
	protected void onPreExecute()
	{
		dialog = new AlertDialog.Builder(dziuraAct).create();
		dialog.setTitle("Zg�oszenie");
		dialog.setCanceledOnTouchOutside(false);
		dziuraAct.wyswietlTekst((dziuraAct.vOption.point.getLatitudeE6() / 1E6)+"/"+(dziuraAct.vOption.point.getLongitudeE6() / 1E6));
		zgloszenie = new DamageNotification(dziuraAct);
	}

	@Override
	protected Integer doInBackground(DziuraActivity... params)
	{
		return zgloszenie.send();
	}
	
	protected void onPostExecute(Integer result)
	{
		if(result==0)
		{
			dialog.setMessage("Dzi�kujemy, zg�oszenie zosta�o pomy�lnie wys�ane");
			dialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
					dziuraAct.appExit();
				}
			});
		}
		else if(result==-1)
		{
			dialog.setMessage("Wysy�anie trwa zbyt d�ugo, spr�buj ponownie za chwil�");
			dialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
		}
		else if(result==-3)
		{
			dialog.setMessage("Brak jednostki obs�uguj�cej teren zg�oszonej szkody");
			dialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
		}
		else
		{
			dialog.setMessage("B��d podczas wysy�ania...");
			dialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
		}
		dziuraAct.vOption.mZglosListener.sendProg.stop();
		dialog.show();
	}

}
