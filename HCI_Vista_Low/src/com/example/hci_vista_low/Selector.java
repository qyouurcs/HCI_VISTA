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
	
	// constructors
	public Selector(){
		top = new Point();
		bottom = new Point();
		Random rnd = new Random(); 
	}
	
	public Selector(int x1, int y1, int x2, int y2, int color){
		top = new Point(x1, y1);
		bottom = new Point(x2, y2);
		this.color = color;
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
