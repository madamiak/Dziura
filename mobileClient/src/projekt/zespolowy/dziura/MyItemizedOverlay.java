package projekt.zespolowy.dziura;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends com.google.android.maps.ItemizedOverlay<OverlayItem>
{
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private DziuraActivity dziuraAct;

	public MyItemizedOverlay(Drawable defaultMarker, DziuraActivity dziuraAct)
	{
		super(boundCenterBottom(defaultMarker));
		this.dziuraAct = dziuraAct;
	}
	
	public void addOverlay(OverlayItem overlay)
	{
	    if(mOverlays.size()==0) //add only when there are no others
	    {
	    	mOverlays.add(overlay);
	    	populate();
	    }
	    else
	    {
	    	mOverlays.clear();
	    	mOverlays.add(overlay);
	    	populate();
	    }
	}

	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	protected boolean onTap(int index) //when tap on marker
	{
//		OverlayItem item = mOverlays.get(index);
//		GeoPoint point = item.getPoint();
		return true;
	}
	
	public boolean onTap(GeoPoint p, MapView mapView)
	{
		
		dziuraAct.vOption.point = p;
		List<Overlay> mapOverlays = dziuraAct.vOption.mapView.getOverlays();
		OverlayItem overlayitem = new OverlayItem(dziuraAct.vOption.point, null, null);
		dziuraAct.vOption.itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.remove(mapOverlays.size()-1);
		mapOverlays.add(dziuraAct.vOption.itemizedOverlay);
		dziuraAct.vOption.isMarkerAdded = true;
		
		Geocoder geoCoder = new Geocoder(dziuraAct.getBaseContext(), Locale.getDefault());
		try 
		{
			List<Address> addresses = geoCoder.getFromLocation(dziuraAct.vOption.point.getLatitudeE6()  / 1E6, 
					dziuraAct.vOption.point.getLongitudeE6() / 1E6, 1);
	
	     	String add = "";
	     	if (addresses.size() > 0) 
	     	{
		      	for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
		      	{
		      		add += addresses.get(0).getAddressLine(i) + "\n";
		      	}
	     	}
	     	add = add.substring(0, add.length()-1);
	     	dziuraAct.wyswietlTekst(add);
		}
		catch (IOException e)
		{                
			dziuraAct.wyswietlTekst(e.getMessage());
			e.printStackTrace();
		}
		catch (StringIndexOutOfBoundsException e)
		{
			dziuraAct.wyswietlTekst(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
