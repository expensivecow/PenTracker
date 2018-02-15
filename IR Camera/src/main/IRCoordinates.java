package main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

public class IRCoordinates {
	private final int MAX_CAMERA_VALUE = 1023;
	private boolean foundAPoint;
	private int numPointsFound;
	private List<Point> coordinates = new ArrayList<Point>();
	
	public IRCoordinates(String inputCoordinates) {
		String[] stringCoordinate = cleanUpString(inputCoordinates);
		
		for(int i = 0; i < 8; i += 2) {
			double x = Double.parseDouble(stringCoordinate[i]);
			double y = Double.parseDouble(stringCoordinate[i+1]);
			Point point = new Point(x, y);
			coordinates.add(point);
		}
		
		foundAPoint = (coordinates.get(0).x != MAX_CAMERA_VALUE && coordinates.get(0).y != MAX_CAMERA_VALUE);
		
		numPointsFound = 0;
		
		if (foundAPoint) {
			for (int i = 0; i < 4; i++) {
				if (coordinates.get(i).x != MAX_CAMERA_VALUE && coordinates.get(i).y != MAX_CAMERA_VALUE) {
					numPointsFound++;
				}
			}
		}
	}

	private String[] cleanUpString(String toClean) {
		String result = toClean.replace("[", "");
		result = result.replace("]", "");
		result = result.replace(" ", "");
		
		return result.split(","); 
	}
	
	public double getXCoordinate(int pointNum) {
		return coordinates.get(pointNum).x;
	}
	
	public double getYCoordinate(int pointNum) {
		return coordinates.get(pointNum).y;
	}
	
	public boolean arePointsFound() {
		return foundAPoint;
	}
	
	public int getNumPointsFound() {
		return numPointsFound;
	}
}
