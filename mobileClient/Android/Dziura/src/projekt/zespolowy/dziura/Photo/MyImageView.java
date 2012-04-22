package projekt.zespolowy.dziura.Photo;

import projekt.zespolowy.dziura.DziuraActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Klasa reprezentujaca widok zdjecia, dziedziczaca po {@link android.widget.ImageView}.
 *
 */
public class MyImageView extends ImageView
{
	private Paint paint;
	private boolean clickable = true;
	private DziuraActivity dziuraAct;
	
	/**
	 * Konstruktor klasy {@link MyImageView}. Wywoluje najpierw konstruktor klasy nadrzednej, po czym inicjalizuje obiekt 
	 * {@link android.graphics.Paint} oraz przypisuje sobie obiekt klasy {@link android.view.View.OnTouchListener}. 
	 * Sluchacz ten sprawdza najpierw, czy w miejscu nacisniecia widoku
	 * nie znajduje sie jeden z dodanych tagow. Jezeli tak jest to zostaje wyswietlony jego opis. Jezeli nacisnieto 'pusty' punkt,
	 * to wyswietlone zostaje pole tekstowe umozliwiajace podanie opisu tagu oraz przyciski, ktore pozwalaja na anulowanie dodawania tagu
	 * lub na jego zapisanie. Po zapisaniu, zostaje w miejscu nacisniecia narysowane male kolo o wylosowanym kolorze. Dla kazdego nowego
	 * taga tworzony jest nowy obiekt klasy {@link PhotoTag}.
	 * <br>Konstruktor wywolywany automatycznie podczas tworzenia widoku. Nie jest wywolywany w kodzie aplikacji.
	 * @param context kontekst aplikacji
	 * @param attr zbior atrybutow
	 */
	public MyImageView(Context context, AttributeSet attr)
	{
		super(context, attr);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE); 
		paint.setStrokeWidth(2);
		this.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{ 
					boolean czyDodacTag = true;
					//sprawdzenie czy nie nacisnieto jednego z tagow, jezeli tak to wyswietlenie jego opisu
					for(int i=0; i<dziuraAct.vCamera.tagList.size(); i++)
				    {
				    	PhotoTag tmp = dziuraAct.vCamera.tagList.get(i);
						if(tmp.getPhotoNo() == dziuraAct.vCamera.duze_foto)
				    	{
				    		if(arg1.getX() > tmp.getX() - 20 && arg1.getX() < tmp.getX() + 20
				    				&& arg1.getY() > tmp.getY() - 20 && arg1.getY() < tmp.getY() + 20)
				    		{
				    			dziuraAct.wyswietlTekst(tmp.getText());
				    			czyDodacTag = false;
				    		}
				    	}
				    }
					if(czyDodacTag)
					{
						dziuraAct.vCamera.img_x = arg1.getX(); 
						dziuraAct.vCamera.img_y = arg1.getY();
						dziuraAct.vCamera.lTagDesc.setVisibility(View.VISIBLE);
						dziuraAct.vCamera.lMinPhotos.setVisibility(View.GONE);
						dziuraAct.vCamera.lButtons.setVisibility(View.GONE);
						if(getClickable())
						{
							dziuraAct.vibrate(100);  
							setClickable(false);
							dziuraAct.vCamera.tagTxt.requestFocus();
						}
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Setter instancji klasy {@link projekt.zespolowy.dziura.DziuraActivity}
	 * @param dziuraActivity referencja do instancji glownej aktywnosci
	 */
	public void setDziuraActivity(DziuraActivity dziuraActivity)
	{
		this.dziuraAct = dziuraActivity;
	}
	
	/**
	 * Funkcja wywolywana podczas przerysowywania widoku. Implementuje rysowanie na widoku. W miejscu,
	 * w ktorym dodany zostal tag/marker narysowane zostaje kolko o promieniu 7 pikseli.
	 * @param canvas obiekt na ktorym mozna rysowac
	 */
	/* (non-Javadoc)
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	    
	    paint.setColor(Color.RED);
	    for(int i=0; i<dziuraAct.vCamera.tagList.size(); i++)
	    {
	    	PhotoTag tmp = dziuraAct.vCamera.tagList.get(i);
	    	if(tmp.getPhotoNo() == dziuraAct.vCamera.duze_foto)
	    	{
	    		short [] color = tmp.getColor();
	    		paint.setColor(Color.argb(color[PhotoTag.ALPHA], color[PhotoTag.RED], color[PhotoTag.GREEN], color[PhotoTag.BLUE]));
	    		float x = tmp.getX();
	    		float y = tmp.getY();
	    		canvas.drawCircle(x, y, 7, paint);
	    	}
	    }
	}
	
	/**
	 * @param clickable true - jezeli ma byc mozliwe klikanie na widok zdjecia, false - w przeciwnym wypadku
	 */
	/* (non-Javadoc)
	 * @see android.view.View#setClickable(boolean)
	 */
	public void setClickable(boolean clickable)
	{
		super.setClickable(clickable);
		this.clickable = clickable;
	}

	/**
	 * @return <li>true - jezeli mozliwe jest klikanie na widok zdjecia<li>false - jezeli niemozliwe
	 */
	public boolean getClickable()
	{
		return this.clickable;
	}
}
