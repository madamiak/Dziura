package projekt.zespolowy.dziura.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import projekt.zespolowy.dziura.DziuraActivity;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Listener odpowiedzialny za nasluchiwanie, czy zostal nacisniety przycisk robienia zdjecia. Implementuje
 * interfejs {@link android.view.View.OnClickListener}.
 *
 */
public class MyOnFotoClickListener implements OnClickListener 
{
	
	private DziuraActivity dziuraAct;
	

	/**
	 * Konstruktor klasy {@link MyOnFotoClickListener}.
	 * @param dziuraActivity obiekt klasy {@link projekt.zespolowy.dziura.DziuraActivity}, dla ktorego Listener ma nasluchiwac
	 */
	public MyOnFotoClickListener(DziuraActivity dziuraActivity)
	{
		this.dziuraAct = dziuraActivity;
	}

	/**
	 * Funkcja wywolywana po nacisnieciu przycisku robienia zdjecia. Sprawdzona zostaje najpierw liczba zdjec juz wykonanych.
	 * Jezeli jest ich juz tyle, ile okresla stala <i>MAX_PHOTOS</i> w klasie {@link projekt.zespolowy.dziura.AppView.CameraView},
	 * to wyswietlony zostaje komunikat, ze nie mozna wykonac wiecej zdjec. W przeciwnym wypadku zdjecie zostaje wykonane.
	 * Zapis zdjecia nastepuje domyslnie na karte SD, jednak jezeli jest ona niedostepna, to zdjecie zostaje zapisane
	 * w pamieci telefonu. Po zapisaniu zdjecia, wyswietlona zostaje jego miniaturka.
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View arg0) 
	{
		if (dziuraAct.vCamera.inPreview == true) 
        {
			boolean czy_mozna_nastepne = false;
			for(int i=0; i<dziuraAct.vCamera.fileNamesBool.length; i++)
			{
				if(dziuraAct.vCamera.fileNamesBool[i]==false)
				{
					czy_mozna_nastepne = true;
					break;
				}
			}
			if(czy_mozna_nastepne)
			{
				//dziuraAct.vCamera.bTakePhoto.setEnabled(false);
				dziuraAct.vCamera.preview.setClickable(false);
				dziuraAct.vCamera.camera.takePicture(null, null, photoCallback);
				dziuraAct.vCamera.inPreview = false;
			}
			else
			{
				dziuraAct.wyswietlTekst("Mo¿esz zrobiæ maksymalnie " + Integer.toString(dziuraAct.vCamera.MAX_PHOTOS) + " zdjêcia, usuñ jedno z ju¿ wykonanych.");
			}
        }
	}
	
	Camera.PictureCallback photoCallback = new Camera.PictureCallback()
    {
    	public void onPictureTaken(byte[] data, Camera camera)
    	{
    		for(int i = 0; i<dziuraAct.vCamera.fileNamesBool.length; i++)
    		{
    			if(dziuraAct.vCamera.fileNamesBool[i]==false)
    			{
    				new SavePhotoTask(i, dziuraAct).execute(data);
    				dziuraAct.vCamera.fileNamesBool[i] = true;
        			camera.startPreview();
        			dziuraAct.vCamera.inPreview = true;
        			try
        			{
						Thread.sleep(500);
					}
        			catch (InterruptedException e)
        			{
						e.printStackTrace();
					}
//        			dziuraAct.vCamera.bTakePhoto.setEnabled(true);
        			dziuraAct.vCamera.preview.setClickable(true);
        			dziuraAct.vCamera.showPhotos();
        			break;
    			}
    		}
    	}
    };

    //zrobienie zdjecia i zapisanie go na sdcard/dziura.jpg
    class SavePhotoTask extends AsyncTask<byte[], String, String>
    {
    	private int number;
    	private DziuraActivity dziuraAct;
    	
    	public SavePhotoTask(int numberOfPhoto, DziuraActivity dziuraAct)
    	{
			this.number = numberOfPhoto;
			this.dziuraAct = dziuraAct;
		}

		@Override
    	protected String doInBackground(byte[]... jpeg)
    	{
			String state = Environment.getExternalStorageState();
			File photo;
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				photo = new File(Environment.getExternalStorageDirectory(), dziuraAct.vCamera.fileNames[number]);
				dziuraAct.vCamera.fileIsOnSd[number] = true;
			}
			else
			{
				//no sd-card available
				photo = new File(dziuraAct.getDir("photo", Context.MODE_PRIVATE), dziuraAct.vCamera.fileNames[number]);
				dziuraAct.vCamera.fileIsOnSd[number] = false;
			}
			if (photo.exists())
			{
				photo.delete();
			}
			else 
			{ //jezeli plik nie istnieje to go tworze
				try 
				{
					photo.createNewFile();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			try 
			{
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			} 
			catch (java.io.IOException e) 
			{
				e.printStackTrace();
			}
			return(null);
    	}
    }
}
