package com.bjerva.tsplex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class RecipeListItemView extends TextView {
	private Paint linePaint;
	private int paperColor;
	boolean draw = true;
	
	public RecipeListItemView(Context context, AttributeSet ats, int ds){
		super(context, ats, ds);
		init();
	}
	
	public RecipeListItemView(Context context){
		super(context);
		init();
	}
	
	public RecipeListItemView(Context context, AttributeSet ats){
		super(context, ats);
		init();
	}
	
	private void init(){
		Resources myResources = getResources();
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.cell_separator));
		paperColor = myResources.getColor(R.color.transparent_white);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		if(draw){
			canvas.drawColor(paperColor);

			//Separator line
			//canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
			canvas.drawLine(0, 0, getMeasuredWidth(), 0, linePaint);
			//canvas.drawLine(0, 0, 0, getMeasuredHeight(), linePaint);
			//canvas.drawLine(getMeasuredWidth(), 0, getMeasuredWidth(), getMeasuredHeight(), linePaint);

			canvas.save();
			super.onDraw(canvas);
			canvas.restore();
		} else {
			super.onDraw(canvas);
		}
	}
}
