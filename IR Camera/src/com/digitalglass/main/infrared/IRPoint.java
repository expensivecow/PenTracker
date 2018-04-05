package com.digitalglass.main.infrared;

import org.opencv.core.Point;

public class IRPoint {
	private int x_rank;
	private int y_rank;
	private Point point;
	
	IRPoint(Point p) {
		setPoint(p);
		x_rank = -1;
		y_rank = -1;
	}

	public void setXRank(int x) {
		this.x_rank = x;
	}
	
	public void setYRank(int y) {
		this.y_rank = y;
	}
	
	public int getXRank() {
		return x_rank;
	}
	
	public int getYRank() {
		return y_rank;
	}
	
	public boolean hasXRank() {
		// Ranks start at 0 by convention
		return x_rank >= 0;
	}
	
	public boolean hasYRank() {
		// Ranks start at 0 by convention
		return y_rank >= 0;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
}
