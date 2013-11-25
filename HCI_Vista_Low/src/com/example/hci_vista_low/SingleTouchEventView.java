package com.example.hci_vista_low;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.hci_vista_low.ListOfSelectors;

public class SingleTouchEventView extends ImageView {
  private Paint paint = new Paint();
  private ListOfSelectors sels = new ListOfSelectors();
  private static final String    TAG                 = "LAM";
  Bitmap image;
  
  public SingleTouchEventView(Context context, AttributeSet attrs, String fn) {
    super(context, attrs);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(6f);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
	image = BitmapFactory.decodeFile(fn); 
	this.setImageBitmap(image);
  }

  @Override
  protected void onDraw(Canvas canvas) {
	  Rect dest = new Rect(0, 0, getWidth(), getHeight());
	  canvas.drawBitmap(image, null, dest, paint);
      sels.drawSelectors(canvas, paint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float eventX = event.getX();
    float eventY = event.getY();
    
    switch (event.getAction()) {
    case MotionEvent.ACTION_MOVE:
    	Log.i(TAG, "MOVE Coordinates "+ event.getX() + " "+ event.getY());
    	sels.movingPoint(new Point((int)eventX, (int)eventY));
    	break;
    case MotionEvent.ACTION_POINTER_DOWN:
        Log.i(TAG, "APD Coordinates "+ event.getX() + " "+ event.getY());                   
        break;
    case MotionEvent.ACTION_DOWN:
    	Log.i(TAG, "AD Coordinates "+ event.getX() + " "+ event.getY()); 
    	sels.addPoint(new Point((int)eventX, (int)eventY));
    	break;
    case MotionEvent.ACTION_UP:
    	Log.i(TAG, "UP Coordinates "+ event.getX() + " "+ event.getY());
    	sels.addMovingPoint(new Point((int)eventX, (int)eventY));
      break;
    default:
      return false;
    }
    // Schedules a repaint.
    invalidate();
    return true;
  }
} 