package projekt.zespolowy.dziura;

public class PhotoTag 
{
	private float x, y;
	private String text;
	int nr_zdjecia;
	
	public PhotoTag(float x, float y, String str, int zdjecie)
	{
		this.x = x;
		this.y = y;
		this.text = str;
		this.nr_zdjecia = zdjecie;
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
		return this.text;
	}
	
	public int getNumerZdjecia()
	{
		return this.nr_zdjecia;
	}
}
