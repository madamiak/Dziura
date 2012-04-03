package projekt.zespolowy.dziura.GPS;

import projekt.zespolowy.dziura.DziuraActivity;
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
		if(dziuraAct.vOption.cGPS.isChecked()==true)
		{
			dziuraAct.vOption.tDamagePlace.setVisibility(View.GONE);
			dziuraAct.vCamera.lMap.setVisibility(View.GONE);
			if(dziuraAct.appMenu != null)
			{
				dziuraAct.appMenu.getMenu().setGroupVisible(1, false);
			}
		}
		else
		{
			boolean isInternetEnabled = dziuraAct.isInternetEnabled();
			if (!(isInternetEnabled)) 
			{  //jesli internet nie jest uruchomiony to pokazujemy okienko zeby wlaczyc internet
				dziuraAct.showWirelessOptions("Aby korzystaæ z mapy, nale¿y po³¹czyæ siê z Internetem. Czy chcesz siê po³¹czyæ?", "Uwaga");
//				Intent wifiOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);  
//				dziuraAct.startActivity(wifiOptionsIntent);
				dziuraAct.vOption.cGPS.setChecked(true);
			}
			else
			{
				dziuraAct.vOption.tDamagePlace.setVisibility(View.VISIBLE);
				dziuraAct.vCamera.lMap.setVisibility(View.VISIBLE);
				if(dziuraAct.appMenu != null)
				{
					dziuraAct.appMenu.getMenu().setGroupVisible(1, true);
				}
			}
		}
	}

}
