package com.example.hci_vista_low;

import java.util.Random;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;


public class Selector {
	private Point top;
	private Point bottom;
	private int color = 0;
	
	
	private boolean visible;
	final public static int TOP_LEFT  = 3;
	final public static int TOP_RIGHT = 1;
	final public static int BOTTOM_LEFT = 2;
	final public static int BOTTOM_RIGHT = 0;
	
	public Selector(Selector a){
		top = new Point();
		bottom = new Point();
		top.x = a.top.x;
		top.y = a.top.y;
		bottom.x = a.bottom.x;
		bottom.y = a.bottom.y;
		color = a.color;
	}
	// constructors
	public Selector(){
		top = new Point();
		bottom = new Point();
	}
	
	public Selector(int x1, int y1, int x2, int y2, int color){
		top = new Point(x1, y1);
		bottom = new Point(x2, y2);
		this.color = color;
	}
	
public void changeBound(int x, int y) {
		if (Math.abs(x - top.x) > Math.abs(x - bottom.x))
			bottom.x = x;
		else
			top.x = x;

		if (Math.abs(y - top.y) > Math.abs(y - bottom.y))
			bottom.y = y;
		else
			top.y = y;
	}
	
	public void moveBoundary(int x, int y){
		int corner = getCorner(x, y);
		if(corner == Selector.TOP_LEFT){
			top.x = x; top.y = y;
		}else if(corner == Selector.BOTTOM_RIGHT){
			bottom.x = x; bottom.y = y;
		}
		else if(corner == Selector.TOP_RIGHT){
			bottom.x = x;
			top.y = y;
		}else{
			bottom.y = y;
			top.x = x;
		}
	}
	
	public int getCorner(int x, int y) {
		// TOPLEFT 3, BottomLeft 2, TOPRIGHT 1, BOTTOMRIGHT 0
		//1^i top (true) 2^1 left (true)
		boolean leftFlag = false;
		if (Math.abs(x - top.x) < Math.abs(x - bottom.x)) //left and right check
			leftFlag = true;//value += 2; // left
		// 0 right
		if (Math.abs(y - top.y) < Math.abs(y - bottom.y)){ //top and bottom check
			if(leftFlag == true)
				return Selector.TOP_LEFT;
			else
				return Selector.TOP_RIGHT;
			//value ++; //top 1
		}
		else{
			if(leftFlag == true)
				return Selector.BOTTOM_LEFT;
			else
				return Selector.BOTTOM_RIGHT;
		}
	}
	// determine if a point is in bound.
	public boolean inBound(int x, int y){
		if( x >= top.x && x <= bottom.x && y >= top.y && y <= bottom.y )
			return true;
		else
			return false;
	}
	
	// move bounding box.
	public void move(int x, int y){
		top.y -= y;
		top.x -= x;
		bottom.y -= y;
		bottom.x -= x;
	}
	
	// scale bounding box.
	public void scale(float f){
		int centerX = (bottom.x+top.x)/2;
		int centerY = (bottom.y+top.y)/2;
		float distx = bottom.x - top.x;
		float disty = bottom.y - top.y;
		distx *=f;
		disty *=f;
		
		top.x = (int) (centerX - distx/2.0);
		top.y = (int) (centerY - disty/2.0);
		bottom.x = (int) (centerX + distx/2.0);
		bottom.y = (int) (centerY + disty/2.0);	
	}
	
	public RectF getRect(){
		return new RectF(top.x, top.y, bottom.x, bottom.y);
	}
	
	public void setTop(int x, int y){
		top.x = x;
		top.y = y;
	}
	public void setBottom(int x, int y){
		bottom.x = x;
		bottom.y = y;
	}
	public Point getTop(){ 
		return top;
	}
	public Point getBottom(){
		return bottom;
	}
	public int getColor(){
		return color;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	
	}
}
