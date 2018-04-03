package main;
import guisupport.ImgWindow;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Scalar;

public class Initializer {
	private final static Scalar GREEN_COLOR = new Scalar(0, 255, 0);
	private final static Scalar SKY_BLUE_COLOR = new Scalar(235, 206, 136);
	private final static Scalar DEEP_BLUE_COLOR = new Scalar(255, 0, 0);

	public static ImgWindow outputWindow;
	private static Board currentBoard;
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 

		Initializer initializer = new Initializer();
		initializer.start();

		currentBoard = new Board(2, GREEN_COLOR, DEEP_BLUE_COLOR, 3);
		
		//Callibration callibration = new Callibration(currentBoard.getCameras());
		//callibration.startCallibration();
		
		while(true) {
			currentBoard.updateImage();
			outputWindow.setImage(currentBoard.getResultingImage());
		}
	}

	private void start(){
		outputWindow = ImgWindow.newWindow("Merged Image");
	}
}