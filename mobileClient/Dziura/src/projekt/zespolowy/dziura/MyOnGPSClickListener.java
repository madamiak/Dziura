package projekt.zespolowy.dziura;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class MyOnGPSClickListener implements OnClickListener 
{
	private DziuraActivity dziuraAct;

	public MyOnGPSClickListener(DziuraActivity dziuraAct)
	{
		this.dziuraAct = dziuraAct;
	}

	public void onClick(View v) 
	{
		if(dziuraAct.cGPS.isChecked()==true)
		{
			dziuraAct.tDamagePlace.setVisibility(View.GONE);
			dziuraAct.lMap.setVisibility(View.GONE);
		}
		else
		{
			boolean isInternetEnabled = dziuraAct.isInternetEnabled();
			if (!(isInternetEnabled)) 
			{  //jesli internet nie jest uruchomiony to pokazujemy okienko zeby wlaczyc internet
				dziuraAct.wyswietlTekst("Po³¹cz siê z Internetem aby korzystaæ z mapy");
				try 
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					dziuraAct.wyswietlTekst(e.getMessage());
				}
				Intent wifiOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);  
				dziuraAct.startActivity(wifiOptionsIntent);
				dziuraAct.cGPS.setChecked(true);
			}
			else
			{
				dziuraAct.tDamagePlace.setVisibility(View.VISIBLE);
				dziuraAct.lMap.setVisibility(View.VISIBLE);
			}
		}
	}

}
