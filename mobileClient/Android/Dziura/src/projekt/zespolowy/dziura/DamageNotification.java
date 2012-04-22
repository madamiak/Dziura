package projekt.zespolowy.dziura;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import projekt.zespolowy.dziura.Photo.Base64;
import projekt.zespolowy.dziura.Photo.PhotoTag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * Klasa, ktora obsluguje zgloszenia o szkodach. Umozliwia wyslanie zgloszenia oraz zapisanie go w pliku w formacie XML.
 * <br><br>Zawiera nastepujace informacje o zgloszeniu:
 * <ul>
 * <li>Wspolrzedne geograficzne (dlugosc i szerokosc) <i> - wymagane</i>
 * <li>S≥owny opis zg≥oszenia <i> - opcjonalny</i>
 * <li>Rodzaj szkody <i> - wymagane</i>
 * <li>Adres email zglaszajacego <i> - opcjonalne</i>
 * <li>ZdjÍcia (zapisane w formacie BASE64 przy pomocy {@link projekt.zespolowy.dziura.Photo.Base64}) <i> - opcjonalne</i>
 * <li>Tagi/markery dodane na poszczegÛlne zdjÍcia (po≥oøenie oraz opis kaødego z nich)<i> - opcjonalne</i>
 * <li>Adres serwera, na ktory zgloszenie zostanie wyslane<i> - wymagane</i>
 * </ul>
 * Po wyslaniu zgloszenia zapisane zostana nastepujace informacje:
 * <ul>
 * <li>Identyfikator zgloszenia, jaki zostal nadany przez serwer. Umozliwia sledzenie zmian stanu zgloszenia. W przypadku bledu identyfikator pusty.
 * <li>Wiadomosc, ktora aplikacja otrzymuje jako odpowiedz od serwera po wyslaniu zgloszenia. W przypadku bledu,
 * zapisana jest tu informacja o przyczynie bledu. W razie powodzenia, komunikat potwierdzajacy prawidlowe przyjecie zgloszenia.
 * </ul>
 *
 */
public class DamageNotification 
{
	
	class Tag
	{
		public int x, y;
		public String desc;
		
		public Tag(int x, int y, String desc)
		{
			this.x = x;
			this.y = y;
			this.desc = desc;
		}
	}
	
	class TagsList
	{
		public TagsList(ArrayList<Tag> lista)
		{
			this.list = lista;
		}
		public ArrayList<Tag> list;
	}
	
	private DziuraActivity dziuraAct;
	private double latitude;
	private double longitude;
	private String description;
	private int damageType;
	private String photosList[];
	private TagsList tagsList[];
	private String email;
	private final String serverURL = "http://dziura.zapto.org:3000";
	private final String serverURL2 = "//res/issue.json";
	private String message;
	private String id;
	private int photosNum = 0;
	private int timeout = 40000;
	
	/**
	 * Konstruktor klasy {@link DamageNotification}. Pobiera z kontrolek aplikacji odpowiednie dane i zapisuje je jako
	 * swoje zmienne lokalne.
	 * @param context instancja glownej aktywnosci aplikacji {@link DziuraActivity}
	 */
	public DamageNotification(DziuraActivity context)
	{
		this.dziuraAct = context;
		this.latitude = dziuraAct.vOption.point.getLatitudeE6()/1E6;
		this.longitude = dziuraAct.vOption.point.getLongitudeE6()/1E6;
		this.description = dziuraAct.vOption.descriptionTxt.getText().toString();
		this.damageType = dziuraAct.vOption.damageTypeListener.getSelectedDamageType() + 1;
		this.email = dziuraAct.vOption.eMail.getText().toString();
		this.photosList = new String[dziuraAct.vCamera.MAX_PHOTOS];
		this.tagsList = new TagsList[dziuraAct.vCamera.MAX_PHOTOS];
	}
	
	/**
	 * Funkcja wysylajaca zgloszenie w formacie JSON (
	 * <a href="https://github.com/mbilski/Dziura/blob/master/docs/rest.txt">specyfikacja interfejsu REST serwera</a>)
	 * i obslugujaca zwrocona przez serwer odpowiedz.
	 * <br><br>W wysylanym zgloszeniu, wszelkie polskie znaki diakrytyczne (w opisie zgloszenia badz opisach tagow) zostaja
	 * 'pozbawione ogonkow', tzn. <i>π</i> zamieniane jest na <i>a</i>, itd.
	 * @return <li>0 - jezeli zgloszenie zostalo prawidlowo wyslane
	 * <li>-1 - w przypadku przekroczenia czasu wysylania (domyslnie 40 sekund)
	 * <li>-2 - w przypadku bledu serwera (przyczyna bledu dostepna przez {@link #getMessage()})
	 * <li>-3 - w przypadku innych bledow
	 */
	public int send()
	{		
		convertPhotosToBase();
		//preparing post
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout); 
		HttpClient httpclient = new DefaultHttpClient(params); 
	    HttpPost request = new HttpPost(serverURL+serverURL2); 
	    StringEntity s = null;
		try 
		{
			String json = createJSON();
			s = new StringEntity(json);
		} 
		catch (UnsupportedEncodingException e1) 
		{
			e1.printStackTrace();
		} 
	    request.setEntity(s);
	    request.setHeader("Accept", "application/json"); 
	    request.setHeader("Content-type", "application/json"); 
	    try 
	    {
	    	HttpResponse response;
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity!=null)
			{
				//read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
				{
					instream = new GZIPInputStream(instream);
				}
				// convert content stream to a String
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				try 
				{
					while ((line = reader.readLine()) != null) 
					{
						sb.append(line + "\n");
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				finally 
				{
					try 
					{
						instream.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				String resultString = sb.toString();
			    instream.close();
			    resultString = resultString.substring(0,resultString.length()-1);
			    JSONObject responseJSON = new JSONObject(resultString);
			    this.id = responseJSON.get("id").toString();
			    this.message = responseJSON.getString("message");
			}
			if(response.getStatusLine().getStatusCode() != 200)
			{
				return -3;
			}
		}
	    catch (SocketTimeoutException e)
	    {
	    	e.printStackTrace();
			return -1;
	    }
	    catch (ConnectTimeoutException e)
	    {
	    	e.printStackTrace();
			return -1;
	    }
	    catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			return -2;
		} 
	    catch (IOException e) 
		{
			e.printStackTrace();
			return -2;
		} 
	    catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	private void convertPhotosToBase()
	{
		//photo to BASE64
		File photo;
		for(int i=0; i<dziuraAct.vCamera.MAX_PHOTOS; i++)
		{
			if(dziuraAct.vCamera.fileNamesBool[i]==true)
			{
				photosNum++;
				ArrayList<Tag> tagTmpList = new ArrayList<Tag>();
				if (dziuraAct.vCamera.fileIsOnSd[i]) 
				{
					photo = new File(Environment.getExternalStorageDirectory(), dziuraAct.vCamera.fileNames[i]);
				}
				else
				{
					//no sd-card available
					photo = new File(dziuraAct.getDir("photo", Context.MODE_PRIVATE), dziuraAct.vCamera.fileNames[i]);
				}
				Bitmap bm = null;
				while (bm == null)
				{
					bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
				}
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, dziuraAct.vCamera.photoQuality, bao);
				byte [] ba = bao.toByteArray();
				String photoBase64 = Base64.encodeBytes(ba);
				
				PhotoTag tmp;
				for(int j = 0; j<dziuraAct.vCamera.tagList.size(); j++)
				{
					tmp = dziuraAct.vCamera.tagList.get(j);
					if(tmp.getPhotoNo() == i)
					{
						//compute tag position
						int fotoHeight = bm.getHeight();
						int fotoWidth = bm.getWidth();
						int imgViewH = tmp.getImgViewHeight();
						int imgViewW = tmp.getImgViewWidth();
						int realTagX = (int)((tmp.getX() / imgViewW) * fotoWidth);
						int realTagY = (int)((tmp.getY() / imgViewH) * fotoHeight);
						
						tagTmpList.add(new Tag(realTagX, realTagY, tmp.getText()));
					}
				}
				if(tagTmpList != null)
				{
					tagsList[i] = new TagsList(tagTmpList);
				}
				photosList[i] = photoBase64;
				//photosList.add(photoBase64);
			}
		}
	}

	private String createJSON()
	{
		String json = "{ \"category_id\": \""+damageType+"\", \"latitude\": \""+latitude+"\", \"longitude\": \""+longitude+"\"";
		if(description != null && description.length() > 0)
		{
			description = deDiacritic(description);
			json += ", \"desc\": \""+description+"\"";
		}
		if(email != null && email.length() > 0)
		{
			json += ", \"notificar_email\": \""+email+"\"";
		}
		if(photosList != null && photosNum > 0)
		{
			json += ", \"photos\": [";
			for(int i=0; i<dziuraAct.vCamera.MAX_PHOTOS; i++)
			{
				if (photosList[i] != null) 
				{
					json += "{ \"image\": \"" +photosList[i]+ "\", \"image_type\": \"image/jpeg\"";

					json += ", \"markers\": [ ";
					if (tagsList[i].list != null && tagsList[i].list.size() > 0)
					{
						for (int j = 0; j < tagsList[i].list.size(); j++)
						{
							json += "{ \"x\": \""
									+ tagsList[i].list.get(j).x
									+ "\", \"y\": \""
									+ tagsList[i].list.get(j).y
									+ "\", \"desc\": \""
									+ tagsList[i].list.get(j).desc + "\" }";
							if (j < tagsList[i].list.size() - 1)
							{
								json += ", ";
							}
						}
					}
					json += " ] ";
					
					json += " }";
					photosNum--;
					if (photosNum > 0)
					{
						json += ", ";
					}
				}
			}
			json += " ] ";
		}
		json += " }";
		return json;
	}

	private String deDiacritic(String description2)
	{
		String tmp = new String(description2);
		while(true)
		{
			if(tmp.contains("π"))
			{
				int idx = tmp.indexOf("π");
				tmp = tmp.substring(0, idx)+"a"+tmp.substring(idx+1);
			}
			else if(tmp.contains("•"))
			{
				int idx = tmp.indexOf("•");
				tmp = tmp.substring(0, idx)+"A"+tmp.substring(idx+1);
			}
			else if(tmp.contains("Ê"))
			{
				int idx = tmp.indexOf("Ê");
				tmp = tmp.substring(0, idx)+"c"+tmp.substring(idx+1);;
			}
			else if(tmp.contains("∆"))
			{
				int idx = tmp.indexOf("∆");
				tmp = tmp.substring(0, idx)+"C"+tmp.substring(idx+1);
			}
			else if(tmp.contains("Í"))
			{
				int idx = tmp.indexOf("Í");
				tmp = tmp.substring(0, idx)+"e"+tmp.substring(idx+1);
			}
			else if(tmp.contains(" "))
			{
				int idx = tmp.indexOf(" ");
				tmp = tmp.substring(0, idx)+"E"+tmp.substring(idx+1);
			}
			else if(tmp.contains("≥"))
			{
				int idx = tmp.indexOf("≥");
				tmp = tmp.substring(0, idx)+"l"+tmp.substring(idx+1);
			}
			else if(tmp.contains("£"))
			{
				int idx = tmp.indexOf("£");
				tmp = tmp.substring(0, idx)+"L"+tmp.substring(idx+1);
			}
			else if(tmp.contains("Û"))
			{
				int idx = tmp.indexOf("Û");
				tmp = tmp.substring(0, idx)+"o"+tmp.substring(idx+1);
			}
			else if(tmp.contains("”"))
			{
				int idx = tmp.indexOf("”");
				tmp = tmp.substring(0, idx)+"O"+tmp.substring(idx+1);
			}
			else if(tmp.contains("ú"))
			{
				int idx = tmp.indexOf("ú");
				tmp = tmp.substring(0, idx)+"s"+tmp.substring(idx+1);
			}
			else if(tmp.contains("å"))
			{
				int idx = tmp.indexOf("å");
				tmp = tmp.substring(0, idx)+"S"+tmp.substring(idx+1);
			}
			else if(tmp.contains("ü"))
			{
				int idx = tmp.indexOf("ü");
				tmp = tmp.substring(0, idx)+"z"+tmp.substring(idx+1);
			}
			else if(tmp.contains("è"))
			{
				int idx = tmp.indexOf("è");
				tmp = tmp.substring(0, idx)+"Z"+tmp.substring(idx+1);
			}
			else if(tmp.contains("ø"))
			{
				int idx = tmp.indexOf("ø");
				tmp = tmp.substring(0, idx)+"z"+tmp.substring(idx+1);
			}
			else if(tmp.contains("Ø"))
			{
				int idx = tmp.indexOf("Ø");
				tmp = tmp.substring(0, idx)+"Z"+tmp.substring(idx+1);
			}
			else
			{
				break;
			}
		}
		return tmp;
	}

	/**
	 * @return <li>wiadomosc zwrocona przez serwer po wyslaniu zgloszenia
	 */
	public String getMessage()
	{
		return this.message;
	}
	
	/**
	 * @return <li>identyfikator zgloszenia zwrocony przez serwer po pomyslnym wyslaniu zgloszenia
	 */
	public String getId()
	{
		return this.id;
	}
	
	/**
	 * @return <li>adres serwera, na ktory wysylane zostaja zgloszenia
	 */
	public String getURL()
	{
		return this.serverURL;
	}
	
	/**
	 * Funkcja zapisujaca utworzone zgloszenie w pliku XML w pamieci telefonu.
	 * @return <li> true - jezeli plik zostal prawidlowo zapisany <li> false - w przeciwnym wypadku
	 */
	public boolean saveXML()
	{
		//TODO: do zrobienia ;)
		
		String notification = new String();
		notification += "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>";
		notification += "<GPS>";
		notification += "<LATITUDE>" +latitude + "</LATITUDE>";
		notification += "<LONGITUDE>"+longitude+ "</LONGITUDE>";
		notification += "</GPS>";
		notification += "<MAIL>" + email + "</MAIL>";
		notification += "<DAMAGE_TYPE>"+damageType+"</DAMAGE_TYPE>";
		notification += "<DESCRIPTION>"+description+"</DESCRIPTION>";
		notification += "<PHOTOS>";
		notification += "<PHOTO>";
		notification += "<TAG>";
		notification += "<POS_X>" + "<?POS_X>";
		notification += "<POS_Y>" + "<?POS_Y>";
		notification += "<DESCRIPTION>"+"</DESCRIPTION>";
		notification += "</TAG>";
		notification += "</PHOTO>";
		notification += "</PHOTOS>"; 
											
		File configFile = new File(dziuraAct.getDir("xml", Context.MODE_PRIVATE),
				"notification.xml");
		if (!configFile.exists())
		{
			try 
			{
				configFile.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return false;
			}
		}
		try 
		{
			FileOutputStream fos = new FileOutputStream(configFile.getPath());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(notification);
			oos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
