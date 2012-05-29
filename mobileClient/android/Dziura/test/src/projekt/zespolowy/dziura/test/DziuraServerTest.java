package projekt.zespolowy.dziura.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import projekt.zespolowy.dziura.DamageNotification;
import projekt.zespolowy.dziura.DziuraActivity;
import android.test.ActivityInstrumentationTestCase2;

public class DziuraServerTest extends ActivityInstrumentationTestCase2<DziuraActivity> 
{
	private DziuraActivity mActivity;
	private double lat, lon;
	private String desc;
	private int damType;
	private String mail;
	private String damName;
	
	public DziuraServerTest()
	{
		super("projekt.zespolowy.dziura", DziuraActivity.class);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		lat = 51.107769;
	    lon = 17.038658;
	    desc = "Opis opcjonalny";
	    damType = 4;
	    mail = "adres@email.pl";
	}

	protected void tearDown()
	{
		mActivity.appExit();
		mActivity = null;
	}
	
	public void testIntegration() throws JSONException
	{
		mActivity.vOption.point = new GeoPoint((int)(lat * 1e6), (int)(lon * 1e6));
		mActivity.vOption.descriptionTxt.setText(desc);
		mActivity.vOption.sDamageType.setSelection(damType);
		mActivity.vOption.damageTypeListener.setSelectedDamageType(damType);
		damName = mActivity.vOption.sDamageType.getSelectedItem().toString();
		mActivity.vOption.eMail.setText(mail);
		for(int i=0; i<mActivity.vCamera.MAX_PHOTOS; i++)
		{
			mActivity.vCamera.fileNamesBool[i] = false;
		}
		DamageNotification damage = new DamageNotification(mActivity);
		damage.send();
		int damageId = Integer.parseInt(damage.getId());
		String resultString = getDamageFromServer(damageId);
		JSONObject jsonResp = new JSONObject(resultString);
		String description = jsonResp.getString("desc");
		String email = jsonResp.getString("notificar_email");
		JSONObject issueJ = jsonResp.getJSONObject("issue");
		JSONObject categoryJ = issueJ.getJSONObject("category");
		String category = categoryJ.getString("name");
		String latitude = issueJ.getString("latitude");
		String longitude = issueJ.getString("longitude");
		assertEquals(desc, description);
		assertEquals(mail, email);
		assertEquals(lat, Double.parseDouble(latitude));
		assertEquals(lon, Double.parseDouble(longitude));
		assertEquals(damName, category);
	}

	private String getDamageFromServer(int id)
	{
		ArrayList<String> lista = new ArrayList<String>();
		//preparing post
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000); 
		HttpClient httpclient = new DefaultHttpClient(params); 
	    HttpGet request = new HttpGet("http://192.168.0.3:3000//res//issue_instances//"+id+".json");
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
			    return resultString;
//			    JSONArray responseJSONArray = new JSONArray(resultString);
//			    for(int i=0; i < responseJSONArray.length(); i++)
//			    {
//			    	JSONObject tmp = responseJSONArray.getJSONObject(i);
//			    	lista.add(tmp.getString("name"));
//			    }
			}
			if(response.getStatusLine().getStatusCode() == 200)
			{
				String[] typesList = new String[lista.size()];
				lista.toArray(typesList);
			}
		}
	    catch (SocketTimeoutException e)
	    {
	    	e.printStackTrace();
			return "error";
	    }
	    catch (ConnectTimeoutException e)
	    {
	    	e.printStackTrace();
			return "error";
	    }
	    catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			return "error";
		} 
	    catch (IOException e) 
		{
			e.printStackTrace();
			return "error";
		}
		return "end";
	}

}
