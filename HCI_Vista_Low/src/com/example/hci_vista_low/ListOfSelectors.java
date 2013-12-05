package com.example.hci_vista_low;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.example.hci_vista_low.Selector;

public class ListOfSelectors {
	// maximium number of bounding boxes
	private final static int MaxBoxs = 2;
	private static boolean isEditable = true;
	private static boolean isAddable = true;
	private List<Selector> selectors;
	private int moveCount = 0;
	private Point tempP, moveP;
	
	private boolean twoPoints = false;
	private Random rnd = new Random(); 
	private int color;

	public void setIsAddable(boolean v){
		isAddable = v;
	}
	public void flipAddable(){
			isAddable = !isAddable;
	}
	public ListOfSelectors(){
		selectors = new ArrayList<Selector>();
		twoPoints = false;
	}
	public List<Selector> getSelectors()
	{
		return selectors;
	}
	public void clear()
	{
		selectors.clear();
	}
	
	public int size()
	{
		return selectors.size();
	}

	public void addSelector(RectF r, int color){
		this.addSelector(new Selector( (int) r.left, (int) r.top, (int) r.right, (int) r.bottom, color));
	}
	
	
	public void addPoint(Point p){
		if(isAddable == false)
			return;
		
		if(twoPoints == false){
			color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)); 
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
			this.selectors.add(new Selector(minX, minY, maxX, maxY, color));
			twoPoints = false;
			tempP = null;

		}
	}
	
	public void addSelector(Selector s){
		selectors.add(s);
	}
	
	public Selector getSelector(int x, int y){
		Iterator<Selector> itr = selectors.iterator();
	    while(itr.hasNext()) {
	    	Selector p = itr.next();
	    	if(p.inBound(x, y))
	    		return p;
	    }
	    return null;
	}
	
	public void drawSelectors(Canvas canvas, Paint paint){
		Iterator<Selector> itr = selectors.iterator();
	    while(itr.hasNext()) {
	    	Selector p = itr.next();
	    	paint.setColor(p.getColor());
	    	canvas.drawRect(p.getTop().x, p.getTop().y, p.getBottom().x, p.getBottom().y, paint); 
	    }
	    
	    if(twoPoints == true){  
			paint.setColor(color);
	    	canvas.drawPoint(tempP.x, tempP.y, paint);
	    }
	}
	public void remove(Selector s) {
		selectors.remove(s);
		s.setVisible(false);
	}
}
