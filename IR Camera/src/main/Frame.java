package main;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class Frame {
	private List<Point> callibrationCorners;
	private List<Point> target;
	private Mat img;
	private Mat mapMatrix;
	private Mat testFrame;
	private int height;
	private int width;
	
	public Frame(Mat image, List<Point> cornerPoints, int w, int h) {
		// Initialize target mat coordinates
		target = new ArrayList<Point>();
		target.add(new Point(0, 0));
		target.add(new Point(image.cols(), 0));
		target.add(new Point(image.cols(), image.rows()));
		target.add(new Point(0, image.rows()));
		
		callibrationCorners = cornerPoints;
		
		updateMapMatrix();
		
		img = image;
		
		testFrame = new Mat(h, w, CvType.CV_8UC3);

		width = w;
		height = h;
	}
	
	private void updateMapMatrix() {
		Mat cornersMat = Converters.vector_Point2f_to_Mat(callibrationCorners);
		Mat targetMat = Converters.vector_Point2f_to_Mat(target);
		
		mapMatrix = Imgproc.getPerspectiveTransform(cornersMat, targetMat);
	}
	
	public Mat getProjection() {
		Mat result = null;
		if(callibrationCorners.size() == 4) {
			Mat proj = new Mat();
			
			Imgproc.warpPerspective(img, proj, mapMatrix, new Size(img.cols(), img.rows()));
			
			result = proj;
		}
		
		return result;
	}
	
	public void updateTestFrame(Point point) {
		Mat transformedImage = new Mat(height, width, CvType.CV_8UC3);
		
		List<Point> newPoints = new ArrayList<Point>();
		newPoints.add(point);
		
		List<Point> transformedPoint = new ArrayList<Point>();

		Core.perspectiveTransform(Converters.vector_Point2f_to_Mat(newPoints), transformedImage, mapMatrix);
		Converters.Mat_to_vector_Point2f(transformedImage, transformedPoint);
		
		for(Point p : transformedPoint) {
			System.out.println("TEST " + p.toString());
			Core.circle(testFrame, p, 3, new Scalar(0, 0, 255), -1);
		}
	}
	
	public Mat getTestFrame() {
		return testFrame;
	}
}