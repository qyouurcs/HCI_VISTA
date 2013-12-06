package com.example.hci_vista_low;

import java.util.Random;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hci_vista_low.ListOfSelectors;
import com.example.hci_vista_low.Selector;

public class SingleTouchEventView extends ImageView implements
		OnDoubleTapListener, OnGestureListener,
		android.view.GestureDetector.OnGestureListener {
	private Paint paint = new Paint();
	private ListOfSelectors sels = new ListOfSelectors();
	private LinearLayout action_layout = null;
	private static final String TAG = "LAM";
	private Bitmap image = null;
	private GestureDetector detector;
	private static final int SWIPE_VELOCITY = 4000;
	private RectF r;
	private Random rnd = new Random();
	private boolean scroll;
	private int color;
	private boolean editMode = false;
	private boolean scaleMode = false;
	private Selector selected;
	private ScaleGestureDetector scaleDetector;
	
	public SingleTouchEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEnabled(false);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		detector = new GestureDetector(this);
		r = new RectF(0, 0, 0, 0);
		scroll = false;
		selected = new Selector();
		scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	public void addSelector(Selector sel) {
		this.sels.addSelector(new Selector(sel));
	}

	/**
	 * Get all the selected areas.
	 * 
	 * @return Bitmap array, which contains all the selected areas.
	 */
	public Bitmap[] getSelectionBitmaps() {
		int selections = sels.size();
		if (selections <= 0)
			return null;
		
		int w = this.getWidth();
		int h = this.getHeight();
		
		image = Bitmap.createScaledBitmap(image, w, h, false);
		//Log.i("LAM", "XS " + image.getHeight() + " XSE " + this.getHeight());
		//Log.i("LAM", "YS " + image.getWidth() + " YSE " + this.getWidth());
		
		Bitmap[] sel_areas = new Bitmap[selections];
		List<Selector> list_sel = sels.getSelectors();
		Iterator<Selector> iter = list_sel.iterator();
		int idx_sel = 0;
		while (iter.hasNext()) {
			Selector item = iter.next();
			int ty = Math.max(1, item.getTop().y);
			int tx = Math.max(1, item.getTop().x);
			int bx = Math.min(w-1, item.getBottom().x);
			int by = Math.min(h-1, item.getBottom().y);
			
			int rec_height = by - ty;
			int rec_width = bx - tx;
			sel_areas[idx_sel] = Bitmap.createBitmap(image, item.getTop().x, item.getTop().y, rec_width, rec_height);
			idx_sel += 1;
		}
		return sel_areas;
	}

	/**
	 * Control the layout of the DisplayActivity.
	 */
	public void setActionLayout(LinearLayout ll) {
		this.action_layout = ll;
	}

	/**
	 * clear all the selections from the image
	 */
	public void clear() {
		sels.clear();
		invalidate();
	}
	
	public Bitmap getImage(){
		return image;
	}

	/**
	 * set the image file that's going to show on the current custom view.
	 * 
	 * @param fn
	 */
	public void setImageFile(String fn, int height, int width) {
		if (image != null)
			image.recycle();// first release the memory.
		
		image = BitmapFactory.decodeFile(fn);//read image
		Log.i("LAM2", "real height " + height + " real width " + width);
		Log.i("LAM2", "before height " + image.getHeight() + " before width " + image.getWidth());
		image = Bitmap.createScaledBitmap(image, width, height, false);
		Log.i("LAM2", "after height " + image.getHeight() + " after width " + image.getWidth());
		this.setImageBitmap(image);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect dest = new Rect(0, 0, getWidth(), getHeight());
		if (image != null) {
			canvas.drawBitmap(image, null, dest, paint);
			if (editMode == false) {
				sels.drawSelectors(canvas, paint);
				if (scroll == true) {
					paint.setColor(color);
					canvas.drawRect(r, paint);
				}
			} else {
				paint.setColor(selected.getColor());
				canvas.drawRect(selected.getRect(), paint);
			}
			// sels.drawSelectors(canvas, paint);

			if (sels.size() > 0)
				this.action_layout.setVisibility(View.VISIBLE);
			else
				this.action_layout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int c = event.getPointerCount();
		if (c > 1) {
			boolean scale = scaleDetector.onTouchEvent(event);
		} else {
			boolean result = detector.onTouchEvent(event); // return the double
															// tap events
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // need this for up to work, not sure
											// why?
				break;
			case MotionEvent.ACTION_UP:
				if (scroll == true && scaleMode == false && editMode == false) {
					scroll = false;
					sels.addSelector(new Selector((int) r.left, (int) r.top,
							(int) r.right, (int) r.bottom, color));
				}
				break;
			default:
				return false;
			}
		}
		// Schedules a repaint.
		invalidate();
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i(TAG, "Double Tapped " + e.getX() + " " + e.getY());
		if (editMode == false) {
			selected = sels.getSelector((int) e.getX(), (int) e.getY());
			if (selected != null) {
				sels.setIsAddable(false);
				editMode = true;
			}
		} else {
			if (selected.inBound((int) e.getX(), (int) e.getY()) == false) {
				editMode = false;
				selected = null;
			}
		}
		invalidate();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {

		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.i(TAG, "onSingleTapConfirmed " + e.getX() + " " + e.getY());
		if (selected == null)
			sels.addPoint(new Point((int) e.getX(), (int) e.getY()));
		else
			selected.changeBound((int) e.getX(), (int) e.getY());

		invalidate();
		return true;
	}

	@Override
	public void onGesture(GestureOverlayView arg0, MotionEvent e1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onGesture " + e1.getX() + " " + e1.getY());
	}

	@Override
	public void onGestureCancelled(GestureOverlayView arg0, MotionEvent e1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onGestureCancelled " + e1.getX() + " " + e1.getY());
	}

	@Override
	public void onGestureEnded(GestureOverlayView arg0, MotionEvent e1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onGestureEnded " + e1.getX() + " " + e1.getY());
	}

	@Override
	public void onGestureStarted(GestureOverlayView arg0, MotionEvent e1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onGestureStarted " + e1.getX() + " " + e1.getY());
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onFling " + e1.getX() + " " + e1.getY());

		if (e1 == null || e2 == null) {
			return false;
		}

		float dX = e2.getX() - e1.getX();
		float dY = e1.getY() - e2.getY();
		double dis = Math.pow(velocityY, 2) + Math.pow(velocityX, 2);
		dis = Math.sqrt(dis);

		if (SWIPE_VELOCITY <= dis) {

			sels.remove(selected);
			invalidate();
			editMode = false;

		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onLongPress " + e.getX() + " " + e.getY());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float x, float y) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onScroll " + x + " " + y);
		if (scaleMode == false) {
			if (editMode == false) {
				if (scroll == false)
					color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
							rnd.nextInt(256));
				scroll = true;
				r = new RectF(Math.min(e1.getX(), e2.getX()), Math.min(
						e1.getY(), e2.getY()), Math.max(e1.getX(), e2.getX()),
						Math.max(e1.getY(), e2.getY()));
			} else {
				Log.i(TAG, "onMove " + x + " " + y);
				selected.move((int) x, (int) y);
			}
		}
		invalidate();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSingleTapUp " + e.getX() + " " + e.getY());
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDown " + e.getX() + " " + e.getY());
		return false;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (selected != null) {
				selected.scale(detector.getScaleFactor());
			}
			scaleMode = true;
			Log.i(TAG, "scale model " + scaleMode);
			invalidate();
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.i(TAG, "scale model " + scaleMode);
			scaleMode = true;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			Log.i(TAG, "scale model " + scaleMode);
			scaleMode = false;
		}
	}
}