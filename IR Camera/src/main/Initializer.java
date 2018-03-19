package main;
import guisupport.ImgWindow;

import java.awt.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import utils.Config;


public class Initializer {
	// Camera Mats
	public static Mat cameraMat;
	public static Mat cameraMat2;
	
	// Vertically Flipped Mats
	public static Mat flipMat;
	public static Mat flipMat2;
	
	// Transformed Mats
	public static Mat warpedMat;
	public static Mat warpedMat2;

	public static Mat beforeMat;
	public static Mat combinedMat;
	
	public static ImgWindow cameraWindow;
	public static ImgWindow camera2Window;
	
	public static List<IRCamera> cameras = new ArrayList<IRCamera>();

	public static Frame frame;
	public static Frame frame2;
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
		// Camera interface 1
		Initializer initializer = new Initializer();  
		
		initializer.start(Color.BLACK);

		cameraWindow.closeWindow();
		camera2Window.closeWindow();
		
		Callibration callibration = new Callibration(cameras);
		callibration.startCallibration();

		while(true) {
			IRCoordinates camCoords = cameras.get(0).updateCoordinates();
			IRCoordinates camCoords2 = cameras.get(1).updateCoordinates();
			
			if(camCoords.arePointsFound()) {
				// X and Y coordinates are flipped before the camera is flipped on its side
		        Point point = new Point(camCoords.getYCoordinate(0), camCoords.getXCoordinate(0));
		        Core.circle(cameraMat, point, 3, new Scalar(0, 0, 255), -1);
		        Core.flip(cameraMat, flipMat, 0);
		        
		        warpedMat = frame.getProjection();
		        
		        //frame.updateTestFrame(new Point(1023 - camCoords.getYCoordinate(0), camCoords.getXCoordinate(0)));
		        
				System.out.println("Camera 1 " + camCoords.getXCoordinate(0) + ", " + camCoords.getYCoordinate(0));	
			}
			
			if(camCoords2.arePointsFound()) {
				// X and Y coordinates are flipped before the camera is flipped on its side
				Point point = new Point(camCoords2.getYCoordinate(0), camCoords2.getXCoordinate(0));
		        Core.circle(cameraMat2, point, 3, new Scalar(0, 255, 255), -1);
		        Core.flip(cameraMat2, flipMat2, 0);
		        
		        warpedMat2 = frame2.getProjection();

		        //frame2.updateTestFrame(new Point(1023 - camCoords2.getYCoordinate(0), camCoords2.getXCoordinate(0)));
		        
				System.out.println("Camera 2 " + camCoords2.getXCoordinate(0) + ", " + camCoords2.getYCoordinate(0));
			}
			
			List<Mat> src = Arrays.asList(warpedMat2, warpedMat);
			List<Mat> src2 = Arrays.asList(frame.getTestFrame(), frame2.getTestFrame());

			Core.hconcat(src, combinedMat);
			Core.hconcat(src2, beforeMat);
			
			cameraWindow.setImage(combinedMat);
			camera2Window.setImage(beforeMat);
		}
	}

	private void start(Color color){
        cameraWindow = ImgWindow.newWindow("Camera 1");
        camera2Window = ImgWindow.newWindow("Camera 2");

        // TODO, create configuration check	
        int height = Integer.parseInt(Config.getInstance().getProperty("CameraFrameHeight"));
        int width = Integer.parseInt(Config.getInstance().getProperty("CameraFrameWidth"));
        
	    cameraMat = new Mat(height, width, CvType.CV_8UC3);
	    cameraMat2 = new Mat(height, width, CvType.CV_8UC3);
	    
	    flipMat = new Mat(height, width, CvType.CV_8UC3);
	    flipMat2 = new Mat(height, width, CvType.CV_8UC3);
	    
	    warpedMat = new Mat(height, width, CvType.CV_8UC3);
	    warpedMat2 = new Mat(height, width, CvType.CV_8UC3);

	    beforeMat = new Mat(height, width*2, CvType.CV_8UC3);
	    combinedMat = new Mat(height, width*2, CvType.CV_8UC3);
	    
        List<Point> callib = new ArrayList<Point>();
        callib.add(new Point(140.0, 196.0));
        callib.add(new Point(759.0, 167.0));
        callib.add(new Point(788.0, 704.0));
        callib.add(new Point(133.0, 730.0));
        
        List<Point> callib2 = new ArrayList<Point>();
        callib2.add(new Point(74.0, 110.0));
        callib2.add(new Point(738.0, 38.0));
        callib2.add(new Point(800.0, 632.0));
        callib2.add(new Point(135.0, 720.0));
        
		cameras.add(new IRCamera(Config.getInstance().getProperty("Camera1_Name"), Config.getInstance().getProperty("Camera1_Serial"), 19200, callib));
		cameras.add(new IRCamera(Config.getInstance().getProperty("Camera2_Name"), Config.getInstance().getProperty("Camera2_Serial"), 19200, callib2));
		
        frame = new Frame(flipMat, callib, width, height);
        frame2 = new Frame(flipMat2, callib2, width, height);
	}
}