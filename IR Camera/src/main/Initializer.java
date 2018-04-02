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
import org.opencv.imgproc.Imgproc;

import utils.Config;


public class Initializer {
	public static ImgWindow outputWindow;
	private static Board currentBoard;
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 

		Initializer initializer = new Initializer();
		initializer.start();
		
		currentBoard = new Board(2, new Scalar(0, 255, 0), new Scalar(255, 0, 0), 5);
		
		while(true) {
			currentBoard.updateImage();
			outputWindow.setImage(currentBoard.getImage());
		}
	}

	private void start(){
		outputWindow = ImgWindow.newWindow("Merged Image");
	}
}