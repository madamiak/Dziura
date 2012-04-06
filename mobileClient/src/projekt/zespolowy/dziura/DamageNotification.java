package projekt.zespolowy.dziura;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
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
import android.util.Log;

public class DamageNotification 
{
	private DziuraActivity dziuraAct;
	private double latitude;
	private double longitude;
	private String description;
	private String damageType;
	private List<String> photosList;
	private String email;
	private final String serverURL = "http://87.99.29.139:3000/res/issue.json";
	
	public DamageNotification(DziuraActivity context)
	{
		this.dziuraAct = context;
		this.latitude = dziuraAct.vOption.point.getLatitudeE6()/1E6;
		this.longitude = dziuraAct.vOption.point.getLongitudeE6()/1E6;
		this.description = dziuraAct.vCamera.descriptionTxt.getText().toString();
		this.damageType = dziuraAct.vOption.sDamageType.getSelectedItem().toString();
		this.email = dziuraAct.vOption.eMail.getText().toString();
		this.photosList = new ArrayList<String>();
	}
	
	public int send()
	{		
		//photo to BASE64
		File photo;
		for(int i=0; i<dziuraAct.vCamera.MAX_PHOTOS; i++)
		{
			if(dziuraAct.vCamera.fileNamesBool[i]==true)
			{
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
						
						//add tag location and text to photo
						//but how?
						//photoBase64 += ("\\\\\\" + realTagX + "\\\\" + realTagY + "\\\\" + tmp.getText() + "\\\\\\"); //probably bad way to do it
					}
				}
				photosList.add(photoBase64);
			}
		}
		
		//preparing post
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000); 
		HttpClient httpclient = new DefaultHttpClient(params); 
	    HttpPost request = new HttpPost(serverURL); 
	    StringEntity s = null;
		try 
		{
			s = new StringEntity("{\"category_id\":\"1\", \"longitude\":\""+longitude+"\", \"latitude\":\""+latitude+"\"}");
		} 
		catch (UnsupportedEncodingException e1) 
		{
			e1.printStackTrace();
			Log.d("dziura", e1.getMessage());
		} 
	    request.setEntity(s);
	    request.setHeader("Accept", "application/json"); 
	    request.setHeader("Content-type", "application/json"); 
	    try 
	    {
	    	HttpResponse response;
	    	Log.d("dziura", "wysylam");
			response = httpclient.execute(request);
			if(response.getStatusLine().getStatusCode() == 500)
			{
				return -3;
			}
			System.out.println(response.getStatusLine().getStatusCode());
			//dziuraAct.wyswietlTekst(""+response.getStatusLine().getStatusCode());
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
					Log.d("dziura", e.getMessage());
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
						Log.d("dziura", e.getMessage());
					}
				}
				String resultString = sb.toString();
			    instream.close();
			    resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"
			    dziuraAct.wyswietlTekst(resultString);
			    // Transform the String into a JSONObject
			    JSONObject jsonObjRecv = new JSONObject(resultString);
			    // Raw DEBUG output of our received JSON object:
			    dziuraAct.wyswietlTekst(jsonObjRecv.toString());
			}
		}
	    catch (SocketTimeoutException e)
	    {
	    	e.printStackTrace();
			//Log.d("dziura", e.getMessage());
			return -1;
	    }
	    catch (ConnectTimeoutException e)
	    {
	    	e.printStackTrace();
			//Log.d("dziura", e.getMessage());
			return -1;
	    }
	    catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			//Log.d("dziura", e.getMessage());
			return -2;
		} 
	    catch (IOException e) 
		{
			e.printStackTrace();
			//Log.d("dziura", e.getMessage());
			return -2;
		} catch (JSONException e) 
		{
			e.printStackTrace();
			//Log.d("dziura", e.getMessage());
		}
		return 0;
	}
	
	public boolean saveXML()
	{
		//TODO: do zrobienia ;)
		// narazie na loga to dam
		/*Log.d("Dziura-xml", "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
		Log.d("Dziura-xml", "<GPS>");
		Log.d("Dziura-xml", "</GPS>");*/
		return true;
	}

}
