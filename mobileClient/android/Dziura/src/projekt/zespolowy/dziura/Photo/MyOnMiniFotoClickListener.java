package projekt.zespolowy.dziura.Photo;

import java.io.File;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.AppView.AfterMiniDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

/**
 * Listener sprawdzajacy czy nacisnieta zostala miniaturka zdjecia. Implementuje
 * interfejs {@link android.view.View.OnClickListener}.
 *
 */
public class MyOnMiniFotoClickListener implements OnClickListener 
{
	private DziuraActivity dziuraAct;
	private int number;
	
	/**
	 * Konstruktor klasy {@link MyOnMiniFotoClickListener}.
	 * @param dziuraActivity obiekt klasy {@link projekt.zespolowy.dziura.DziuraActivity}, dla ktorego Listener ma nasluchiwac
	 * @param i numer zdjecia ktorego dotyczy ten konkretny Listener
	 */
	public MyOnMiniFotoClickListener(DziuraActivity dziuraActivity, int i) 
	{
		this.dziuraAct = dziuraActivity;
		this.number = i;
	}

	/**
	 * Funkcja wywolywana, kiedy miniaturka zostala kliknieta. Chowa podglad z aparatu, jezeli jest widoczny, i wyswietla
	 * na {@link MyImageView} duze zdjecie. Jego rozmiar zostaje dopasowany do rzeczywistych proporcji zdjecia i
	 * do wielkosci ekranu.
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View arg0) 
	{
		if(dziuraAct.appConfig.getShowInitDialog()==true)
		{
			if(dziuraAct.miniDialogShowed == false)
			{
				AfterMiniDialog dialog = new AfterMiniDialog(dziuraAct);
				dialog.showDialog();
				dziuraAct.miniDialogShowed = true;
			}
		}
		if(dziuraAct.vCamera.lPhotos.getVisibility() == View.VISIBLE)
		{
			//
		}
		else
		{
			dziuraAct.vCamera.lCameraPreview.setVisibility(View.GONE);
			dziuraAct.vCamera.lPhotos.setVisibility(View.VISIBLE);
			dziuraAct.vCamera.camera.stopPreview();
			dziuraAct.vCamera.inPreview = false;
		}
		//get display size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		dziuraAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int displayWidth = displaymetrics.widthPixels;
        int displayHeight = displaymetrics.heightPixels;
        
        //show chosen photo on img_big
        File photo;
		if (dziuraAct.vCamera.fileIsOnSd[number]) 
		{
			photo = new File(Environment.getExternalStorageDirectory(), dziuraAct.vCamera.fileNames[number]);
		}
		else
		{
			//no sd-card available
			photo = new File(dziuraAct.getDir("photo", Context.MODE_PRIVATE), dziuraAct.vCamera.fileNames[number]);
		}
		Bitmap bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
		int imgW = bm.getWidth();
		int imgH = bm.getHeight();
		double imageRatio = (double)imgH / (double)imgW;
		
		//sprawdzam ktory rozmiar ekranu jest wiekszy i zmieniam odpowiednio zmienne, aby width byl dluzszy
		if(displayWidth < displayHeight)
		{
			int tmp = displayWidth;
			displayWidth = displayHeight;
			displayHeight = tmp;
		}
		
		int maxBigH = displayHeight - dziuraAct.tabWidget.getHeight();
		//jezeli wersja androida 3 (czyli tablet) to zawsze wyswietlany bedzie pasek, wiec odejmuje dodatkowo 50
		if(Build.VERSION.SDK_INT < 14 && Build.VERSION.SDK_INT >= 11)
		{
			maxBigH -= 50;
		}
		int maxBigW = displayWidth;
		int imgViewH = maxBigH;
		int imgViewW = 0;
		while(true)
		{
			imgViewW = (int)((double)imgViewH / (double)imageRatio);
			if(imgViewW <= maxBigW)
			{
				break;
			}
			else
			{
				imgViewH--;
			}
		}
		bm = Bitmap.createScaledBitmap(bm, imgViewW, imgViewH, true);
		LayoutParams bgParam = dziuraAct.vCamera.lBigPhotoBG.getLayoutParams();
		LayoutParams imgParam = dziuraAct.vCamera.imgBig.getLayoutParams();
		bgParam.width = imgViewW;
		bgParam.height = imgViewH;
		imgParam.width = imgViewW - 10;
		imgParam.height = imgViewH - 10;
		dziuraAct.vCamera.lBigPhotoBG.setLayoutParams(bgParam);
		dziuraAct.vCamera.imgBig.setLayoutParams(imgParam);
		dziuraAct.vCamera.imgBig.setImageBitmap(bm);
		dziuraAct.vCamera.duze_foto = number;
		dziuraAct.vCamera.showDeleteTagsButton();
	}

}
