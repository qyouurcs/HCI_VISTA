package com.example.hci_vista_low;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gallery.BitmapTool;
import com.example.gallery.ScreenTool;
import com.example.gallery.ScreenTool.Screen;

public class PictureActivity extends Activity implements OnTouchListener,
		OnGestureListener {

	private ImageView pictureImageView;
	private Button btn_cancel;
	private Button btn_confirm;
	private int pictureImageViewWidth = 0;
	private int pictureImageViewHeight = 0;

	private Screen screen;
	private int screenWidth;
	private int screenHeight;

	private ArrayList<String> filePath;
	private int position;

	private Matrix setMatrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix initialMatrix = new Matrix();

	private GestureDetector gestureDetector;
	private boolean isReturnGesture = true;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDistance = 1f;

	private Bitmap bitmap;
	private int bitmapWidth;
	private int bitmapHeight;
	private int bitmapRealWidth = 0;
	private int bitmapRealHeight = 0;

	private float scaleTotalTimes = 1f;
	private float scaleNowTimes;
	private float scaleLastTimes = 0f;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		this.pictureImageView = (ImageView) this
				.findViewById(R.id.pictureImageView);
		pictureImageView.setLongClickable(true);
		pictureImageView.setOnTouchListener(this);

		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PictureActivity.this, DisplayResults.class);
				intent.putExtra(MainActivity.EXTRA_INTENT_MESSAGE, filePath.get(position));
				Log.e("Image_Gallery","StartIntent " + filePath.get(position));
				startActivity(intent);
			}
		});
		
		btn_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				PictureActivity.this.finish();
			}
		});
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		filePath = bundle.getStringArrayList("filePath");
		position = bundle.getInt("index");

		screen = ScreenTool.getScreenPix(this);
		this.screenWidth = screen.widthPixels;
		this.screenHeight = screen.heightPixels;

		gestureDetector = new GestureDetector(this);
		gestureDetector.setIsLongpressEnabled(true);

		this.LoadBitmap();

	}

	public boolean onTouch(View v, MotionEvent event) {

		ImageView view = (ImageView) v;
		float scaleTemp = 1f;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			setMatrix.set(view.getImageMatrix());
			savedMatrix.set(setMatrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			oldDistance = spacing(event);
			if (oldDistance > 10f) {
				savedMatrix.set(setMatrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (this.scaleNowTimes != 0
					&& Math.abs(this.scaleLastTimes - this.scaleNowTimes) > 0.0001) {
				this.scaleTotalTimes = this.scaleTotalTimes
						* this.scaleNowTimes;
				this.scaleLastTimes = this.scaleNowTimes;
			}

			float moveResult[] = this.computerLimit(this.scaleTotalTimes,
					event.getX());

			if (moveResult == null) {
				setMatrix.set(initialMatrix);
				this.isReturnGesture = true;
			} else {
				if (moveResult[2] == 1f) {
					this.isReturnGesture = true;
				}
				setMatrix.postTranslate(moveResult[0], moveResult[1]);
			}
			break;

		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				setMatrix.set(savedMatrix);
				setMatrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					setMatrix.set(savedMatrix);
					scaleTemp = newDist / oldDistance;
					this.scaleNowTimes = scaleTemp;
					setMatrix.postScale(scaleTemp, scaleTemp, mid.x, mid.y);
					this.isReturnGesture = false;
				}
			}
			break;
		}
		view.setImageMatrix(setMatrix);
		if (this.isReturnGesture)
			return gestureDetector.onTouchEvent(event);
		else
			return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float x = e2.getX() - e1.getX();
		float y = e2.getY() - e1.getY();
		float x_limit = screen.widthPixels / 3;
		float x_abs = Math.abs(x);
		float y_abs = Math.abs(y);
		if (x_abs >= y_abs) {
			if (x > x_limit || x < -x_limit) {
				if (x > 0) {
					position--;
					while (position >= 0 && !isFile(filePath.get(position))) {
						position--;
					}
					if (position >= 0) {
						TranslateAnimation trans = new TranslateAnimation(-x,
								0, -y, 0);
						trans.setDuration(300);
						pictureImageView.startAnimation(trans);

						this.LoadBitmap();
					} else {
						show("To the top now.");
						position++;
						while (!isFile(filePath.get(position))) {
							position++;
						}
					}
				} else if (x <= 0) {
					position++;
					while (position < filePath.size()
							&& !isFile(filePath.get(position))) {
						position++;
					}
					if (position < filePath.size()) {
						
						TranslateAnimation trans = new TranslateAnimation(-x,
								0, -y, 0);
						trans.setDuration(500);
						pictureImageView.startAnimation(trans);

						this.LoadBitmap();
					} else {
						show("Reach the bottom.");
						position--;
						while (!isFile(filePath.get(position))) {
							position--;
						}
					}
				}
			}
		}
		return true;
	}

	private void initialMatrix() {
		Matrix tempMatrix = this.pictureImageView.getImageMatrix();
		tempMatrix.reset();

		int imageWidth = this.bitmapWidth;
		int imageHeight = this.bitmapHeight;

		float temp = 1f;
		float moveHeight = 0f;
		float moveWidth = 0f;

		if (imageWidth > imageHeight) {
			if (this.pictureImageViewWidth > imageWidth) {
				moveWidth = (this.pictureImageViewWidth - imageWidth) / 2;
				temp = 1;
			} else {
				
				temp = (float) this.pictureImageViewWidth / imageWidth;
				tempMatrix.postScale(temp, temp);
				moveWidth = 0f;
			}
			float height = imageHeight * temp;
			moveHeight = (this.pictureImageViewHeight - height) / 2;
			tempMatrix.postTranslate(moveWidth, moveHeight);
		}
		
		else {
			if (this.pictureImageViewHeight > imageHeight) {
				// moveHeight = (imageViewHeight - imageHeight) / 2;
				if (this.pictureImageViewWidth > imageWidth) {
					moveWidth = (this.pictureImageViewWidth - imageWidth) / 2;
					temp = 1;
				} else {
					temp = (float) this.pictureImageViewWidth / imageWidth;
					tempMatrix.postScale(temp, temp);
					moveWidth = 0f;
				}
				moveHeight = ((float) this.pictureImageViewHeight - imageHeight) / 2;
			}
			else {
				temp = (float) pictureImageViewHeight / imageHeight;
				moveHeight = 0;
				moveWidth = (this.pictureImageViewWidth - imageWidth * temp) / 2;
				if (imageWidth * temp - this.pictureImageViewWidth > 0) {
					temp = (float) this.pictureImageViewWidth / imageWidth;
					moveWidth = 0;
					moveHeight = (this.pictureImageViewHeight - imageHeight
							* temp) / 2;
				}
				tempMatrix.postScale(temp, temp);
			}
			tempMatrix.postTranslate(moveWidth, moveHeight);

		}
		this.pictureImageView.setImageMatrix(tempMatrix);

		this.initialMatrix.set(tempMatrix);
		/*
		 * float initialMatrixValue[] = new float[9];
		 * tempMatrix.getValues(initialMatrixValue); this.shrinkMatrixValue =
		 * initialMatrixValue[0];
		 */

		this.computerImageRealSize(temp);
	}

	private void computerImageRealSize(float times) {
		this.bitmapRealWidth = (int) (this.bitmapWidth * times);
		this.bitmapRealHeight = (int) (this.bitmapHeight * times);
	}

	public void getImageViewSize() {
		this.pictureImageViewHeight = this.pictureImageView.getHeight();
		this.pictureImageViewWidth = this.pictureImageView.getWidth();
		if (pictureImageViewHeight == 0 || pictureImageViewWidth == 0) {
			pictureImageViewWidth = this.screenWidth;
			pictureImageViewHeight = this.screenHeight - 80;
		}
	}

	private float[] computerLimit(float times, float endX) {
		float resultWidth = 0f;
		float resultHeight = 0f;

		float nowImageWidth = this.bitmapRealWidth * times;
		float nowImageHeight = this.bitmapRealHeight * times;

		if (nowImageWidth <= this.pictureImageViewWidth) {
			resultWidth = (this.pictureImageViewWidth - nowImageWidth) / 2;
		} else {
			resultWidth = (nowImageWidth - pictureImageViewWidth) * -1;
		}
		if (nowImageHeight <= this.pictureImageViewHeight) {
			resultHeight = (this.pictureImageViewHeight - nowImageHeight) / 2;
		} else {
			resultHeight = (nowImageHeight - pictureImageViewHeight) * -1;
		}

		if (this.changShrink()) {
			return null;
		}
		return this.changeLimit(resultWidth, resultHeight, nowImageWidth,
				nowImageHeight, times, endX);
	}

	private boolean changShrink() {
		float nowMatrixValue[] = new float[9];
		float initialMatrixValue[] = new float[9];
		this.initialMatrix.getValues(initialMatrixValue);
		this.pictureImageView.getImageMatrix().getValues(nowMatrixValue);
		if (nowMatrixValue[0] < initialMatrixValue[0]) {
			this.scaleTotalTimes = 1f;
			this.scaleLastTimes = 0f;
			this.scaleNowTimes = 0f;
			return true;
		}
		return false;
	}

	private float[] changeLimit(float limitWidth, float limitHeight,
			float nowImageWidth, float nowImageHeight, float times, float endX) {
		float matrixValue[] = new float[9];
		this.pictureImageView.getImageMatrix().getValues(matrixValue);

		float width = matrixValue[2];
		float height = matrixValue[5];

		float moveWidth = 0f;
		float moveHeight = 0f;

		float isScroll = 0f;

		if (nowImageWidth - this.pictureImageViewWidth > 0) {
			if (width - limitWidth < 0) {
				moveWidth = limitWidth - width;
				if (Math.abs(this.start.x - endX) - this.screenWidth / 2 > 0) {
					isScroll = 1f;
				}
			}
			if (width > 0) {
				moveWidth = -width;
				if (Math.abs(this.start.x - endX) - this.screenWidth / 2 > 0) {
					isScroll = 1f;
				}
			}
			if (nowImageHeight - this.pictureImageViewHeight > 0) {
				if (height - limitHeight < 0) {
					moveHeight = limitHeight - height;
				}
				if (height > 0) {
					moveHeight = -height;
				}
			}
			else {
				if (!this.isFloatEqual(height, limitHeight)) {
					moveHeight = limitHeight - height;
				}
			}
		}
		else {
			if (!this.isFloatEqual(width, limitWidth)) {
				moveWidth = (limitWidth - width);
				if (Math.abs(this.start.x - endX) - this.screenWidth / 2 > 0) {
					isScroll = 1f;
				}
			}
			if (nowImageHeight - this.pictureImageViewHeight > 0) {
				if (height - limitHeight < 0) {
					moveHeight = limitHeight - height;
				}
				if (height > 0) {
					moveHeight = -height;
				}
			} else {
				if (!this.isFloatEqual(height, limitHeight)) {
					moveHeight = (limitHeight - height);
				}
			}

		}
		return new float[] { moveWidth, moveHeight, isScroll };
	}

	private boolean isFloatEqual(float compare1, float compare2) {
		if (Math.abs(compare1 - compare2) < 0.00001) {
			return true;
		}
		return false;
	}

	private void LoadBitmap() {
		try {
			bitmap = BitmapTool.getBitmap(filePath.get(position),
					this.screenWidth, this.screenHeight);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bitmapWidth = bitmap.getWidth();
		this.bitmapHeight = bitmap.getHeight();
		pictureImageView.setImageBitmap(bitmap);
		this.adjustBitmap();
	}

	private void adjustBitmap() {
		this.getImageViewSize();
		this.initialMatrix();
		this.scaleTotalTimes = 1f;
	}

	private void show(String value) {
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}

	private boolean isFile(String path) {
		File file = new File(path);
		if (file.isFile())
			return true;
		else
			return false;

	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {

	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
