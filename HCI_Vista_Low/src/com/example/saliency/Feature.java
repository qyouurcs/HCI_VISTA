package com.example.saliency;

import org.opencv.core.Mat;


public class Feature{
	
		Mat originalimage;
		Mat SaliencyMap;
		Mat shrinked;
		Mat result;
		/* whatever features you want to add*/
		
		public Feature()
		{
			originalimage = new Mat();
			shrinked = new Mat();
			result = new Mat();
			SaliencyMap = new Mat();
			
			/* here initialize all your features declared above*/
		}
}