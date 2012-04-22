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
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Klasa odpowiedzialna za widok aparatu i zdjec. Znajduja sie tu zmienne wszystkich kontrolek/przyciskow itd
 * z tego widoku oraz funkcje na nich operujace.
 * 
 * @see projekt.zespolowy.dziura.AppView.OptionView
 */
public class CameraView
{

	// Camera view stuff.
	/**
	 * Podglad obrazu z aparatu.
	 */
	public SurfaceView preview 		= null;
	
	/**
	 * Obiekt konieczny do powiazania obiektu aparatu {@link #camera} z widokiem podgladu {@link #preview}.
	 */
	public SurfaceHolder previewHolder = null;
	
	/**
	 * Obiekt reprezentujacy aparat telefonu.
	 */
	public Camera camera 				= null;
	
	/**
	 * Zmienna ta mowi, czy podglad obrazu z aparatu jest aktualnie wlaczony czy nie.
	 */
	public boolean inPreview 			= false;
	
	// Maximum number of photos.
	/**
	 * Maksymalna liczba zdjec, jaka moze zostac w aplikacji wywolana.
	 */
	public final int MAX_PHOTOS = 3;
	
	//layouty - zeby wyswietlic/schowac kontrolki to nie pojedynczo tylko caly layout, ktory je zawiera
	/**
	 * Layout zawierajacy podglad obrazu z aparatu {@link #preview} (oraz przycisk robienia zdjec {@link #bTakePhoto}).
	 */
	public LinearLayout lCameraPreview;
	
	/**
	 * Layout zawierajacy {@link #imgBig} sluzacy do wyswietlania duzego zdjecia oraz przycisku skupione w
	 * {@link #lButtons}, a takze przyciski i pole tekstowe wyswietlane
	 * podczas dodawania do zdjecia taga, zebrane w {@link #lTagDesc}.
	 */
	public LinearLayout lPhotos;
	
	/**
	 * Layout zawierajacy miniaturki zdjec {@link #imgMinis}.
	 */
	public LinearLayout lMinPhotos;
	
	/**
	 * Zawiera kontrolki sluzace do opisywania tagow ({@link #bSave}, {@link #bCancel}, {@link #tagTxt}).
	 */
	public LinearLayout lTagDesc;
	
	/**
	 * Layout zawiera w sobie przyciski sluzace do kasowania zdjecia
	 * ({@link #bDeletePhoto}), wykonania kolejnego zdjecia ({@link #bTakeNextPhoto}) i usuniecia tagow ze zdjecia ({@link #bUsunTagi}).
	 */
	public LinearLayout lButtons;

	// Keeps file names which contain photos.
	/**
	 * Tablica zawierajaca nazwy plikow ze zdjeciami.
	 */
	public final String[] fileNames = new String[MAX_PHOTOS]; 
	
	/**
	 * Elementy tej tablicy mowia, czy dany plik ze zdjeciem jest uzywany (<i>true</i>) czy nie (<i>false</i>).
	 */
	public Boolean[] fileNamesBool = new Boolean[MAX_PHOTOS]; //tablica przechowujaca boole, czy plik jest uzywany czy nie
	
	/**
	 * Elementy tej tablicy mowia, czy dane zdjecie jest zapisane na karcie SD (<i>true</i>) czy w pamieci telefonu (<i>false</i>).
	 */
	public Boolean[] fileIsOnSd = new Boolean[MAX_PHOTOS]; //if photo saved on sd-card - true, else false
	
	/**
	 * Wartosc okreslajaca jakosc zdjecia, jakie zostanie wyslane.
	 */
	public int photoQuality = 70;
	
	//number of photo on img_big
	/**
	 * Numer zdjecia, ktore jest aktualnie wyswietlone jako duze na {@link #imgBig}. <i>-1</i> jezeli zadne nie jest.
	 */
	public int duze_foto = -1;

	//coordinates of point where tag will be added
	/**
	 * Pozioma wspolrzedna punktu widoku zdjecia {@link #imgBig}, w ktorym tag zostanie dodany.
	 */
	public float img_x = 0;
	
	/**
	 * Pionowa wspolrzedna punktu widoku zdjecia {@link #imgBig}, w ktorym tag zostanie dodany.
	 */
	public float img_y = 0;

	// list containing tags added to photos
	/**
	 * Lista zawierajaca dodane do zdjec tagi.
	 */
	public List<PhotoTag> tagList;
	
	/**
	 * Przycisk sluzacy do wykonania zdjecia.
	 */
	public ImageButton bTakePhoto;
	
	/**
	 * Przycisk sluzacy do usuniecia zdjecia.
	 */
	public ImageButton bDeletePhoto;
	
	/**
	 * Przycisk sluzacy do przelaczania z widoku ogladania zdjecia do widoku robienia zdjec.
	 */
	public ImageButton bTakeNextPhoto;
	
	/**
	 * Przycisk umozliwiajacy usuniecie tagow z aktualnie wyswietlanego zdjecia.
	 */
	public ImageButton bUsunTagi;
	
	/**
	 * Tablica zawierajaca widoki miniaturek zdjec.
	 */
	public ImageView[] imgMinis;
	
	/**
	 * Widok wyswietlajacy duze zdjecie i umozliwiajacy dodawanie na nim tagow.
	 */
	public MyImageView imgBig;
	
	/**
	 * Pole tekstowe sluzace do wprowadzenia opisu dodawanego taga.
	 */
	public EditText tagTxt;
	
	/**
	 * Przycisk zapisujacy dodawany tag.
	 */
	public ImageButton bSave;
	
	/**
	 * Przycisk ten anuluje dodawanie taga.
	 */
	public ImageButton bCancel;
	
	/**
	 * Widok aparatu zawierajacy wszystkie elementy zwiazane z robieniem i usuwaniem zdjec oraz dodawaniem tagow. Sklada
	 * sie z:
	 * <ul>
	 * <li>{@link #lCameraPreview}
	 * <li>{@link #lMinPhotos}
	 * <li>{@link #lPhotos}
	 * </ul>
	 */
	public View mCameraView;

	private DziuraActivity app;
	
	/**
	 * Konstruktor klasy {@link CameraView}. Wykonuje nastepujace czynnosci:
	 * <ul>
	 * <li>przypisuje do zmiennej lokalnej referencje do instancji klasy {@link DziuraActivity} podana jako argument
	 * <li>przypisuje do zmiennych lokalnych odpowiadajace im widoki, layouty, przyciski
	 * <li>inicjalizuje dzia³anie gestów w tym widoku
	 * <li>generuje losowe nazwy plikow, ktore posluza do zapisania zdjec na czas dzialania programu
	 * <li>przypisuje 'sluchaczy' przyciskom
	 * <li>ustawia orientacje ekranu na <i>LANDSCAPE</i>
	 * </ul>
	 * @param app instancja aktywnosci aplikacji
	 */
	public CameraView(final DziuraActivity app) 
	{
		this.app = app;

		mCameraView = app.findViewById(R.id.cameraView);
		

		setLayouts();
		
		app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// gestures
		initGestureCamera();

		//ustawienie nazw plikow
		Random rand = new Random();
		for(int i = 0; i<MAX_PHOTOS; i++) 
		{
			// TODO: check if file exist. //po co to sprawdzac? uzytkownikowi na pewno nie usuniemy jakiegos pliku, a z aplikacji swoje mozemy
			Float f = rand.nextFloat();
			fileNames[i] = "dziura_"+f.hashCode()+Integer.toString(i)+".jpg";
			fileNamesBool[i] = false;
			fileIsOnSd[i] = true;
		}

		tagList = new ArrayList<PhotoTag>();

		tagTxt = (EditText) app.findViewById(R.id.editText7);
		bSave = (ImageButton) app.findViewById(R.id.buttonSave);
		bCancel = (ImageButton) app.findViewById(R.id.buttonCancel);
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
					lMinPhotos.setVisibility(View.VISIBLE);
					lButtons.setVisibility(View.VISIBLE);
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
				lMinPhotos.setVisibility(View.VISIBLE);
				lButtons.setVisibility(View.VISIBLE);
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
			imgMinis[i].setPadding(5, 5, 0, 0);
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
		bDeletePhoto = (ImageButton) app.findViewById(R.id.buttonDeletePhoto);
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
		bTakeNextPhoto = (ImageButton) app.findViewById(R.id.buttonNextPhoto);
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
		bUsunTagi = (ImageButton) app.findViewById(R.id.buttonDeleteTags);
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
		lPhotos.setVisibility(View.GONE);
		lCameraPreview.setVisibility(View.VISIBLE);
	}

	
	private void initGestureCamera() {
     GestureOverlayView gestures = (GestureOverlayView) app.findViewById(R.id.gesturesCamera);
      gestures.addOnGesturePerformedListener(new GestureListener(app));
	}
	
	private void setLayouts() {
		lCameraPreview = (LinearLayout) app.findViewById(R.id.linLayCameraPreview);
		lPhotos = (LinearLayout) app.findViewById(R.id.linLayPhotos);
		lMinPhotos = (LinearLayout) app.findViewById(R.id.linLayMinPhotos);
		lTagDesc = (LinearLayout) app.findViewById(R.id.linLayTagDesc);
		lButtons = (LinearLayout) app.findViewById(R.id.linLayButtons);
	}
	
	/**
	 * Umo¿liwia wznowienie pracy aparatu.
	 */
	public void resume()
	{
		if (camera == null && app.tabHost.getCurrentTab() == 0) 
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
	 * Koñczy pracê aparatu i usuwa zdjêcia wykonane przez aplikacjê.
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
	
	/**
	 * Sprawdza, czy na aktualnie wyswietlonym zdjeciu (duzym, nie na miniaturkach) sa dodane jakies tagi. Jezeli tak
	 * to wyswietla obok zdjecia klawisz umozliwiajacy ich usuniecie.
	 */
	public void showDeleteTagsButton() 
	{
		for(int i=0; i < tagList.size(); i++) 
		{
			if(tagList.get(i).getPhotoNo() == duze_foto) 
			{
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

	/**
	 * Funkcja wyswietlajaca na ekranie miniaturki wykonanych zdjec. Ich rozmiar jest dopasowany do rozmiaru ekranu,
	 * aby zmiescily sie zarowno na szerokosc jak i wysokosc.
	 */
	public void showPhotos() 
	{
		int showedPhotos = 0;
		
		//pobranie wielkosci ekranu
		DisplayMetrics displaymetrics = new DisplayMetrics();
		app.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int displayWidth = displaymetrics.widthPixels;
        int displayHeight = displaymetrics.heightPixels;
		        
		for(int i=0; i<fileNamesBool.length; i++) 
		{
			if(fileNamesBool[i] == true) 
			{
				imgMinis[i].setVisibility(View.VISIBLE);
				File photo = null;
				if(fileIsOnSd[i] == true) 
				{
					photo = new File(Environment.getExternalStorageDirectory(), fileNames[i]);
				} 
				else 
				{
					photo = new File(app.getDir("photo", Context.MODE_PRIVATE), fileNames[i]);
				}
				Bitmap bm = null;
				while(bm == null) 
				{
					bm = BitmapFactory.decodeFile(photo.getAbsolutePath());
				}
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
				
				int maxMiniH = (int)((displayHeight - 15 - app.tabWidget.getHeight())/(double)MAX_PHOTOS);
				int maxMiniW = displayWidth - preview.getWidth() - 10 - bTakePhoto.getWidth();
				int imgViewW = maxMiniW;
				int imgViewH = 0;
				while(true)
				{
					imgViewH = (int)((double)imgViewW * (double)imageRatio);
					if(imgViewH <= maxMiniH)
					{
						break;
					}
					else
					{
						imgViewW--;
					}
				}
				bm = Bitmap.createScaledBitmap(bm, imgViewW, imgViewH, true);
				LayoutParams imgParam = imgMinis[i].getLayoutParams();
				imgParam.width = imgViewW;
				imgParam.height = imgViewH;
				imgMinis[i].setLayoutParams(imgParam);
				imgMinis[i].setImageBitmap(bm);
				showedPhotos++;
			} 
			else 
			{
				imgMinis[i].setVisibility(View.GONE);
			}
		}
		if(showedPhotos != 0) 
		{
			lMinPhotos.setVisibility(View.VISIBLE);
		}
	}
}
