package projekt.zespolowy.dziura.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import projekt.zespolowy.dziura.DziuraActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.EditText;

public class DziuraActivityTest extends ActivityInstrumentationTestCase2<DziuraActivity>
{
	private DziuraActivity mActivity;
	private EditText mMail;
	
	public DziuraActivityTest()
	{
		super("projekt.zespolowy.dziura", DziuraActivity.class);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		mMail = (EditText) mActivity.findViewById(projekt.zespolowy.dziura.R.id.editTextMail);
	}
	
	protected void tearDown()
	{
		mActivity.appExit();
		mActivity = null;
	}
	
	/**
	 * Sprawdzenie czy initDialog poprawnie sie wyswietla
	 */
	public void testShowInitDialog()
	{
		if(mActivity.appConfig.getShowInitDialog())
		{
			assertTrue(mActivity.dialog.isShowing());
			mActivity.dialog.cancel();
		}
		else
		{
			assertFalse(mActivity.dialog.isShowing());
		}
	}
	
	/**
	 * Sprawdzenie czy zrobione zdjecie jest na pewno zapisywane na karce/w pamieci wewnetrznej
	 */
	public void testMakePhoto() throws Throwable
	{
		int i=0;
		for(; i<mActivity.vCamera.MAX_PHOTOS; i++)
		{
			if(mActivity.vCamera.fileNamesBool[i] == false)
			{
				break;
			}
		}
		ClickThread klikacz = new ClickThread(mActivity.vCamera.bTakePhoto);
		runTestOnUiThread(klikacz);
		Thread.sleep(3000);
		File photo;
		if (mActivity.vCamera.fileIsOnSd[i]) 
		{
			photo = new File(Environment.getExternalStorageDirectory(), mActivity.vCamera.fileNames[i]);
		}
		else
		{
			//no sd-card available
			photo = new File(mActivity.getDir("photo", Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE), mActivity.vCamera.fileNames[i]);
		}
		assertTrue(photo.exists());
	}
	
	/**
	 * Sprawdzenie czy sprawdzacz emaili dziala poprawnie
	 */
	public void testMailChecker()
	{
		mMail.setText("poprawny@mail.com");
		assertEquals(Color.GREEN, mMail.getCurrentTextColor());
		mMail.setText("blednymail.com");
		assertEquals(Color.RED, mMail.getCurrentTextColor());
		mMail.setText("bledny@mailcom");
		assertEquals(Color.RED, mMail.getCurrentTextColor());
		mMail.setText("bledny@mailcom.");
		assertEquals(Color.RED, mMail.getCurrentTextColor());
	}
	
	/**
	 * Sprawdzenie, czy zdjêcia zostaj¹ usuniête po zamkniêciu aplikacji.
	 */
	public void testDeletingPhotos() throws IOException
	{
		File photo;
		for(int i=0; i<mActivity.vCamera.MAX_PHOTOS; i++) 
		{
			if (i % 2 == 0)
			{
				photo = new File(Environment.getExternalStorageDirectory(), mActivity.vCamera.fileNames[i]);
				mActivity.vCamera.fileIsOnSd[i] = true;
			}
			else
			{
				//no sd-card available
				photo = new File(mActivity.getDir("photo", Context.MODE_PRIVATE), mActivity.vCamera.fileNames[i]);
				mActivity.vCamera.fileIsOnSd[i] = false;
			}
			if (photo.exists())
			{
				photo.createNewFile();
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write("qwertyuiop".getBytes());
				fos.close();
			}
			photo = null;
		}
		mActivity.appExit();
		for(int i=0; i<mActivity.vCamera.MAX_PHOTOS; i++) 
		{
			if (mActivity.vCamera.fileIsOnSd[i])
			{
				photo = new File(Environment.getExternalStorageDirectory(), mActivity.vCamera.fileNames[i]);
			}
			else
			{
				//no sd-card available
				photo = new File(mActivity.getDir("photo", Context.MODE_PRIVATE), mActivity.vCamera.fileNames[i]);
			}
			assertFalse(photo.exists());
			photo = null;
		}
	}
	
	class ClickThread extends Thread
	{
		
		private ImageButton button;

		public ClickThread(ImageButton bTakePhoto)
		{
			button = bTakePhoto;
		}
		
		public void run()
		{
			button.performClick();
		}
		
	}
}
