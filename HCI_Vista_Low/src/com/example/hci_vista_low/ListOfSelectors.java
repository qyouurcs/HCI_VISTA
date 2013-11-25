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
import android.graphics.RectF;
import android.util.Log;

import com.example.hci_vista_low.Selector;

public class ListOfSelectors {
	private List selectors;
	private int moveCount = 0;
	private Point tempP, moveP;
	private boolean twoPoints = false;
	private boolean movingPoints = false;
	
	public ListOfSelectors(){
		selectors = new ArrayList();
		twoPoints = false;
		movingPoints = false;
		moveCount = 0;
	}
	
	public void movingPoint(Point p){
		if(moveCount++ > 5){
			movingPoints = true;
			moveP = p;
		}
	}
	
	public void addMovingPoint(Point p){
		if( movingPoints == true && moveCount > 5){
			this.addPoint(p);
		}
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
			int absDist = (maxX-minX) + (maxY-minY);
			
			if(this.movingPoints == false){
				this.selectors.add(new Selector(minX, minY, maxX, maxY));
				twoPoints = false;
				movingPoints = false;
				tempP = null;
			}else if(this.movingPoints == true && absDist >= 20){
				this.selectors.add(new Selector(minX, minY, maxX, maxY));
				movingPoints = false;
				twoPoints = false;
			}
			moveCount = 0;
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
	    
	    if(twoPoints == true){
	    	canvas.drawPoint(tempP.x, tempP.y, paint);
	    }
	    
	    if(movingPoints == true){
	    	RectF r = new RectF(Math.min(tempP.x, moveP.x), Math.min(tempP.y, moveP.y), 
	    			Math.max(tempP.x, moveP.x), Math.max(tempP.y, moveP.y));
	    	canvas.drawRect(r, paint);
	    }
	}
}
