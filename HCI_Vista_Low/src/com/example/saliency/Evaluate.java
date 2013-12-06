package com.example.saliency;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

import android.graphics.Color;
import android.util.Log;
import com.example.hci_vista_low.*;
/*
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import edu.rochester.R;

import java.applet.*; 
import java.awt.Component; 
import java.awt.image.*;  
*/

/*define a class for each .m file*/
public class Evaluate {
	
	/*the following static method is actually doing all the jobs in the evalate.m*/
	public static Selector doEvaluate(Mat imgRGB, int width, int height)
	{	
		Selector select = new Selector();
		int oWidth, oHeight, ow, oh;
		int x1 = 10000;
		int x2 = 0;
		int y1 = 10000;
		int y2 = 0;
		Mat I = new Mat();
		Mat copy = new Mat();
		Feature feature = new Feature();
		Saliency sal = new Saliency();
		
		System.out.println("b1");
		
		try{
		
			imgRGB.copyTo(copy);
			
			System.out.println("b11");
			
			copy.copyTo(feature.originalimage);
			
			System.out.println("b111");
			
			ow = width;
			oh = height;
			
			oWidth = imgRGB.width();
			oHeight= imgRGB.height();
			System.out.println("Orignal"+ oh+ ow);
			//
			//
			//
			
			//
			System.out.println("b2");

			if(oWidth*oHeight > 512*512)
			{
				Imgproc.resize(copy, copy, new Size(Math.round(Math.sqrt(512*512*oWidth/oHeight)), Math.round(Math.sqrt(512*512*oHeight/oWidth))), 0, 0, Imgproc.INTER_CUBIC);		
				//Imgproc.resize(copy,copy,new Size(512,512),0,0,Imgproc.INTER_CUBIC);
				oWidth = copy.width();
				oHeight= copy.height();
				System.out.println("Changing Size"+ oWidth+ oHeight);
			}
			
			System.out.println("b3");
			copy.copyTo(feature.shrinked);
			
			Imgproc.cvtColor(copy, I, Imgproc.COLOR_RGB2GRAY);
			
			
			/*example*/
			
				//Imgproc.GaussianBlur(feature.result, feature.result, new Size(5, 5), 0.5);
			
			//feature.result = I.colRange(100, 200);
			Mat idx = salient(I,sal.Saliencymap);
			System.out.println("Saliency Detect over");
			
			
			int count = 0;
			for (int i = 0;i<oHeight;i++)
			{
				for (int j = 0;j<oWidth;j++)
				{
					count +=  idx.get(i,j)[0];
				}
			}
			System.out.println("count " + count);
			
			
			
			feature.result = I;
			sal.Saliencymap.copyTo(feature.SaliencyMap);
			System.out.println("Saliency size"+ feature.SaliencyMap.height()+ feature.SaliencyMap.width());
			
			count = 0;
			for(int i = 0; i<((int) oHeight*0.9);i++)
			{		
				for(int j = 0; j<((int) oWidth*0.9);j++)
				{	
					if (idx.get(i,j)[0] != 0 && i>oHeight*0.4 && j>oWidth*0.1)
					{
						
						if (i<x1){
							x1 = i;
							}
						if (i>x2){
							x2 = i;
							}
						if (j<y1){
							y1 = j;
							}
						if (j>y2){
							y2 = j;
							}
						count +=1;
					}
				}
			}
			
			System.out.println("count " + count);
			System.out.println("x1"+x1+"x2"+x2+"y1"+y1+"y2"+y2);
			
			
			//System.out.println("For the First time"+x1+x2+y1+y2);
			//System.out.println("Int"+y1+"Orig"+ow+"Changed"+oWidth+"dev"+y1/oWidth+"Double"+(double)y1/oWidth+"Double"+(double)y1*ow/oWidth);
			System.out.println("Orig"+ow);
			y1 = (int) (double)(y1*ow/oWidth);
			y2 = (int) (double)(y2*ow/oWidth);
			x1 = (int) (double)(x1*oh/oHeight);
			x2 = (int) (double)(x2*oh/oHeight);
			
			System.out.println("x1"+x1+"x2"+x2+"y1"+y1+"y2"+y2);


		select = new Selector(y1,x1, y2, x2, Color.RED);  
		
		}
		 catch(Exception e)
         {
           Log.e(e.toString(), null);
         }
		
		return select;					
	}
	
/*
	    public static Saliency slainecydetect (Mat I){
	 
		Saliency sal = new Saliency();
		Mat I_shrink = new Mat();
		Mat F = new Mat();
		Mat IF = new Mat();
		Mat magnitude = new Mat();
		Mat myAngle = new Mat();
		Mat mySpectralResidual = new Mat();
		Mat myLogAmplitude = new Mat();
		Mat saliencyMap = new Mat();
		Mat ab = new Mat();
		//System.out.println((double)64.0/I.height());
		try{
		Imgproc.resize(I, I_shrink, new Size(),(double)64.0/I.cols(),(double)64.0/I.cols(),Imgproc.INTER_LINEAR);
		
		I_shrink.convertTo(I_shrink, CvType.CV_32FC1);
		System.out.println("Starting DFT");
		
		Mat padded = new Mat();
		int m = Core.getOptimalDFTSize(I_shrink.rows());
		int n = Core.getOptimalDFTSize(I_shrink.cols());
		Imgproc.copyMakeBorder(I_shrink, padded, 0, m-I_shrink.rows(), 0, n-I_shrink.cols(), Imgproc.BORDER_CONSTANT,Scalar.all(0));
		padded.convertTo(padded, CvType.CV_32FC1);	

		System.out.println("Start merging"); 
		
	//	Core.dft(I_shrink, F);
	//	System.out.println("F is " + F.channels());
	//	double[] check = F.get(0, 0);
	//	for (int i = 0;i<check.length;i++)
	//	System.out.println(check[i]);

		List<Mat> planes = new ArrayList<Mat>();
		planes.add(padded);
		planes.add(Mat.zeros(padded.size(), CvType.CV_32FC1));
		Core.merge(planes, F);
		System.out.println("Merge complete");
		
		
		Core.dft(F, F);
		
		Core.split(F, planes);
		
		//Core.magnitude(planes.get(0), planes.get(1), magnitude);
		System.out.println("DFT Finished");
		Core.split(F, planes);
		Core.magnitude(planes.get(0), planes.get(1), magnitude);
		
		//Core.absdiff(F, Scalar.all(0), magnitude);
		
		//Core.add(magnitude, Scalar.all(1),magnitude);
		Core.log(magnitude, myLogAmplitude);
		System.out.println("Magnitude Get");
		
		Core.phase(planes.get(1), planes.get(0), myAngle);
		//Core.phase(F, F, myAngle, false);
		
		//Mat average = Mat.ones(3,3,CvType.CV_32FC1);
		//average.mul( Mat.ones(3,3,CvType.CV_32FC1), (double)1/9);	
		//System.out.println("Check for Average Point One "+average.get(0, 0)[0]);
		Imgproc.blur(myLogAmplitude, mySpectralResidual, new Size(3,3),new Point(-1, -1), Imgproc.BORDER_REPLICATE);
		//Imgproc.filter2D(myLogAmplitude, mySpectralResidual, -1, average);
		//mySpectralResidual = myLogAmplitude + mySpectralResidual;
		
		System.out.println("myMagnitude "+magnitude.get(13, 13)[0]);
		System.out.println("myLogMagnitude "+myLogAmplitude.get(13, 13)[0]);
		System.out.println("mySpectralResidual "+mySpectralResidual.get(13, 13)[0]);
		
		Core.subtract(myLogAmplitude, mySpectralResidual, mySpectralResidual);
		System.out.println("mySpectralResidual "+mySpectralResidual.get(13, 13)[0]);
		
		Mat tmp = new Mat();
		Mat tmp2 = new Mat();
		mySpectralResidual.copyTo(tmp);
		myAngle.copyTo(tmp2);
		double temp;
		
		for (int i =0; i<mySpectralResidual.rows(); i++)
		{
			for (int j = 0; j< mySpectralResidual.cols();j++)
			{
				temp = Math.exp(mySpectralResidual.get(i, j)[0])+Math.cos(myAngle.get(i, j)[0]);
				tmp.put(i, j, temp);
				temp = Math.exp(mySpectralResidual.get(i, j)[0])+Math.sin(myAngle.get(i, j)[0]);
				tmp2.put(i, j, temp);
			}
		}
		
		planes.clear();
		planes.add(tmp);
		planes.add(tmp2);
		
		
		
		Core.merge(planes, IF);
		System.out.println("mySpectralResidual "+mySpectralResidual.get(13, 13)[0]);
		System.out.println("myAngle "+myAngle.get(13, 13)[0]);
		System.out.println("tmp "+tmp.get(13, 13)[0]);
		System.out.println("Second Merge complete");
		//Core.exp(IF, IF);
		
		//IF.convertTo(IF, CvType.CV_32FC1);
		System.out.println("Exp complete");
		//Core.idft(IF, saliencyMap);
		Core.dft(IF, saliencyMap,Core.DFT_INVERSE|Core.DFT_COMPLEX_OUTPUT|Core.DFT_SCALE,0);
		//Core.dft(IF, saliencyMap, Core.DFT_INVERSE, 0);
		System.out.println("IDFT complete");
		planes.clear();
		Core.split(saliencyMap, planes);
		Core.magnitude(planes.get(0), planes.get(1), saliencyMap);
		Core.pow(saliencyMap, 2, saliencyMap);
		Imgproc.GaussianBlur(saliencyMap, saliencyMap,new Size(9,9), 2.5);
		//Core.normalize(saliencyMap, saliencyMap, 0, 1,Core.NORM_MINMAX);
		
		myLogAmplitude.copyTo(saliencyMap);
		Imgproc.resize(saliencyMap, saliencyMap, new Size(I.width() , I.height()));

		saliencyMap.convertTo(saliencyMap, CvType.CV_8UC1);
		} catch (Exception e) {
			 Log.e(e.toString(), null);
		};
		//myLogAmplitude.copyTo(sal.Saliencymap);
		//mySpectralResidual.copyTo(sal.Saliencymap);
	//	mySpectralResidual.copyTo(sal.Saliencymap);
		
		saliencyMap.copyTo(sal.Saliencymap);
		return sal;
		
	}

*/	
	
public static Mat salient(Mat I,Mat saliencyMap){
	Mat imgInput = new Mat();
	Mat fft = new Mat();
	Mat ifft = new Mat();
	Mat magnitude = new Mat();
	Mat phase = new Mat();
	Mat spectralResidual = new Mat();
	Mat magnitudeLog = new Mat();
	Mat angle = new Mat();
	//Mat saliencyMap = new Mat();
	int oWidth = I.width();
	int oHeight = I.height();
	System.out.println("MATI"+ oWidth+ oHeight);
	
	Imgproc.resize(I, imgInput, new Size(),(double)256.0/I.cols(),(double)256.0/I.cols(),Imgproc.INTER_LINEAR);
	
	imgInput.convertTo(imgInput, CvType.CV_32FC1);
	//toFile(imgInput, "imgInput");
	
	Mat padded = new Mat(imgInput.rows(), imgInput.cols(), CvType.CV_32FC1, Scalar.all(0));
	//int m = Core.getOptimalDFTSize(imgInput.rows());
	//int n = Core.getOptimalDFTSize(imgInput.cols());
	//Imgproc.copyMakeBorder(imgInput, padded, 0, m-imgInput.rows(), 0, n-imgInput.cols(), Imgproc.BORDER_CONSTANT,Scalar.all(0));
	//padded.convertTo(padded, CvType.CV_32FC1);	
	
	// merge
	List<Mat> planes = new ArrayList<Mat>();
	planes.add(imgInput);
	planes.add(padded);
	//planes.add(padded);
	//planes.add(Mat.zeros(padded.size(), CvType.CV_32FC1));
	Core.merge(planes, fft);
	
	// dft
	Core.dft(fft, fft);
	
	
	// *********** DEBUG *****************
	//Core.dft(fft, magnitudeLog, Core.DFT_INVERSE|Core.DFT_REAL_OUTPUT|Core.DFT_SCALE, 0);
	//System.out.println(magnitudeLog.channels());
	//toFile(magnitudeLog, "imgBack");
	//Highgui.imwrite("image/magnitude.jpg", magnitudeLog);
	// ************** END DEBUG ********************
	
	
	Core.split(fft, planes);
	Core.magnitude(planes.get(0), planes.get(1), magnitude);
	Core.add(magnitude, Scalar.all(1), magnitude);
	Core.log(magnitude, magnitudeLog);		
	
	// get phase
	Core.phase(planes.get(0), planes.get(1), angle);
	
	Imgproc.blur(magnitudeLog, spectralResidual, new Size(3.0, 3.0), new Point(-1, -1), Imgproc.BORDER_REPLICATE);

	Core.subtract(magnitudeLog, spectralResidual, spectralResidual);
	
	//toFile(average, "filter_average");

	// merge for idft
	planes.clear();
	planes.add(spectralResidual);
	planes.add(angle);

	//Core.merge(planes, ifft);
	//Core.exp(ifft, ifft);
	Core.exp(planes.get(0), planes.get(0));
	Mat tmp0 = planes.get(0).clone();
	Mat tmp1 = planes.get(1).clone();
	cosMat(planes.get(1));
	Core.multiply(planes.get(0), planes.get(1), planes.get(0));
	sinMat(tmp1);
	Core.multiply(tmp0, tmp1, planes.get(1));
	Core.merge(planes, ifft);
	
	System.out.println(ifft.get(5, 5)[0] + " " + ifft.get(5, 5)[1]);
	System.out.println(planes.get(0).get(5, 5)[0]);
	System.out.println(planes.get(1).get(5, 5)[0]);
	Core.dft(ifft, ifft, Core.DFT_INVERSE|Core.DFT_COMPLEX_OUTPUT|Core.DFT_SCALE, 0);
	Core.split(ifft, planes);

	Core.magnitude(planes.get(0), planes.get(1), saliencyMap);
	// saliency map
	Core.pow(saliencyMap, 2.0, saliencyMap);
	Imgproc.GaussianBlur(saliencyMap, saliencyMap,new Size(9,9), 2.5);
	
	//Imgproc.filter2D(saliencyMap, saliencyMap, -1, Imgproc.getGaussianKernel(19, 3.5));
	Imgproc.resize(saliencyMap, saliencyMap, new Size(oWidth,oHeight));
	MinMaxLocResult minmax = Core.minMaxLoc(saliencyMap);
	double min = minmax.minVal;
	double max = minmax.maxVal;
	System.out.println("saliencyMap min: " + min);
	System.out.println("saliencyMap max: " + max);
	Core.multiply(saliencyMap, Scalar.all(255/max), saliencyMap);

	Mat idx = myKmeans(saliencyMap);
	return idx;
}


public static void sinMat(Mat mat) {
	for (int ii=0; ii<mat.rows(); ii++) {
		for (int jj=0; jj<mat.cols(); jj++) {
			mat.put(ii, jj, Math.sin(mat.get(ii, jj)[0]));
		}
	}
}

public static void cosMat(Mat mat) {
	for (int ii=0; ii<mat.rows(); ii++) {
		for (int jj=0; jj<mat.cols(); jj++) {
			mat.put(ii, jj, Math.cos(mat.get(ii, jj)[0]));
		}
	}
}

public static Mat myKmeans(Mat saliencyMap) {
	Mat ab = new Mat();
	int oWidth, oHeight;
	oWidth = saliencyMap.width();
	oHeight= saliencyMap.height();
	ab = saliencyMap.reshape (0,oWidth*oHeight);
	ab.convertTo(ab, CvType.CV_32FC1);
	// K means starts here
	
	  Mat labels = new Mat();
	 int clustercount;
	  Mat centers = new Mat();
	  TermCriteria criteria = new TermCriteria(TermCriteria.MAX_ITER|TermCriteria.EPS,15,0.01);
	Core.kmeans(ab, 3, labels, criteria, 5, Core.KMEANS_RANDOM_CENTERS,centers);
	
	ab = labels.reshape(0, oHeight);
	
	Mat MostSalient = new Mat();
	Mat MediumSalient = new Mat();
	Mat LeastSalient = new Mat();

	ab.convertTo(ab, CvType.CV_32FC1);
	Imgproc.threshold(ab, MostSalient, 1.0, 1.0, Imgproc.THRESH_BINARY);
	Imgproc.threshold(ab, MediumSalient, 0, 1.0, Imgproc.THRESH_BINARY);
	Core.subtract(MediumSalient, MostSalient, MediumSalient);
	Imgproc.threshold(ab, LeastSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);

	
	Mat check1 = new Mat();
	Mat check2 = new Mat();
	Mat check3 = new Mat();
	int check1count,check2count,check3count;
	
	Core.multiply(saliencyMap, MostSalient,check1);
	Core.multiply(saliencyMap, MediumSalient,check2);
	Core.multiply(saliencyMap, LeastSalient,check3);
	/*
	check1count = Core.countNonZero(check1);
	check2count = Core.countNonZero(check2);
	check3count = Core.countNonZero(check3);
*/

	check1count = Core.countNonZero(MostSalient);
	check2count = Core.countNonZero(MediumSalient);
	check3count = Core.countNonZero(LeastSalient);
	
	double check1value = 0;
	for (int i = 0;i<oHeight;i++)
	{
		for (int j = 0;j<oWidth;j++)
		{
			if (check1.get(i,j)[0] > 0)
					{
					check1value += saliencyMap.get(i,j)[0];
					}
		}
	}
	check1value = check1value/check1count;
	
	double check2value = 0;
	for (int i = 0;i<oHeight;i++)
	{
		for (int j = 0;j<oWidth;j++)
		{
			if (check2.get(i,j)[0] > 0)
					{
					check2value += saliencyMap.get(i,j)[0];
					}
		}
	}
	check2value = check2value/check2count;
	
	double check3value = 0;
	for (int i = 0;i<oHeight;i++)
	{
		for (int j = 0;j<oWidth;j++)
		{
			if (check3.get(i,j)[0] > 0)
					{
					check3value += saliencyMap.get(i,j)[0];
					}
		}
	}
	check3value = check3value/check3count;
	Mat tmp = new Mat();
	if (Math.max(check1value, Math.max(check2value,check3value)) == check1value)
	{		
	if (check2value < check3value)
	{
		//label 2.0 for most, 1.0 for least, 0 for medium
		Imgproc.threshold(ab, MediumSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);
		Imgproc.threshold(ab, LeastSalient, 0, 1.0, Imgproc.THRESH_BINARY);
		Core.subtract(LeastSalient, MostSalient, MediumSalient);
		
	}
	}else
	{
		if (check2value > check3value)
		{
			if (check1value > check3value){
				// label 2.0 for medium, 1.0 for most and 0 for least
			Imgproc.threshold(ab, MediumSalient, 1.0, 1.0, Imgproc.THRESH_BINARY_INV);
			Imgproc.threshold(ab, MostSalient, 0, 1.0, Imgproc.THRESH_BINARY);
			Core.subtract(MostSalient, MediumSalient, MediumSalient);
			Imgproc.threshold(ab, LeastSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);		
			}else{
				//label 2.0 for least, 1.0 for most, 0 for medium
				Imgproc.threshold(ab, LeastSalient, 1.0, 1.0, Imgproc.THRESH_BINARY_INV);
				Imgproc.threshold(ab, MostSalient, 0, 1.0, Imgproc.THRESH_BINARY);
				Core.subtract(MostSalient, LeastSalient, MediumSalient);
				Imgproc.threshold(ab, MediumSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);		
		}
		}else{
			if (check1value > check3value){
				// label 2.0 for medium, 1.0 for least and 0 for most
			Imgproc.threshold(ab, MediumSalient, 1.0, 1.0, Imgproc.THRESH_BINARY_INV);
			Imgproc.threshold(ab, LeastSalient, 0, 1.0, Imgproc.THRESH_BINARY);
			Core.subtract(LeastSalient, MediumSalient, MediumSalient);
			Imgproc.threshold(ab, MostSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);		
			}else{
				//label 2.0 for least, 1.0 for medium, 0 for most
				Imgproc.threshold(ab, LeastSalient, 1.0, 1.0, Imgproc.THRESH_BINARY_INV);
				Imgproc.threshold(ab, MediumSalient, 0, 1.0, Imgproc.THRESH_BINARY);
				Core.subtract(MediumSalient, LeastSalient, MediumSalient);
				Imgproc.threshold(ab, MostSalient, 0, 1.0, Imgproc.THRESH_BINARY_INV);		
		}
		}
			
	}
	

	int count = 0;
	System.out.println("Hight"+oHeight+"length"+oWidth);
	for (int i = 0;i<oHeight;i++)
	{
		for (int j = 0;j<oWidth;j++)
		{
			count +=  MostSalient.get(i,j)[0];
		}
	}
	System.out.println("count " + count);
	return MostSalient;
	
}
}

class Saliency {
	static Mat Saliencymap = new Mat();
}
