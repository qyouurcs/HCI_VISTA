package com.example.hci_vista_low;

import java.util.Random;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;


public class Selector {
	private Point top;
	private Point bottom;
	private int color = 0;
	
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
	
	public boolean inBound(int x, int y){
		if( x >= top.x && x <= bottom.x && y >= top.y && y <= bottom.y )
			return true;
		else
			return false;
	}
	
	public void move(int x, int y){
		top.y -= y;
		top.x -= x;
		bottom.y -= y;
		bottom.x -= x;
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
}
