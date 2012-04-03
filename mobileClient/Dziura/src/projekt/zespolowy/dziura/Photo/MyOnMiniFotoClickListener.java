package projekt.zespolowy.dziura.Photo;

import java.io.File;

import projekt.zespolowy.dziura.DziuraActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

public class MyOnMiniFotoClickListener implements OnClickListener 
{
	private DziuraActivity dziuraAct;
	private int number;
	
	public MyOnMiniFotoClickListener(DziuraActivity dziuraActivity, int i) 
	{
		this.dziuraAct = dziuraActivity;
		this.number = i;
	}

	public void onClick(View arg0) 
	{
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
		int fotoHeight = bm.getHeight();
		int fotoWidth = bm.getWidth();
		int viewHeight = (int)(((float)fotoHeight / (float)fotoWidth) * (float)displayWidth);
		bm = Bitmap.createScaledBitmap(bm, displayWidth, viewHeight, true);
		dziuraAct.vCamera.imgBig.setImageBitmap(bm);
		dziuraAct.vCamera.duze_foto = number;
		dziuraAct.vCamera.showDeleteTagsButton();
	}

}
