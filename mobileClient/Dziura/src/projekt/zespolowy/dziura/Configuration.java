package projekt.zespolowy.dziura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import android.content.Context;

public class Configuration implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7625219107208688045L;
	private boolean useGPS;
	private boolean sendMail;
	private String eMail;
	private double lat;
	private double lon;
	private String cameraSettings;
	private boolean mapSatellite;
	private boolean showInitDialog;
	
	public Configuration()
	{
		useGPS = true;
		sendMail = false;
		eMail = "";
	    lat = 51.107769;
	    lon = 17.038658;
	    cameraSettings = null;
	    mapSatellite = false;
	    showInitDialog = true;
	}
	
	public boolean getUseGPS()
	{
		return useGPS;
	}
	
	public boolean getSendMail()
	{
		return sendMail;
	}
	
	public String getEMail()
	{
		return eMail;
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLon()
	{
		return lon;
	}
	
	public String getCameraSettings()
	{
		return cameraSettings;
	}
	
	public boolean getMapSatellite()
	{
		return mapSatellite;
	}
	
	public boolean getShowInitDialog()
	{
		return showInitDialog;
	}
	
	public void setUseGPS(boolean use)
	{
		useGPS = use;
	}
	
	public void setSendMail(boolean send)
	{
		sendMail = send;
	}
	
	public void setEMail(String mail)
	{
		eMail = mail;
	}
	
	public void setLat(double latitude)
	{
		lat = latitude;
	}
	
	public void setLon(double longitude)
	{
		lon = longitude;
	}
	
	public void setCameraSettings(String camSettings)
	{
		cameraSettings = camSettings;
	}
	
	public void setMapSatellite(boolean satellite)
	{
		mapSatellite = satellite;
	}
	
	public void setShowInitDialog(boolean show)
	{
		showInitDialog = show;
	}
	
	public Configuration load(String fileName, DziuraActivity dziuraAct)
	{
		Configuration conf = this;
		File configFile = new File(dziuraAct.getDir("config", Context.MODE_PRIVATE), fileName);
		if ( configFile.exists())
		{
			try 
			{
				FileInputStream fis = new FileInputStream(configFile.getPath());
				ObjectInputStream ois = new ObjectInputStream(fis);
				conf = (Configuration) ois.readObject();
				fis.close();
			} 
			catch (FileNotFoundException e) 
			{
				dziuraAct.wyswietlTekst("B³¹d podczas ³adowania konfiguracji: brak pliku");
			} 
			catch (StreamCorruptedException e) 
			{
				dziuraAct.wyswietlTekst("B³¹d podczas ³adowania konfiguracji: b³êdny stream");
			} 
			catch (IOException e) 
			{
				dziuraAct.wyswietlTekst("B³¹d podczas ³adowania konfiguracji: b³¹d I/O");
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				dziuraAct.wyswietlTekst("B³¹d podczas ³adowania konfiguracji: nie znaleziono klasy"); // TODO: text wyswietlajacy dac do stalych stringow (tak jak dla widokow)
			}
		}
		else
		{
			//
		}
		return conf;
	}
	
	public void save(String fileName, DziuraActivity dziuraAct)
	{
		// FIXME: "config" jako stala powinna byc
		File configFile = new File(dziuraAct.getDir("config", Context.MODE_PRIVATE), fileName);
		if(!configFile.exists())
		{
			try 
			{
				configFile.createNewFile();
			} 
			catch (IOException e) 
			{
				dziuraAct.wyswietlTekst("B³¹d podczas tworzenia pliku konfiguracji: b³¹d I/O");
			}
		}
		try 
		{
			FileOutputStream fos = new FileOutputStream(configFile.getPath());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(dziuraAct.appConfig);
			oos.close();
		} 
		catch (FileNotFoundException e) 
		{
			dziuraAct.wyswietlTekst("B³¹d podczas zapisywania konfiguracji: brak pliku");
		} 
		catch (IOException e) 
		{
			dziuraAct.wyswietlTekst("B³¹d podczas zapisywania konfiguracji: b³¹d I/O");
			e.printStackTrace();
		}
	}
}
