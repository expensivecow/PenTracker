package main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

public class IRCoordinates {
	private final int MAX_CAMERA_VALUE = 1023;
	private boolean foundAPoint;
	private int numPointsFound;
	private List<IRPoint> coordinates = new ArrayList<IRPoint>();
	
	public IRCoordinates(String inputCoordinates) {
		updateCoordinates(inputCoordinates);
		
		if (coordinates.size() > 0) 
			foundAPoint = (coordinates.get(0).getPoint().x != MAX_CAMERA_VALUE && coordinates.get(0).getPoint().y != MAX_CAMERA_VALUE);
		else 
			foundAPoint = false;
		
		numPointsFound = 0;
		
		if (foundAPoint) {
			for (int i = 0; i < coordinates.size(); i++) {
				if (coordinates.get(i).getPoint().x != MAX_CAMERA_VALUE && coordinates.get(i).getPoint().y != MAX_CAMERA_VALUE) {
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
	
	public List<IRPoint> getIRPoints() {
		return coordinates;
	}
	
	public void updateCoordinates(String inputCoordinates) {
		String[] stringCoordinate = cleanUpString(inputCoordinates);
		
		for(int i = 0; i < stringCoordinate.length-1; i += 2) {
			double x = Double.parseDouble(stringCoordinate[i]);
			double y = Double.parseDouble(stringCoordinate[i+1]);
			IRPoint point = new IRPoint(new Point(x, y));
			coordinates.add(point);
		}
	}
	
	public double getXCoordinate(int pointNum) {
		return coordinates.get(pointNum).getPoint().x;
	}
	
	public double getYCoordinate(int pointNum) {
		return coordinates.get(pointNum).getPoint().y;
	}
	
	public boolean arePointsFound() {
		return foundAPoint;
	}
	
	public int getNumPointsFound() {
		return numPointsFound;
	}
	
	public String toString() {
		String result = "";
		
		for(int i = 0; i < coordinates.size(); i++) {
			result += (int) coordinates.get(i).getPoint().x + "," + (int) coordinates.get(i).getPoint().y;
			
			if(i != 3) {
				result += ",";
			}
		}
		
		return result;
	}
	
	public void translateCoordinates() {
		List<IRPoint> points = new ArrayList<IRPoint>();
		
		for(int i = 0; i < coordinates.size(); i++) {
			IRPoint currPoint = coordinates.get(i);
			
			double x = currPoint.getPoint().x;
			double y = currPoint.getPoint().y;
			
			points.add(new IRPoint(new Point(y, 1023.0 - x)));
		}
		
		coordinates = points;
	}
	
	public void orderPoints() {
		// Rank x and y points	
		for(int i = 0; i < 4; i++) {
			double maxX = Integer.MAX_VALUE;
			double maxY = Integer.MAX_VALUE;

			int currentMinX = 0;
			int currentMinY = 0;
			
			// Get the min X value that isnt ranked yet
			for (int j = 0; j < 4; j++) {
				IRPoint currPoint = coordinates.get(j);
				
				if (!currPoint.hasXRank() && currPoint.getPoint().x <= maxX) {
					currentMinX = j;
					maxX = currPoint.getPoint().x;
				}
			}

			// Get the min Y value that isnt ranked yet
			for (int k = 0; k < 4; k++) {
				IRPoint currPoint = coordinates.get(k);
				
				if (!currPoint.hasYRank() && currPoint.getPoint().y <= maxY) {
					currentMinY = k;
					maxY = currPoint.getPoint().y;
				}
			}
			
			// Set the rank so that the coordinates x/y won't be checked again
			coordinates.get(currentMinX).setXRank(i);
			coordinates.get(currentMinY).setYRank(i);
		}
		
		List<IRPoint> orderedCoordinates = new ArrayList<IRPoint>();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				IRPoint currPoint = coordinates.get(j);

				// for x ranked 0 || 1 and y ranked 0 || 1 = first point
				if (i == 0 && (currPoint.getXRank() == 0 || currPoint.getXRank() == 1) && (currPoint.getYRank() == 0 || currPoint.getYRank() == 1)) {
					orderedCoordinates.add(currPoint);
				}
				// for x ranked 2 || 3 and y ranked 0 || 1 = second point
				if (i == 1 && (currPoint.getXRank() == 2 || currPoint.getXRank() == 3) && (currPoint.getYRank() == 0 || currPoint.getYRank() == 1)) {
					orderedCoordinates.add(currPoint);
				}
				// for x ranked 2 || 3 and y ranked 2 || 3 = third point
				if (i == 2 && (currPoint.getXRank() == 2 || currPoint.getXRank() == 3) && (currPoint.getYRank() == 2 || currPoint.getYRank() == 3)) {
					orderedCoordinates.add(currPoint);
				}
				// for x ranked 0 || 1 and y ranked 2 || 3 = first point
				if (i == 3 && (currPoint.getXRank() == 0 || currPoint.getXRank() == 1) && (currPoint.getYRank() == 2 || currPoint.getYRank() == 3)) {
					orderedCoordinates.add(currPoint);
				}
			}
		}
		
		coordinates = orderedCoordinates;
	}
}
