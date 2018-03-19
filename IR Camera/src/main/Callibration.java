package main;

import guisupport.ImgWindow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import utils.Config;

public class Callibration {
	private static final int NUM_CAMERA_POINTS_REQUIRED = 4;
	List<IRCamera> callibrationCameras;
	Mat cameraMat;
	Mat flipMat;
	ImgWindow callibrationWindow;
	
	public Callibration(List<IRCamera> cameras) {
		callibrationCameras = cameras;

        // TODO, create configuration check	
        int height = Integer.parseInt(Config.getInstance().getProperty("CameraFrameHeight"));
        int width = Integer.parseInt(Config.getInstance().getProperty("CameraFrameWidth"));
        
	    cameraMat = new Mat(height, width, CvType.CV_8UC3);
	    flipMat = new Mat(height, width, CvType.CV_8UC3);
	}
	
	public void startCallibration() throws FileNotFoundException, IOException {
		System.out.println("Starting Callibration");
		
		callibrationWindow = ImgWindow.newWindow("Callibration Window");
		for(IRCamera camera : callibrationCameras) {
			callibrateSingleCamera(camera);
		}
	}
	
	private void callibrateSingleCamera(IRCamera camera) throws FileNotFoundException, IOException {
		IRCoordinates coordinates = camera.updateCoordinates();
		IRCoordinates prevCoordinates = null;
        
		while (coordinates.getNumPointsFound() != NUM_CAMERA_POINTS_REQUIRED) {
			for (int i = 0; i < coordinates.getNumPointsFound(); i++) {
				Point point = new Point(coordinates.getYCoordinate(i), coordinates.getXCoordinate(i));
		        Core.circle(cameraMat, point, 3, new Scalar(0, 0, 255), -1);
			}
			
			Core.flip(cameraMat, flipMat, 0);
			callibrationWindow.setImage(flipMat);
			
			coordinates = camera.updateCoordinates();
		}		
		
		coordinates.orderPoints();
		saveCallibration(camera, coordinates);
		System.out.println(coordinates.toString());
		System.out.println("4 points found!");
	}
	
	public void saveCallibration(IRCamera camera, IRCoordinates coordinates) throws FileNotFoundException, IOException {
		List<IRPoint> pointsToSave = coordinates.getIRPoints();
		
		for(int i = 0; i < 4; i++) {
			Config.getInstance().saveProperty(camera.getName() + "_x" + Integer.toString(i), Double.toString(pointsToSave.get(i).getPoint().x));
			Config.getInstance().saveProperty(camera.getName() + "_y" + Integer.toString(i), Double.toString(pointsToSave.get(i).getPoint().y));
		}
		
		return;
	}
}
