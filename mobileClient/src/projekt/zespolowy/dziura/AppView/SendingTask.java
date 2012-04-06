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
		dialog.setTitle("Zg³oszenie");
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
			dialog.setMessage("Dziêkujemy, zg³oszenie zosta³o pomyœlnie wys³ane");
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
			dialog.setMessage("Wysy³anie trwa zbyt d³ugo, spróbuj ponownie za chwilê");
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
			dialog.setMessage("Brak jednostki obs³uguj¹cej teren zg³oszonej szkody");
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
			dialog.setMessage("B³¹d podczas wysy³ania...");
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
