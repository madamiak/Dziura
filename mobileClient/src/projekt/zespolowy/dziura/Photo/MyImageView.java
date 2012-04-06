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

public class MyImageView extends ImageView
{
	private Paint paint;
	private boolean clickable = true;
	private DziuraActivity dziuraAct;
	
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
						if(clickable)
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
	
	public void setDziuraActivity(DziuraActivity dziuraActivity)
	{
		this.dziuraAct = dziuraActivity;
	}
	
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
	    		//canvas.drawRect(x-6, y-6, x+6, y+6, paint);
	    		canvas.drawCircle(x, y, 7, paint);
	    	}
	    }
	}
	
	public void setClickable(boolean clickable)
	{
		super.setClickable(clickable);
		this.clickable = clickable;
	}

}
