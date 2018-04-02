package main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import utils.Config;

public class Board {
	// Camera Mats
	private List<Mat> cameraMats;
	private List<Mat> flipMats;
	private List<Mat> warpedMats;
	private List<Frame> frames;
	private List<IRCoordinates> coordinates;
	private Scalar currentColor;
	
	private List<List<Point>> callibPerCamera;
	
	private Mat imageBeforeScale;

	private List<IRCamera> cameras;
	private int numCameras;
	private int thickness;
	
	public Board(int numCameras, Scalar keyColor, Scalar defaultColor, int defaultThickness) {
		this.numCameras = numCameras;
		
		thickness = defaultThickness;
		currentColor = defaultColor;
		
        int height = Integer.parseInt(Config.getInstance().getProperty("CameraFrameHeight"));
        int width = Integer.parseInt(Config.getInstance().getProperty("CameraFrameWidth"));

        // Initialize Lists that holds Mat Objects
        cameraMats = new ArrayList<Mat>();
        flipMats = new ArrayList<Mat>();
        warpedMats = new ArrayList<Mat>();
        
        // Initialize List of frames
        frames = new ArrayList<Frame>();
        
        // Initialize List that holds callibration points per camera
        callibPerCamera = new ArrayList<List<Point>>();
        
        // Initialize List that holds cameras
        cameras = new ArrayList<IRCamera>();
        
        // Initialize camera mat height/width and keyed color
        for(int i = 0; i < numCameras; i++) {
        	cameraMats.add(new Mat(height, width, CvType.CV_8UC3));
        	flipMats.add(new Mat(height, width, CvType.CV_8UC3));
        	warpedMats.add(new Mat(height, width, CvType.CV_8UC3));
        }
        
        // Initialize List of coordinates used to detect points for each camera
        coordinates = new ArrayList<IRCoordinates>();
        
        // TODO: Create a list of callibration from configuration
        List<Point> callib = new ArrayList<Point>();
        callib.add(new Point(288.0, 232.0));
        callib.add(new Point(714.0, 373.0));
        callib.add(new Point(497.0, 995.0));
        callib.add(new Point(66.0, 825.0));
        
        List<Point> callib2 = new ArrayList<Point>();
        callib2.add(new Point(116.0, 161.0));
        callib2.add(new Point(579.0, 153.0));
        callib2.add(new Point(590.0, 798.0));
        callib2.add(new Point(125.0, 813.0));
        
        callibPerCamera.add(callib);
        callibPerCamera.add(callib2);
        
        for(int i = 0; i < numCameras; i++) {
        	frames.add(new Frame(flipMats.get(i), callibPerCamera.get(i), width, height));
        }
        
        // Initialize cameras with callibration points
        for(int i = 0; i < numCameras; i++) {
        	cameras.add(new IRCamera(Config.getInstance().getProperty("Camera" + Integer.toString(i+1) + "_Name"), Config.getInstance().getProperty("Camera" + Integer.toString(i+1) + "_Serial"), 19200));
        }

        imageBeforeScale = new Mat(height, width*numCameras, CvType.CV_8UC3);
        
        // Set all mats to the default color
        imageBeforeScale.setTo(keyColor);
        
        for(int i = 0; i < numCameras; i++) {
        	cameraMats.get(i).setTo(keyColor);
        	flipMats.get(i).setTo(keyColor);
        	warpedMats.get(i).setTo(keyColor);
        }
	}
	
	public void updateImage() {
		for(int i = 0; i < numCameras; i++) {
			IRCoordinates currCoordinates = cameras.get(i).updateCoordinates();
			
			if(currCoordinates.arePointsFound()) {
				Point point = new Point(currCoordinates.getYCoordinate(0), currCoordinates.getXCoordinate(0));
				
				System.out.println(cameras.get(i).getName() + " point found: " + point.x + ", " + point.y);
				
				Core.circle(cameraMats.get(i), point, thickness, currentColor, -1);
				Core.flip(cameraMats.get(i), flipMats.get(i), 0);
				
				warpedMats.set(i, frames.get(i).getProjection());
			}
		}
		Core.hconcat(warpedMats, imageBeforeScale);
	}
	
	public Mat getImage() {
		return imageBeforeScale;
	}
}
