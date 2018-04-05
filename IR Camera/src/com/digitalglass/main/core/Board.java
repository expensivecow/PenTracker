package com.digitalglass.main.core;

import java.util.ArrayList;
import java.util.List;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.digitalglass.main.infrared.IRCamera;
import com.digitalglass.main.infrared.IRCoordinates;
import com.digitalglass.main.utils.Config;


public class Board {
	// Mats used to transform the original image to a flattened artifact
	private List<Mat> cameraMats;
	private List<Mat> flipMats;
	private List<Mat> warpedMats;

	// Image of the merged camera frames before scaling
	private Mat imageBeforeScale;
	
	// Image after scaling to 1536x1080
	private Mat imageAfterScale;
	
	// Final image fit to 1920x1080
	private Mat resultingImage;
	
	private List<Frame> frames;
	// Drawing Characteristics
	private Scalar currentColor;
	private int thickness;

	private List<List<Point>> callibPerCamera;

	private int scaleW;
	private int scaleH;
	private int resolutionW;
	private int resolutionH;
	
	private boolean drawFlag = false;
	private List<Point> prevPoints;
	
	// Temporary line list until line is established
	private List<List<Point>> tempLineList;
	
	private List<IRCamera> cameras;
	private int numCameras;

	public Board(int numCameras, Scalar keyColor, Scalar defaultColor, int defaultThickness) {
		this.numCameras = numCameras;

		thickness = defaultThickness;
		currentColor = defaultColor;

		scaleW = Integer.parseInt(Config.getInstance().getProperty("ScaleWidth"));
		scaleH = Integer.parseInt(Config.getInstance().getProperty("ScaleHeight"));
		
		resolutionW = Integer.parseInt(Config.getInstance().getProperty("ResolutionWidth"));
		resolutionH = Integer.parseInt(Config.getInstance().getProperty("ResolutionHeight"));
		
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
		for (int i = 0; i < numCameras; i++) {
			cameraMats.add(new Mat(height, width, CvType.CV_8UC3));
			flipMats.add(new Mat(height, width, CvType.CV_8UC3));
			warpedMats.add(new Mat(height, width, CvType.CV_8UC3));
		}

		new ArrayList<IRCoordinates>();

		// Initialize cameras
		for (int i = 0; i < numCameras; i++) {
			cameras.add(new IRCamera(Config.getInstance().getProperty(
					"Camera" + Integer.toString(i + 1) + "_Name"), Config
					.getInstance().getProperty(
							"Camera" + Integer.toString(i + 1) + "_Serial"),
					19200));
		}

		for (int i = 0; i < numCameras; i++) {
			List<Point> callib = new ArrayList<Point>();
			for (int j = 0; j < 4; j++) {
				double x = Double.parseDouble(Config.getInstance().getProperty(
						cameras.get(i).getCameraName() + "_x" + j));
				double y = Double.parseDouble(Config.getInstance().getProperty(
						cameras.get(i).getCameraName() + "_y" + j));

				callib.add(new Point(x, y));
			}

			callibPerCamera.add(callib);
			frames.add(new Frame(flipMats.get(i), callib, width, height));
		}

		imageBeforeScale = new Mat(height, width * numCameras, CvType.CV_8UC3);
		imageAfterScale = new Mat(scaleH, scaleW, CvType.CV_8UC3);
		resultingImage = new Mat(resolutionH, resolutionW, CvType.CV_8UC3);
		
		// Set all mats to the default color
		imageBeforeScale.setTo(keyColor);
		imageAfterScale.setTo(keyColor);
		resultingImage.setTo(keyColor);

		for (int i = 0; i < numCameras; i++) {
			cameraMats.get(i).setTo(keyColor);
			flipMats.get(i).setTo(keyColor);
			warpedMats.get(i).setTo(keyColor);
		}
		
		tempLineList = new ArrayList<List<Point>>();
		
		prevPoints = new ArrayList<Point>();
		currPoints = new ArrayList<Point>();
		for (int i = 0; i < numCameras; i++) {
			prevPoints.add(null);
			tempLineList.add(new ArrayList<Point>());
		}
	}

	public void updateImage() {
		for (int i = 0; i < numCameras; i++) {
			IRCoordinates currCoordinates = cameras.get(i).getCurrentCoordinates();

			List<Point> currPointList = tempLineList.get(i);
			
			if (currCoordinates.arePointsFound() && drawFlag == true) {
				Point currPoint = new Point(currCoordinates.getYCoordinate(0),
						currCoordinates.getXCoordinate(0));
				
				Point prevPoint = prevPoints.get(i);
				
				if (prevPoint == null) {
					Core.circle(cameraMats.get(i), currPoint, thickness, currentColor, -1);
				}
				else {
					Core.line(cameraMats.get(i), prevPoint, currPoint, currentColor, thickness);
				}
				
				currPointList.add(currPoint);
				
				Core.flip(cameraMats.get(i), flipMats.get(i), 0);

				warpedMats.set(i, frames.get(i).getProjection());
			}
			else {
				prevPoints.set(i, null);
				currPointList.clear();
			}
		}
		
		Core.hconcat(warpedMats, imageBeforeScale);
		Imgproc.resize(imageBeforeScale, imageAfterScale, imageAfterScale.size(), 0, 0, Imgproc.INTER_LINEAR);
		
		imageAfterScale.copyTo(resultingImage.rowRange((resolutionH-scaleH)/2, (resolutionH-scaleH)/2 + scaleH).colRange((resolutionW-scaleW)/2, (resolutionW-scaleW)/2 + scaleW));
	}
	
	public void fillFrames(Scalar keyColor) {
		for (int i = 0; i < numCameras; i++) {
			cameraMats.get(i).setTo(keyColor);
			flipMats.get(i).setTo(keyColor);
			warpedMats.get(i).setTo(keyColor);
		}
		
		// Set all mats to the default color
		imageBeforeScale.setTo(keyColor);
		imageAfterScale.setTo(keyColor);
		resultingImage.setTo(keyColor);
	}

	public Mat getImageBeforeScale() {
		return imageBeforeScale;
	}
	
	public Mat getImageAfterScale() {
		return imageAfterScale;
	}
	
	public Mat getResultingImage() {
		return resultingImage;
	}

	public List<IRCamera> getCameras() {
		return cameras;
	}

	public List<List<Point>> getCallibrations() {
		return callibPerCamera;
	}
	
	public void updateCurrentColor(Scalar color) {
		currentColor = color;
	}
	
	public void updateDrawFlag(boolean flag) {
		drawFlag = flag;
	}
}
