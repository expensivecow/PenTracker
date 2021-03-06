package com.digitalglass.main.callibration;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.digitalglass.main.gui.ImgWindow;
import com.digitalglass.main.infrared.IRCamera;
import com.digitalglass.main.infrared.IRCoordinates;
import com.digitalglass.main.infrared.IRPoint;
import com.digitalglass.main.utils.Config;


public class Callibration {
	private static final int NUM_CAMERA_POINTS_REQUIRED = 4;
	List<IRCamera> callibrationCameras;
	Mat cameraMat;
	Mat flipMat;
	Mat tempColorMat;
	ImgWindow callibrationWindow;
	
	public Callibration(List<IRCamera> cameras) {
		callibrationCameras = cameras;

        // TODO, create configuration check	
        int height = Integer.parseInt(Config.getInstance().getProperty("CameraFrameHeight"));
        int width = Integer.parseInt(Config.getInstance().getProperty("CameraFrameWidth"));

	    cameraMat = new Mat(height, width, CvType.CV_8UC3);
	    tempColorMat = new Mat(height, width, CvType.CV_8UC3);
	    
	    tempColorMat.setTo(new Scalar(0, 255, 0));
	    flipMat = new Mat(height, width, CvType.CV_8UC3);
	}
	
	public void startCallibration() throws FileNotFoundException, IOException {
		System.out.println("Starting Callibration");
		
		callibrationWindow = ImgWindow.newWindow("Callibration Window");
		for(IRCamera camera : callibrationCameras) {
			System.out.println("Callibrating " + camera.getCameraName());
			callibrateSingleCamera(camera);
		}
	}
	
	private void callibrateSingleCamera(IRCamera camera) throws FileNotFoundException, IOException {
		IRCoordinates coordinates = camera.getCurrentCoordinates();
		IRCoordinates prevCoordinates = null;
        
		while (coordinates.getNumPointsFound() != NUM_CAMERA_POINTS_REQUIRED) {
			System.out.println(coordinates.toString());
			
			for (int i = 0; i < coordinates.getNumPointsFound(); i++) {
				Point point = new Point(coordinates.getYCoordinate(i), coordinates.getXCoordinate(i));
		        Core.circle(cameraMat, point, 7, new Scalar(0, 0, 255), -1);
			}
			
			Core.flip(cameraMat, flipMat, 0);
			callibrationWindow.setImage(flipMat);
			
			
			coordinates = camera.getCurrentCoordinates();
		}		
		
		coordinates.translateCoordinates();
		coordinates.orderPoints();
		saveCallibration(camera, coordinates);
		System.out.println(coordinates.toString());
		System.out.println("4 points found!");
	}
	
	public void saveCallibration(IRCamera camera, IRCoordinates coordinates) throws FileNotFoundException, IOException {
		List<IRPoint> pointsToSave = coordinates.getIRPoints();
		
		for(int i = 0; i < 4; i++) {
			Config.getInstance().saveProperty(camera.getCameraName() + "_x" + Integer.toString(i), Double.toString(pointsToSave.get(i).getPoint().x));
			Config.getInstance().saveProperty(camera.getCameraName() + "_y" + Integer.toString(i), Double.toString(pointsToSave.get(i).getPoint().y));
		}
		
		return;
	}
}
