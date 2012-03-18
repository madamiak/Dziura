package projekt.zespolowy.dziura;

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
	//private DziuraActivity dziuraAct;
	private Paint paint;
	private boolean clickable = true;
	private DziuraActivity dziuraAct;
	
	public MyImageView(Context context, AttributeSet attr)
	{
		super(context, attr);
		//this.dziuraAct = dziuraAct;
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE); 
		paint.setStrokeWidth(2);
		this.setOnTouchListener(new OnTouchListener()
		{

			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) { 
					dziuraAct.img_x = arg1.getX(); 
					dziuraAct.img_y = arg1.getY();
					dziuraAct.lTagDesc.setVisibility(View.VISIBLE);
					if(clickable)
					{
						dziuraAct.vibrate(200);  
						setClickable(false);
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
	    for(int i=0; i<dziuraAct.tagList.size(); i++)
	    {
	    	if(dziuraAct.tagList.get(i).nr_zdjecia == dziuraAct.duze_foto)
	    	{
	    		float x = dziuraAct.tagList.get(i).getX();
	    		float y = dziuraAct.tagList.get(i).getY();
	    		canvas.drawRect(x-4, y-4, x+4, y+4, paint);
	    	}
	    }
	}
	
	public void setClickable(boolean clickable)
	{
		super.setClickable(clickable);
		this.clickable = clickable;
	}

}
