package projekt.zespolowy.dziura.Photo;

import java.util.Random;

/**
 * Klasa reprezentujaca tag dodany do zdjecia. Zawiera jego pozycje oraz zalaczony opis.
 *
 */
public class PhotoTag 
{
	
	/**
	 * Stala okreslajaca pozycje koloru czerwonego w tablicy kolorow.
	 */
	public static final short RED = 0;
	
	/**
	 * Stala okreslajaca pozycje koloru zielonego w tablicy kolorow.
	 */
	public static final short GREEN = 1;
	
	/**
	 * Stala okreslajaca pozycje koloru niebieskiego w tablicy kolorow.
	 */
	public static final short BLUE = 2;
	
	/**
	 * Stala okreslajaca pozycje wspolczynnika alfa w tablicy kolorow.
	 */
	public static final short ALPHA = 3;
	
	private short[] mColor = {0,0,0,0};
	private float x, y;
	private String mText;
	private int mPhotoNo;
	private int imgViewHeight;
	private int imgViewWidth;
	
	/**
	 * Konstruktor klasy {@link PhotoTag}. Pobiera punkt nacisniecia duzego zdjecia ({@link MyImageView}) oraz opis, po
	 * czym losuje kolor markera.
	 * @param x polozenie taga w poziomie
	 * @param y polozenie taga w pionie
	 * @param str opis taga
	 * @param zdjecie numer zdjecia do ktorego tag jest dodawany
	 * @param imgViewHeight wysokosc {@link MyImageView} na ktorym wyswietlane jest zdjecie
	 * @param imgViewWidth szerokosc {@link MyImageView} na ktorym wyswietlane jest zdjecie
	 */
	public PhotoTag(float x, float y, String str, int zdjecie, int imgViewHeight, int imgViewWidth)
	{
		this.x = x;
		this.y = y;
		this.mText = str;
		this.mPhotoNo = zdjecie;
		Random rand = new Random();
		mColor[RED] = (short) rand.nextInt(256);
		mColor[GREEN] = (short) rand.nextInt(256);
		mColor[BLUE] = (short) rand.nextInt(256);
		mColor[ALPHA] = (short) (255);
		this.imgViewHeight = imgViewHeight;
		this.imgViewWidth = imgViewWidth;
	}
	
	/**
	 * @return <li>pozycja taga w poziomie
	 */
	public float getX()
	{
		return this.x;
	}
	
	/**
	 * @return <li>pozycja taga w pionie
	 */
	public float getY()
	{
		return this.y;
	}
	
	/**
	 * @return <li>opis taga
	 */
	public String getText()
	{
		return this.mText;
	}
	
	/**
	 * @return numer zdjecia, na ktorym tag zostal dodany
	 */
	public int getPhotoNo()
	{
		return this.mPhotoNo;
	}
	
	/**
	 * @return kolor taga
	 */
	public short[] getColor()
	{
		return mColor;
	}
	
	/**
	 * @return wysokosc {@link MyImageView} na ktorym wyswietlane bylo zdjecie podczas dodania taga
	 */
	public int getImgViewHeight()
	{
		return this.imgViewHeight;
	}
	
	/**
	 * @return szerokosc {@link MyImageView} na ktorym wyswietlane bylo zdjecie podczas dodania taga
	 */
	public int getImgViewWidth()
	{
		return this.imgViewWidth;
	}
}
