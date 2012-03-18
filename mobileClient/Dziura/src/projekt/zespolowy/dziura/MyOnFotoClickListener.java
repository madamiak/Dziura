package projekt.zespolowy.dziura;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyOnFotoClickListener implements OnClickListener 
{
	
	private DziuraActivity dziuraAct;
	

	public MyOnFotoClickListener(DziuraActivity dziuraActivity)
	{
		this.dziuraAct = dziuraActivity;
	}

	public void onClick(View arg0) 
	{
		if (dziuraAct.inPreview == true) 
        {
			boolean czy_mozna_nastepne = false;
			for(int i=0; i<dziuraAct.file_names_bool.length; i++)
			{
				if(dziuraAct.file_names_bool[i]==false)
				{
					czy_mozna_nastepne = true;
					break;
				}
			}
			if(czy_mozna_nastepne)
			{
				dziuraAct.camera.takePicture(null, null, photoCallback);
				dziuraAct.inPreview = false;
			}
			else
			{
				wyswietlTekst("Mo¿esz zrobiæ maksymalnie " + Integer.toString(dziuraAct.MAX_PHOTOS) + " zdjêcia, usuñ jedno z ju¿ wykonanych.");
			}
        }
	}
	
	Camera.PictureCallback photoCallback = new Camera.PictureCallback()
    {
    	public void onPictureTaken(byte[] data, Camera camera)
    	{
    		for(int i = 0; i<dziuraAct.file_names_bool.length; i++)
    		{
    			if(dziuraAct.file_names_bool[i]==false)
    			{
    				new SavePhotoTask(dziuraAct.file_names[i]).execute(data);
    				dziuraAct.file_names_bool[i] = true;
        			camera.startPreview();
        			dziuraAct.inPreview = true;
        			try
        			{
						Thread.sleep(500);
					}
        			catch (InterruptedException e)
        			{
        				wyswietlTekst(e.getMessage());
						e.printStackTrace();
					}
        			//TODO: jesli bedzie funkcja to odkomentaowac
        			dziuraAct.pokazZdjecia();
        			break;
    			}
    		}
    	}
    };

    //zrobienie zdjecia i zapisanie go na sdcard/dziura.jpg
    class SavePhotoTask extends AsyncTask<byte[], String, String>
    {
    	private String foto;
    	
    	public SavePhotoTask(String file_names)
    	{
			this.foto = file_names;
		}

		@Override
    	protected String doInBackground(byte[]... jpeg)
    	{
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				File photo = new File(Environment.getExternalStorageDirectory(), foto);
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
						wyswietlTekst(e.getMessage());
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
					wyswietlTekst(e.getMessage());
				}
			}
			else
			{
				dziuraAct.wyswietlTekst("Brak karty pamiêci");
				//TODO: zapisaæ plik w pamiêci wewnêtrznej
			}
			return(null);
    	}
    }

    private void wyswietlTekst(String tekst)
    {
    	Toast.makeText(dziuraAct.getApplicationContext(), tekst, Toast.LENGTH_LONG).show();
    }
}
