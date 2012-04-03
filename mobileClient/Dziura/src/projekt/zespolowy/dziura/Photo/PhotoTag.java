package projekt.zespolowy.dziura.Photo;

import java.util.Random;

public class PhotoTag 
{
	public static final short RED = 0;
	public static final short GREEN = 1;
	public static final short BLUE = 2;
	public static final short ALPHA = 3;
	
	private short[] mColor = {0,0,0,0};
	private float x, y;
	private String mText;
	private int mPhotoNo;
	private int imgViewHeight;
	private int imgViewWidth;
	
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
		mColor[ALPHA] = (short) rand.nextInt(256);
		this.imgViewHeight = imgViewHeight;
		this.imgViewWidth = imgViewWidth;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public String getText()
	{
		return this.mText;
	}
	
	public int getPhotoNo()
	{
		return this.mPhotoNo;
	}
	
	public short[] getColor()
	{
		return mColor;
	}
	
	public int getImgViewHeight()
	{
		return this.imgViewHeight;
	}
	
	public int getImgViewWidth()
	{
		return this.imgViewWidth;
	}
}
