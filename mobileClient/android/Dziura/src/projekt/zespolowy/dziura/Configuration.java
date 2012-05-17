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

/**
 * Klasa zawieraj¹ca ustawienia aplikacji oraz umo¿liwiaj¹ca ich zapisywanie 
 * oraz odczytywanie z pliku znajduj¹cego siê w pamiêci telefonu. Klasa ta
 * implementuje interfejs Serializable, co umo¿liwia wykorzystanie ObjectInputStream
 *  i ObjectOutputStream do zapisania/odczytania obiektu klasy Configuration jako ca³oœci.
 * <br><br>Zapisywane s¹ nastêpuj¹ce informacje:
 * <ul>
 * <li>Czy u¿ytkownik korzysta³ z odbiornika GPS czy wskazywa³ lokalizacjê na mapie.
 * <li>Czy u¿ytkownik chcia³ otrzymaæ informacje o zg³oszeniu na email.
 * <li>Wpisany email.
 * <li>Lokalizacjê ostatniego zg³oszenia.
 * <li>Ustawienia aparatu.
 * <li>Czy mapa ustawiona by³a w tryb zwyk³ej mapy czy zdjêæ satelitarnych.
 * <li>Czy u¿ytkownik chce, aby ekran pocz¹tkowy wyœwietla³ siê, czy nie.
 * <li>Aktualna lista rodzajów szkód pobrana z serwera.
 * <li>Sposób wysy³ania zg³oszenia (czy wysy³ane ma byæ na serwer czy na adres email).
 * </ul> 
 *  
 * 
 * @see java.io.Serializable
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutputStream
 *
 */
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
	private String[] damageTypes;
	private boolean sendUsingServer;
	
	/**
	 * Konstruktor domyœlny klasy {@link Configuration}. Inicjalizuje obiekt z nastêpuj¹cymi ustawieniami pocz¹tkowymi:
	 * <ul>
	 * <li>Wykorzystywanie GPSa,brak mapy
	 * <li>Nie otrzymywanie powiadomieñ na email
	 * <li>Adres email pusty
	 * <li>Wspó³rzêdne geograficzne - Wroc³aw
	 * <li>Brak ustawieñ aparatu
	 * <li>Korzystanie z trybu zwyk³ej mapy, widok z satelity wy³¹czony
	 * <li>Wyœwietlanie ekranu pocz¹tkowego
	 * <li>Domyœlna lista rodzajów szkód
	 * <li>Wysy³anie zg³oszenia na serwer
	 * </ul>
	 * 
	 */
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
	    damageTypes = new String[]{"Dziury w jezdni", "Dziury w chodniku", "Uszkodzony znak drogowy", "Graffiti",
	    			"Zalegaj¹cy œnieg", "Oblodzenie", "Zalana ulica", "Dewastacja", "Zaœmiecenie", "Zwierzêta"};
	    sendUsingServer = true;
	}
	
	/**
	 * @return <li>true - je¿eli GPS ma byæ u¿ywany<li>flase - je¿eli u¿ywana ma byæ mapa
	 */
	public boolean getUseGPS()
	{
		return useGPS;
	}
	
	/**
	 * @return <li>true - je¿eli powiadomienia o zg³oszeniu na email maj¹ byæ wysy³ane<li>false - je¿eli maj¹ nie byæ wysy³ane
	 */
	public boolean getSendMail()
	{
		return sendMail;
	}
	
	/**
	 * @return <li>zapisany adres email
	 */
	public String getEMail()
	{
		return eMail;
	}
	
	/**
	 * @return <li>szerokoœæ geograficzna
	 */
	public double getLat()
	{
		return lat;
	}
	
	/**
	 * @return <li>d³ugoœæ geograficzna
	 */
	public double getLon()
	{
		return lon;
	}
	
	/**
	 * @return <li>ustawienia kamery ( uzyskane przez 
	 * <a href="http://developer.android.com/reference/android/hardware/Camera.Parameters.html#flatten()">
	 * android.hardware.Camera.Parameters.flatten()
	 * </a>, mozna wczytac przez 
	 * <a href="http://developer.android.com/reference/android/hardware/Camera.Parameters.html#unflatten(java.lang.String)">
	 * android.hardware.Camera.Parameters.unflatten(String flattened)</a> )
	 */
	public String getCameraSettings()
	{
		return cameraSettings;
	}
	
	/**
	 * @return <li>true - jezeli uzywany jest tryb zdjec satelitarnych <li>false - w przeciwynym wypadku
	 */
	public boolean getMapSatellite()
	{
		return mapSatellite;
	}
	
	/**
	 * @return <li>true - jezeli ekran poczatkowy ma byc wyswietlany<li>else - w przeciwnym wypadku
	 */
	public boolean getShowInitDialog()
	{
		return showInitDialog;
	}
	
	/**
	 * @return <li> tablica zawierajaca rodzaje szkod
	 */
	public String[] getDamageTypes()
	{
		return damageTypes;
	}
	
	/**
	 * @return <li> true - jezeli zgloszenie ma byc wysylane na serwer
	 * <li> false - jezelie zgloszenie zostanie wyslane przez email
	 */
	public boolean getSendUsingServer()
	{
		return sendUsingServer;
	}
	
	/**
	 * @param server true - jezeli zgloszenie wysylane ma byc na serwer, false - jezeli na email
	 */
	public void setSendUsingServer(boolean server)
	{
		sendUsingServer = server;
	}
	
	/**
	 * @param use true - jezeli uzywany ma byc odbiornik GPS, false - jezeli wykorzystana zostanie mapa
	 */
	public void setUseGPS(boolean use)
	{
		useGPS = use;
	}
	
	/**
	 * @param send true - jezeli ma byc wysylane powiadomienie na email, false - w przeciwnym wypadku
	 */
	public void setSendMail(boolean send)
	{
		sendMail = send;
	}
	
	/**
	 * @param mail adres email, na ktory wysylane maja byc powiadomienia o zmianie stanu zgloszenia
	 */
	public void setEMail(String mail)
	{
		eMail = mail;
	}
	
	/**
	 * @param latitude szerokosc geograficzna
	 */
	public void setLat(double latitude)
	{
		lat = latitude;
	}
	
	/**
	 * @param longitude dlugosc geograficzna
	 */
	public void setLon(double longitude)
	{
		lon = longitude;
	}
	
	/**
	 * @param camSettings ustawienia aparatu ( uzyskane przez 
	 * <a href="http://developer.android.com/reference/android/hardware/Camera.Parameters.html#flatten()">
	 * android.hardware.Camera.Parameters.flatten()
	 * </a>, mozna wczytac przez 
	 * <a href="http://developer.android.com/reference/android/hardware/Camera.Parameters.html#unflatten(java.lang.String)">
	 * android.hardware.Camera.Parameters.unflatten(String flattened)</a> )
	 */
	public void setCameraSettings(String camSettings)
	{
		cameraSettings = camSettings;
	}
	
	/**
	 * @param satellite true - jezeli uzywany ma byc tryb zdjec satelitarnych, false - jezeli uzywana ma byc zwykla mapa
	 */
	public void setMapSatellite(boolean satellite)
	{
		mapSatellite = satellite;
	}
	
	/**
	 * @param show - true jezeli ekran pcozatkowy ma byc wyswietlany, false - w przeciwnym wypadku
	 */
	public void setShowInitDialog(boolean show)
	{
		showInitDialog = show;
	}
	
	/**
	 * @param typesArray tablica rodzajow szkod
	 */
	public void setDamageTypes(String[] typesArray)
	{
		damageTypes = typesArray;
	}
	
	/**
	 * Funkcja ta wczytuje ustawienia aplikacji z pliku konfiguracyjnego, ktorego nazwa zostala podana jako parametr.
	 * @param fileName nazwa pliku, z ktorego wczytane maja zostac ustawienia
	 * @param dziuraAct glowna aktywnosc aplikacji {@link DziuraActivity}
	 * @return obiekt klasy {@link Configuration}, zawierajacy wczytane ustawienia
	 */
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
				e.printStackTrace();
			}
		}
		else
		{
			//
		}
		return conf;
	}
	
	/**
	 * Funkcja zapisuje konfiguracje aplikacji przechowywana w instancji klasy {@link Configuration}, dla ktorej zostala wywolana, w pliku konfiguracyjnym, ktorego nazwe nalezy podac jako parametr funkcji.
	 * @param fileName nazwa pliku, do ktorego maja zostac zapisane ustawienia
	 * @param dziuraAct glowna aktywnosc aplikacji {@link DziuraActivity}
	 */
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
