package projekt.zespolowy.dziura;

import java.io.File;

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
		if(dziuraAct.lPhotos.getVisibility() == View.VISIBLE)
		{
			//
		}
		else
		{
			dziuraAct.lCameraPreview.setVisibility(View.GONE);
			dziuraAct.lPhotos.setVisibility(View.VISIBLE);
			dziuraAct.camera.stopPreview();
			dziuraAct.inPreview = false;
		}
		//get display size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		dziuraAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int displayWidth = displaymetrics.widthPixels;
        
        //show chosen photo on img_big
		File photo = new File(Environment.getExternalStorageDirectory(), dziuraAct.file_names[number]);
		Bitmap bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
		int fotoHeight = bm.getHeight();
		int fotoWidth = bm.getWidth();
		int viewHeight = (int)((fotoHeight / fotoWidth) * displayWidth);
		bm = Bitmap.createScaledBitmap(bm, displayWidth, viewHeight, true);
		dziuraAct.imgBig.setImageBitmap(bm);
		dziuraAct.duze_foto = number;
		dziuraAct.showDeleteTagsButton();
	}

}
