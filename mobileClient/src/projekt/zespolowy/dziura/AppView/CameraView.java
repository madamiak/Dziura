package projekt.zespolowy.dziura.AppView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.GestureListener;
import projekt.zespolowy.dziura.R;
import projekt.zespolowy.dziura.Photo.MyImageView;
import projekt.zespolowy.dziura.Photo.MyOnFotoClickListener;
import projekt.zespolowy.dziura.Photo.MyOnMiniFotoClickListener;
import projekt.zespolowy.dziura.Photo.PhotoTag;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.text.Editable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CameraView {

	// Camera view stuff.
	public SurfaceView preview 		= null;
	public SurfaceHolder previewHolder = null;
	public Camera camera 				= null;
	public boolean inPreview 			= false;
	// Maximum number of photos.
	public final int MAX_PHOTOS = 3;
	
	//layouty - zeby wyswietlic/schowac kontrolki to nie pojedynczo tylko caly layout, ktory je zawiera
	public LinearLayout lCameraPreview;
	public LinearLayout lPhotos;
	public LinearLayout lMinPhotos;
	public LinearLayout lTagDesc;
	public LinearLayout lMap;
	public LinearLayout lCameraOnly;

	// TODO: nakladka na kamere (klasa), ktora bedzie obslugiwac te pierdoly z plikami zdjec?
	// Keeps file names which contain photos.
	public final String[] fileNames = new String[MAX_PHOTOS]; 
	public Boolean[] fileNamesBool = new Boolean[MAX_PHOTOS]; //tablica przechowujaca boole, czy plik jest uzywany czy nie
	public Boolean[] fileIsOnSd = new Boolean[MAX_PHOTOS]; //if photo saved on sd-card - true, else false
	public int photoQuality = 70;
	
	//number of photo on img_big
	public int duze_foto = -1;

	//coordinates of point where tag will be added
	public float img_x = 0;
	public float img_y = 0;

	// list containing tags added to photos
	public List<PhotoTag> tagList;
	
	public ImageButton bTakePhoto;
	public Button bDeletePhoto;
	public Button bTakeNextPhoto;
	public Button bUsunTagi;
	public ImageView[] imgMinis;
	public MyImageView imgBig;
	public EditText tagTxt;
	public Button bSave;
	public Button bCancel;
	public EditText descriptionTxt;
	
	public View mCameraView;

	private DziuraActivity app;
	
	public CameraView(final DziuraActivity app) {
		this.app = app;

		mCameraView = app.findViewById(R.id.cameraView);
		

		setLayouts();
		
		//app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		app.isCameraViewSet = true;
		// gestures
		initGestureCamera();
		

		//ustawienie nazw plikow
		Random rand = new Random();
		for(int i = 0; i<MAX_PHOTOS; i++) 
		{
			// TODO: check if file exist.
			Float f = rand.nextFloat();
			fileNames[i] = "dziura_"+f.hashCode()+Integer.toString(i)+".jpg";
			fileNamesBool[i] = false;
			fileIsOnSd[i] = true;
		}

		tagList = new ArrayList<PhotoTag>();

		tagTxt = (EditText) app.findViewById(R.id.editText7);
		bSave = (Button) app.findViewById(R.id.buttonSave);
		bCancel = (Button) app.findViewById(R.id.buttonCancel);
		bSave.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) 
			{
				Editable txt = tagTxt.getText();
				if(txt.length() != 0)
				{
					String str_txt = txt.toString();
					int imgViewWidth = imgBig.getWidth();
					int imgViewHeight = imgBig.getHeight();
					PhotoTag tag = new PhotoTag(img_x, img_y, str_txt, duze_foto, imgViewHeight, imgViewWidth);
					tagList.add(tag);
					tagTxt.setText("");
					lTagDesc.setVisibility(View.GONE);
					imgBig.setClickable(true);
					showDeleteTagsButton();
				}
				else
				{
					app.wyswietlTekst("Podpisz zaznaczony fragment");
					app.vibrate(300);
					tagTxt.requestFocus();
				}
			}
		});

		bCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) 
			{
				tagTxt.setText("");
				lTagDesc.setVisibility(View.GONE);
				imgBig.setClickable(true);
				showDeleteTagsButton();
			}
		});

		imgBig = (MyImageView) app.findViewById(R.id.imageViewCamera);
		imgBig.setDziuraActivity(app);

		imgMinis = new ImageView[MAX_PHOTOS];
		imgMinis[0] = (ImageView) app.findViewById(R.id.imageViewPhoto1);
		imgMinis[1] = (ImageView) app.findViewById(R.id.imageViewPhoto2);
		imgMinis[2] = (ImageView) app.findViewById(R.id.imageViewPhoto3);
		for(int i = 0; i<MAX_PHOTOS; i++)
		{
			imgMinis[i].setPadding(5, 0, 5, 0);
			imgMinis[i].setOnClickListener(new MyOnMiniFotoClickListener(app, i));
		}

		//wlaczenie aparatu
		preview = (SurfaceView) app.findViewById(R.id.surfaceViewCamera);
		preview.setOnClickListener(new MyOnFotoClickListener(app));
		previewHolder = preview.getHolder();
		previewHolder.addCallback(app.surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		if(camera == null)
		{
			camera = Camera.open();
		}

		//zrobienie zdjêcia
		bTakePhoto = (ImageButton) app.findViewById(R.id.imageButton1);
		bTakePhoto.setOnClickListener(new MyOnFotoClickListener(app));

		//usuniecie zdjecia
		bDeletePhoto = (Button) app.findViewById(R.id.buttonDeletePhoto);
		bDeletePhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(duze_foto != -1) {
					File photo = null;
					if(fileIsOnSd[duze_foto] == true) {
						photo = new File(Environment.getExternalStorageDirectory(), fileNames[duze_foto]);
					} else {
						photo = new File(app.getDir("photo", Context.MODE_PRIVATE), fileNames[duze_foto]);
					}
					if (photo.exists()) {
						photo.delete();
						pokazPodglad();
						fileNamesBool[duze_foto] = false;
						showPhotos();
						for(int i=0; i<tagList.size(); i++) {
							if(tagList.get(i).getPhotoNo() == duze_foto) {
								tagList.remove(i);
								i--;
							}
						}
					}
				}
				showDeleteTagsButton();
			}
		});

		//zrobienie nastepnego zdjecia
		bTakeNextPhoto = (Button) app.findViewById(R.id.buttonNextPhoto);
		bTakeNextPhoto.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				pokazPodglad();
				lTagDesc.setVisibility(View.GONE);
				showDeleteTagsButton();
			}
		});
		
		//usuniecie tagow ze zdjecia
		bUsunTagi = (Button) app.findViewById(R.id.buttonDeleteTags);
		bUsunTagi.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				for(int i=0; i<tagList.size(); i++) {
					if(tagList.get(i).getPhotoNo() == duze_foto) {
						tagList.remove(i);
						i--;
					}
				}
				imgBig.postInvalidate();
				showDeleteTagsButton();
			}
		});
		
		descriptionTxt = (EditText) app.findViewById(R.id.editTextDescription);
		lPhotos.setVisibility(View.GONE);
		lCameraPreview.setVisibility(View.VISIBLE);
	}

	
	public void initGestureCamera() {
     GestureOverlayView gestures = (GestureOverlayView) app.findViewById(R.id.gesturesCamera);
      gestures.addOnGesturePerformedListener(new GestureListener(app));
	}
	
	public void setLayouts() {
		lCameraPreview = (LinearLayout) app.findViewById(R.id.linLayCameraPreview);
		lPhotos = (LinearLayout) app.findViewById(R.id.linLayPhotos);
		lMinPhotos = (LinearLayout) app.findViewById(R.id.linLayMinPhotos);
		lTagDesc = (LinearLayout) app.findViewById(R.id.linLayTagDesc);
		lMap = (LinearLayout) app.findViewById(R.id.linLayMap);		
	}
	
	/**
	 * Action on resume application.
	 */
	public void resume()
	{
		if (camera == null && app.isCameraViewSet) 
		{
			camera = Camera.open();
			if(preview.getVisibility() == View.VISIBLE)
			{
				inPreview = true;
				camera.startPreview();
			}
			try
			{
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
	}
	
	/**
	 * Action on destroy application.
	 */
	public void destroy() {
		File photo;
		for(int i=0; i<MAX_PHOTOS; i++) {
			if (fileIsOnSd[i]) {
				photo = new File(Environment.getExternalStorageDirectory(), fileNames[i]);
			} else {
				//no sd-card available
				photo = new File(app.getDir("photo", Context.MODE_PRIVATE), fileNames[i]);
			}
			
			if (photo.exists()) {
				photo.delete();
			}
			photo = null;
		}
	}
	
	public void showDeleteTagsButton() {
		for(int i=0; i < tagList.size(); i++) {
			if(tagList.get(i).getPhotoNo() == duze_foto) {
				bUsunTagi.setVisibility(View.VISIBLE);
				return;
			}
		}
		bUsunTagi.setVisibility(View.GONE);
	}

	private void pokazPodglad() {
		if(!inPreview) {
			lCameraPreview.setVisibility(View.VISIBLE);
			camera.startPreview();
			inPreview = true;
			lPhotos.setVisibility(View.GONE);
		}
	}

	public void showPhotos() {
		int showedPhotos = 0;
		for(int i=0; i<fileNamesBool.length; i++) {
			if(fileNamesBool[i] == true) {
				imgMinis[i].setVisibility(View.VISIBLE);
				File photo = null;
				if(fileIsOnSd[i] == true) {
					photo = new File(Environment.getExternalStorageDirectory(), fileNames[i]);
				} else {
					photo = new File(app.getDir("photo", Context.MODE_PRIVATE), fileNames[i]);
				}
				Bitmap bm = null;
				while(bm == null) {
					bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
				}
				bm = Bitmap.createScaledBitmap(bm, 80, 60, true);
				imgMinis[i].setImageBitmap(bm);
				showedPhotos++;
			} else {
				imgMinis[i].setVisibility(View.GONE);
			}
		}
		if(showedPhotos != 0) {
			lMinPhotos.setVisibility(View.VISIBLE);
		}
	}
}
