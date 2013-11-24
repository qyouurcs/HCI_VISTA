package com.example.hci_vista_low;

import android.graphics.Point;


public class Selector {
	private Point top;
	private Point bottom;
	
	public Selector(){
		top = new Point();
		bottom = new Point();
	}
	public Selector(int x1, int y1, int x2, int y2){
		top = new Point(x1, y1);
		bottom = new Point(x2, y2);
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
}
