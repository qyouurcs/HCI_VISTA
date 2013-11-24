package com.example.hci_vista_low;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.example.hci_vista_low.Selector;

public class ListOfSelectors {
	private List selectors;
	private Point tempP;
	private boolean twoPoints = false;
	
	public ListOfSelectors(){
		selectors = new ArrayList();
		twoPoints = false;
	}
	
	public void addPoint(Point p){
		if(twoPoints == false){
			tempP = p;
			twoPoints = true;
		}
		else{
			int minX, maxX, minY, maxY;
			if( p.x < tempP.x ){
				minX = p.x;
				maxX = tempP.x;
			}else{
				maxX = p.x;
				minX = tempP.x;
			}
			
			if( p.y < tempP.y ){
				minY = p.y;
				maxY = tempP.y;
			}else{
				maxY = p.y;
				minY = tempP.y;
			}		
			this.selectors.add(new Selector(minX, minY, maxX, maxY));
			twoPoints = false;
		}
	}
	
	public void addSelector(Selector s){
		selectors.add(s);
	}
	
	public void drawSelectors(Canvas canvas, Paint paint){
		Iterator<Selector> itr = selectors.iterator();
	    while(itr.hasNext()) {
	    	Selector p = itr.next();
	    	canvas.drawRect(p.getTop().x, p.getTop().y, p.getBottom().x, p.getBottom().y, paint); 
	    }
	}
}
